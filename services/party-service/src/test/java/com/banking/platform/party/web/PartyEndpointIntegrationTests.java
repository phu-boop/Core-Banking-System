package com.banking.platform.party.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.List;
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
import org.testcontainers.utility.MountableFile;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PartyEndpointIntegrationTests {
    private static final String MIGRATOR = "party_service_migrator";
    private static final String APP = "party_service_app";
    private static final String MIGRATOR_PASSWORD = UUID.randomUUID().toString();
    private static final String APP_PASSWORD = UUID.randomUUID().toString();
    private static final String ISSUER = "https://idp.example/realms/banking";
    private static final String AUDIENCE = "party-api";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17.12")
                    .withDatabaseName("party_test")
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

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", () -> APP);
        registry.add("spring.datasource.password", () -> APP_PASSWORD);
        registry.add("party.database.url", POSTGRES::getJdbcUrl);
        registry.add("party.database.username", () -> APP);
        registry.add("party.database.password", () -> APP_PASSWORD);
        registry.add("party.security.issuer-uri", () -> ISSUER);
        registry.add("party.security.jwk-set-uri", () -> "https://idp.example/realms/banking/certs");
        registry.add("party.security.audience", () -> AUDIENCE);
    }

    @BeforeAll
    static void provision() throws Exception {
        POSTGRES.execInContainer("psql", "-v", "ON_ERROR_STOP=1", "-U", "postgres",
                "-d", "party_test", "-c",
                "CREATE ROLE " + MIGRATOR + " LOGIN PASSWORD '" + MIGRATOR_PASSWORD + "';"
                        + "CREATE ROLE " + APP + " LOGIN PASSWORD '" + APP_PASSWORD + "';"
                        + "CREATE SCHEMA party_schema AUTHORIZATION " + MIGRATOR + ";"
                        + "REVOKE ALL ON SCHEMA party_schema FROM PUBLIC;"
                        + "GRANT USAGE ON SCHEMA party_schema TO " + APP + ";");
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), MIGRATOR, MIGRATOR_PASSWORD)
                .schemas("party_schema")
                .defaultSchema("party_schema")
                .createSchemas(false)
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }

    @BeforeEach
    void mockToken() {
        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), MIGRATOR, MIGRATOR_PASSWORD);
                var statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE party_schema.idempotency_record, "
                    + "party_schema.party_audit, party_schema.individual_profile, party_schema.party");
        } catch (Exception exception) {
            throw new IllegalStateException("Could not reset Party test data", exception);
        }
        when(jwtDecoder.decode("staff-token")).thenReturn(jwt(
                "staff-token", "staff-subject", List.of("create", "read", "update")));
    }

    @Test
    void createIsIdempotentAndReplayDoesNotCreateDuplicate() throws Exception {
        String body = """
                {"partyType":"INDIVIDUAL","displayName":"Test Customer","managingBranchId":"c31ed50c-efba-4baf-bba4-2c2e5f6b8f1b",
                 "individualProfile":{"firstName":"Test","lastName":"Customer","dateOfBirth":"1990-01-01","nationalIdHash":"0123456789abcdef0123456789abcdef"}}
                """;
        HttpHeaders headers = bearerHeaders("staff-token", "create-key");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> first = restTemplate.postForEntity("/v1/customers", request, String.class);
        ResponseEntity<String> replay = restTemplate.postForEntity("/v1/customers", request, String.class);

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(replay.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode firstBody = objectMapper.readTree(first.getBody());
        JsonNode replayBody = objectMapper.readTree(replay.getBody());
        assertThat(replayBody.get("partyId").asText()).isEqualTo(firstBody.get("partyId").asText());
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM party_schema.party", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM party_schema.party_audit", Integer.class)).isEqualTo(1);
    }

    @Test
    void idempotencyConflictAndStaleVersionAreRejected() throws Exception {
        HttpHeaders headers = bearerHeaders("staff-token", "conflict-key");
        headers.setContentType(MediaType.APPLICATION_JSON);
        String firstBody = """
                {"partyType":"ORGANIZATION","displayName":"Organization A","managingBranchId":null}
                """;
        ResponseEntity<String> first = restTemplate.postForEntity(
                "/v1/customers", new HttpEntity<>(firstBody, headers), String.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String differentBody = """
                {"partyType":"ORGANIZATION","displayName":"Organization B","managingBranchId":null}
                """;
        ResponseEntity<String> conflict = restTemplate.postForEntity(
                "/v1/customers", new HttpEntity<>(differentBody, headers), String.class);
        assertThat(conflict.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        UUID partyId = UUID.fromString(objectMapper.readTree(first.getBody()).get("partyId").asText());
        HttpHeaders patchHeaders = bearerHeaders("staff-token", null);
        patchHeaders.setContentType(MediaType.APPLICATION_JSON);
        String patch = """
                {"displayName":"Organization A Updated","managingBranchId":null,"version":0}
                """;
        ResponseEntity<String> updated = restTemplate.exchange(
                "/v1/customers/" + partyId, HttpMethod.PATCH,
                new HttpEntity<>(patch, patchHeaders), String.class);
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> stale = restTemplate.exchange(
                "/v1/customers/" + partyId, HttpMethod.PATCH,
                new HttpEntity<>(patch, patchHeaders), String.class);
        assertThat(stale.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void runtimeRoleCannotModifyPartyOrAccessOtherSchema() {
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM party_schema.party", Integer.class)).isZero();
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                "SELECT count(*) FROM party_schema.flyway_schema_history", Integer.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                "SELECT count(*) FROM other_service_schema.private_probe", Integer.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM party_schema.party", Integer.class)).isZero();
    }

    private HttpHeaders bearerHeaders(String token, String idempotencyKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-Correlation-Id", "test-correlation-" + UUID.randomUUID());
        if (idempotencyKey != null) {
            headers.set("Idempotency-Key", idempotencyKey);
        }
        return headers;
    }

    private static Jwt jwt(String token, String subject, List<String> permissions) {
        Instant now = Instant.now();
        return Jwt.withTokenValue(token)
                .header("alg", "RS256")
                .issuer(ISSUER)
                .subject(subject)
                .audience(List.of(AUDIENCE))
                .claim("party_permissions", permissions)
                .issuedAt(now.minusSeconds(30))
                .expiresAt(now.plusSeconds(300))
                .build();
    }
}
