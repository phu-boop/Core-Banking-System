package com.banking.platform.ledger.domain;

import java.util.List;
import java.util.UUID;

public record JournalRequest(
        UUID transactionId,
        String operationKey,
        String currency,
        List<JournalEntryRequest> entries) {
}
