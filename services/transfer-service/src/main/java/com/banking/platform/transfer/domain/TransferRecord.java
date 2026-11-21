package com.banking.platform.transfer.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferRecord(
        UUID transferId,
        UUID organizationId,
        String operationType,
        String operationReference,
        BigDecimal amount,
        String currency,
        UUID approvalRequestId,
        Integer approvalPolicyVersion,
        String approvalStatus,
        TransferStatus status,
        String makerSubject,
        String correlationId,
        Instant createdAt,
        Instant updatedAt,
        UUID paymentRequestId,
        String paymentStatus) {
    public TransferRecord(UUID transferId, UUID organizationId, String operationType,
                          String operationReference, BigDecimal amount, String currency,
                          UUID approvalRequestId, Integer approvalPolicyVersion,
                          String approvalStatus, TransferStatus status, String makerSubject,
                          String correlationId, Instant createdAt, Instant updatedAt) {
        this(transferId, organizationId, operationType, operationReference, amount,
                currency, approvalRequestId, approvalPolicyVersion, approvalStatus, status,
                makerSubject, correlationId, createdAt, updatedAt, null, null);
    }
}
