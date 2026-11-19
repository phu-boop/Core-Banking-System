package com.banking.platform.branch.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
class BranchEndpointIntegrationTests {
    private static final String MIGRATOR = "branch_service_migrator";
    private static final String APP = "branch_service_app";
    private static final String MIGRATOR_PASSWORD = UUID.randomUUID().toString();
    private static final String APP_PASSWORD = UUID.randomUUID().toString();
    private static final String ISSUER = "https://idp.example/realms/banking";
    private static final String AUDIENCE = "branch-api";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17.12")
                    .withDatabaseName("branch_test")
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
        registry.add("branch.security.issuer-uri", () -> ISSUER);
        registry.add("branch.security.jwk-set-uri", () -> "https://idp.example/realms/banking/certs");
        registry.add("branch.security.audience", () -> AUDIENCE);
    }

    @BeforeAll
    static void provisionDatabase() throws Exception {
        POSTGRES.execInContainer("psql", "-v", "ON_ERROR_STOP=1", "-U", "postgres",
                "-d", "branch_test", "-c",
                "CREATE ROLE " + MIGRATOR + " LOGIN PASSWORD '" + MIGRATOR_PASSWORD + "';"
                        + "CREATE ROLE " + APP + " LOGIN PASSWORD '" + APP_PASSWORD + "';"
                        + "CREATE SCHEMA branch_schema AUTHORIZATION " + MIGRATOR + ";"
                        + "REVOKE ALL ON SCHEMA branch_schema FROM PUBLIC;"
                        + "GRANT USAGE ON SCHEMA branch_schema TO " + APP + ";");
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), MIGRATOR, MIGRATOR_PASSWORD)
                .schemas("branch_schema")
                .defaultSchema("branch_schema")
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
            statement.execute("TRUNCATE TABLE branch_schema.branch_audit, branch_schema.branch");
        } catch (Exception exception) {
            throw new IllegalStateException("Could not reset Branch test data", exception);
        }
        when(jwtDecoder.decode("admin-token"))
                .thenReturn(jwt("admin-token", "admin-subject", List.of("branch:admin", "branch:read"), List.of()));
        when(jwtDecoder.decode("reader-token"))
                .thenReturn(jwt("reader-token", "reader-subject", List.of("branch:read"), List.of()));
        when(jwtDecoder.decode("no-branch-access"))
                .thenReturn(jwt("no-branch-access", "no-access", List.of(), List.of()));
    }

    @Test
    void requiresAuthenticationAndBranchAdministrationForCreation() {
        ResponseEntity<String> anonymous = restTemplate.postForEntity(
                "/v1/branches", createBody("HCM-01", "Ho Chi Minh"), String.class);
        assertThat(anonymous.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        ResponseEntity<String> forbidden = post("reader-token", "HCM-01", "Ho Chi Minh");
        assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void createsBranchAndListsWithStatusFilterAndPageBounds() throws Exception {
        ResponseEntity<String> created = post("admin-token", " hcm-01 ", " Ho Chi Minh ");
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode branch = objectMapper.readTree(created.getBody());
        assertThat(branch.get("code").asText()).isEqualTo("HCM-01");
        assertThat(branch.get("name").asText()).isEqualTo("Ho Chi Minh");
        assertThat(branch.get("status").asText()).isEqualTo("ACTIVE");
        assertThat(created.getHeaders().getFirst("X-Correlation-Id")).isNotBlank();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM branch_schema.branch_audit", Integer.class)).isEqualTo(1);

        ResponseEntity<String> page = restTemplate.exchange(
                "/v1/branches?status=ACTIVE&page=0&size=10",
                HttpMethod.GET, new HttpEntity<>(headers("admin-token")), String.class);
        assertThat(page.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode result = objectMapper.readTree(page.getBody());
        assertThat(result.get("items")).hasSize(1);
        assertThat(result.get("totalElements").asLong()).isEqualTo(1);

        ResponseEntity<String> invalidPage = restTemplate.exchange(
                "/v1/branches?page=-1&size=10",
                HttpMethod.GET, new HttpEntity<>(headers("admin-token")), String.class);
        assertThat(invalidPage.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ResponseEntity<String> invalidStatus = restTemplate.exchange(
                "/v1/branches?status=UNKNOWN",
                HttpMethod.GET, new HttpEntity<>(headers("admin-token")), String.class);
        assertThat(invalidStatus.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void codeIsUniqueWithoutCaseSensitivityAndScopedReadHidesOtherBranches() throws Exception {
        ResponseEntity<String> first = post("admin-token", "HN-01", "Ha Noi");
        ResponseEntity<String> duplicate = post("admin-token", "hn-01", "Another Name");
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        UUID branchId = UUID.fromString(objectMapper.readTree(first.getBody()).get("branchId").asText());
        when(jwtDecoder.decode("scoped-token")).thenReturn(jwt(
                "scoped-token", "scoped-subject", List.of(), List.of(Map.of(
                        "permission", "branch:read",
                        "scopeType", "BRANCH",
                        "scopeId", branchId.toString()))));

        ResponseEntity<String> scopedGet = restTemplate.exchange(
                "/v1/branches/" + branchId, HttpMethod.GET,
                new HttpEntity<>(headers("scoped-token")), String.class);
        assertThat(scopedGet.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> hiddenGet = restTemplate.exchange(
                "/v1/branches/" + branchId, HttpMethod.GET,
                new HttpEntity<>(headers("no-branch-access")), String.class);
        assertThat(hiddenGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<String> scopedList = restTemplate.exchange(
                "/v1/branches?page=0&size=10", HttpMethod.GET,
                new HttpEntity<>(headers("scoped-token")), String.class);
        assertThat(scopedList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(objectMapper.readTree(scopedList.getBody()).get("totalElements").asLong()).isEqualTo(1);
    }

    @Test
    void runtimeRoleCanReadOwnedDataButCannotAccessMigrationOrOtherSchemas() {
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM branch_schema.branch", Integer.class)).isZero();
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                "SELECT count(*) FROM branch_schema.flyway_schema_history", Integer.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                "SELECT count(*) FROM other_service_schema.private_probe", Integer.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
    }

    private ResponseEntity<String> post(String token, String code, String name) {
        HttpHeaders headers = headers(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.postForEntity(
                "/v1/branches", new HttpEntity<>(createBody(code, name), headers), String.class);
    }

    private String createBody(String code, String name) {
        return "{\"code\":\"" + code + "\",\"name\":\"" + name + "\"}";
    }

    private HttpHeaders headers(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-Correlation-Id", "branch-test-" + UUID.randomUUID());
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
                .claim("branch_permissions", permissions)
                .claim("branch_grants", grants)
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
