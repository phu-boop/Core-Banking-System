package com.banking.platform.reporting.application;

import java.util.Collection;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class ReportingAuthorizationService {
    public void requireRead(Jwt jwt, String reportType) {
        if (!hasPermission(jwt, "report:" + reportType.toLowerCase() + ":read")) {
            throw new AccessDeniedException("Reporting read permission is required");
        }
    }

    public void requireScope(Jwt jwt, String organizationId, String accountId, String branchId) {
        if (organizationId != null && !hasScope(jwt, "organizations", organizationId)) {
            throw new AccessDeniedException("Organization scope is denied");
        }
        if (accountId != null && !hasScope(jwt, "accounts", accountId)) {
            throw new AccessDeniedException("Account scope is denied");
        }
        if (branchId != null && !hasScope(jwt, "branches", branchId)) {
            throw new AccessDeniedException("Branch scope is denied");
        }
    }

    private boolean hasPermission(Jwt jwt, String permission) {
        Object claim = jwt.getClaims().get("report_permissions");
        return claim instanceof Collection<?> values
                && values.stream().anyMatch(permission::equals);
    }

    private boolean hasScope(Jwt jwt, String claimName, String value) {
        Object claim = jwt.getClaims().get(claimName);
        return claim instanceof Collection<?> values
                && values.stream().anyMatch(value::equals);
    }
}
