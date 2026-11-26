package com.banking.platform.identity.persistence;

import com.banking.platform.identity.domain.IdentityMapping;
import com.banking.platform.identity.domain.IdentityStatus;
import com.banking.platform.identity.domain.ScopeType;
import com.banking.platform.identity.domain.ScopedGrant;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HexFormat;
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
            FROM (
                SELECT permission, scope_type, scope_id
                FROM identity_schema.authorization_grant
                WHERE user_id = ?
                  AND EXISTS (
                      SELECT 1
                      FROM identity_schema.permission_catalog
                      WHERE permission_code = authorization_grant.permission
                        AND status = 'ACTIVE'
                  )
                UNION
                SELECT permission_catalog.permission_code AS permission,
                       role_assignment.scope_type,
                       role_assignment.scope_id
                FROM identity_schema.role_assignment
                JOIN identity_schema.user_identity
                  ON user_identity.user_id = role_assignment.user_id
                JOIN identity_schema.role_version
                  ON role_version.role_version_id = role_assignment.role_version_id
                JOIN identity_schema.role_catalog
                  ON role_catalog.role_code = role_version.role_code
                JOIN identity_schema.role_version_permission
                  ON role_version_permission.role_version_id = role_version.role_version_id
                JOIN identity_schema.permission_catalog
                  ON permission_catalog.permission_code =
                     role_version_permission.permission_code
                WHERE role_assignment.user_id = ?
                  AND user_identity.status = 'ACTIVE'
                  AND role_assignment.status = 'ACTIVE'
                  AND role_assignment.valid_from <= CURRENT_TIMESTAMP
                  AND (role_assignment.expires_at IS NULL
                       OR CURRENT_TIMESTAMP < role_assignment.expires_at)
                  AND role_assignment.revoked_at IS NULL
                  AND role_version.status = 'PUBLISHED'
                  AND role_catalog.status = 'ACTIVE'
                  AND permission_catalog.status = 'ACTIVE'
            ) effective_grants
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
        return jdbcTemplate.query(FIND_GRANTS_SQL, SCOPED_GRANT_ROW_MAPPER, userId, userId);
    }

    @Override
    public void recordResolutionAudit(
            String actor,
            String action,
            String resourceId,
            String result,
            String correlationId) {
        jdbcTemplate.update("""
                INSERT INTO identity_schema.identity_audit
                    (audit_id, actor, action, resource_type, resource_id, result,
                     occurred_at, correlation_id)
                VALUES (?, ?, ?, 'IDENTITY_PRINCIPAL', ?, ?, CURRENT_TIMESTAMP, ?)
                """,
                UUID.randomUUID(), actor, action, resourceId, result, correlationId);
    }

    public static String opaquePrincipalReference(String issuer, String subject) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] value = digest.digest((issuer + "\n" + subject).getBytes(StandardCharsets.UTF_8));
            return "sha256:" + HexFormat.of().formatHex(value);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
