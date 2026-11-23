package com.banking.platform.corporate.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ApprovalPolicyResponse(
        UUID policyId,
        UUID organizationId,
        String operationType,
        int version,
        ApprovalPolicyStatus status,
        List<ApprovalPolicyLevel> levels,
        Instant createdAt,
        Instant activatedAt) {}
