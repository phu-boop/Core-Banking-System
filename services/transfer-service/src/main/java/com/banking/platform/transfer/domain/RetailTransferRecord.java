package com.banking.platform.transfer.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RetailTransferRecord(
        UUID transferId,
        UUID transactionId,
        RetailTransferType operationType,
        UUID sourceAccountId,
        UUID destinationAccountId,
        UUID processingBranchId,
        BigDecimal amount,
        String currency,
        TransferStatus status,
        UUID ledgerJournalId,
        String correlationId,
        Instant createdAt,
        Instant updatedAt) {
}
