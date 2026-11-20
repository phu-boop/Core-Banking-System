package com.banking.platform.corporate.application;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class OrganizationAuthorizationService {
    public void requireCreate(Jwt jwt) {
        if (!hasGlobalPermission(jwt, "organization:create")) {
            throw new AccessDeniedException("Organization creation permission is required");
        }
    }

    public boolean canRead(Jwt jwt, UUID organizationId) {
        return hasGlobalPermission(jwt, "organization:read")
                || hasScopedRead(jwt, organizationId);
    }

    public void requireApprovalPolicyManagement(Jwt jwt, UUID organizationId) {
        if (!hasScopedPermission(jwt, "approval-policy:manage", organizationId)) {
            throw new AccessDeniedException("Organization-scoped approval-policy:manage permission is required");
        }
    }

    private boolean hasGlobalPermission(Jwt jwt, String permission) {
        Object claim = jwt.getClaims().get("corporate_permissions");
        return claim instanceof Collection<?> values
                && values.stream().anyMatch(permission::equals);
    }

    private boolean hasScopedRead(Jwt jwt, UUID organizationId) {
        return hasScopedPermission(jwt, "organization:read", organizationId);
    }

    private boolean hasScopedPermission(Jwt jwt, String permission, UUID organizationId) {
        Object claim = jwt.getClaims().get("corporate_grants");
        if (!(claim instanceof Collection<?> grants)) {
            return false;
        }
        return grants.stream().anyMatch(value -> value instanceof Map<?, ?> grant
                && permission.equals(grant.get("permission"))
                && "ORGANIZATION".equals(grant.get("scopeType"))
                && organizationId.toString().equals(grant.get("scopeId")));
    }
}
