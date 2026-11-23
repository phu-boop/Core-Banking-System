package com.banking.platform.audit.persistence;

import com.banking.platform.audit.domain.AuditRecord;
import com.banking.platform.audit.domain.AuditRecordRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAuditRepository implements AuditRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<AuditRecord> mapper = (rs, rowNum) -> new AuditRecord(
            rs.getObject("audit_record_id", UUID.class),
            rs.getString("event_id"),
            rs.getString("actor_id"),
            rs.getString("channel"),
            rs.getString("action"),
            rs.getString("resource_type"),
            rs.getString("resource_id"),
            rs.getString("organization_id"),
            rs.getString("branch_id"),
            rs.getString("result"),
            rs.getString("correlation_id"),
            rs.getTimestamp("occurred_at").toInstant(),
            rs.getTimestamp("recorded_at").toInstant());

    public JdbcAuditRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<AuditRecord> findByEventId(String eventId) {
        List<AuditRecord> rows = jdbc.query(
                "SELECT * FROM audit_schema.audit_records WHERE event_id = ?",
                mapper, eventId);
        return rows.stream().findFirst();
    }

    @Override
    public AuditRecord insert(AuditRecordRequest request) {
        UUID id = UUID.randomUUID();
        jdbc.update("""
                INSERT INTO audit_schema.audit_records
                (audit_record_id,event_id,actor_id,channel,action,resource_type,resource_id,
                 organization_id,branch_id,result,correlation_id,occurred_at)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
                """,
                id, request.eventId(), request.actorId(), request.channel(), request.action(),
                request.resourceType(), request.resourceId(), request.organizationId(),
                request.branchId(), request.result(), request.correlationId(),
                Timestamp.from(request.occurredAt()));
        return findByEventId(request.eventId()).orElseThrow();
    }

    @Override
    public List<AuditRecord> search(String actorId, String resourceType, String resourceId,
                                    String organizationId, String branchId, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM audit_schema.audit_records WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (actorId != null) {
            sql.append(" AND actor_id = ?");
            args.add(actorId);
        }
        if (resourceType != null) {
            sql.append(" AND resource_type = ?");
            args.add(resourceType);
        }
        if (resourceId != null) {
            sql.append(" AND resource_id = ?");
            args.add(resourceId);
        }
        if (organizationId != null) {
            sql.append(" AND organization_id = ?");
            args.add(organizationId);
        }
        if (branchId != null) {
            sql.append(" AND branch_id = ?");
            args.add(branchId);
        }
        sql.append(" ORDER BY occurred_at DESC LIMIT ?");
        args.add(Math.max(1, Math.min(limit, 200)));
        return jdbc.query(sql.toString(), mapper, args.toArray());
    }
}
