package com.banking.platform.reporting.persistence;

import com.banking.platform.reporting.domain.ExportJob;
import com.banking.platform.reporting.domain.ExportRequest;
import com.banking.platform.reporting.domain.ReportRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReportingRepository {
    List<Map<String, Object>> findRows(String reportType, ReportRequest request);
    Optional<Instant> findSourceWatermark(String reportType, ReportRequest request);
    Optional<ExportJob> findExportByKey(String exportKey);
    ExportJob insertExport(String reportType, ExportRequest request);
}
