package com.banking.platform.account.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.banking.platform.account.AccountServiceApplication;
import com.banking.platform.account.domain.AccountStatus;
import com.banking.platform.account.domain.AccountResponse;
import com.banking.platform.account.integration.IdentityGrant;
import com.banking.platform.account.integration.IdentityPrincipal;
import com.banking.platform.account.integration.IdentityPrincipalClient;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest(classes = AccountServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountEndpointIntegrationTests {
    @MockBean
    JwtDecoder jwtDecoder;
    @MockBean
    IdentityPrincipalClient identityPrincipalClient;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.12")
            .withDatabaseName("account_test");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("account.security.issuer-uri", () -> "https://issuer.example");
        registry.add("account.security.jwk-set-uri", () -> "https://issuer.example/jwks");
        registry.add("account.security.audience", () -> "account-service");
        registry.add("account.identity.base-url", () -> "http://localhost:8080");
        registry.add("account.identity.allow-http", () -> true);
    }

    @Autowired
    TestRestTemplate rest;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void createsAndTransitionsAccount() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject("tester")
                .claim("account_permissions", List.of("account:create", "account:read", "account:manage"))
                .build();
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("test-token");

        UUID partyId = UUID.randomUUID();
        CreateAccountRequest request = new CreateAccountRequest(partyId, "ACC-" + UUID.randomUUID().toString().substring(0, 8));

        var created = rest.postForEntity("/v1/accounts",
                new HttpEntity<>(request, headers), AccountStateResponse.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().status()).isEqualTo(AccountStatus.ACTIVE);

        var blocked = rest.postForEntity("/v1/accounts/" + created.getBody().accountId() + "/block",
                new HttpEntity<>(new StateChangeRequest("risk hold"), headers), AccountStateResponse.class);
        assertThat(blocked.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(blocked.getBody().status()).isEqualTo(AccountStatus.BLOCKED);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM account_schema.account_status_history",
                Integer.class)).isGreaterThanOrEqualTo(1);
    }

    @Test
    void customerCanReadOnlyAnAccountGrantedByIdentityService() {
        UUID partyId = UUID.randomUUID();
        Jwt jwt = staffJwt();
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        when(identityPrincipalClient.currentPrincipal("test-token"))
                .thenReturn(new IdentityPrincipal(
                        UUID.randomUUID(),
                        partyId,
                        List.of(new IdentityGrant("account:read", "SELF", partyId.toString()))));
        HttpHeaders headers = bearerHeaders();
        CreateAccountRequest request = new CreateAccountRequest(
                partyId, "ACC-" + UUID.randomUUID().toString().substring(0, 8));

        var created = rest.postForEntity("/v1/accounts",
                new HttpEntity<>(request, headers), AccountResponse.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var read = rest.exchange(
                "/v1/accounts/" + created.getBody().accountId(),
                org.springframework.http.HttpMethod.GET,
                new HttpEntity<>(headers),
                AccountResponse.class,
                new Object[0]);

        assertThat(read.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(read.getBody().partyId()).isEqualTo(partyId);
    }

    @Test
    void customerCannotReadAnAccountOutsideIdentityPartyScope() {
        UUID ownerPartyId = UUID.randomUUID();
        Jwt jwt = staffJwt();
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        when(identityPrincipalClient.currentPrincipal("test-token"))
                .thenReturn(new IdentityPrincipal(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        List.of(new IdentityGrant("account:read", "SELF", ownerPartyId.toString()))));
        HttpHeaders headers = bearerHeaders();
        CreateAccountRequest request = new CreateAccountRequest(
                ownerPartyId, "ACC-" + UUID.randomUUID().toString().substring(0, 8));

        var created = rest.postForEntity("/v1/accounts",
                new HttpEntity<>(request, headers), AccountResponse.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var read = rest.exchange(
                "/v1/accounts/" + created.getBody().accountId(),
                org.springframework.http.HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(read.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private Jwt staffJwt() {
        return Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject("staff-subject")
                .claim("account_permissions", List.of("account:create"))
                .build();
    }

    private HttpHeaders bearerHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("test-token");
        return headers;
    }

    private record AccountStateResponse(UUID accountId, AccountStatus status) {}
}
