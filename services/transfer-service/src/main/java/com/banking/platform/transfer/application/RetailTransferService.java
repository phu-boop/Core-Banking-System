package com.banking.platform.transfer.application;

import com.banking.platform.transfer.domain.RetailTransferRecord;
import com.banking.platform.transfer.domain.RetailTransferRequest;
import com.banking.platform.transfer.domain.TransferStatus;
import com.banking.platform.transfer.integration.LedgerExecutionClient;
import com.banking.platform.transfer.persistence.RetailTransferRepository;
import com.banking.platform.transfer.persistence.RetailTransferRepository.RetailIdempotencyRecord;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

@Service
public class RetailTransferService {
    private static final String OPERATION = "retail-internal-transfer";
    private final RetailTransferRepository repository;
    private final LedgerExecutionClient ledger;

    public RetailTransferService(RetailTransferRepository repository,
                                 LedgerExecutionClient ledger) {
        this.repository = repository;
        this.ledger = ledger;
    }

    @Transactional
    public RetailTransferRecord submit(RetailTransferRequest request, String idempotencyKey,
                                       String actor, String correlationId) {
        validate(request);
        String businessScope = "branch:" + request.processingBranchId();
        String payloadHash = payloadHash(request);
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new TransferExceptions.ValidationException("Idempotency-Key is required");
        }

        Optional<RetailIdempotencyRecord> replay = repository.findReplay(
                idempotencyKey, actor, businessScope, OPERATION);
        if (replay.isPresent()) {
            if (!replay.get().payloadHash().equals(payloadHash)) {
                throw new TransferExceptions.ConflictException(
                        "Idempotency-Key conflicts with a different payload");
            }
            return repository.find(replay.get().transferId())
                    .orElseThrow(() -> new TransferExceptions.NotFoundException(
                            replay.get().transferId()));
        }

        UUID transferId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        Instant now = Instant.now();
        RetailTransferRecord record = new RetailTransferRecord(
                transferId, transactionId, request.operationType(),
                request.sourceAccountId(), request.destinationAccountId(),
                request.processingBranchId(), request.amount(), "VND",
                TransferStatus.ACCEPTED, null, correlationId, now, now);
        repository.insert(record);
        repository.saveIdempotency(idempotencyKey, actor, businessScope,
                OPERATION, payloadHash, transferId);
        repository.audit(transferId, actor, "RETAIL_SUBMIT", "ACCEPTED", correlationId);

        try {
            repository.updateStatus(transferId, TransferStatus.PROCESSING, null);
            UUID journalId = ledger.postInternalJournal(
                    transactionId, request.sourceAccountId(),
                    request.destinationAccountId(), request.amount(),
                    idempotencyKey, correlationId);
            repository.updateStatus(transferId, TransferStatus.SETTLED, journalId);
            repository.audit(transferId, actor, "LEDGER_POST", "SETTLED", correlationId);
            return repository.find(transferId).orElseThrow();
        } catch (ResourceAccessException exception) {
            repository.updateStatus(transferId, TransferStatus.PROCESSING, null);
            repository.audit(transferId, actor, "LEDGER_POST", "PROCESSING", correlationId);
            return repository.find(transferId).orElseThrow();
        } catch (DuplicateKeyException exception) {
            return repository.find(transferId).orElseThrow();
        } catch (RuntimeException exception) {
            repository.updateStatus(transferId, TransferStatus.FAILED, null);
            repository.audit(transferId, actor, "LEDGER_POST", "FAILED", correlationId);
            return repository.find(transferId).orElseThrow();
        }
    }

    @Transactional(readOnly = true)
    public RetailTransferRecord get(UUID transferId) {
        return repository.find(transferId)
                .orElseThrow(() -> new TransferExceptions.NotFoundException(transferId));
    }

    private void validate(RetailTransferRequest request) {
        if (request == null || request.operationType() == null
                || request.processingBranchId() == null
                || request.sourceAccountId() == null
                || request.destinationAccountId() == null
                || !"VND".equals(request.currency())
                || request.amount() == null
                || request.amount().compareTo(BigDecimal.ZERO) <= 0
                || request.amount().scale() > 2) {
            throw new TransferExceptions.ValidationException(
                    "operationType, branch, accounts, positive VND amount are required");
        }
        if (request.sourceAccountId().equals(request.destinationAccountId())) {
            throw new TransferExceptions.ValidationException(
                    "Source and destination accounts must differ");
        }
    }

    private String payloadHash(RetailTransferRequest request) {
        try {
            String payload = request.operationType() + "|" + request.sourceAccountId() + "|"
                    + request.destinationAccountId() + "|" + request.processingBranchId()
                    + "|" + request.amount().toPlainString() + "|VND";
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to hash retail transfer payload", exception);
        }
    }
}
