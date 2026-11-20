package com.banking.platform.corporate.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.banking.platform.corporate.domain.PartyReferenceResponse;
import com.banking.platform.corporate.domain.PartyType;
import com.banking.platform.corporate.integration.OidcPartyReferenceClient.PartyServiceUnavailableException;
import com.banking.platform.corporate.integration.PartyReferenceClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganizationEndpointIntegrationTests {
    private static final String MIGRATOR = "corporate_service_migrator";
    private static final String APP = "corporate_service_app";
    private static final String MIGRATOR_PASSWORD = UUID.randomUUID().toString();
    private static final String APP_PASSWORD = UUID.randomUUID().toString();
    private static final String ISSUER = "https://idp.example/realms/banking";
    private static final String AUDIENCE = "corporate-api";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17.12")
                    .withDatabaseName("corporate_test")
                    .withUsername("postgres")
                    .withPassword("test-admin-password");

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtDecoder jwtDecoder;
    @MockBean
    private PartyReferenceClient partyReferenceClient;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", () -> APP);
        registry.add("spring.datasource.password", () -> APP_PASSWORD);
        registry.add("corporate.security.issuer-uri", () -> ISSUER);
        registry.add("corporate.security.jwk-set-uri", () -> "https://idp.example/realms/banking/certs");
        registry.add("corporate.security.audience", () -> AUDIENCE);
        registry.add("corporate.security.party-service-base-url", () -> "http://localhost:8091");
        registry.add("corporate.security.party-client-id", () -> "corporate-party-client");
        registry.add("corporate.security.party-client-secret", () -> "test-party-client-secret");
        registry.add("corporate.security.token-uri", () -> "https://idp.example/realms/banking/protocol/openid-connect/token");
        registry.add("spring.security.oauth2.client.registration.party-service.client-id",
                () -> "corporate-party-client");
        registry.add("spring.security.oauth2.client.registration.party-service.client-secret",
                () -> "test-party-client-secret");
        registry.add("spring.security.oauth2.client.registration.party-service.scope", () -> "party.read");
        registry.add("spring.security.oauth2.client.provider.keycloak.token-uri",
                () -> "https://idp.example/realms/banking/protocol/openid-connect/token");
    }

    @BeforeAll
    static void provisionDatabase() throws Exception {
        POSTGRES.execInContainer("psql", "-v", "ON_ERROR_STOP=1", "-U", "postgres",
                "-d", "corporate_test", "-c",
                "CREATE ROLE " + MIGRATOR + " LOGIN PASSWORD '" + MIGRATOR_PASSWORD + "';"
                        + "CREATE ROLE " + APP + " LOGIN PASSWORD '" + APP_PASSWORD + "';"
                        + "CREATE SCHEMA corporate_schema AUTHORIZATION " + MIGRATOR + ";"
                        + "REVOKE ALL ON SCHEMA corporate_schema FROM PUBLIC;"
                        + "GRANT USAGE ON SCHEMA corporate_schema TO " + APP + ";");
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), MIGRATOR, MIGRATOR_PASSWORD)
                .schemas("corporate_schema")
                .defaultSchema("corporate_schema")
                .createSchemas(false)
                .locations("classpath:db/migration")
                .load()
                .migrate();
        try (Connection connection = adminConnection(); var statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA other_service_schema AUTHORIZATION postgres");
            statement.execute("CREATE TABLE other_service_schema.private_probe (id UUID PRIMARY KEY)");
        }
    }

    @BeforeEach
    void resetDatabaseAndConfigureTokens() {
        try (Connection connection = migratorConnection(); var statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE corporate_schema.organization_audit, "
                    + "corporate_schema.organization");
        } catch (Exception exception) {
            throw new IllegalStateException("Could not reset Corporate test data", exception);
        }
        when(jwtDecoder.decode("admin-token")).thenReturn(jwt(
                "admin-token", "admin-subject", List.of("organization:create", "organization:read"), List.of()));
        when(jwtDecoder.decode("reader-token")).thenReturn(jwt(
                "reader-token", "reader-subject", List.of("organization:read"), List.of()));
        when(jwtDecoder.decode("no-access-token")).thenReturn(jwt(
                "no-access-token", "no-access", List.of(), List.of()));
    }

    @Test
    void createsAndReadsOrganizationAfterValidatingOrganizationParty() throws Exception {
        UUID partyId = UUID.randomUUID();
        when(partyReferenceClient.findById(partyId)).thenReturn(
                java.util.Optional.of(new PartyReferenceResponse(partyId, PartyType.ORGANIZATION)));

        ResponseEntity<String> created = post("admin-token", partyId);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode response = objectMapper.readTree(created.getBody());
        assertThat(response.get("partyId").asText()).isEqualTo(partyId.toString());
        assertThat(response.get("status").asText()).isEqualTo("ACTIVE");
        assertThat(created.getHeaders().getFirst("X-Correlation-Id")).isNotBlank();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM corporate_schema.organization_audit", Integer.class)).isEqualTo(1);

        UUID organizationId = UUID.fromString(response.get("organizationId").asText());
        ResponseEntity<String> fetched = restTemplate.exchange(
                "/v1/organizations/" + organizationId, HttpMethod.GET,
                new HttpEntity<>(headers("reader-token")), String.class);
        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(objectMapper.readTree(fetched.getBody()).get("organizationId").asText())
                .isEqualTo(organizationId.toString());
    }

    @Test
    void rejectsMissingOrIndividualPartyAndDoesNotPersistOrganization() {
        UUID missingPartyId = UUID.randomUUID();
        when(partyReferenceClient.findById(missingPartyId)).thenReturn(java.util.Optional.empty());
        ResponseEntity<String> absent = post("admin-token", missingPartyId);
        assertThat(absent.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        UUID individualPartyId = UUID.randomUUID();
        when(partyReferenceClient.findById(individualPartyId)).thenReturn(
                java.util.Optional.of(new PartyReferenceResponse(individualPartyId, PartyType.INDIVIDUAL)));
        ResponseEntity<String> wrongType = post("admin-token", individualPartyId);
        assertThat(wrongType.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM corporate_schema.organization", Integer.class)).isZero();
    }

    @Test
    void enforcesPartyOneToOneMappingAndAuthorization() throws Exception {
        UUID partyId = UUID.randomUUID();
        when(partyReferenceClient.findById(partyId)).thenReturn(
                java.util.Optional.of(new PartyReferenceResponse(partyId, PartyType.ORGANIZATION)));
        assertThat(post("admin-token", partyId).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(post("admin-token", partyId).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(post("reader-token", UUID.randomUUID()).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        UUID organizationId = UUID.fromString(jdbcTemplate.queryForObject(
                "SELECT organization_id FROM corporate_schema.organization WHERE party_id = ?",
                UUID.class, partyId).toString());
        ResponseEntity<String> hidden = restTemplate.exchange(
                "/v1/organizations/" + organizationId, HttpMethod.GET,
                new HttpEntity<>(headers("no-access-token")), String.class);
        assertThat(hidden.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        when(jwtDecoder.decode("scoped-reader")).thenReturn(jwt(
                "scoped-reader", "scoped-subject", List.of(), List.of(Map.of(
                        "permission", "organization:read",
                        "scopeType", "ORGANIZATION",
                        "scopeId", organizationId.toString()))));
        ResponseEntity<String> scoped = restTemplate.exchange(
                "/v1/organizations/" + organizationId, HttpMethod.GET,
                new HttpEntity<>(headers("scoped-reader")), String.class);
        assertThat(scoped.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void partyServiceFailureReturnsUnavailableRatherThanSuccess() {
        UUID partyId = UUID.randomUUID();
        when(partyReferenceClient.findById(partyId))
                .thenThrow(new PartyServiceUnavailableException());
        ResponseEntity<String> response = post("admin-token", partyId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM corporate_schema.organization", Integer.class)).isZero();
    }

    @Test
    void runtimeRoleCannotReadMigrationHistoryOrOtherSchemas() {
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM corporate_schema.organization", Integer.class)).isZero();
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                "SELECT count(*) FROM corporate_schema.flyway_schema_history", Integer.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                "SELECT count(*) FROM other_service_schema.private_probe", Integer.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
    }

    private ResponseEntity<String> post(String token, UUID partyId) {
        HttpHeaders headers = headers(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"partyId\":\"" + partyId + "\"}";
        return restTemplate.postForEntity(
                "/v1/organizations", new HttpEntity<>(body, headers), String.class);
    }

    private HttpHeaders headers(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-Correlation-Id", "corporate-test-" + UUID.randomUUID());
        return headers;
    }

    private static Jwt jwt(String token, String subject, List<String> permissions,
                           List<Map<String, String>> grants) {
        Instant now = Instant.now();
        return Jwt.withTokenValue(token)
                .header("alg", "RS256")
                .issuer(ISSUER)
                .subject(subject)
                .audience(List.of(AUDIENCE))
                .claim("corporate_permissions", permissions)
                .claim("corporate_grants", grants)
                .issuedAt(now.minusSeconds(30))
                .expiresAt(now.plusSeconds(300))
                .build();
    }

    private static Connection adminConnection() throws Exception {
        return DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    private static Connection migratorConnection() throws Exception {
        return DriverManager.getConnection(POSTGRES.getJdbcUrl(), MIGRATOR, MIGRATOR_PASSWORD);
    }
}
