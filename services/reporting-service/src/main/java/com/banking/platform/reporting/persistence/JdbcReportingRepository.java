package com.banking.platform.reporting.persistence;

import com.banking.platform.reporting.domain.ExportJob;
import com.banking.platform.reporting.domain.ExportRequest;
import com.banking.platform.reporting.domain.ReportRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcReportingRepository implements ReportingRepository {
    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    public JdbcReportingRepository(JdbcTemplate jdbc, ObjectMapper objectMapper) {
        this.jdbc = jdbc;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Map<String, Object>> findRows(String reportType, ReportRequest request) {
        int offset = (request.page() - 1) * request.pageSize();
        return jdbc.query("""
                SELECT payload
                FROM reporting_schema.report_projections
                WHERE report_type = ?
                  AND (? IS NULL OR organization_id = ?)
                  AND (? IS NULL OR account_id = ?)
                  AND (? IS NULL OR branch_id = ?)
                  AND (? IS NULL OR channel = ?)
                  AND source_at >= ? AND source_at <= ?
                ORDER BY source_at DESC
                LIMIT ? OFFSET ?
                """,
                (rs, row) -> {
                    try {
                        return objectMapper.readValue(rs.getString("payload"),
                                new TypeReference<Map<String, Object>>() {});
                    } catch (Exception e) {
                        throw new IllegalStateException("Invalid reporting projection payload", e);
                    }
                },
                reportType,
                request.organizationId(), request.organizationId(),
                request.accountId(), request.accountId(),
                request.branchId(), request.branchId(),
                request.channel(), request.channel(),
                Timestamp.from(request.from()), Timestamp.from(request.to()),
                request.pageSize(), offset);
    }

    @Override
    public Optional<Instant> findSourceWatermark(String reportType, ReportRequest request) {
        Timestamp watermark = jdbc.queryForObject("""
                SELECT max(source_at)
                FROM reporting_schema.report_projections
                WHERE report_type = ?
                  AND (? IS NULL OR organization_id = ?)
                  AND (? IS NULL OR account_id = ?)
                  AND (? IS NULL OR branch_id = ?)
                  AND (? IS NULL OR channel = ?)
                  AND source_at >= ? AND source_at <= ?
                """, Timestamp.class,
                reportType,
                request.organizationId(), request.organizationId(),
                request.accountId(), request.accountId(),
                request.branchId(), request.branchId(),
                request.channel(), request.channel(),
                Timestamp.from(request.from()), Timestamp.from(request.to()));
        return Optional.ofNullable(watermark).map(Timestamp::toInstant);
    }

    @Override
    public Optional<ExportJob> findExportByKey(String exportKey) {
        List<ExportJob> jobs = jdbc.query("""
                SELECT * FROM reporting_schema.export_jobs WHERE export_key = ?
                """, (rs, row) -> new ExportJob(
                        rs.getObject("report_job_id", UUID.class),
                        rs.getString("export_key"),
                        rs.getString("report_type"),
                        rs.getString("format"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toInstant()),
                exportKey);
        return jobs.stream().findFirst();
    }

    @Override
    public ExportJob insertExport(String reportType, ExportRequest request) {
        UUID id = UUID.randomUUID();
        jdbc.update("""
                INSERT INTO reporting_schema.export_jobs
                (report_job_id, export_key, report_type, format, organization_id,
                 account_id, branch_id, channel, range_start, range_end, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id, request.exportKey(), reportType, request.format(),
                request.organizationId(), request.accountId(), request.branchId(),
                request.channel(), Timestamp.from(request.from()), Timestamp.from(request.to()),
                "ACCEPTED");
        return new ExportJob(id, request.exportKey(), reportType,
                request.format(), "ACCEPTED", Instant.now());
    }
}
