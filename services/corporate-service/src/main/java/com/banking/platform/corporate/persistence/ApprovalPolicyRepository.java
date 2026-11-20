package com.banking.platform.corporate.persistence;

import com.banking.platform.corporate.application.OrganizationService.OrganizationNotFoundException;
import com.banking.platform.corporate.application.ApprovalPolicyService.ApprovalPolicyConflictException;
import com.banking.platform.corporate.application.ApprovalPolicyService.ApprovalPolicyNotFoundException;
import com.banking.platform.corporate.domain.ApprovalPolicyLevel;
import com.banking.platform.corporate.domain.ApprovalPolicyResponse;
import com.banking.platform.corporate.domain.ApprovalPolicyStatus;
import com.banking.platform.corporate.web.ApprovalPolicyLevelRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ApprovalPolicyRepository {
    private final JdbcTemplate jdbcTemplate;

    public ApprovalPolicyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public ApprovalPolicyResponse create(UUID policyId, UUID organizationId, String operationType,
                                         List<ApprovalPolicyLevelRequest> levels,
                                         String actor, String correlationId) {
        lockOrganization(organizationId, operationType);
        Integer currentVersion = jdbcTemplate.queryForObject("""
                SELECT COALESCE(MAX(policy_version), 0)
                FROM corporate_schema.approval_policy
                WHERE organization_id = ? AND operation_type = ?
                """, Integer.class, organizationId, operationType);
        int version = currentVersion + 1;
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.approval_policy
                    (policy_id, organization_id, operation_type, policy_version, status, created_by)
                VALUES (?, ?, ?, ?, 'DRAFT', ?)
                """, policyId, organizationId, operationType, version, actor);
        for (int index = 0; index < levels.size(); index++) {
            ApprovalPolicyLevelRequest level = levels.get(index);
            jdbcTemplate.update("""
                    INSERT INTO corporate_schema.approval_policy_level
                        (policy_level_id, policy_id, step_number, role_code, mandate_scope)
                    VALUES (?, ?, ?, ?, ?)
                    """, UUID.randomUUID(), policyId, index + 1, level.roleCode(), level.mandateScope());
        }
        writeAudit(actor, "APPROVAL_POLICY_CREATE", policyId, correlationId);
        return find(organizationId, policyId).orElseThrow();
    }

    public List<ApprovalPolicyResponse> list(UUID organizationId, String operationType) {
        requireOrganization(organizationId);
        String sql = """
                SELECT p.policy_id, p.organization_id, p.operation_type, p.policy_version,
                       p.status, p.created_at, p.activated_at,
                       l.step_number, l.role_code, l.mandate_scope
                FROM corporate_schema.approval_policy p
                LEFT JOIN corporate_schema.approval_policy_level l ON l.policy_id = p.policy_id
                WHERE p.organization_id = ?
                """ + (operationType == null ? "" : " AND p.operation_type = ?")
                + " ORDER BY p.created_at DESC, p.policy_version DESC, l.step_number";
        Map<UUID, PolicyBuilder> policies = new LinkedHashMap<>();
        RowCallbackHandler rowHandler = resultSet -> addPolicyRow(policies, resultSet);
        if (operationType == null) {
            jdbcTemplate.query(sql, rowHandler, organizationId);
        } else {
            jdbcTemplate.query(sql, rowHandler, organizationId, operationType);
        }
        return policies.values().stream().map(PolicyBuilder::build).toList();
    }

    @Transactional
    public ApprovalPolicyResponse activate(UUID organizationId, UUID policyId,
                                           String actor, String correlationId) {
        requireOrganization(organizationId);
        String operationType = jdbcTemplate.query("""
                SELECT operation_type
                FROM corporate_schema.approval_policy
                WHERE policy_id = ? AND organization_id = ?
                """, resultSet -> resultSet.next() ? resultSet.getString("operation_type") : null,
                policyId, organizationId);
        if (operationType == null) {
            throw new ApprovalPolicyNotFoundException(policyId);
        }
        lockOrganization(organizationId, operationType);
        String status = jdbcTemplate.query("""
                SELECT status
                FROM corporate_schema.approval_policy
                WHERE policy_id = ? AND organization_id = ?
                FOR UPDATE
                """, resultSet -> resultSet.next() ? resultSet.getString("status") : null,
                policyId, organizationId);
        if (status == null) {
            throw new ApprovalPolicyNotFoundException(policyId);
        }
        if (ApprovalPolicyStatus.ACTIVE.name().equals(status)) {
            return find(organizationId, policyId).orElseThrow();
        }
        if (!ApprovalPolicyStatus.DRAFT.name().equals(status)
                && !ApprovalPolicyStatus.INACTIVE.name().equals(status)) {
            throw new ApprovalPolicyConflictException("This approval policy cannot be activated");
        }
        jdbcTemplate.update("""
                UPDATE corporate_schema.approval_policy
                SET status = 'INACTIVE'
                WHERE organization_id = ? AND operation_type = ? AND status = 'ACTIVE'
                """, organizationId, operationType);
        jdbcTemplate.update("""
                UPDATE corporate_schema.approval_policy
                SET status = 'ACTIVE', activated_at = CURRENT_TIMESTAMP
                WHERE policy_id = ? AND organization_id = ?
                """, policyId, organizationId);
        writeAudit(actor, "APPROVAL_POLICY_ACTIVATE", policyId, correlationId);
        return find(organizationId, policyId).orElseThrow();
    }

    private Optional<ApprovalPolicyResponse> find(UUID organizationId, UUID policyId) {
        return list(organizationId, null).stream()
                .filter(policy -> policy.policyId().equals(policyId))
                .findFirst();
    }

    private void lockOrganization(UUID organizationId, String operationType) {
        String lockKey = organizationId + ":" + operationType;
        jdbcTemplate.query(
                "SELECT pg_advisory_xact_lock(hashtextextended(?, 0))",
                (ResultSetExtractor<Void>) resultSet -> {
                    resultSet.next();
                    return null;
                },
                lockKey);
        requireOrganization(organizationId);
    }

    private void requireOrganization(UUID organizationId) {
        Boolean exists = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM corporate_schema.organization
                    WHERE organization_id = ?
                )
                """, Boolean.class, organizationId);
        if (!Boolean.TRUE.equals(exists)) {
            throw new OrganizationNotFoundException(organizationId);
        }
    }

    private void writeAudit(String actor, String action, UUID policyId, String correlationId) {
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.organization_audit
                    (audit_id, actor, action, resource_type, resource_id, result,
                     occurred_at, correlation_id)
                VALUES (?, ?, ?, 'APPROVAL_POLICY', ?, 'SUCCESS', CURRENT_TIMESTAMP, ?)
                """, UUID.randomUUID(), actor, action, policyId.toString(), correlationId);
    }

    private void addPolicyRow(Map<UUID, PolicyBuilder> policies, java.sql.ResultSet resultSet)
            throws java.sql.SQLException {
        UUID policyId = resultSet.getObject("policy_id", UUID.class);
        PolicyBuilder policy = policies.get(policyId);
        if (policy == null) {
            policy = new PolicyBuilder(
                    policyId,
                    resultSet.getObject("organization_id", UUID.class),
                    resultSet.getString("operation_type"),
                    resultSet.getInt("policy_version"),
                    ApprovalPolicyStatus.valueOf(resultSet.getString("status")),
                    resultSet.getTimestamp("created_at").toInstant(),
                    timestampToInstant(resultSet.getTimestamp("activated_at")));
            policies.put(policyId, policy);
        }
        int stepNumber = resultSet.getInt("step_number");
        if (!resultSet.wasNull()) {
            policy.levels.add(new ApprovalPolicyLevel(
                    stepNumber,
                    resultSet.getString("role_code"),
                    resultSet.getString("mandate_scope")));
        }
    }

    private static Instant timestampToInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    private static final class PolicyBuilder {
        private final UUID policyId;
        private final UUID organizationId;
        private final String operationType;
        private final int version;
        private final ApprovalPolicyStatus status;
        private final Instant createdAt;
        private final Instant activatedAt;
        private final List<ApprovalPolicyLevel> levels = new ArrayList<>();

        private PolicyBuilder(UUID policyId, UUID organizationId, String operationType,
                              int version, ApprovalPolicyStatus status,
                              Instant createdAt, Instant activatedAt) {
            this.policyId = policyId;
            this.organizationId = organizationId;
            this.operationType = operationType;
            this.version = version;
            this.status = status;
            this.createdAt = createdAt;
            this.activatedAt = activatedAt;
        }

        private ApprovalPolicyResponse build() {
            return new ApprovalPolicyResponse(policyId, organizationId, operationType, version,
                    status, List.copyOf(levels), createdAt, activatedAt);
        }
    }
}
