package com.banking.platform.corporate.domain;

import java.time.Instant;
import java.util.UUID;

public record MandateResponse(
        UUID mandateId,
        UUID membershipId,
        String mandateScope,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
