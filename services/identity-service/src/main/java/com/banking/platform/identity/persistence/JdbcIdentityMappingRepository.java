package com.banking.platform.identity.persistence;

import com.banking.platform.identity.domain.IdentityMapping;
import com.banking.platform.identity.domain.IdentityStatus;
import com.banking.platform.identity.domain.ScopeType;
import com.banking.platform.identity.domain.ScopedGrant;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcIdentityMappingRepository implements IdentityMappingRepository {

    private static final String FIND_MAPPING_SQL = """
            SELECT user_id, party_id, status
            FROM identity_schema.user_identity
            WHERE issuer = ? AND subject = ?
            """;

    private static final String FIND_GRANTS_SQL = """
            SELECT permission, scope_type, scope_id
            FROM identity_schema.authorization_grant
            WHERE user_id = ?
            ORDER BY permission, scope_type, scope_id
            """;

    private static final RowMapper<IdentityMapping> IDENTITY_MAPPING_ROW_MAPPER =
            (resultSet, rowNumber) -> new IdentityMapping(
                    resultSet.getObject("user_id", UUID.class),
                    resultSet.getObject("party_id", UUID.class),
                    IdentityStatus.valueOf(resultSet.getString("status")));

    private static final RowMapper<ScopedGrant> SCOPED_GRANT_ROW_MAPPER =
            (resultSet, rowNumber) -> new ScopedGrant(
                    resultSet.getString("permission"),
                    ScopeType.valueOf(resultSet.getString("scope_type")),
                    resultSet.getString("scope_id"));

    private final JdbcTemplate jdbcTemplate;

    public JdbcIdentityMappingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<IdentityMapping> findByIssuerAndSubject(String issuer, String subject) {
        return jdbcTemplate.query(FIND_MAPPING_SQL, IDENTITY_MAPPING_ROW_MAPPER, issuer, subject)
                .stream()
                .findFirst();
    }

    @Override
    public List<ScopedGrant> findGrantsByUserId(UUID userId) {
        return jdbcTemplate.query(FIND_GRANTS_SQL, SCOPED_GRANT_ROW_MAPPER, userId);
    }
}
