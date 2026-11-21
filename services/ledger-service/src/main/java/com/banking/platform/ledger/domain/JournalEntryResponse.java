package com.banking.platform.ledger.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record JournalEntryResponse(
        UUID entryId,
        UUID accountId,
        EntrySide side,
        BigDecimal amount,
        String currency) {
}
