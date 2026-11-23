package com.banking.platform.reporting.domain;

import java.time.Instant;

public record ExportRequest(
        String exportKey,
        String organizationId,
        String accountId,
        String branchId,
        String channel,
        Instant from,
        Instant to,
        String format) {
}
