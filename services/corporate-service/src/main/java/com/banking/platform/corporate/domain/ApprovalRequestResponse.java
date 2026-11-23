package com.banking.platform.corporate.domain;

import java.time.Instant;
import java.util.UUID;

public record ApprovalRequestResponse(
        UUID approvalId,
        UUID organizationId,
        String operationType,
        String operationReference,
        int policyVersion,
        ApprovalRequestStatus status,
        Integer currentStep,
        Instant submittedAt,
        Instant completedAt) {}
