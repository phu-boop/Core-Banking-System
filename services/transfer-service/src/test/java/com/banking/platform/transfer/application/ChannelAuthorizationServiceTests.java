package com.banking.platform.transfer.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

class ChannelAuthorizationServiceTests {
    private final ChannelAuthorizationService service =
            new ChannelAuthorizationService();

    @Test
    void retailSubmitRequiresMatchingAccountAndBranchGrants() {
        UUID accountA = UUID.randomUUID();
        UUID accountB = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        Jwt jwt = jwt(Map.of(
                "account_grants", List.of(
                        grant("account:read", "ACCOUNT", accountA),
                        grant("account:read", "ACCOUNT", accountB)),
                "branch_grants", List.of(
                        grant("branch:read", "BRANCH", branchId))));

        assertDoesNotThrow(() -> service.requireRetailSubmit(
                jwt, branchId, accountA, accountB));
    }

    @Test
    void outOfScopeBranchIsDenied() {
        UUID accountA = UUID.randomUUID();
        UUID accountB = UUID.randomUUID();
        Jwt jwt = jwt(Map.of(
                "account_grants", List.of(
                        grant("account:read", "ACCOUNT", accountA),
                        grant("account:read", "ACCOUNT", accountB)),
                "branch_grants", List.of()));

        assertThrows(AccessDeniedException.class,
                () -> service.requireRetailSubmit(
                        jwt, UUID.randomUUID(), accountA, accountB));
    }

    private Map<String, String> grant(String permission,
                                      String scopeType,
                                      UUID scopeId) {
        return Map.of("permission", permission,
                "scopeType", scopeType,
                "scopeId", scopeId.toString());
    }

    private Jwt jwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("test")
                .header("alg", "none")
                .subject("tester")
                .claims(values -> values.putAll(claims))
                .build();
    }
}
