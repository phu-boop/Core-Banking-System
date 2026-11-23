package com.banking.platform.account.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import com.banking.platform.account.config.IdentityClientProperties;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withForbiddenRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class IdentityPrincipalClientTests {

    @Test
    void forwardsTheValidatedAccessTokenAndReadsIdentityPrincipal() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://identity.example");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        UUID userId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        server.expect(requestTo("http://identity.example/v1/identity/me"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andRespond(withSuccess("""
                        {
                          "userId": "%s",
                          "partyId": "%s",
                          "grants": [
                            {"permission":"account:read","scopeType":"SELF","scopeId":"%s"}
                          ]
                        }
                        """.formatted(userId, partyId, partyId), MediaType.APPLICATION_JSON));
        IdentityPrincipalClient client = new IdentityPrincipalClient(builder.build());

        IdentityPrincipal principal = client.currentPrincipal("access-token");

        assertThat(principal.userId()).isEqualTo(userId);
        assertThat(principal.partyId()).isEqualTo(partyId);
        assertThat(principal.grants()).containsExactly(
                new IdentityGrant("account:read", "SELF", partyId.toString()));
        server.verify();
    }

    @Test
    void rejectsIdentityServiceForbiddenResponse() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://identity.example");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("http://identity.example/v1/identity/me"))
                .andRespond(withForbiddenRequest());
        IdentityPrincipalClient client = new IdentityPrincipalClient(builder.build());

        assertThatThrownBy(() -> client.currentPrincipal("access-token"))
                .isInstanceOf(AccessDeniedException.class);
        server.verify();
    }
}
