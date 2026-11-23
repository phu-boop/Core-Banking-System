package com.banking.platform.ledger.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record JournalResponse(
        UUID journalId,
        UUID transactionId,
        String status,
        OffsetDateTime postedAt,
        List<JournalEntryResponse> entries) {
}
