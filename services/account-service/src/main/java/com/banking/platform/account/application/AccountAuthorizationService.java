package com.banking.platform.account.application;

import com.banking.platform.account.domain.AccountResponse;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AccountAuthorizationService {
    public void requireCreate(Jwt jwt) {
        if (!hasGlobalPermission(jwt, "account:create")) {
            throw new AccessDeniedException("Account create permission is required");
        }
    }

    public void requireRead(Jwt jwt, AccountResponse account) {
        if (!canRead(jwt, account.partyId(), account.accountId())) {
            throw new AccessDeniedException("Account read permission is required");
        }
    }

    public void requireManage(Jwt jwt, AccountResponse account) {
        if (!hasGlobalPermission(jwt, "account:manage")
                && !hasScopedPermission(jwt, "account:manage", account.accountId())
                && !hasScopedPartyPermission(jwt, account.partyId())) {
            throw new AccessDeniedException("Account manage permission is required");
        }
    }

    private boolean canRead(Jwt jwt, UUID partyId, UUID accountId) {
        return hasGlobalPermission(jwt, "account:read")
                || hasScopedPermission(jwt, "account:read", accountId)
                || hasScopedPartyPermission(jwt, partyId);
    }

    private boolean hasGlobalPermission(Jwt jwt, String permission) {
        Object claim = jwt.getClaims().get("account_permissions");
        return claim instanceof Collection<?> values
                && values.stream().anyMatch(permission::equals);
    }

    private boolean hasScopedPermission(Jwt jwt, String permission, UUID accountId) {
        Object claim = jwt.getClaims().get("account_grants");
        if (!(claim instanceof Collection<?> grants)) {
            return false;
        }
        return grants.stream().anyMatch(value -> value instanceof Map<?, ?> grant
                && permission.equals(grant.get("permission"))
                && "ACCOUNT".equals(grant.get("scopeType"))
                && accountId.toString().equals(grant.get("scopeId")));
    }

    private boolean hasScopedPartyPermission(Jwt jwt, UUID partyId) {
        Object claim = jwt.getClaims().get("party_grants");
        if (!(claim instanceof Collection<?> grants)) {
            return false;
        }
        return grants.stream().anyMatch(value -> value instanceof Map<?, ?> grant
                && "party:self".equals(grant.get("permission"))
                && "PARTY".equals(grant.get("scopeType"))
                && partyId.toString().equals(grant.get("scopeId")));
    }
}
