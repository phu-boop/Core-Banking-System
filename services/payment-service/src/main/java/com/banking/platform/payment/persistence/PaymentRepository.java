package com.banking.platform.payment.persistence;

import com.banking.platform.payment.domain.PaymentRecord;
import com.banking.platform.payment.domain.PaymentStatus;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {
    private final JdbcTemplate jdbc;

    public PaymentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(PaymentRecord payment) {
        jdbc.update("INSERT INTO payment_schema.payments " +
                "(payment_id,transfer_id,amount,currency,destination_reference,status,provider_reference,correlation_id,created_at,updated_at) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)",
                payment.paymentId(), payment.transferId(), payment.amount(), payment.currency(),
                payment.destinationReference(), payment.status().name(), payment.providerReference(),
                payment.correlationId(), Timestamp.from(payment.createdAt()), Timestamp.from(payment.updatedAt()));
    }

    public Optional<PaymentRecord> find(UUID paymentId) {
        return jdbc.query("SELECT * FROM payment_schema.payments WHERE payment_id=?",
                rs -> rs.next() ? Optional.of(map(rs)) : Optional.empty(), paymentId);
    }

    public Optional<IdempotencyRecord> findIdempotency(String key, String actor,
                                                        String operation, String businessScope) {
        return jdbc.query("SELECT payment_id,payload_hash FROM payment_schema.payment_idempotency " +
                        "WHERE idempotency_key=? AND actor_subject=? AND operation=? AND business_scope=?",
                rs -> rs.next()
                        ? Optional.of(new IdempotencyRecord(
                                UUID.fromString(rs.getString("payment_id")),
                                rs.getString("payload_hash")))
                        : Optional.empty(),
                key, actor, operation, businessScope);
    }

    public void saveIdempotency(String key, String actor, String operation, String businessScope,
                                String payloadHash, UUID paymentId) {
        jdbc.update("INSERT INTO payment_schema.payment_idempotency " +
                        "(idempotency_key,actor_subject,operation,business_scope,payload_hash,payment_id) " +
                        "VALUES (?,?,?,?,?,?)",
                key, actor, operation, businessScope, payloadHash, paymentId);
    }

    public void updateStatus(UUID paymentId, PaymentStatus status, String providerReference) {
        jdbc.update("UPDATE payment_schema.payments SET status=?,provider_reference=COALESCE(?,provider_reference),updated_at=? WHERE payment_id=?",
                status.name(), providerReference, Timestamp.from(Instant.now()), paymentId);
    }

    public void recordOutcome(UUID paymentId, UUID outcomeId, String outcome,
                              String outcomeSource, String providerReference, String correlationId) {
        jdbc.update("INSERT INTO payment_schema.payment_outcomes " +
                        "(outcome_id,payment_id,outcome,outcome_source,provider_reference,correlation_id) " +
                        "VALUES (?,?,?,?,?,?)",
                outcomeId, paymentId, outcome, outcomeSource, providerReference, correlationId);
    }

    public void audit(UUID paymentId, String actor, String action, String result, String outcomeSource,
                      String providerReference, String correlationId) {
        jdbc.update("INSERT INTO payment_schema.payment_audit " +
                        "(audit_id,payment_id,actor_subject,action,result,outcome_source,provider_reference,correlation_id) " +
                        "VALUES (?,?,?,?,?,?,?,?)",
                UUID.randomUUID(), paymentId, actor, action, result, outcomeSource, providerReference, correlationId);
    }

    public boolean outcomeExists(UUID paymentId, String outcome, String outcomeSource,
                                 String providerReference) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM payment_schema.payment_outcomes " +
                "WHERE payment_id=? AND outcome=? AND outcome_source=? AND COALESCE(provider_reference,'')=COALESCE(?, '')",
                Integer.class, paymentId, outcome, outcomeSource, providerReference);
        return count != null && count > 0;
    }

    private PaymentRecord map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new PaymentRecord(
                UUID.fromString(rs.getString("payment_id")),
                UUID.fromString(rs.getString("transfer_id")),
                rs.getBigDecimal("amount"),
                rs.getString("currency"),
                rs.getString("destination_reference"),
                PaymentStatus.valueOf(rs.getString("status")),
                rs.getString("provider_reference"),
                rs.getString("correlation_id"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant());
    }

    public record IdempotencyRecord(UUID paymentId, String payloadHash) {}
}
