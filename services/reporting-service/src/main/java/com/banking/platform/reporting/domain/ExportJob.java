package com.banking.platform.reporting.domain;

import java.time.Instant;
import java.util.UUID;

public record ExportJob(
        UUID reportJobId,
        String exportKey,
        String reportType,
        String format,
        String status,
        Instant createdAt) {
}
