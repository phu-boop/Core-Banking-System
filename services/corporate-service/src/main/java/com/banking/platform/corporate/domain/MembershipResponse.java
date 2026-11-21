package com.banking.platform.corporate.domain;

import java.time.Instant;
import java.util.UUID;

public record MembershipResponse(
        UUID membershipId,
        UUID organizationId,
        String subject,
        String roleCode,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
