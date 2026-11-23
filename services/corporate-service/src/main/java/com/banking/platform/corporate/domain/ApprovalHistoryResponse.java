package com.banking.platform.corporate.domain;

import java.time.Instant;
import java.util.UUID;

public record ApprovalHistoryResponse(
        UUID historyId,
        int stepNumber,
        String actor,
        ApprovalDecision decision,
        String reason,
        int policyVersion,
        Instant occurredAt,
        String correlationId) {}
