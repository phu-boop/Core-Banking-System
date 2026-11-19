package com.banking.platform.branch.domain;

import java.time.Instant;
import java.util.UUID;

public record BranchResponse(
        UUID branchId,
        String code,
        String name,
        BranchStatus status,
        Instant createdAt,
        Instant updatedAt) {}
