package com.banking.platform.transfer.application;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class ChannelAuthorizationService {
    public void requireRetailSubmit(Jwt jwt, UUID processingBranchId,
                                    UUID sourceAccountId, UUID destinationAccountId) {
        if (!hasAccountPermission(jwt, sourceAccountId)
                || !hasAccountPermission(jwt, destinationAccountId)
                || !hasBranchPermission(jwt, processingBranchId)) {
            throw new AccessDeniedException(
                    "Internet Banking account and Teller branch scope are required");
        }
    }

    public void requireTransferRead(Jwt jwt, UUID transferId) {
        if (!hasGlobalPermission(jwt, "transfer:read")
                && !hasScopedPermission(jwt, "transfer:read", transferId)) {
            throw new AccessDeniedException("Transfer read permission is required");
        }
    }

    private boolean hasAccountPermission(Jwt jwt, UUID accountId) {
        if (hasGlobalPermission(jwt, "account:manage")
                || hasGlobalPermission(jwt, "account:read")) {
            return true;
        }
        Object claim = jwt.getClaims().get("account_grants");
        if (!(claim instanceof Collection<?> grants)) {
            return false;
        }
        return grants.stream().anyMatch(value -> value instanceof Map<?, ?> grant
                && ("account:read".equals(grant.get("permission"))
                    || "account:manage".equals(grant.get("permission")))
                && "ACCOUNT".equals(grant.get("scopeType"))
                && accountId.toString().equals(grant.get("scopeId")));
    }

    private boolean hasBranchPermission(Jwt jwt, UUID branchId) {
        if (hasGlobalPermission(jwt, "branch:read")
                || hasGlobalPermission(jwt, "branch:manage")) {
            return true;
        }
        Object claim = jwt.getClaims().get("branch_grants");
        if (!(claim instanceof Collection<?> grants)) {
            return false;
        }
        return grants.stream().anyMatch(value -> value instanceof Map<?, ?> grant
                && ("branch:read".equals(grant.get("permission"))
                    || "branch:manage".equals(grant.get("permission")))
                && "BRANCH".equals(grant.get("scopeType"))
                && branchId.toString().equals(grant.get("scopeId")));
    }

    private boolean hasGlobalPermission(Jwt jwt, String permission) {
        String claimName = permission.substring(0, permission.indexOf(':')) + "_permissions";
        Object claim = jwt.getClaims().get(claimName);
        return claim instanceof Collection<?> values
                && values.stream().anyMatch(permission::equals);
    }

    private boolean hasScopedPermission(Jwt jwt, String permission, UUID resourceId) {
        Object claim = jwt.getClaims().get("transfer_grants");
        if (!(claim instanceof Collection<?> grants)) {
            return false;
        }
        return grants.stream().anyMatch(value -> value instanceof Map<?, ?> grant
                && permission.equals(grant.get("permission"))
                && "TRANSFER".equals(grant.get("scopeType"))
                && resourceId.toString().equals(grant.get("scopeId")));
    }
}
