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
import com.banking.platform.identity.domain.ScopeType;
import com.banking.platform.identity.persistence.JdbcIdentityMappingRepository;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
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
    private com.banking.platform.identity.persistence.IdentityMappingRepository identityMappingRepository;

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
            insertGrant.setString(2, "account:read");
            insertGrant.setString(3, "SELF");
            insertGrant.setString(4, ACTIVE_PARTY_ID.toString());
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
    void configureMockTokens() throws SQLException {
        try (Connection migrator = migratorConnection(); Statement statement = migrator.createStatement()) {
            statement.execute("TRUNCATE TABLE identity_schema.identity_audit");
            statement.execute("TRUNCATE TABLE identity_schema.role_assignment");
        }
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
    void exposesOpenApiDocumentWithoutAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"openapi\"");
    }

    @Test
    void returnsOnlyTheProvisionedPrincipalAndItsScopedGrants() throws Exception {
        ResponseEntity<String> response = getPrincipal("active-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("userId").asText()).isEqualTo(ACTIVE_USER_ID.toString());
        assertThat(body.get("partyId").asText()).isEqualTo(ACTIVE_PARTY_ID.toString());
        assertThat(body.get("grants").get(0).get("permission").asText()).isEqualTo("account:read");
        assertThat(body.get("grants").get(0).get("scopeType").asText()).isEqualTo("SELF");
        assertThat(body.get("grants").get(0).get("scopeId").asText()).isEqualTo(ACTIVE_PARTY_ID.toString());
        assertThat(response.getBody()).doesNotContain("active-subject");
        assertThat(jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM identity_schema.identity_audit "
                                + "WHERE actor = ? AND action = 'IDENTITY_PRINCIPAL_RESOLVE' "
                                + "AND result = 'SUCCESS'",
                        Integer.class,
                        ACTIVE_USER_ID.toString()))
                .isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM identity_schema.identity_audit "
                                + "WHERE resource_id LIKE 'sha256:%'",
                        Integer.class))
                .isEqualTo(1);
    }

    @Test
    void deniesUnknownAndDisabledMappingsWithTheSameStatus() {
        assertThat(getPrincipal("unknown-token").getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(getPrincipal("disabled-token").getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM identity_schema.identity_audit "
                                + "WHERE actor = 'UNKNOWN' AND result = 'DENIED'",
                        Integer.class))
                .isEqualTo(2);
    }

    @Test
    void runtimeRoleCanReadOwnedMappingsButCannotWriteOrReadOtherSchemas() {
        assertThat(jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM identity_schema.user_identity", Integer.class))
                .isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM identity_schema.role_catalog", Integer.class))
                .isEqualTo(10);
        assertThatThrownBy(() -> jdbcTemplate.update(
                        "INSERT INTO identity_schema.role_catalog "
                                + "(role_code, display_name, category, description, status) "
                                + "VALUES ('RUNTIME_TEST', 'Runtime Test', 'TEST', 'Denied write', 'ACTIVE')"))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.update(
                        "UPDATE identity_schema.user_identity SET status = 'DISABLED' WHERE user_id = ?",
                        ACTIVE_USER_ID))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.update(
                        "UPDATE identity_schema.identity_audit SET result = 'CHANGED' "
                                + "WHERE actor = 'UNKNOWN'"))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM identity_schema.flyway_schema_history", Integer.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM " + OTHER_SCHEMA + ".private_probe", Integer.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
    }

    @Test
    void seededCatalogIsIdempotentAndDoesNotChangeLegacyGrantsOrAssignments() throws Exception {
        UUID assignmentId = UUID.randomUUID();
        try (Connection migrator = migratorConnection()) {
            insertRoleAssignment(migrator, assignmentId, ACTIVE_USER_ID,
                    "10000000-0000-4000-8000-000000000001",
                    "SELF", ACTIVE_PARTY_ID.toString(), "ACTIVE",
                    Instant.now().minusSeconds(10), null, null);

            int permissionsBefore = count(migrator, "identity_schema.permission_catalog");
            int rolesBefore = count(migrator, "identity_schema.role_catalog");
            int versionsBefore = count(migrator, "identity_schema.role_version");
            int rolePermissionsBefore = count(migrator, "identity_schema.role_version_permission");

            ClassPathResource seed = new ClassPathResource(
                    "db/migration/V4__seed_default_global_roles_and_permissions.sql");
            ScriptUtils.executeSqlScript(migrator, seed);
            ScriptUtils.executeSqlScript(migrator, seed);

            assertThat(count(migrator, "identity_schema.permission_catalog")).isEqualTo(permissionsBefore);
            assertThat(count(migrator, "identity_schema.role_catalog")).isEqualTo(rolesBefore);
            assertThat(count(migrator, "identity_schema.role_version")).isEqualTo(versionsBefore);
            assertThat(count(migrator, "identity_schema.role_version_permission"))
                    .isEqualTo(rolePermissionsBefore);
            assertThat(count(migrator, "identity_schema.authorization_grant")).isEqualTo(1);
            assertThat(jdbcTemplate.queryForObject(
                            "SELECT count(*) FROM identity_schema.role_assignment WHERE assignment_id = ?",
                            Integer.class, assignmentId))
                    .isEqualTo(1);
            assertThat(jdbcTemplate.queryForObject(
                            "SELECT assigned_by FROM identity_schema.role_assignment WHERE assignment_id = ?",
                            String.class, assignmentId))
                    .isEqualTo("TEST_OPERATOR");
        }
    }

    @Test
    void everySeededRoleHasAVersionAndEveryVersionPermissionExists() {
        assertThat(jdbcTemplate.queryForObject("""
                        SELECT count(*)
                        FROM identity_schema.role_catalog role_catalog
                        WHERE NOT EXISTS (
                            SELECT 1
                            FROM identity_schema.role_version role_version
                            WHERE role_version.role_code = role_catalog.role_code
                        )
                        """, Integer.class))
                .isZero();
        assertThat(jdbcTemplate.queryForObject("""
                        SELECT count(*)
                        FROM identity_schema.role_version_permission role_version_permission
                        LEFT JOIN identity_schema.permission_catalog permission_catalog
                          USING (permission_code)
                        WHERE permission_catalog.permission_code IS NULL
                        """, Integer.class))
                .isZero();
        assertThat(jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM identity_schema.role_assignment", Integer.class))
                .isZero();
    }

    @Test
    void overlappingAssignmentsRemainEffectiveUntilTheirOwnRevocationOrExpiry() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        UUID firstAssignment = UUID.randomUUID();
        UUID secondAssignment = UUID.randomUUID();
        UUID expiredAssignment = UUID.randomUUID();
        UUID revokedAssignment = UUID.randomUUID();

        try (Connection connection = migratorConnection()) {
            connection.setAutoCommit(false);
            JdbcTemplate transaction = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
            insertActiveUser(transaction, userId, partyId);
            Instant now = Instant.now();
            String retailVersion = "10000000-0000-4000-8000-000000000001";

            insertRoleAssignment(transaction, firstAssignment, userId, retailVersion,
                    "SELF", partyId.toString(), "ACTIVE", now.minusSeconds(60), null, null);
            insertRoleAssignment(transaction, secondAssignment, userId, retailVersion,
                    "SELF", partyId.toString(), "ACTIVE", now.minusSeconds(60), null, null);
            insertRoleAssignment(transaction, expiredAssignment, userId, retailVersion,
                    "SELF", partyId.toString(), "ACTIVE",
                    now.minusSeconds(120), now.minusSeconds(60), null);
            insertRoleAssignment(transaction, revokedAssignment, userId, retailVersion,
                    "SELF", partyId.toString(), "REVOKED", now.minusSeconds(60), null, now);

            assertThat(findGrants(transaction, userId))
                    .filteredOn(grant -> grant.permission().equals("account:read"))
                    .containsExactly(new com.banking.platform.identity.domain.ScopedGrant(
                            "account:read", ScopeType.SELF, partyId.toString()));

            transaction.update("""
                    UPDATE identity_schema.role_assignment
                    SET status = 'REVOKED', revoked_by = 'TEST_OPERATOR', revoked_at = CURRENT_TIMESTAMP
                    WHERE assignment_id = ?
                    """, firstAssignment);
            assertThat(findGrants(transaction, userId))
                    .anyMatch(grant -> grant.permission().equals("account:read"));

            transaction.update("""
                    UPDATE identity_schema.role_assignment
                    SET status = 'REVOKED', revoked_by = 'TEST_OPERATOR', revoked_at = CURRENT_TIMESTAMP
                    WHERE assignment_id = ?
                    """, secondAssignment);
            assertThat(findGrants(transaction, userId))
                    .noneMatch(grant -> grant.permission().equals("account:read"));
            connection.rollback();
        }
    }

    @Test
    void restrictedPermissionsAndLegacyRowsAreNotReturnedAsEffectiveGrants() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        try (Connection connection = migratorConnection()) {
            connection.setAutoCommit(false);
            JdbcTemplate transaction = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
            insertActiveUser(transaction, userId, partyId);
            transaction.update("""
                    INSERT INTO identity_schema.authorization_grant
                        (user_id, permission, scope_type, scope_id)
                    VALUES (?, 'ledger:post', 'BRANCH', ?)
                    """, userId, UUID.randomUUID().toString());
            insertRoleAssignment(transaction, UUID.randomUUID(), userId,
                    "10000000-0000-4000-8000-000000000001",
                    "SELF", partyId.toString(), "ACTIVE", Instant.now().minusSeconds(10), null, null);
            transaction.update("""
                    UPDATE identity_schema.permission_catalog
                    SET status = 'RESTRICTED'
                    WHERE permission_code = 'account:read'
                    """);

            assertThat(findGrants(transaction, userId)).isEmpty();
            assertThat(transaction.queryForObject("""
                    SELECT count(*) FROM identity_schema.authorization_grant
                    WHERE user_id = ? AND permission = 'ledger:post'
                    """, Integer.class, userId)).isEqualTo(1);
            connection.rollback();
        }
    }

    @Test
    void unsupportedScopesAndDraftRoleVersionsCannotBeAssigned() throws Exception {
        assertThatThrownBy(() -> ScopeType.valueOf("GLOBAL"))
                .isInstanceOf(IllegalArgumentException.class);

        try (Connection connection = migratorConnection()) {
            JdbcTemplate migrator = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
            assertThatThrownBy(() -> insertRoleAssignment(migrator, UUID.randomUUID(), ACTIVE_USER_ID,
                    "10000000-0000-4000-8000-000000000002",
                    "ORGANIZATION", UUID.randomUUID().toString(), "ACTIVE",
                    Instant.now(), null, null))
                    .isInstanceOf(org.springframework.dao.DataAccessException.class);

            assertThatThrownBy(() -> insertRoleAssignment(migrator, UUID.randomUUID(), ACTIVE_USER_ID,
                    "10000000-0000-4000-8000-000000000001",
                    "GLOBAL", UUID.randomUUID().toString(), "ACTIVE",
                    Instant.now(), null, null))
                    .isInstanceOf(org.springframework.dao.DataAccessException.class);
        }
    }

    @Test
    void publishedRoleVersionsAndTheirPermissionsAreImmutable() throws Exception {
        try (Connection connection = migratorConnection()) {
            JdbcTemplate migrator = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
            assertThatThrownBy(() -> migrator.update("""
                    UPDATE identity_schema.role_version
                    SET created_by = 'CHANGED'
                    WHERE role_version_id = '10000000-0000-4000-8000-000000000001'
                    """))
                    .isInstanceOf(org.springframework.dao.DataAccessException.class);
        }

        try (Connection connection = migratorConnection()) {
            JdbcTemplate migrator = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
            assertThatThrownBy(() -> migrator.update("""
                    INSERT INTO identity_schema.role_version_permission
                        (role_version_id, permission_code)
                    VALUES ('10000000-0000-4000-8000-000000000001', 'account:manage')
                    """))
                    .isInstanceOf(org.springframework.dao.DataAccessException.class);
        }
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

    private static int count(Connection connection, String table) throws SQLException {
        try (Statement statement = connection.createStatement();
                var result = statement.executeQuery("SELECT count(*) FROM " + table)) {
            result.next();
            return result.getInt(1);
        }
    }

    private static void insertActiveUser(JdbcTemplate jdbcTemplate, UUID userId, UUID partyId) {
        jdbcTemplate.update("""
                INSERT INTO identity_schema.user_identity
                    (user_id, issuer, subject, party_id, status)
                VALUES (?, ?, ?, ?, 'ACTIVE')
                """, userId, ISSUER, "role-test-" + userId, partyId);
    }

    private static void insertRoleAssignment(
            Connection connection,
            UUID assignmentId,
            UUID userId,
            String roleVersionId,
            String scopeType,
            String scopeId,
            String status,
            Instant validFrom,
            Instant expiresAt,
            Instant revokedAt) {
        insertRoleAssignment(
                new JdbcTemplate(new SingleConnectionDataSource(connection, true)),
                assignmentId, userId, roleVersionId, scopeType, scopeId, status,
                validFrom, expiresAt, revokedAt);
    }

    private static void insertRoleAssignment(
            JdbcTemplate jdbcTemplate,
            UUID assignmentId,
            UUID userId,
            String roleVersionId,
            String scopeType,
            String scopeId,
            String status,
            Instant validFrom,
            Instant expiresAt,
            Instant revokedAt) {
        jdbcTemplate.update("""
                INSERT INTO identity_schema.role_assignment
                    (assignment_id, user_id, role_version_id, scope_type, scope_id,
                     status, assigned_by, valid_from, expires_at, revoked_by, revoked_at)
                VALUES (?, ?, ?, ?, ?, ?, 'TEST_OPERATOR', ?, ?, ?, ?)
                """,
                assignmentId, userId, UUID.fromString(roleVersionId), scopeType, scopeId,
                status, java.sql.Timestamp.from(validFrom),
                expiresAt == null ? null : java.sql.Timestamp.from(expiresAt),
                revokedAt == null ? null : "TEST_OPERATOR",
                revokedAt == null ? null : java.sql.Timestamp.from(revokedAt));
    }

    private static List<com.banking.platform.identity.domain.ScopedGrant> findGrants(
            JdbcTemplate jdbcTemplate, UUID userId) {
        return new JdbcIdentityMappingRepository(jdbcTemplate).findGrantsByUserId(userId);
    }
}
