package com.banking.platform.transfer.application;

import com.banking.platform.transfer.application.TransferExceptions.ConflictException;
import com.banking.platform.transfer.application.TransferExceptions.NotFoundException;
import com.banking.platform.transfer.domain.CorporateApprovalResponse;
import com.banking.platform.transfer.domain.SubmitTransferRequest;
import com.banking.platform.transfer.domain.TransferRecord;
import com.banking.platform.transfer.domain.TransferStatus;
import com.banking.platform.transfer.integration.CorporateApprovalClient;
import com.banking.platform.transfer.integration.PaymentSubmissionClient;
import com.banking.platform.transfer.persistence.TransferRepository;
import com.banking.platform.transfer.persistence.TransferRepository.IdempotencyRecord;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApprovalEnforcedTransferService {
    private final TransferRepository repository;
    private final CorporateApprovalClient corporate;
    private final ExecutionAdapter executionAdapter;
    private final PaymentSubmissionClient payment;

    @Autowired
    public ApprovalEnforcedTransferService(TransferRepository repository,
                                           CorporateApprovalClient corporate,
                                           ExecutionAdapter executionAdapter) {
        this(repository, corporate, executionAdapter, null);
    }

    public ApprovalEnforcedTransferService(TransferRepository repository,
                                           CorporateApprovalClient corporate,
                                           ExecutionAdapter executionAdapter,
                                           PaymentSubmissionClient payment) {
        this.repository = repository;
        this.corporate = corporate;
        this.executionAdapter = executionAdapter;
        this.payment = payment;
    }

    @Transactional
    public TransferRecord submit(SubmitTransferRequest request, String idempotencyKey,
                                 String actor, String accessToken, String correlationId) {
        String payloadHash = payloadHash(request);
        String operation = "corporate-transfer-submit";
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<IdempotencyRecord> replay = repository.findIdempotentTransfer(
                    idempotencyKey, actor, request.organizationId(), operation);
            if (replay.isPresent()) {
                if (!replay.get().payloadHash().equals(payloadHash)) {
                    throw new ConflictException("Idempotency-Key conflicts with a different payload");
                }
                return repository.find(replay.get().transferId())
                        .orElseThrow(() -> new NotFoundException(replay.get().transferId()));
            }
        }

        UUID transferId = UUID.randomUUID();
        Instant now = Instant.now();
        CorporateApprovalResponse approval = corporate.createRequest(
                request.organizationId(), request.operationType(), request.operationReference(),
                accessToken, correlationId);
        TransferStatus status = status(approval.status());
        TransferStatus transferStatus = status(approval.status());
        TransferRecord transfer = new TransferRecord(
                transferId, request.organizationId(), request.operationType(),
                request.operationReference(), request.amount(), request.currency(),
                approval.approvalId(), approval.policyVersion(), approval.status(),
                transferStatus, actor, correlationId, now, now);
        repository.insert(transfer);
        repository.audit(transferId, actor, "SUBMIT", approval.approvalId(),
                approval.policyVersion(), approval.status(), correlationId);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            repository.saveIdempotency(idempotencyKey, actor, request.organizationId(),
                    operation, payloadHash, transferId);
        }
        if (transferStatus == TransferStatus.APPROVED
                && "INTERBANK".equalsIgnoreCase(request.operationType())
                && payment != null) {
            com.banking.platform.transfer.domain.PaymentSubmissionResponse submission =
                    payment.submit(transferId, request.operationReference(),
                            request.amount(), request.currency(), accessToken, correlationId);
            repository.updatePayment(transferId, submission.paymentId(),
                    submission.status(),
                    submission.status().equals("UNRESOLVED")
                            ? TransferStatus.UNRESOLVED_INTERBANK
                            : TransferStatus.PENDING_INTERBANK);
            repository.audit(transferId, actor, "SUBMIT_INTERBANK",
                    approval.approvalId(), approval.policyVersion(),
                    "PENDING_INTERBANK:" + submission.status(), correlationId);
            return repository.find(transferId).orElse(transfer);
        }
        return transfer;
    }

    @Transactional(readOnly = true)
    public TransferRecord get(UUID transferId) {
        return repository.find(transferId).orElseThrow(() -> new NotFoundException(transferId));
    }

    @Transactional
    public TransferRecord refreshApproval(UUID organizationId, UUID transferId,
                                          String accessToken, String actor, String correlationId) {
        TransferRecord transfer = get(transferId);
        if (transfer.status() == TransferStatus.REJECTED
                || transfer.status() == TransferStatus.EXECUTION_REQUESTED) {
            return transfer;
        }
        CorporateApprovalResponse approval = corporate.getRequest(
                organizationId, transfer.approvalRequestId(), accessToken, correlationId);
        TransferStatus status = status(approval.status());
        repository.updateApproval(transfer.transferId(), approval.approvalId(),
                approval.policyVersion(), approval.status(), status);
        repository.audit(transfer.transferId(), actor, "REFRESH_APPROVAL",
                approval.approvalId(), approval.policyVersion(), approval.status(), correlationId);
        return get(transfer.transferId());
    }

    @Transactional
    public TransferRecord refreshPayment(UUID transferId, String accessToken,
                                         String actor, String correlationId) {
        TransferRecord transfer = get(transferId);
        if (transfer.paymentRequestId() == null || payment == null) {
            return transfer;
        }
        com.banking.platform.transfer.domain.PaymentSubmissionResponse response =
                payment.get(transfer.paymentRequestId(), accessToken, correlationId);
        TransferStatus status = switch (response.status()) {
            case "SETTLED" -> TransferStatus.INTERBANK_SETTLED;
            case "FAILED" -> TransferStatus.INTERBANK_FAILED;
            case "UNRESOLVED" -> TransferStatus.UNRESOLVED_INTERBANK;
            default -> TransferStatus.PENDING_INTERBANK;
        };
        repository.updatePayment(transferId, transfer.paymentRequestId(),
                response.status(), status);
        repository.audit(transferId, actor, "REFRESH_PAYMENT",
                transfer.approvalRequestId(), transfer.approvalPolicyVersion(),
                "PAYMENT:" + response.status(), correlationId);
        return get(transferId);
    }

    @Transactional
    public TransferRecord requestExecution(UUID transferId, String actor, String correlationId) {
        TransferRecord transfer = get(transferId);
        if (transfer.status() != TransferStatus.APPROVED) {
            throw new ConflictException("Transfer execution requires APPROVED status");
        }
        executionAdapter.requestExecution(transfer);
        repository.updateApproval(transfer.transferId(), transfer.approvalRequestId(),
                transfer.approvalPolicyVersion(), transfer.approvalStatus(),
                TransferStatus.EXECUTION_REQUESTED);
        repository.audit(transfer.transferId(), actor, "REQUEST_EXECUTION",
                transfer.approvalRequestId(), transfer.approvalPolicyVersion(),
                "EXECUTION_REQUESTED", correlationId);
        return get(transfer.transferId());
    }

    @Transactional
    public void cancel(UUID transferId) {
        TransferRecord transfer = get(transferId);
        if (transfer.status() == TransferStatus.REJECTED
                || transfer.status() == TransferStatus.EXECUTION_REQUESTED) {
            throw new ConflictException("Transfer cancellation is not supported in this state");
        }
        throw new UnsupportedOperationException("Transfer cancellation is outside the current approval slice");
    }

    private TransferStatus status(String approvalStatus) {
        return switch (approvalStatus) {
            case "APPROVED" -> TransferStatus.APPROVED;
            case "REJECTED" -> TransferStatus.REJECTED;
            default -> TransferStatus.PENDING_APPROVAL;
        };
    }

    private String payloadHash(SubmitTransferRequest request) {
        try {
            String payload = request.organizationId() + "|" + request.operationType() + "|"
                    + request.operationReference() + "|" + request.amount() + "|" + request.currency();
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash transfer payload", ex);
        }
    }
}
