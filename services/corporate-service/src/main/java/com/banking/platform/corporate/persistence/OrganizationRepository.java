package com.banking.platform.corporate.persistence;

import com.banking.platform.corporate.domain.OrganizationResponse;
import com.banking.platform.corporate.domain.OrganizationStatus;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OrganizationRepository {
    private final JdbcTemplate jdbcTemplate;

    public OrganizationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public OrganizationResponse create(UUID organizationId, UUID partyId,
                                       String actor, String correlationId) {
        Instant now = Instant.now();
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.organization
                    (organization_id, party_id, status, created_at, updated_at)
                VALUES (?, ?, 'ACTIVE', ?, ?)
                """, organizationId, partyId, Timestamp.from(now), Timestamp.from(now));
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.organization_audit
                    (audit_id, actor, action, resource_type, resource_id, result,
                     occurred_at, correlation_id)
                VALUES (?, ?, 'ORGANIZATION_CREATE', 'ORGANIZATION', ?, 'SUCCESS',
                        CURRENT_TIMESTAMP, ?)
                """, UUID.randomUUID(), actor, organizationId.toString(), correlationId);
        return findById(organizationId).orElseThrow();
    }

    public Optional<OrganizationResponse> findById(UUID organizationId) {
        List<OrganizationResponse> rows = jdbcTemplate.query("""
                SELECT organization_id, party_id, status, created_at, updated_at
                FROM corporate_schema.organization
                WHERE organization_id = ?
                """,
                (rs, rowNum) -> new OrganizationResponse(
                        rs.getObject("organization_id", UUID.class),
                        rs.getObject("party_id", UUID.class),
                        OrganizationStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()),
                organizationId);
        return rows.stream().findFirst();
    }
}
