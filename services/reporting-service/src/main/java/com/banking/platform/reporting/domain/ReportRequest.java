package com.banking.platform.reporting.domain;

import java.time.Instant;

public record ReportRequest(
        String organizationId,
        String accountId,
        String branchId,
        String channel,
        Instant from,
        Instant to,
        Integer page,
        Integer pageSize) {
}
