package com.banking.platform.ledger.persistence;

import com.banking.platform.ledger.domain.BalanceResponse;
import com.banking.platform.ledger.domain.EntrySide;
import com.banking.platform.ledger.domain.JournalEntryRequest;
import com.banking.platform.ledger.domain.JournalEntryResponse;
import com.banking.platform.ledger.domain.JournalResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLedgerRepository implements LedgerRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcLedgerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<JournalResponse> findByOperationKey(String operationKey) {
        return jdbcTemplate.query("""
                SELECT journal_id FROM ledger_schema.journal
                WHERE operation_key = ?
                """, (rs, rowNum) -> rs.getObject("journal_id", UUID.class), operationKey)
                .stream().findFirst().map(this::findById).flatMap(value -> value);
    }

    @Override
    public JournalResponse post(UUID journalId, UUID transactionId, String operationKey,
                                List<JournalEntryRequest> entries, String currency,
                                String actor, String correlationId) {
        jdbcTemplate.update("""
                INSERT INTO ledger_schema.journal
                    (journal_id, transaction_id, operation_key, status)
                VALUES (?, ?, ?, 'POSTED')
                """, journalId, transactionId, operationKey);
        insertEntries(journalId, entries, currency);
        audit(actor, "POST_JOURNAL", journalId, "SUCCESS", correlationId);
        return findById(journalId).orElseThrow();
    }

    @Override
    public Optional<JournalResponse> findById(UUID journalId) {
        Optional<JournalResponse> journal = jdbcTemplate.query("""
                SELECT journal_id, transaction_id, status, posted_at
                FROM ledger_schema.journal WHERE journal_id = ?
                """, this::mapJournal, journalId).stream().findFirst();
        return journal.map(value -> new JournalResponse(
                value.journalId(), value.transactionId(), value.status(), value.postedAt(),
                findEntries(value.journalId())));
    }

    @Override
    public Optional<JournalResponse> findReversal(UUID originalJournalId) {
        return jdbcTemplate.query("""
                SELECT journal_id FROM ledger_schema.journal
                WHERE reversal_of_journal_id = ?
                """, (rs, rowNum) -> rs.getObject("journal_id", UUID.class), originalJournalId)
                .stream().findFirst().flatMap(this::findById);
    }

    @Override
    public JournalResponse reverse(UUID reversalJournalId, UUID transactionId, String operationKey,
                                   UUID originalJournalId, List<JournalEntryRequest> entries,
                                   String reasonCode, String actor, String correlationId) {
        jdbcTemplate.update("""
                INSERT INTO ledger_schema.journal
                    (journal_id, transaction_id, operation_key, status, reversal_of_journal_id)
                VALUES (?, ?, ?, 'POSTED', ?)
                """, reversalJournalId, transactionId, operationKey, originalJournalId);
        insertEntries(reversalJournalId, entries, "VND");
        audit(actor, "REVERSE_JOURNAL:" + reasonCode, reversalJournalId, "SUCCESS", correlationId);
        return findById(reversalJournalId).orElseThrow();
    }

    @Override
    public BalanceResponse getBalance(UUID accountId) {
        BigDecimalBalance balance = jdbcTemplate.queryForObject("""
                SELECT COALESCE(SUM(CASE WHEN side = 'DEBIT' THEN amount ELSE -amount END), 0)
                FROM ledger_schema.journal_entry
                WHERE account_id = ?
                """, (rs, rowNum) -> new BigDecimalBalance(rs.getBigDecimal(1)), accountId);
        return new BalanceResponse(accountId, "VND", balance.value(), OffsetDateTime.now());
    }

    private void insertEntries(UUID journalId, List<JournalEntryRequest> entries, String currency) {
        for (JournalEntryRequest entry : entries) {
            jdbcTemplate.update("""
                    INSERT INTO ledger_schema.journal_entry
                        (entry_id, journal_id, account_id, side, amount, currency)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """, UUID.randomUUID(), journalId, entry.accountId(),
                    entry.side().name(), entry.amount(), currency);
        }
    }

    private List<JournalEntryResponse> findEntries(UUID journalId) {
        return jdbcTemplate.query("""
                SELECT entry_id, account_id, side, amount, currency
                FROM ledger_schema.journal_entry WHERE journal_id = ?
                """, (rs, rowNum) -> new JournalEntryResponse(
                        rs.getObject("entry_id", UUID.class),
                        rs.getObject("account_id", UUID.class),
                        EntrySide.valueOf(rs.getString("side")),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency")), journalId);
    }

    private JournalResponse mapJournal(ResultSet rs, int rowNum) throws SQLException {
        return new JournalResponse(
                rs.getObject("journal_id", UUID.class),
                rs.getObject("transaction_id", UUID.class),
                rs.getString("status"),
                rs.getObject("posted_at", OffsetDateTime.class),
                List.of());
    }

    private void audit(String actor, String action, UUID resourceId,
                       String result, String correlationId) {
        jdbcTemplate.update("""
                INSERT INTO ledger_schema.ledger_audit
                    (audit_id, actor, action, resource_id, result, correlation_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """, UUID.randomUUID(), actor, action, resourceId.toString(), result, correlationId);
    }

    private record BigDecimalBalance(java.math.BigDecimal value) {}
}
