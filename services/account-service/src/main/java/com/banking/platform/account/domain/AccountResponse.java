package com.banking.platform.account.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID accountId,
        String accountNumber,
        UUID partyId,
        String accountType,
        AccountStatus status,
        String currency,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
