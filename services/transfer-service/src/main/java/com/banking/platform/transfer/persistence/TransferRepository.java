package com.banking.platform.transfer.persistence;

import com.banking.platform.transfer.domain.TransferRecord;
import com.banking.platform.transfer.domain.TransferStatus;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransferRepository {
    private final JdbcTemplate jdbc;

    public TransferRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Optional<IdempotencyRecord> findIdempotentTransfer(String key, String actor, UUID organizationId,
                                                              String operation) {
        return jdbc.query(
                "SELECT transfer_id, payload_hash FROM transfer_schema.transfer_idempotency " +
                "WHERE idempotency_key=? AND actor_subject=? AND organization_id=? AND operation=?",
                rs -> rs.next()
                        ? Optional.of(new IdempotencyRecord(
                                UUID.fromString(rs.getString("transfer_id")),
                                rs.getString("payload_hash")))
                        : Optional.empty(),
                key, actor, organizationId, operation);
    }

    public void saveIdempotency(String key, String actor, UUID organizationId, String operation,
                                String payloadHash, UUID transferId) {
        jdbc.update("INSERT INTO transfer_schema.transfer_idempotency " +
                "(idempotency_key,actor_subject,organization_id,operation,payload_hash,transfer_id) VALUES (?,?,?,?,?,?)",
                key, actor, organizationId, operation, payloadHash, transferId);
    }

    public void insert(TransferRecord transfer) {
        jdbc.update("INSERT INTO transfer_schema.transfers " +
                "(transfer_id,organization_id,operation_type,operation_reference,amount,currency," +
                "approval_request_id,approval_policy_version,approval_status,status,maker_subject,correlation_id,created_at,updated_at) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                transfer.transferId(), transfer.organizationId(), transfer.operationType(), transfer.operationReference(),
                transfer.amount(), transfer.currency(), transfer.approvalRequestId(), transfer.approvalPolicyVersion(),
                transfer.approvalStatus(), transfer.status().name(), transfer.makerSubject(), transfer.correlationId(),
                Timestamp.from(transfer.createdAt()), Timestamp.from(transfer.updatedAt()));
    }

    public Optional<TransferRecord> find(UUID transferId) {
        return jdbc.query("SELECT * FROM transfer_schema.transfers WHERE transfer_id=?",
                rs -> rs.next() ? Optional.of(map(rs)) : Optional.empty(), transferId);
    }

    public void updateApproval(UUID transferId, UUID approvalRequestId, Integer policyVersion,
                               String approvalStatus, TransferStatus status) {
        jdbc.update("UPDATE transfer_schema.transfers SET approval_request_id=?, approval_policy_version=?, " +
                "approval_status=?, status=?, updated_at=? WHERE transfer_id=?",
                approvalRequestId, policyVersion, approvalStatus, status.name(), Timestamp.from(Instant.now()), transferId);
    }

    public void updatePayment(UUID transferId, UUID paymentRequestId, String paymentStatus,
                              TransferStatus status) {
        jdbc.update("UPDATE transfer_schema.transfers SET payment_request_id=?, payment_status=?, status=?, updated_at=? WHERE transfer_id=?",
                paymentRequestId, paymentStatus, status.name(), Timestamp.from(Instant.now()), transferId);
    }

    public void audit(UUID transferId, String actor, String action, UUID approvalRequestId,
                      Integer policyVersion, String result, String correlationId) {
        jdbc.update("INSERT INTO transfer_schema.transfer_audit " +
                "(audit_id,transfer_id,actor_subject,action,approval_request_id,approval_policy_version,result,correlation_id) " +
                "VALUES (?,?,?,?,?,?,?,?)",
                UUID.randomUUID(), transferId, actor, action, approvalRequestId, policyVersion, result, correlationId);
    }

    private TransferRecord map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new TransferRecord(
                UUID.fromString(rs.getString("transfer_id")),
                UUID.fromString(rs.getString("organization_id")),
                rs.getString("operation_type"),
                rs.getString("operation_reference"),
                rs.getBigDecimal("amount"),
                rs.getString("currency"),
                rs.getObject("approval_request_id", UUID.class),
                (Integer) rs.getObject("approval_policy_version"),
                rs.getString("approval_status"),
                TransferStatus.valueOf(rs.getString("status")),
                rs.getString("maker_subject"),
                rs.getString("correlation_id"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant(),
                rs.getObject("payment_request_id", UUID.class),
                rs.getString("payment_status"));
    }

    public record IdempotencyRecord(UUID transferId, String payloadHash) {}
}
