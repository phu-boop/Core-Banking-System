package com.banking.platform.account.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.banking.platform.account.AccountServiceApplication;
import com.banking.platform.account.domain.AccountStatus;
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

    private record AccountStateResponse(UUID accountId, AccountStatus status) {}
}
