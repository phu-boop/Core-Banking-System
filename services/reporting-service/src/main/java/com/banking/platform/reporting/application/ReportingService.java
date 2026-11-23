package com.banking.platform.reporting.application;

import com.banking.platform.reporting.domain.ExportJob;
import com.banking.platform.reporting.domain.ExportRequest;
import com.banking.platform.reporting.domain.ReportRequest;
import com.banking.platform.reporting.domain.ReportResponse;
import com.banking.platform.reporting.persistence.ReportingRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {
    private static final Set<String> ALLOWED_REPORTS = Set.of(
            "ACCOUNT", "TRANSACTION", "AUDIT", "OPERATIONAL");
    private static final Set<String> ALLOWED_FORMATS = Set.of("CSV", "JSON");
    private static final Duration MAX_RANGE = Duration.ofDays(31);

    private final ReportingRepository repository;

    public ReportingService(ReportingRepository repository) {
        this.repository = repository;
    }

    public ReportResponse query(String reportType, ReportRequest request) {
        String normalized = normalizeReportType(reportType);
        validateRange(request.from(), request.to());
        if (request.page() == null || request.page() < 1) {
            throw new IllegalArgumentException("page must be at least 1");
        }
        if (request.pageSize() == null || request.pageSize() < 1 || request.pageSize() > 100) {
            throw new IllegalArgumentException("pageSize must be between 1 and 100");
        }
        Instant generatedAt = Instant.now();
        Instant watermark = repository.findSourceWatermark(normalized, request)
                .orElse(generatedAt);
        return new ReportResponse(normalized,
                repository.findRows(normalized, request), generatedAt, watermark);
    }

    public ExportJob export(String reportType, ExportRequest request) {
        String normalized = normalizeReportType(reportType);
        validateRange(request.from(), request.to());
        if (request.exportKey() == null || request.exportKey().isBlank()) {
            throw new IllegalArgumentException("exportKey is required");
        }
        if (request.format() == null
                || !ALLOWED_FORMATS.contains(request.format().toUpperCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Unsupported export format");
        }
        return repository.findExportByKey(request.exportKey())
                .orElseGet(() -> repository.insertExport(normalized,
                        new ExportRequest(request.exportKey(), request.organizationId(),
                                request.accountId(), request.branchId(), request.channel(),
                                request.from(), request.to(),
                                request.format().toUpperCase(Locale.ROOT))));
    }

    private String normalizeReportType(String reportType) {
        if (reportType == null) {
            throw new IllegalArgumentException("reportType is required");
        }
        String normalized = reportType.toUpperCase(Locale.ROOT);
        if (!ALLOWED_REPORTS.contains(normalized)) {
            throw new IllegalArgumentException("Unknown report type");
        }
        return normalized;
    }

    private void validateRange(Instant from, Instant to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to are required");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("to must not precede from");
        }
        if (Duration.between(from, to).compareTo(MAX_RANGE) > 0) {
            throw new IllegalArgumentException("Report range exceeds 31 days");
        }
    }
}
