package com.banking.platform.audit.application;

import java.util.Collection;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuditAuthorizationService {
    public void requireWrite(Jwt jwt) {
        if (!hasPermission(jwt, "audit:write")) {
            throw new AccessDeniedException("Audit write permission is required");
        }
    }

    public void requireRead(Jwt jwt) {
        if (!hasPermission(jwt, "audit:read")) {
            throw new AccessDeniedException("Audit read permission is required");
        }
    }

    private boolean hasPermission(Jwt jwt, String permission) {
        Object claim = jwt.getClaims().get("audit_permissions");
        if (claim instanceof Collection<?> values
                && values.stream().anyMatch(permission::equals)) {
            return true;
        }
        Object grants = jwt.getClaims().get("audit_grants");
        if (!(grants instanceof Collection<?> entries)) {
            return false;
        }
        return entries.stream()
                .filter(Map.class::isInstance)
                .map(value -> (Map<?, ?>) value)
                .anyMatch(grant -> permission.equals(grant.get("permission")));
    }
}
