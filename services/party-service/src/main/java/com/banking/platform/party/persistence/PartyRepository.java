package com.banking.platform.party.persistence;

import com.banking.platform.party.domain.PartyResponse;
import com.banking.platform.party.domain.PartyStatus;
import com.banking.platform.party.domain.PartyType;
import com.banking.platform.party.web.CreatePartyRequest;
import com.banking.platform.party.web.UpdatePartyRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PartyRepository {
    private final JdbcTemplate jdbcTemplate;

    public PartyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public PartyResponse create(UUID partyId, CreatePartyRequest request) {
        Instant now = Instant.now();
        jdbcTemplate.update("""
                INSERT INTO party_schema.party
                    (party_id, party_type, status, display_name, managing_branch_id, version, created_at, updated_at)
                VALUES (?, ?, 'ACTIVE', ?, ?, 0, ?, ?)
                """,
                partyId, request.partyType().name(), request.displayName(),
                request.managingBranchId(), Timestamp.from(now), Timestamp.from(now));

        if (request.partyType() == PartyType.INDIVIDUAL && request.individualProfile() != null) {
            var profile = request.individualProfile();
            jdbcTemplate.update("""
                    INSERT INTO party_schema.individual_profile
                        (party_id, first_name, last_name, date_of_birth, national_id_hash)
                    VALUES (?, ?, ?, ?, ?)
                    """, partyId, profile.firstName(), profile.lastName(),
                    profile.dateOfBirth(), profile.nationalIdHash());
        }
        return findById(partyId).orElseThrow();
    }

    public Optional<PartyResponse> findById(UUID partyId) {
        var rows = jdbcTemplate.query("""
                SELECT party_id, party_type, status, display_name, managing_branch_id,
                       version, created_at, updated_at
                FROM party_schema.party
                WHERE party_id = ?
                """,
                (rs, rowNum) -> new PartyResponse(
                        rs.getObject("party_id", UUID.class),
                        PartyType.valueOf(rs.getString("party_type")),
                        PartyStatus.valueOf(rs.getString("status")),
                        rs.getString("display_name"),
                        rs.getObject("managing_branch_id", UUID.class),
                        rs.getLong("version"),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()),
                partyId);
        return rows.stream().findFirst();
    }

    @Transactional
    public int update(UUID partyId, long expectedVersion, UpdatePartyRequest request) {
        return jdbcTemplate.update("""
                UPDATE party_schema.party
                SET display_name = ?, managing_branch_id = ?, version = version + 1,
                    updated_at = CURRENT_TIMESTAMP
                WHERE party_id = ? AND version = ?
                """, request.displayName(), request.managingBranchId(), partyId, expectedVersion);
    }

    public void recordAudit(String actor, String action, String resourceId, String result,
                            String correlationId, UUID partyId, UUID branchId) {
        jdbcTemplate.update("""
                INSERT INTO party_schema.party_audit
                    (audit_id, actor, action, resource_type, resource_id, result,
                     timestamp, correlation_id, party_id, branch_id)
                VALUES (?, ?, ?, 'PARTY', ?, ?, CURRENT_TIMESTAMP, ?, ?, ?)
                """, UUID.randomUUID(), actor, action, resourceId, result, correlationId, partyId, branchId);
    }
}
