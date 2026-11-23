package com.banking.platform.account.persistence;

import com.banking.platform.account.domain.AccountResponse;
import com.banking.platform.account.domain.AccountStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAccountRepository implements AccountRepository {
    private static final RowMapper<AccountResponse> ROW_MAPPER =
            JdbcAccountRepository::mapAccount;

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AccountResponse create(UUID accountId, String accountNumber, UUID partyId) {
        jdbcTemplate.update("""
                INSERT INTO account_schema.account
                    (account_id, account_number, party_id, account_type, status, currency)
                VALUES (?, ?, ?, 'PAYMENT', 'ACTIVE', 'VND')
                """, accountId, accountNumber, partyId);
        return findById(accountId).orElseThrow();
    }

    @Override
    public Optional<AccountResponse> findById(UUID accountId) {
        return jdbcTemplate.query("""
                SELECT account_id, account_number, party_id, account_type, status, currency,
                       created_at, updated_at
                FROM account_schema.account
                WHERE account_id = ?
                """, ROW_MAPPER, accountId).stream().findFirst();
    }

    @Override
    public boolean transition(UUID accountId, AccountStatus fromStatus, AccountStatus toStatus,
                              String reason, String actor, String correlationId) {
        int updated = jdbcTemplate.update("""
                UPDATE account_schema.account
                SET status = ?, updated_at = CURRENT_TIMESTAMP
                WHERE account_id = ? AND status = ?
                """, toStatus.name(), accountId, fromStatus.name());
        if (updated == 1) {
            jdbcTemplate.update("""
                    INSERT INTO account_schema.account_status_history
                        (history_id, account_id, from_status, to_status, reason, actor, correlation_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """, UUID.randomUUID(), accountId, fromStatus.name(), toStatus.name(),
                    reason, actor, correlationId);
        }
        return updated == 1;
    }

    private static AccountResponse mapAccount(ResultSet rs, int rowNum) throws SQLException {
        return new AccountResponse(
                rs.getObject("account_id", UUID.class),
                rs.getString("account_number"),
                rs.getObject("party_id", UUID.class),
                rs.getString("account_type"),
                AccountStatus.valueOf(rs.getString("status")),
                rs.getString("currency"),
                rs.getObject("created_at", java.time.OffsetDateTime.class),
                rs.getObject("updated_at", java.time.OffsetDateTime.class));
    }
}
