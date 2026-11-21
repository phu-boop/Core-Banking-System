package com.banking.platform.corporate.persistence;

import com.banking.platform.corporate.application.MembershipService.MembershipNotFoundException;
import com.banking.platform.corporate.application.MembershipService.MembershipValidationException;
import com.banking.platform.corporate.application.OrganizationService.OrganizationNotFoundException;
import com.banking.platform.corporate.domain.MandateResponse;
import com.banking.platform.corporate.domain.MembershipResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MembershipRepository {
    private final JdbcTemplate jdbcTemplate;

    public MembershipRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public MembershipResponse createMembership(UUID organizationId, String subject,
                                               String roleCode, String actor,
                                               String correlationId) {
        requireOrganization(organizationId);
        UUID membershipId = UUID.randomUUID();
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.membership
                    (membership_id, organization_id, subject, role_code, status)
                VALUES (?, ?, ?, ?, 'ACTIVE')
                """, membershipId, organizationId, subject, roleCode);
        writeAudit(actor, "CORPORATE_MEMBERSHIP_CREATE",
                membershipId, correlationId);
        List<MembershipResponse> memberships =
                findMembership(organizationId, membershipId);
        if (memberships.isEmpty()) {
            throw new MembershipNotFoundException(membershipId);
        }
        return memberships.getFirst();
    }

    public List<MembershipResponse> listMemberships(UUID organizationId) {
        requireOrganization(organizationId);
        return jdbcTemplate.query("""
                SELECT membership_id, organization_id, subject, role_code, status,
                       created_at, updated_at
                FROM corporate_schema.membership
                WHERE organization_id = ?
                ORDER BY created_at, membership_id
                """, (resultSet, rowNum) -> new MembershipResponse(
                        resultSet.getObject("membership_id", UUID.class),
                        resultSet.getObject("organization_id", UUID.class),
                        resultSet.getString("subject"),
                        resultSet.getString("role_code"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("created_at").toInstant(),
                        resultSet.getTimestamp("updated_at").toInstant()),
                organizationId);
    }

    @Transactional
    public MandateResponse createMandate(UUID organizationId, UUID membershipId,
                                         String mandateScope, String actor,
                                         String correlationId) {
        requireOrganization(organizationId);
        Integer membershipCount = jdbcTemplate.queryForObject("""
                SELECT count(*)
                FROM corporate_schema.membership
                WHERE organization_id = ? AND membership_id = ? AND status = 'ACTIVE'
                """, Integer.class, organizationId, membershipId);
        if (membershipCount == null || membershipCount == 0) {
            throw new MembershipNotFoundException(membershipId);
        }
        UUID mandateId = UUID.randomUUID();
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.mandate
                    (mandate_id, membership_id, mandate_scope, status)
                VALUES (?, ?, ?, 'ACTIVE')
                """, mandateId, membershipId, mandateScope);
        writeAudit(actor, "CORPORATE_MANDATE_CREATE",
                mandateId, correlationId);
        return jdbcTemplate.queryForObject("""
                SELECT mandate_id, membership_id, mandate_scope, status,
                       created_at, updated_at
                FROM corporate_schema.mandate
                WHERE mandate_id = ?
                """, (resultSet, rowNum) -> new MandateResponse(
                        resultSet.getObject("mandate_id", UUID.class),
                        resultSet.getObject("membership_id", UUID.class),
                        resultSet.getString("mandate_scope"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("created_at").toInstant(),
                        resultSet.getTimestamp("updated_at").toInstant()),
                mandateId);
    }

    private List<MembershipResponse> findMembership(UUID organizationId,
                                                     UUID membershipId) {
        return jdbcTemplate.query("""
                SELECT membership_id, organization_id, subject, role_code, status,
                       created_at, updated_at
                FROM corporate_schema.membership
                WHERE organization_id = ? AND membership_id = ?
                """, (resultSet, rowNum) -> new MembershipResponse(
                        resultSet.getObject("membership_id", UUID.class),
                        resultSet.getObject("organization_id", UUID.class),
                        resultSet.getString("subject"),
                        resultSet.getString("role_code"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("created_at").toInstant(),
                        resultSet.getTimestamp("updated_at").toInstant()),
                organizationId, membershipId);
    }

    private void requireOrganization(UUID organizationId) {
        Boolean exists = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM corporate_schema.organization
                    WHERE organization_id = ?
                )
                """, Boolean.class, organizationId);
        if (!Boolean.TRUE.equals(exists)) {
            throw new OrganizationNotFoundException(organizationId);
        }
    }

    private void writeAudit(String actor, String action, UUID resourceId,
                            String correlationId) {
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.organization_audit
                    (audit_id, actor, action, resource_type, resource_id, result,
                     occurred_at, correlation_id)
                VALUES (?, ?, ?, 'CORPORATE_AUTHORIZATION', ?, 'SUCCESS',
                        CURRENT_TIMESTAMP, ?)
                """, UUID.randomUUID(), actor, action, resourceId.toString(),
                correlationId);
    }
}
