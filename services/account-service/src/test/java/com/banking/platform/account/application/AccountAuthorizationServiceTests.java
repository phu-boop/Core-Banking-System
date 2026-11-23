package com.banking.platform.account.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.banking.platform.account.domain.AccountResponse;
import com.banking.platform.account.domain.AccountStatus;
import com.banking.platform.account.integration.IdentityGrant;
import com.banking.platform.account.integration.IdentityPrincipal;
import com.banking.platform.account.integration.IdentityPrincipalClient;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountAuthorizationServiceTests {
    private final IdentityPrincipalClient identityClient = mock(IdentityPrincipalClient.class);
    private final AccountAuthorizationService service = new AccountAuthorizationService(identityClient);

    @Test
    void selfGrantFromIdentityCanReadOwnedAccount() {
        UUID partyId = UUID.randomUUID();
        AccountResponse account = account(partyId);
        when(identityClient.currentPrincipal("test")).thenReturn(principal(
                partyId, new IdentityGrant("account:read", "SELF", partyId.toString())));

        assertDoesNotThrow(() -> service.requireRead(jwt(), account));
    }

    @Test
    void selfGrantCannotReadAnotherPartyAccount() {
        UUID partyId = UUID.randomUUID();
        AccountResponse account = account(UUID.randomUUID());
        when(identityClient.currentPrincipal("test")).thenReturn(principal(
                partyId, new IdentityGrant("account:read", "SELF", partyId.toString())));

        assertThrows(AccessDeniedException.class,
                () -> service.requireRead(jwt(), account));
    }

    @Test
    void partyLinkWithoutMatchingReadGrantCannotReadAccount() {
        UUID partyId = UUID.randomUUID();
        when(identityClient.currentPrincipal("test")).thenReturn(principal(
                partyId, new IdentityGrant("account:manage", "SELF", partyId.toString())));

        assertThrows(AccessDeniedException.class,
                () -> service.requireRead(jwt(), account(partyId)));
    }

    @Test
    void identityWithoutPartyLinkCannotReadCustomerAccount() {
        when(identityClient.currentPrincipal("test")).thenReturn(principal(null));

        assertThrows(AccessDeniedException.class,
                () -> service.requireRead(jwt(), account(UUID.randomUUID())));
    }

    private AccountResponse account(UUID partyId) {
        OffsetDateTime now = OffsetDateTime.now();
        return new AccountResponse(UUID.randomUUID(), "ACC-123456",
                partyId, "PAYMENT", AccountStatus.ACTIVE, "VND", now, now);
    }

    private IdentityPrincipal principal(UUID partyId, IdentityGrant... grants) {
        return new IdentityPrincipal(UUID.randomUUID(), partyId, List.of(grants));
    }

    private Jwt jwt() {
        return Jwt.withTokenValue("test")
                .header("alg", "none")
                .subject("tester")
                .build();
    }
}
