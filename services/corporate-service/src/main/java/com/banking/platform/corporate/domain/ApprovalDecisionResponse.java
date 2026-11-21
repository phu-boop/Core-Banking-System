package com.banking.platform.corporate.domain;

import java.util.UUID;

public record ApprovalDecisionResponse(
        UUID approvalId,
        ApprovalRequestStatus status,
        Integer currentStep,
        UUID historyId) {}
