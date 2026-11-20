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
            statement.execute("TRUNCATE TABLE corporate_schema.approval_history, "
                    + "corporate_schema.approval_step, corporate_schema.approval_request, "
                    + "corporate_schema.approval_policy_level, corporate_schema.approval_policy, "
                    + "corporate_schema.organization_audit, corporate_schema.organization");
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

    @Test
    void createsAndListsOrganizationScopedDraftPolicyWithOrderedLevels() throws Exception {
        UUID organizationId = createOrganization();
        String token = policyAdminToken(organizationId);
        ResponseEntity<String> created = postPolicy(token, organizationId, """
                {
                  "operationType":"INTERNAL_TRANSFER",
                  "levels":[
                    {"roleCode":"CHECKER_1","mandateScope":"PAYMENT"},
                    {"roleCode":"CHECKER_2","mandateScope":"PAYMENT"}
                  ]
                }
                """);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode policy = objectMapper.readTree(created.getBody());
        assertThat(policy.get("organizationId").asText()).isEqualTo(organizationId.toString());
        assertThat(policy.get("operationType").asText()).isEqualTo("INTERNAL_TRANSFER");
        assertThat(policy.get("version").asInt()).isEqualTo(1);
        assertThat(policy.get("status").asText()).isEqualTo("DRAFT");
        assertThat(policy.get("levels").get(0).get("stepNumber").asInt()).isEqualTo(1);
        assertThat(policy.get("levels").get(1).get("stepNumber").asInt()).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM corporate_schema.approval_policy_level", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM corporate_schema.organization_audit "
                        + "WHERE action = 'APPROVAL_POLICY_CREATE'", Integer.class)).isEqualTo(1);

        ResponseEntity<String> listed = restTemplate.exchange(
                "/v1/organizations/" + organizationId + "/approval-policies?operationType=INTERNAL_TRANSFER",
                HttpMethod.GET, new HttpEntity<>(headers(token)), String.class);
        assertThat(listed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(objectMapper.readTree(listed.getBody()).size()).isEqualTo(1);
    }

    @Test
    void activatingReplacementPolicyLeavesOnlyOneActiveVersion() throws Exception {
        UUID organizationId = createOrganization();
        String token = policyAdminToken(organizationId);
        UUID firstPolicyId = createPolicy(token, organizationId, "INTERNAL_TRANSFER");
        assertThat(activatePolicy(token, organizationId, firstPolicyId).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        UUID secondPolicyId = createPolicy(token, organizationId, "INTERNAL_TRANSFER");

        ResponseEntity<String> activated = activatePolicy(token, organizationId, secondPolicyId);
        assertThat(activated.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jdbcTemplate.queryForObject("""
                SELECT count(*) FROM corporate_schema.approval_policy
                WHERE organization_id = ? AND operation_type = 'INTERNAL_TRANSFER' AND status = 'ACTIVE'
                """, Integer.class, organizationId)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("""
                SELECT status FROM corporate_schema.approval_policy WHERE policy_id = ?
                """, String.class, firstPolicyId)).isEqualTo("INACTIVE");
        assertThat(jdbcTemplate.queryForObject("""
                SELECT policy_version FROM corporate_schema.approval_policy WHERE policy_id = ?
                """, Integer.class, secondPolicyId)).isEqualTo(2);
        assertThat(activatePolicy(token, organizationId, firstPolicyId).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(jdbcTemplate.queryForObject("""
                SELECT count(*) FROM corporate_schema.approval_policy
                WHERE organization_id = ? AND operation_type = 'INTERNAL_TRANSFER' AND status = 'ACTIVE'
                """, Integer.class, organizationId)).isEqualTo(1);
    }

    @Test
    void policyManagementRequiresOrganizationScopedPermission() throws Exception {
        UUID organizationId = createOrganization();
        ResponseEntity<String> denied = postPolicy("no-access-token", organizationId, """
                {"operationType":"INTERNAL_TRANSFER","levels":[
                  {"roleCode":"CHECKER","mandateScope":"PAYMENT"}
                ]}
                """);
        assertThat(denied.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        String wrongOrganizationToken = policyAdminToken(UUID.randomUUID());
        ResponseEntity<String> wrongScope = restTemplate.exchange(
                "/v1/organizations/" + organizationId + "/approval-policies",
                HttpMethod.GET, new HttpEntity<>(headers(wrongOrganizationToken)), String.class);
        assertThat(wrongScope.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void rejectsPolicyWithoutOrderedLevels() throws Exception {
        UUID organizationId = createOrganization();
        ResponseEntity<String> response = postPolicy(policyAdminToken(organizationId), organizationId, """
                {"operationType":"INTERNAL_TRANSFER","levels":[]}
                """);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM corporate_schema.approval_policy", Integer.class)).isZero();
    }

    @Test
    void generatedCorrelationIdIsPersistedForPolicyAudit() throws Exception {
        UUID organizationId = createOrganization();
        String token = policyAdminToken(organizationId);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> created = restTemplate.postForEntity(
                "/v1/organizations/" + organizationId + "/approval-policies",
                new HttpEntity<>("""
                        {"operationType":"INTERNAL_TRANSFER","levels":[
                          {"roleCode":"CHECKER","mandateScope":"PAYMENT"}
                        ]}
                        """, headers), String.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String correlationId = created.getHeaders().getFirst("X-Correlation-Id");
        assertThat(correlationId).isNotBlank();
        assertThat(jdbcTemplate.queryForObject("""
                SELECT correlation_id FROM corporate_schema.organization_audit
                WHERE action = 'APPROVAL_POLICY_CREATE'
                """, String.class)).isEqualTo(correlationId);
    }

    @Test
    void runtimeRoleCannotMutateImmutablePolicyVersionOrLevels() throws Exception {
        UUID organizationId = createOrganization();
        UUID policyId = createPolicy(policyAdminToken(organizationId), organizationId, "INTERNAL_TRANSFER");

        assertThatThrownBy(() -> jdbcTemplate.update("""
                UPDATE corporate_schema.approval_policy
                SET policy_version = 99
                WHERE policy_id = ?
                """, policyId)).isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.update("""
                UPDATE corporate_schema.approval_policy_level
                SET role_code = 'UNAUTHORIZED'
                WHERE policy_id = ?
                """, policyId)).isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThat(jdbcTemplate.queryForObject("""
                SELECT policy_version FROM corporate_schema.approval_policy WHERE policy_id = ?
                """, Integer.class, policyId)).isEqualTo(1);
    }

    @Test
    void requestSchemaCannotBindPolicyFromAnotherOrganizationOrOperation() throws Exception {
        UUID organizationId = createOrganization();
        UUID otherOrganizationId = createOrganization();
        UUID policyId = createPolicy(policyAdminToken(organizationId), organizationId, "INTERNAL_TRANSFER");

        assertThatThrownBy(() -> insertApprovalRequest(
                organizationId, policyId, "DIFFERENT_OPERATION"))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThatThrownBy(() -> insertApprovalRequest(
                otherOrganizationId, policyId, "INTERNAL_TRANSFER"))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
    }

    private void insertApprovalRequest(UUID organizationId, UUID policyId, String operationType) {
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.approval_request
                    (approval_request_id, organization_id, policy_id, operation_type,
                     operation_reference, maker_subject, status)
                VALUES (?, ?, ?, ?, 'owner-reference', 'test-maker', 'PENDING')
                """, UUID.randomUUID(), organizationId, policyId, operationType);
    }

    private UUID createOrganization() {
        UUID organizationId = UUID.randomUUID();
        jdbcTemplate.update("""
                INSERT INTO corporate_schema.organization
                    (organization_id, party_id, status, created_at, updated_at)
                VALUES (?, ?, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """, organizationId, UUID.randomUUID());
        return organizationId;
    }

    private String policyAdminToken(UUID organizationId) {
        String token = "policy-admin-" + organizationId;
        when(jwtDecoder.decode(token)).thenReturn(jwt(
                token, "policy-admin-subject", List.of(), List.of(Map.of(
                        "permission", "approval-policy:manage",
                        "scopeType", "ORGANIZATION",
                        "scopeId", organizationId.toString()))));
        return token;
    }

    private UUID createPolicy(String token, UUID organizationId, String operationType) throws Exception {
        ResponseEntity<String> created = postPolicy(token, organizationId, """
                {"operationType":"%s","levels":[
                  {"roleCode":"CHECKER","mandateScope":"PAYMENT"}
                ]}
                """.formatted(operationType));
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return UUID.fromString(objectMapper.readTree(created.getBody()).get("policyId").asText());
    }

    private ResponseEntity<String> postPolicy(String token, UUID organizationId, String body) {
        HttpHeaders headers = headers(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.postForEntity(
                "/v1/organizations/" + organizationId + "/approval-policies",
                new HttpEntity<>(body, headers), String.class);
    }

    private ResponseEntity<String> activatePolicy(String token, UUID organizationId, UUID policyId) {
        return restTemplate.postForEntity(
                "/v1/organizations/" + organizationId + "/approval-policies/" + policyId + "/activate",
                new HttpEntity<>(null, headers(token)), String.class);
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
