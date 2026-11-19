package com.banking.platform.party.domain;

import java.time.Instant;
import java.util.UUID;

public record PartyResponse(
        UUID partyId,
        PartyType partyType,
        PartyStatus status,
        String displayName,
        UUID managingBranchId,
        long version,
        Instant createdAt,
        Instant updatedAt) {}
