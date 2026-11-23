package com.banking.platform.corporate.persistence;

import com.banking.platform.corporate.application.ApproverEligibilityClient;
import com.banking.platform.corporate.application.ApprovalRequestConflictException;
import com.banking.platform.corporate.application.ApprovalRequestNotFoundException;
import com.banking.platform.corporate.application.ApprovalRequestValidationException;
import com.banking.platform.corporate.application.IneligibleApproverException;
import com.banking.platform.corporate.application.MakerCheckerException;
import com.banking.platform.corporate.domain.ApprovalDecision;
import com.banking.platform.corporate.domain.ApprovalDecisionResponse;
import com.banking.platform.corporate.domain.ApprovalHistoryResponse;
import com.banking.platform.corporate.domain.ApprovalRequestResponse;
import com.banking.platform.corporate.domain.ApprovalRequestStatus;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ApprovalRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ApproverEligibilityClient eligibilityClient;

    public ApprovalRepository(JdbcTemplate jdbcTemplate,
                              ApproverEligibilityClient eligibilityClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.eligibilityClient = eligibilityClient;
    }

    @Transactional
    public ApprovalRequestResponse create(UUID organizationId, String operationType,
                                          String operationReference, String makerSubject,
                                          String correlationId) {
        if (operationReference == null || operationReference.trim().isEmpty()) {
            throw new ApprovalRequestValidationException("operationReference is required");
        }
        if (operationReference.length() > 256) {
            throw new ApprovalRequestValidationException("operationReference must not exceed 256 characters");
        }
        if (operationType == null || operationType.trim().isEmpty()) {
            throw new ApprovalRequestValidationException("operationType is required");
        }

        String normalizedOperationType = operationType.trim();
        List<PolicyRow> policies = jdbcTemplate.query("""
                SELECT policy_id, policy_version
                FROM corporate_schema.approval_policy
                WHERE organization_id = ? AND operation_type = ? AND status = 'ACTIVE'
                """, (resultSet, rowNum) -> new PolicyRow(
                        resultSet.getObject("policy_id", UUID.class),
                        resultSet.getInt("policy_version")),
                organizationId, normalizedOperationType);
        if (policies.isEmpty()) {
            throw new ApprovalRequestConflictException("No active approval policy exists for this operation type");
        }
        PolicyRow policy = policies.getFirst();

        UUID approvalId = UUID.randomUUID();
        Instant now = Instant.now();
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.approval_request
                    (approval_request_id, organization_id, policy_id, operation_type,
                     operation_reference, maker_subject, status, submitted_at)
                VALUES (?, ?, ?, ?, ?, ?, 'PENDING', ?)
                """, approvalId, organizationId, policy.policyId(), normalizedOperationType,
                operationReference.trim(), makerSubject, Timestamp.from(now));

        List<PolicyLevelRow> levels = jdbcTemplate.query("""
                SELECT step_number, role_code, mandate_scope
                FROM corporate_schema.approval_policy_level
                WHERE policy_id = ?
                ORDER BY step_number
                """, (resultSet, rowNum) -> new PolicyLevelRow(
                        resultSet.getInt("step_number"),
                        resultSet.getString("role_code"),
                        resultSet.getString("mandate_scope")),
                policy.policyId());
        if (levels.isEmpty()) {
            throw new ApprovalRequestConflictException("The active approval policy has no levels");
        }
        for (PolicyLevelRow level : levels) {
            jdbcTemplate.update("""
                    INSERT INTO corporate_schema.approval_step
                        (approval_step_id, approval_request_id, step_number,
                         role_code, mandate_scope, status)
                    VALUES (?, ?, ?, ?, ?, 'PENDING')
                    """, UUID.randomUUID(), approvalId, level.stepNumber(),
                    level.roleCode(), level.mandateScope());
        }

        jdbcTemplate.update("""
                INSERT INTO corporate_schema.organization_audit
                    (audit_id, actor, action, resource_type, resource_id, result,
                     occurred_at, correlation_id)
                VALUES (?, ?, 'APPROVAL_REQUEST_CREATE', 'APPROVAL_REQUEST', ?,
                        'SUCCESS', CURRENT_TIMESTAMP, ?)
                """, UUID.randomUUID(), makerSubject, approvalId.toString(), correlationId);

        return new ApprovalRequestResponse(
                approvalId, organizationId, normalizedOperationType,
                operationReference.trim(), policy.policyVersion(),
                ApprovalRequestStatus.PENDING, levels.getFirst().stepNumber(),
                now, null);
    }

    public ApprovalRequestResponse get(UUID organizationId, UUID approvalId) {
        List<RequestRow> requests = jdbcTemplate.query("""
                SELECT r.approval_request_id, r.organization_id, r.operation_type,
                       r.operation_reference, p.policy_version, r.status,
                       r.submitted_at, r.completed_at,
                       (SELECT MIN(step_number) FROM corporate_schema.approval_step s
                        WHERE s.approval_request_id = r.approval_request_id
                          AND s.status = 'PENDING') AS current_step
                FROM corporate_schema.approval_request r
                JOIN corporate_schema.approval_policy p ON p.policy_id = r.policy_id
                WHERE r.organization_id = ? AND r.approval_request_id = ?
                """, (resultSet, rowNum) -> new RequestRow(
                        resultSet.getObject("approval_request_id", UUID.class),
                        resultSet.getObject("organization_id", UUID.class),
                        resultSet.getString("operation_type"),
                        resultSet.getString("operation_reference"),
                        resultSet.getInt("policy_version"),
                        ApprovalRequestStatus.valueOf(resultSet.getString("status")),
                        nullableTimestamp(resultSet.getTimestamp("submitted_at")),
                        nullableTimestamp(resultSet.getTimestamp("completed_at")),
                        nullableInt(resultSet, "current_step")),
                organizationId, approvalId);
        if (requests.isEmpty()) {
            throw new ApprovalRequestNotFoundException(approvalId);
        }
        RequestRow request = requests.getFirst();
        return new ApprovalRequestResponse(
                request.approvalId(), request.organizationId(), request.operationType(),
                request.operationReference(), request.policyVersion(), request.status(),
                request.currentStep(), request.submittedAt(), request.completedAt());
    }

    @Transactional
    public ApprovalDecisionResponse decide(UUID organizationId, UUID approvalId,
                                           String actorSubject, ApprovalDecision decision,
                                           String reason, String idempotencyKey,
                                           String correlationId) {
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            throw new ApprovalRequestValidationException("Idempotency-Key is required");
        }
        String normalizedKey = idempotencyKey.trim();
        if (normalizedKey.length() > 128) {
            throw new ApprovalRequestValidationException("Idempotency-Key must not exceed 128 characters");
        }
        String payloadHash = payloadHash(approvalId, actorSubject, decision, reason);

        List<RequestLockRow> requests = jdbcTemplate.query("""
                SELECT r.approval_request_id, r.status, r.maker_subject
                FROM corporate_schema.approval_request r
                WHERE r.organization_id = ? AND r.approval_request_id = ?
                FOR UPDATE
                """, (resultSet, rowNum) -> new RequestLockRow(
                        resultSet.getObject("approval_request_id", UUID.class),
                        ApprovalRequestStatus.valueOf(resultSet.getString("status")),
                        resultSet.getString("maker_subject")),
                organizationId, approvalId);
        if (requests.isEmpty()) {
            throw new ApprovalRequestNotFoundException(approvalId);
        }
        RequestLockRow request = requests.getFirst();
        IdempotencyRow replay = findReplay(
                organizationId, approvalId, actorSubject, normalizedKey, payloadHash);
        if (replay != null) {
            ApprovalRequestStatus status = ApprovalRequestStatus.valueOf(replay.resultStatus);
            return new ApprovalDecisionResponse(
                    approvalId, status, replay.resultCurrentStep, replay.resultHistoryId);
        }
        if (request.status() == ApprovalRequestStatus.REJECTED
                || request.status() == ApprovalRequestStatus.APPROVED) {
            throw new ApprovalRequestConflictException("Approval request is already terminal");
        }
        if (request.makerSubject().equals(actorSubject)) {
            throw new MakerCheckerException("The maker cannot approve or reject their own request");
        }

        List<StepRow> steps = jdbcTemplate.query("""
                SELECT approval_step_id, step_number, role_code, mandate_scope, status
                FROM corporate_schema.approval_step
                WHERE approval_request_id = ? AND status = 'PENDING'
                ORDER BY step_number
                FOR UPDATE
                """, (resultSet, rowNum) -> new StepRow(
                        resultSet.getObject("approval_step_id", UUID.class),
                        resultSet.getInt("step_number"),
                        resultSet.getString("role_code"),
                        resultSet.getString("mandate_scope"),
                        resultSet.getString("status")),
                approvalId);
        if (steps.isEmpty()) {
            throw new ApprovalRequestConflictException("No pending approval step is available");
        }
        StepRow step = steps.getFirst();
        if (!eligibilityClient.isEligible(
                organizationId, actorSubject, step.roleCode(), step.mandateScope())) {
            throw new IneligibleApproverException(
                    "Actor is not eligible for the current approval step");
        }

        UUID historyId = UUID.randomUUID();
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.approval_history
                    (history_id, approval_request_id, policy_id, actor, step_number,
                     decision, reason, occurred_at, correlation_id)
                SELECT ?, approval_request_id, policy_id, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?
                FROM corporate_schema.approval_request
                WHERE approval_request_id = ?
                """, historyId, actorSubject, step.stepNumber(),
                decision.name(), reason, correlationId, approvalId);

        ApprovalRequestStatus finalStatus;
        Integer nextStep = null;
        if (decision == ApprovalDecision.REJECT) {
            jdbcTemplate.update("""
                    UPDATE corporate_schema.approval_step
                    SET status = 'REJECTED', decided_by = ?, decided_at = CURRENT_TIMESTAMP
                    WHERE approval_step_id = ?
                    """, actorSubject, step.approvalStepId());
            finalStatus = ApprovalRequestStatus.REJECTED;
            jdbcTemplate.update("""
                    UPDATE corporate_schema.approval_request
                    SET status = 'REJECTED', completed_at = CURRENT_TIMESTAMP
                    WHERE approval_request_id = ?
                    """, approvalId);
        } else {
            jdbcTemplate.update("""
                    UPDATE corporate_schema.approval_step
                    SET status = 'APPROVED', decided_by = ?, decided_at = CURRENT_TIMESTAMP
                    WHERE approval_step_id = ?
                    """, actorSubject, step.approvalStepId());
            Integer remaining = jdbcTemplate.queryForObject("""
                    SELECT count(*)
                    FROM corporate_schema.approval_step
                    WHERE approval_request_id = ? AND status = 'PENDING'
                    """, Integer.class, approvalId);
            if (remaining == 0) {
                finalStatus = ApprovalRequestStatus.APPROVED;
                jdbcTemplate.update("""
                        UPDATE corporate_schema.approval_request
                        SET status = 'APPROVED', completed_at = CURRENT_TIMESTAMP
                        WHERE approval_request_id = ?
                        """, approvalId);
            } else {
                finalStatus = ApprovalRequestStatus.PENDING;
                nextStep = jdbcTemplate.queryForObject("""
                        SELECT MIN(step_number)
                        FROM corporate_schema.approval_step
                        WHERE approval_request_id = ? AND status = 'PENDING'
                        """, Integer.class, approvalId);
            }
        }

        try {
            jdbcTemplate.update("""
                    INSERT INTO corporate_schema.approval_decision_idempotency
                        (idempotency_id, organization_id, approval_request_id,
                         actor_subject, idempotency_key, payload_hash, result_status,
                         result_current_step, result_history_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    UUID.randomUUID(), organizationId, approvalId, actorSubject,
                    normalizedKey, payloadHash, finalStatus.name(),
                    nextStep, historyId);
        } catch (DuplicateKeyException exception) {
            throw new ApprovalRequestConflictException(
                    "Idempotency-Key was reused with a conflicting payload");
        }

        jdbcTemplate.update("""
                INSERT INTO corporate_schema.organization_audit
                    (audit_id, actor, action, resource_type, resource_id, result,
                     occurred_at, correlation_id)
                VALUES (?, ?, 'APPROVAL_REQUEST_DECIDE', 'APPROVAL_REQUEST', ?,
                        ?, CURRENT_TIMESTAMP, ?)
                """, UUID.randomUUID(), actorSubject, approvalId.toString(),
                finalStatus.name(), correlationId);
        return new ApprovalDecisionResponse(
                approvalId, finalStatus, nextStep, historyId);
    }

    public List<ApprovalHistoryResponse> history(UUID organizationId, UUID approvalId) {
        return jdbcTemplate.query("""
                SELECT h.history_id, h.step_number, h.actor, h.decision, h.reason,
                       h.occurred_at, h.correlation_id, p.policy_version
                FROM corporate_schema.approval_history h
                JOIN corporate_schema.approval_request r
                  ON r.approval_request_id = h.approval_request_id
                JOIN corporate_schema.approval_policy p ON p.policy_id = h.policy_id
                WHERE r.organization_id = ? AND h.approval_request_id = ?
                ORDER BY h.occurred_at, h.step_number
                """, (resultSet, rowNum) -> new ApprovalHistoryResponse(
                        resultSet.getObject("history_id", UUID.class),
                        resultSet.getInt("step_number"),
                        resultSet.getString("actor"),
                        ApprovalDecision.valueOf(resultSet.getString("decision")),
                        resultSet.getString("reason"),
                        resultSet.getInt("policy_version"),
                        resultSet.getTimestamp("occurred_at").toInstant(),
                        resultSet.getString("correlation_id")),
                organizationId, approvalId);
    }

    private IdempotencyRow findReplay(UUID organizationId, UUID approvalId,
                                      String actorSubject, String key, String payloadHash) {
        try {
            return jdbcTemplate.query("""
                    SELECT payload_hash, result_status, result_current_step,
                           result_history_id
                    FROM corporate_schema.approval_decision_idempotency
                    WHERE organization_id = ? AND approval_request_id = ?
                      AND actor_subject = ? AND idempotency_key = ?
                      AND expires_at > CURRENT_TIMESTAMP
                    """, resultSet -> {
                        if (!resultSet.next()) {
                            return null;
                        }
                        String storedHash = resultSet.getString("payload_hash");
                        if (!payloadHash.equals(storedHash)) {
                            throw new ApprovalRequestConflictException(
                                    "Idempotency-Key was reused with a different payload");
                        }
                        return new IdempotencyRow(
                                storedHash,
                                resultSet.getString("result_status"),
                                nullableInt(resultSet, "result_current_step"),
                                resultSet.getObject("result_history_id", UUID.class));
                    }, organizationId, approvalId, actorSubject, key);
        } catch (DuplicateKeyException exception) {
            throw new ApprovalRequestConflictException(
                    "A conflicting decision command was already recorded");
        }
    }

    private static String payloadHash(UUID approvalId, String actorSubject,
                                      ApprovalDecision decision, String reason) {
        String payload = approvalId + "\n" + actorSubject + "\n" + decision
                + "\n" + (reason == null ? "" : reason);
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private static Instant nullableTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    private static Integer nullableInt(java.sql.ResultSet resultSet, String column) {
        try {
            int value = resultSet.getInt(column);
            return resultSet.wasNull() ? null : value;
        } catch (java.sql.SQLException exception) {
            throw new IllegalStateException("Could not read nullable integer", exception);
        }
    }

    private record PolicyRow(UUID policyId, int policyVersion) {}
    private record PolicyLevelRow(int stepNumber, String roleCode, String mandateScope) {}
    private record RequestRow(UUID approvalId, UUID organizationId, String operationType,
                              String operationReference, int policyVersion,
                              ApprovalRequestStatus status, Instant submittedAt,
                              Instant completedAt, Integer currentStep) {}
    private record RequestLockRow(UUID approvalId, ApprovalRequestStatus status,
                                  String makerSubject) {}
    private record StepRow(UUID approvalStepId, int stepNumber, String roleCode,
                           String mandateScope, String status) {}
    private record IdempotencyRow(String payloadHash, String resultStatus,
                                  Integer resultCurrentStep, UUID resultHistoryId) {}
}
