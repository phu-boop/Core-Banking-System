package com.banking.platform.transfer.integration;

import java.util.UUID;

public interface LedgerExecutionClient {
    UUID postInternalJournal(UUID transactionId, UUID sourceAccountId,
                              UUID destinationAccountId, java.math.BigDecimal amount,
                              String operationKey, String correlationId);
}
