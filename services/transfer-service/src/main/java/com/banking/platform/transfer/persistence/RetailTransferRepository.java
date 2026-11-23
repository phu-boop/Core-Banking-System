package com.banking.platform.transfer.persistence;

import com.banking.platform.transfer.domain.RetailTransferRecord;
import com.banking.platform.transfer.domain.RetailTransferType;
import com.banking.platform.transfer.domain.TransferStatus;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RetailTransferRepository {
    private final JdbcTemplate jdbc;

    public RetailTransferRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<RetailIdempotencyRecord> findReplay(
            String key, String actor, String businessScope, String operation) {
        return jdbc.query("""
                SELECT transfer_id, payload_hash
                FROM transfer_schema.retail_transfer_idempotency
                WHERE idempotency_key = ? AND actor_subject = ? 
                  AND business_scope = ? AND operation = ?
                """, rs -> rs.next()
                        ? Optional.of(new RetailIdempotencyRecord(
                                UUID.fromString(rs.getString("transfer_id")),
                                rs.getString("payload_hash")))
                        : Optional.empty(),
                key, actor, businessScope, operation);
    }

    public void saveIdempotency(String key, String actor, String businessScope,
                                String operation, String payloadHash, UUID transferId) {
        jdbc.update("""
                INSERT INTO transfer_schema.retail_transfer_idempotency
                    (idempotency_key, actor_subject, business_scope, operation, payload_hash, transfer_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """, key, actor, businessScope, operation, payloadHash, transferId);
    }

    public void insert(RetailTransferRecord transfer) {
        jdbc.update("""
                INSERT INTO transfer_schema.retail_transfer
                    (transfer_id, transaction_id, operation_type, source_account_id,
                     destination_account_id, processing_branch_id, amount, currency,
                     status, ledger_journal_id, correlation_id, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                transfer.transferId(), transfer.transactionId(), transfer.operationType().name(),
                transfer.sourceAccountId(), transfer.destinationAccountId(),
                transfer.processingBranchId(), transfer.amount(), transfer.currency(),
                transfer.status().name(), transfer.ledgerJournalId(), transfer.correlationId(),
                Timestamp.from(transfer.createdAt()), Timestamp.from(transfer.updatedAt()));
    }

    public Optional<RetailTransferRecord> find(UUID transferId) {
        return jdbc.query("""
                SELECT transfer_id, transaction_id, operation_type, source_account_id,
                       destination_account_id, processing_branch_id, amount, currency,
                       status, ledger_journal_id, correlation_id, created_at, updated_at
                FROM transfer_schema.retail_transfer WHERE transfer_id = ?
                """, rs -> rs.next() ? Optional.of(map(rs)) : Optional.empty(), transferId);
    }

    public void updateStatus(UUID transferId, TransferStatus status, UUID ledgerJournalId) {
        jdbc.update("""
                UPDATE transfer_schema.retail_transfer
                SET status = ?, ledger_journal_id = ?, updated_at = ?
                WHERE transfer_id = ?
                """, status.name(), ledgerJournalId, Timestamp.from(Instant.now()), transferId);
    }

    public void audit(UUID transferId, String actor, String action, String result,
                      String correlationId) {
        jdbc.update("""
                INSERT INTO transfer_schema.transfer_audit
                    (audit_id, transfer_id, actor_subject, action, result, correlation_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """, UUID.randomUUID(), transferId, actor, action, result, correlationId);
    }

    private RetailTransferRecord map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new RetailTransferRecord(
                rs.getObject("transfer_id", UUID.class),
                rs.getObject("transaction_id", UUID.class),
                RetailTransferType.valueOf(rs.getString("operation_type")),
                rs.getObject("source_account_id", UUID.class),
                rs.getObject("destination_account_id", UUID.class),
                rs.getObject("processing_branch_id", UUID.class),
                rs.getBigDecimal("amount"),
                rs.getString("currency"),
                TransferStatus.valueOf(rs.getString("status")),
                rs.getObject("ledger_journal_id", UUID.class),
                rs.getString("correlation_id"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant());
    }

    public record RetailIdempotencyRecord(UUID transferId, String payloadHash) {}
}
