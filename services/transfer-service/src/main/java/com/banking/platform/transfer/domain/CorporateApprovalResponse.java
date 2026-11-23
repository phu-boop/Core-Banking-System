package com.banking.platform.transfer.domain;

import java.time.Instant;
import java.util.UUID;

public record CorporateApprovalResponse(
        UUID approvalId,
        UUID organizationId,
        String operationType,
        String operationReference,
        int policyVersion,
        String status,
        Integer currentStep,
        Instant submittedAt,
        Instant completedAt) {}
