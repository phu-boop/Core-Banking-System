package com.banking.platform.corporate.application;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcApproverEligibilityClient implements ApproverEligibilityClient {
    private final JdbcTemplate jdbcTemplate;

    public JdbcApproverEligibilityClient(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isEligible(UUID organizationId, String subject,
                              String roleCode, String mandateScope) {
        if (organizationId == null || subject == null
                || roleCode == null || mandateScope == null) {
            return false;
        }
        Boolean eligible = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1
                    FROM corporate_schema.membership m
                    JOIN corporate_schema.mandate d ON d.membership_id = m.membership_id
                    WHERE m.organization_id = ?
                      AND m.subject = ?
                      AND m.role_code = ?
                      AND m.status = 'ACTIVE'
                      AND d.mandate_scope = ?
                      AND d.status = 'ACTIVE'
                )
                """, Boolean.class, organizationId, subject,
                roleCode.trim().toUpperCase(), mandateScope.trim().toUpperCase());
        return Boolean.TRUE.equals(eligible);
    }
}
