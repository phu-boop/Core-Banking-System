package com.banking.platform.identity.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
class IdentityPrincipalEndpointIntegrationTests {

    private static final String MIGRATOR_ROLE = "identity_service_migrator";
    private static final String APP_ROLE = "identity_service_app";
    private static final String OWNED_SCHEMA = "identity_schema";
    private static final String OTHER_SCHEMA = "other_service_schema";
    private static final String ISSUER = "https://idp.example/realms/banking";
    private static final String AUDIENCE = "identity-api";
    private static final String ADMIN_PASSWORD = "test-admin-password";
    private static final String MIGRATOR_PASSWORD = UUID.randomUUID().toString();
    private static final String APP_PASSWORD = UUID.randomUUID().toString();
    private static final UUID ACTIVE_USER_ID =
            UUID.fromString("94f30fc9-66ab-436f-a8f4-4f4aa97f0908");
    private static final UUID ACTIVE_PARTY_ID =
            UUID.fromString("c66d85a9-d728-4b9d-9e83-ae02cc7a2c74");
    private static final UUID DISABLED_USER_ID =
            UUID.fromString("6e1f52a5-26b8-4d1f-9189-934cf947aee5");

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17.12")
                    .withDatabaseName("identity_test")
                    .withUsername("postgres")
                    .withPassword(ADMIN_PASSWORD)
                    .withEnv("IDENTITY_SERVICE_MIGRATOR_PASSWORD", MIGRATOR_PASSWORD)
                    .withEnv("IDENTITY_SERVICE_APP_PASSWORD", APP_PASSWORD);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    @DynamicPropertySource
    static void configureApplication(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", () -> APP_ROLE);
        registry.add("spring.datasource.password", () -> APP_PASSWORD);
        registry.add("identity.database.url", POSTGRES::getJdbcUrl);
        registry.add("identity.database.username", () -> APP_ROLE);
        registry.add("identity.database.password", () -> APP_PASSWORD);
        registry.add("identity.security.issuer-uri", () -> ISSUER);
        registry.add("identity.security.jwk-set-uri", () -> "https://idp.example/realms/banking/certs");
        registry.add("identity.security.audience", () -> AUDIENCE);
    }

    @org.junit.jupiter.api.BeforeAll
    static void provisionDatabase() throws Exception {
        String baseDirectory = System.getProperty("identity.service.basedir");
        Path bootstrapPath = Path.of(baseDirectory, "database", "bootstrap", "identity-service-ownership.sql");
        POSTGRES.copyFileToContainer(
                MountableFile.forHostPath(bootstrapPath.toString()),
                "/tmp/identity-service-ownership.sql");
        var bootstrapResult = POSTGRES.execInContainer(
                "psql",
                "-v",
                "ON_ERROR_STOP=1",
                "--username",
                POSTGRES.getUsername(),
                "--dbname",
                POSTGRES.getDatabaseName(),
                "-f",
                "/tmp/identity-service-ownership.sql");
        assertThat(bootstrapResult.getExitCode())
                .withFailMessage("Identity ownership bootstrap failed: %s", bootstrapResult.getStderr())
                .isZero();

        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), MIGRATOR_ROLE, MIGRATOR_PASSWORD)
                .schemas(OWNED_SCHEMA)
                .defaultSchema(OWNED_SCHEMA)
                .createSchemas(false)
                .locations("classpath:db/migration")
                .load()
                .migrate();

        try (Connection admin = adminConnection(); Statement statement = admin.createStatement()) {
            statement.execute("CREATE SCHEMA " + OTHER_SCHEMA + " AUTHORIZATION postgres");
            statement.execute("CREATE TABLE " + OTHER_SCHEMA + ".private_probe (id UUID PRIMARY KEY)");
        }

        try (Connection migrator = migratorConnection(); var insertUser = migrator.prepareStatement(
                        "INSERT INTO identity_schema.user_identity "
                                + "(user_id, issuer, subject, party_id, status) VALUES (?, ?, ?, ?, ?)");
                var insertGrant = migrator.prepareStatement(
                        "INSERT INTO identity_schema.authorization_grant "
                                + "(user_id, permission, scope_type, scope_id) VALUES (?, ?, ?, ?)")) {
            insertUser.setObject(1, ACTIVE_USER_ID);
            insertUser.setString(2, ISSUER);
            insertUser.setString(3, "active-subject");
            insertUser.setObject(4, ACTIVE_PARTY_ID);
            insertUser.setString(5, "ACTIVE");
            insertUser.executeUpdate();

            insertGrant.setObject(1, ACTIVE_USER_ID);
            insertGrant.setString(2, "customer:read");
            insertGrant.setString(3, "SELF");
            insertGrant.setString(4, ACTIVE_USER_ID.toString());
            insertGrant.executeUpdate();

            insertUser.setObject(1, DISABLED_USER_ID);
            insertUser.setString(2, ISSUER);
            insertUser.setString(3, "disabled-subject");
            insertUser.setObject(4, null);
            insertUser.setString(5, "DISABLED");
            insertUser.executeUpdate();
        }
    }

    @BeforeEach
    void configureMockTokens() {
        when(jwtDecoder.decode("active-token")).thenReturn(jwt("active-token", "active-subject"));
        when(jwtDecoder.decode("disabled-token")).thenReturn(jwt("disabled-token", "disabled-subject"));
        when(jwtDecoder.decode("unknown-token")).thenReturn(jwt("unknown-token", "unknown-subject"));
    }

    @Test
    void requiresAnAuthenticatedBearerToken() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/identity/me", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void returnsOnlyTheProvisionedPrincipalAndItsScopedGrants() throws Exception {
        ResponseEntity<String> response = getPrincipal("active-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("userId").asText()).isEqualTo(ACTIVE_USER_ID.toString());
        assertThat(body.get("partyId").asText()).isEqualTo(ACTIVE_PARTY_ID.toString());
        assertThat(body.get("grants").get(0).get("permission").asText()).isEqualTo("customer:read");
        assertThat(body.get("grants").get(0).get("scopeType").asText()).isEqualTo("SELF");
        assertThat(response.getBody()).doesNotContain("active-subject");
    }

    @Test
    void deniesUnknownAndDisabledMappingsWithTheSameStatus() {
        assertThat(getPrincipal("unknown-token").getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(getPrincipal("disabled-token").getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void runtimeRoleCanReadOwnedMappingsButCannotWriteOrReadOtherSchemas() {
        assertThat(jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM identity_schema.user_identity", Integer.class))
                .isEqualTo(2);
        assertThatThrownBy(() -> jdbcTemplate.update(
                        "UPDATE identity_schema.user_identity SET status = 'DISABLED' WHERE user_id = ?",
                        ACTIVE_USER_ID))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM identity_schema.flyway_schema_history", Integer.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM " + OTHER_SCHEMA + ".private_probe", Integer.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
    }

    @Test
    void issuerSubjectPairIsUnique() throws Exception {
        try (Connection migrator = migratorConnection();
                var insert = migrator.prepareStatement(
                        "INSERT INTO identity_schema.user_identity "
                                + "(user_id, issuer, subject, status) VALUES (?, ?, ?, 'ACTIVE')")) {
            insert.setObject(1, UUID.randomUUID());
            insert.setString(2, ISSUER);
            insert.setString(3, "active-subject");

            assertThatThrownBy(insert::executeUpdate)
                    .isInstanceOf(PSQLException.class)
                    .satisfies(exception ->
                            assertThat(((SQLException) exception).getSQLState()).isEqualTo("23505"));
        }
    }

    private ResponseEntity<String> getPrincipal(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return restTemplate.exchange(
                "/v1/identity/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);
    }

    private static Jwt jwt(String token, String subject) {
        Instant now = Instant.now();
        return Jwt.withTokenValue(token)
                .header("alg", "RS256")
                .issuer(ISSUER)
                .subject(subject)
                .audience(List.of(AUDIENCE))
                .issuedAt(now.minusSeconds(30))
                .expiresAt(now.plusSeconds(300))
                .build();
    }

    private static Connection adminConnection() throws SQLException {
        return DriverManager.getConnection(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword());
    }

    private static Connection migratorConnection() throws SQLException {
        return DriverManager.getConnection(POSTGRES.getJdbcUrl(), MIGRATOR_ROLE, MIGRATOR_PASSWORD);
    }
}
