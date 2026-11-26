package com.banking.platform.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.platform.identity.domain.IdentityMapping;
import com.banking.platform.identity.domain.IdentityStatus;
import com.banking.platform.identity.domain.ScopeType;
import com.banking.platform.identity.domain.ScopedGrant;
import com.banking.platform.identity.persistence.IdentityMappingRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

class IdentityPrincipalServiceTests {

    private static final String ISSUER = "https://idp.example/realms/banking";
    private static final String SUBJECT = "external-subject";
    private static final UUID USER_ID = UUID.fromString("94f30fc9-66ab-436f-a8f4-4f4aa97f0908");
    private static final UUID PARTY_ID = UUID.fromString("c66d85a9-d728-4b9d-9e83-ae02cc7a2c74");

    private final IdentityMappingRepository repository = org.mockito.Mockito.mock(IdentityMappingRepository.class);
    private final IdentityPrincipalService service = new IdentityPrincipalService(repository);

    @Test
    void resolvesActiveMappingAndItsScopedGrants() {
        var grants = List.of(new ScopedGrant("account:read", ScopeType.SELF, PARTY_ID.toString()));
        when(repository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(new IdentityMapping(USER_ID, PARTY_ID, IdentityStatus.ACTIVE)));
        when(repository.findGrantsByUserId(USER_ID)).thenReturn(grants);

        var principal = service.resolve(ISSUER, SUBJECT);

        assertThat(principal.userId()).isEqualTo(USER_ID);
        assertThat(principal.partyId()).isEqualTo(PARTY_ID);
        assertThat(principal.grants()).containsExactlyElementsOf(grants);
        verify(repository).findGrantsByUserId(USER_ID);
    }

    @Test
    void deniesUnknownSubjectWithoutCreatingAMapping() {
        when(repository.findByIssuerAndSubject(ISSUER, SUBJECT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resolve(ISSUER, SUBJECT))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Identity mapping is not active");
        verify(repository).findByIssuerAndSubject(ISSUER, SUBJECT);
        verify(repository, never()).findGrantsByUserId(USER_ID);
    }

    @Test
    void deniesDisabledMappingWithoutReturningGrants() {
        when(repository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(new IdentityMapping(USER_ID, null, IdentityStatus.DISABLED)));

        assertThatThrownBy(() -> service.resolve(ISSUER, SUBJECT))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Identity mapping is not active");
        verify(repository, never()).findGrantsByUserId(USER_ID);
    }
}
