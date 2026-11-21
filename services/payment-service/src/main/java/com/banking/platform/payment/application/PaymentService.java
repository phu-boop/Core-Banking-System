package com.banking.platform.payment.application;

import com.banking.platform.payment.application.PaymentExceptions.ConflictException;
import com.banking.platform.payment.application.PaymentExceptions.NotFoundException;
import com.banking.platform.payment.domain.PaymentOutcomeRequest;
import com.banking.platform.payment.domain.PaymentRecord;
import com.banking.platform.payment.domain.PaymentStatus;
import com.banking.platform.payment.domain.SubmitPaymentRequest;
import com.banking.platform.payment.persistence.PaymentRepository;
import com.banking.platform.payment.persistence.PaymentRepository.IdempotencyRecord;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final com.banking.platform.payment.integration.InterbankProviderAdapter providerAdapter;

    public PaymentService(PaymentRepository repository,
                          com.banking.platform.payment.integration.InterbankProviderAdapter providerAdapter) {
        this.repository = repository;
        this.providerAdapter = providerAdapter;
    }

    @Transactional
    public PaymentRecord submit(SubmitPaymentRequest request, String idempotencyKey,
                                String actor, String correlationId) {
        if (!"VND".equals(request.currency())) {
            throw new IllegalArgumentException("Only VND interbank payment is supported");
        }
        String payloadHash = payloadHash(request);
        String operation = "payment-submit";
        String businessScope = request.transferId().toString();
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<IdempotencyRecord> replay = repository.findIdempotency(
                    idempotencyKey, actor, operation, businessScope);
            if (replay.isPresent()) {
                if (!replay.get().payloadHash().equals(payloadHash)) {
                    throw new ConflictException("Idempotency-Key conflicts with a different payload");
                }
                return repository.find(replay.get().paymentId())
                        .orElseThrow(() -> new NotFoundException(replay.get().paymentId()));
            }
        }

        Instant now = Instant.now();
        UUID paymentId = UUID.randomUUID();
        PaymentRecord payment = new PaymentRecord(
                paymentId, request.transferId(), request.amount(), request.currency(),
                request.destinationReference(), PaymentStatus.PENDING_SUBMISSION,
                null, correlationId, now, now);
        repository.insert(payment);
        repository.audit(paymentId, actor, "SUBMIT", "PENDING_SUBMISSION",
                "TRANSFER_BOUNDARY", null, correlationId);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            repository.saveIdempotency(idempotencyKey, actor, operation, businessScope,
                    payloadHash, paymentId);
        }
        if (providerAdapter != null) {
            com.banking.platform.payment.integration.InterbankProviderAdapter.ProviderSubmissionResponse providerResponse =
                    providerAdapter.submit(
                            paymentId,
                            request.destinationReference(),
                            request.amount(),
                            request.currency(),
                            correlationId);
            if ("UNRESOLVED".equals(providerResponse.status())) {
                repository.recordOutcome(paymentId, UUID.randomUUID(), "UNRESOLVED",
                        "MOCK_PROVIDER_QUERY", providerResponse.providerReference(), correlationId);
                repository.audit(paymentId, actor, "PROVIDER_SUBMIT", "UNRESOLVED",
                        "MOCK_PROVIDER", providerResponse.providerReference(), correlationId);
                repository.updateStatus(paymentId, PaymentStatus.UNRESOLVED,
                        providerResponse.providerReference());
                return get(paymentId);
            }
            if ("FAILED".equals(providerResponse.status())) {
                repository.recordOutcome(paymentId, UUID.randomUUID(), "FAILED",
                        "MOCK_PROVIDER", providerResponse.providerReference(), correlationId);
                repository.audit(paymentId, actor, "PROVIDER_SUBMIT", "FAILED",
                        "MOCK_PROVIDER", providerResponse.providerReference(), correlationId);
                repository.updateStatus(paymentId, PaymentStatus.FAILED,
                        providerResponse.providerReference());
                return get(paymentId);
            }
            repository.updateStatus(paymentId, PaymentStatus.PENDING_SUBMISSION,
                    providerResponse.providerReference());
            return get(paymentId);
        }
        return payment;
    }

    @Transactional(readOnly = true)
    public PaymentRecord get(UUID paymentId) {
        return repository.find(paymentId).orElseThrow(() -> new NotFoundException(paymentId));
    }

    @Transactional
    public PaymentRecord recordOutcome(UUID paymentId, PaymentOutcomeRequest request) {
        PaymentRecord payment = get(paymentId);
        if (payment.status() == PaymentStatus.SETTLED || payment.status() == PaymentStatus.FAILED) {
            if (repository.outcomeExists(paymentId, request.outcome(), request.outcomeSource(),
                    request.providerReference())) {
                return payment;
            }
            throw new ConflictException("Terminal payment outcome cannot be changed");
        }

        PaymentStatus outcomeStatus = switch (request.outcome()) {
            case "SETTLED" -> PaymentStatus.SETTLED;
            case "FAILED" -> PaymentStatus.FAILED;
            case "UNRESOLVED" -> PaymentStatus.UNRESOLVED;
            default -> throw new IllegalArgumentException("Unsupported payment outcome");
        };
        if (outcomeStatus == PaymentStatus.UNRESOLVED
                && (request.outcomeSource() == null || request.outcomeSource().isBlank())) {
            throw new IllegalArgumentException("Outcome source is required");
        }
        if ((outcomeStatus == PaymentStatus.SETTLED || outcomeStatus == PaymentStatus.FAILED)
                && (request.outcomeSource() == null || request.outcomeSource().isBlank())) {
            throw new IllegalArgumentException("Verified outcome source is required");
        }

        repository.recordOutcome(paymentId, UUID.randomUUID(), outcomeStatus.name(),
                request.outcomeSource(), request.providerReference(), request.correlationId());
        repository.audit(paymentId, request.outcomeSource(), "RECORD_OUTCOME", outcomeStatus.name(),
                request.outcomeSource(), request.providerReference(), request.correlationId());
        repository.updateStatus(paymentId, outcomeStatus, request.providerReference());
        return get(paymentId);
    }

    private String payloadHash(SubmitPaymentRequest request) {
        try {
            String payload = request.transferId() + "|" + request.destinationReference() + "|"
                    + request.amount() + "|" + request.currency();
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash payment payload", ex);
        }
    }
}
