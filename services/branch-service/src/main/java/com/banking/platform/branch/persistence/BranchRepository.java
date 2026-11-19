package com.banking.platform.branch.persistence;

import com.banking.platform.branch.domain.BranchPage;
import com.banking.platform.branch.domain.BranchResponse;
import com.banking.platform.branch.domain.BranchStatus;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BranchRepository {
    private static final RowMapper<BranchResponse> BRANCH_MAPPER = (rs, rowNum) -> new BranchResponse(
            rs.getObject("branch_id", UUID.class),
            rs.getString("code"),
            rs.getString("name"),
            BranchStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant());

    private final NamedParameterJdbcTemplate jdbc;

    public BranchRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public BranchResponse create(UUID branchId, String code, String name) {
        Instant now = Instant.now();
        jdbc.update("""
                INSERT INTO branch_schema.branch
                    (branch_id, code, name, status, created_at, updated_at)
                VALUES (:id, :code, :name, 'ACTIVE', :now, :now)
                """, Map.of(
                "id", branchId,
                "code", code,
                "name", name,
                "now", Timestamp.from(now)));
        return findById(branchId).orElseThrow();
    }

    public Optional<BranchResponse> findById(UUID branchId) {
        var rows = jdbc.query("""
                SELECT branch_id, code, name, status, created_at, updated_at
                FROM branch_schema.branch
                WHERE branch_id = :id
                """, Map.of("id", branchId), BRANCH_MAPPER);
        return rows.stream().findFirst();
    }

    public BranchPage findPage(BranchStatus status, int page, int size,
                               boolean allBranches, List<UUID> branchIds) {
        if (!allBranches && branchIds.isEmpty()) {
            return new BranchPage(List.of(), page, size, 0);
        }
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("status", status == null ? "" : status.name())
                .addValue("allBranches", allBranches)
                .addValue("branchIds", branchIds.isEmpty() ? List.of(new UUID(0, 0)) : branchIds)
                .addValue("limit", size)
                .addValue("offset", (long) page * size);
        String scopeClause = "(:allBranches = true OR branch_id IN (:branchIds))";
        String statusClause = "(:status = '' OR status = :status)";
        List<BranchResponse> items = jdbc.query("""
                SELECT branch_id, code, name, status, created_at, updated_at
                FROM branch_schema.branch
                WHERE %s AND %s
                ORDER BY name, branch_id
                LIMIT :limit OFFSET :offset
                """.formatted(statusClause, scopeClause), parameters, BRANCH_MAPPER);
        Long count = jdbc.queryForObject("""
                SELECT count(*)
                FROM branch_schema.branch
                WHERE %s AND %s
                """.formatted(statusClause, scopeClause), parameters, Long.class);
        return new BranchPage(items, page, size, count == null ? 0 : count);
    }

    public void recordCreateAudit(String actor, UUID branchId, String correlationId) {
        jdbc.update("""
                INSERT INTO branch_schema.branch_audit
                    (audit_id, actor, action, resource_type, resource_id, result,
                     occurred_at, correlation_id)
                VALUES (:auditId, :actor, 'BRANCH_CREATE', 'BRANCH', :resourceId,
                        'SUCCESS', CURRENT_TIMESTAMP, :correlationId)
                """, Map.of(
                "auditId", UUID.randomUUID(),
                "actor", actor,
                "resourceId", branchId.toString(),
                "correlationId", correlationId));
    }
}
