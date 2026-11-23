package com.banking.platform.account.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.banking.platform.account.domain.AccountResponse;
import com.banking.platform.account.domain.AccountStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

class AccountAuthorizationServiceTests {
    private final AccountAuthorizationService service =
            new AccountAuthorizationService();

    @Test
    void selfPartyGrantCanReadOwnedAccount() {
        UUID partyId = UUID.randomUUID();
        AccountResponse account = account(partyId);
        Jwt jwt = jwt(Map.of("party_grants", List.of(Map.of(
                "permission", "party:self",
                "scopeType", "PARTY",
                "scopeId", partyId.toString()))));

        assertDoesNotThrow(() -> service.requireRead(jwt, account));
    }

    @Test
    void partyGrantCannotReadAnotherPartyAccount() {
        UUID partyId = UUID.randomUUID();
        AccountResponse account = account(UUID.randomUUID());
        Jwt jwt = jwt(Map.of("party_grants", List.of(Map.of(
                "permission", "party:self",
                "scopeType", "PARTY",
                "scopeId", partyId.toString()))));

        assertThrows(AccessDeniedException.class,
                () -> service.requireRead(jwt, account));
    }

    private AccountResponse account(UUID partyId) {
        OffsetDateTime now = OffsetDateTime.now();
        return new AccountResponse(UUID.randomUUID(), "ACC-123456",
                partyId, "PAYMENT", AccountStatus.ACTIVE, "VND", now, now);
    }

    private Jwt jwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("test")
                .header("alg", "none")
                .subject("tester")
                .claims(values -> values.putAll(claims))
                .build();
    }
}
