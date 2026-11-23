package com.banking.platform.reporting.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ReportResponse(
        String reportType,
        List<Map<String, Object>> rows,
        Instant generatedAt,
        Instant sourceWatermark) {
}
