package com.banking.platform.reporting.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.platform.reporting.domain.ExportJob;
import com.banking.platform.reporting.domain.ExportRequest;
import com.banking.platform.reporting.domain.ReportRequest;
import com.banking.platform.reporting.persistence.ReportingRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportingServiceTests {
    private ReportingRepository repository;
    private ReportingService service;

    @BeforeEach
    void setUp() {
        repository = mock(ReportingRepository.class);
        service = new ReportingService(repository);
    }

    @Test
    void rejectsUnknownReportAndUnsupportedFormat() {
        ExportRequest request = exportRequest("XML");
        assertThrows(IllegalArgumentException.class,
                () -> service.export("UNKNOWN", request));
        assertThrows(IllegalArgumentException.class,
                () -> service.export("ACCOUNT", request));
    }

    @Test
    void rejectsRangesOverThirtyOneDays() {
        Instant now = Instant.parse("2026-11-22T00:00:00Z");
        ReportRequest request = new ReportRequest(
                "org-1", "acc-1", null, "INTERNET_BANKING",
                now, now.plus(32, ChronoUnit.DAYS), 1, 100);

        assertThrows(IllegalArgumentException.class,
                () -> service.query("ACCOUNT", request));
    }

    @Test
    void duplicateExportKeyIsReplaySafe() {
        ExportRequest request = exportRequest("CSV");
        ExportJob existing = new ExportJob(UUID.randomUUID(), "export-1",
                "ACCOUNT", "CSV", "ACCEPTED", Instant.now());
        when(repository.findExportByKey("export-1")).thenReturn(Optional.of(existing));

        ExportJob result = service.export("ACCOUNT", request);

        assertEquals(existing, result);
        verify(repository, never()).insertExport(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(ExportRequest.class));
    }

    @Test
    void projectionResponseIncludesWatermarkWithoutLedgerAuthority() {
        Instant from = Instant.parse("2026-11-22T00:00:00Z");
        Instant watermark = Instant.parse("2026-11-22T00:05:00Z");
        ReportRequest request = new ReportRequest(
                "org-1", "acc-1", null, "INTERNET_BANKING",
                from, from.plus(1, ChronoUnit.DAYS), 1, 100);
        when(repository.findSourceWatermark("ACCOUNT", request))
                .thenReturn(Optional.of(watermark));
        when(repository.findRows("ACCOUNT", request))
                .thenReturn(List.of(Map.of("account_id", "acc-1", "projected_status", "AVAILABLE")));

        var result = service.query("ACCOUNT", request);

        assertEquals(watermark, result.sourceWatermark());
        assertEquals("AVAILABLE", result.rows().getFirst().get("projected_status"));
        assertEquals(false, result.rows().getFirst().containsKey("authoritative_balance"));
    }

    private ExportRequest exportRequest(String format) {
        Instant now = Instant.parse("2026-11-22T00:00:00Z");
        return new ExportRequest("export-1", "org-1", "acc-1", null,
                "INTERNET_BANKING", now, now.plus(1, ChronoUnit.DAYS), format);
    }
}
