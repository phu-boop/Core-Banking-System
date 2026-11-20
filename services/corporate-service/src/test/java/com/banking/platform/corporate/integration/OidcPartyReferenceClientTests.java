package com.banking.platform.corporate.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.banking.platform.corporate.domain.PartyType;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class OidcPartyReferenceClientTests {
    @Test
    void obtainsClientCredentialsTokenAndSendsItOnlyToPartyService() {
        UUID partyId = UUID.randomUUID();
        OAuth2AuthorizedClientManager manager = mock(OAuth2AuthorizedClientManager.class);
        OAuth2AuthorizedClient authorizedClient = mock(OAuth2AuthorizedClient.class);
        when(manager.authorize(argThat(request ->
                "party-service".equals(request.getClientRegistrationId())
                        && "corporate-service".equals(request.getPrincipal().getName()))))
                .thenReturn(authorizedClient);
        when(authorizedClient.getAccessToken()).thenReturn(new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "service-access-token",
                Instant.now().minusSeconds(10),
                Instant.now().plusSeconds(300)));

        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://party.example/v1/customers/" + partyId))
                .andExpect(header("Authorization", "Bearer service-access-token"))
                .andRespond(withSuccess("""
                        {"partyId":"%s","partyType":"ORGANIZATION","status":"ACTIVE",
                         "displayName":"Example Corp","managingBranchId":null,"version":0,
                         "createdAt":"2026-11-20T00:00:00Z","updatedAt":"2026-11-20T00:00:00Z"}
                        """.formatted(partyId), MediaType.APPLICATION_JSON));

        var client = new OidcPartyReferenceClient(manager,
                builder.baseUrl("https://party.example").build());

        var party = client.findById(partyId).orElseThrow();

        assertThat(party.partyId()).isEqualTo(partyId);
        assertThat(party.partyType()).isEqualTo(PartyType.ORGANIZATION);
        server.verify();
        verify(manager).authorize(argThat(request ->
                "party-service".equals(request.getClientRegistrationId())));
    }
}
