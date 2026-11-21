package com.banking.platform.ledger.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record JournalEntryRequest(UUID accountId, EntrySide side, BigDecimal amount) {
}
