package com.banking.platform.audit.domain;

import java.time.Instant;

public record AuditRecordRequest(
        String eventId,
        String actorId,
        String channel,
        String action,
        String resourceType,
        String resourceId,
        String organizationId,
        String branchId,
        String result,
        String correlationId,
        Instant occurredAt) {}
