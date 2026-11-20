package com.banking.platform.corporate.domain;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID organizationId,
        UUID partyId,
        OrganizationStatus status,
        Instant createdAt,
        Instant updatedAt) {}
