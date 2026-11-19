package com.banking.platform.branch.application;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class BranchAuthorizationService {
    public void requireAdministration(Jwt jwt) {
        if (!hasGlobalPermission(jwt, "branch:admin")) {
            throw new AccessDeniedException("Branch administration permission is required");
        }
    }

    public List<UUID> requireListVisibility(Jwt jwt) {
        if (hasGlobalPermission(jwt, "branch:read")) {
            return null;
        }
        List<UUID> branchIds = scopedBranchIds(jwt);
        if (branchIds.isEmpty()) {
            throw new AccessDeniedException("Branch read permission is required");
        }
        return branchIds;
    }

    public boolean canRead(Jwt jwt, UUID branchId) {
        return hasGlobalPermission(jwt, "branch:read")
                || scopedBranchIds(jwt).contains(branchId);
    }

    private boolean hasGlobalPermission(Jwt jwt, String permission) {
        Object claim = jwt.getClaims().get("branch_permissions");
        return claim instanceof Collection<?> values
                && values.stream().anyMatch(permission::equals);
    }

    private List<UUID> scopedBranchIds(Jwt jwt) {
        Object claim = jwt.getClaims().get("branch_grants");
        if (!(claim instanceof Collection<?> grants)) {
            return List.of();
        }
        Set<UUID> branchIds = new LinkedHashSet<>();
        for (Object value : grants) {
            if (!(value instanceof Map<?, ?> grant)
                    || !"branch:read".equals(grant.get("permission"))
                    || !"BRANCH".equals(grant.get("scopeType"))
                    || !(grant.get("scopeId") instanceof String scopeId)) {
                continue;
            }
            try {
                branchIds.add(UUID.fromString(scopeId));
            } catch (IllegalArgumentException ignored) {
                // Invalid signed grant data is not treated as an authorization grant.
            }
        }
        return List.copyOf(branchIds);
    }
}
