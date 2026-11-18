package com.banking.platform.servicetemplate.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.testcontainers.utility.MountableFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class DatabaseOwnershipIntegrationTests {

    private static final String MIGRATOR_ROLE = "service_template_migrator";
    private static final String APP_ROLE = "service_template_app";
    private static final String OWNED_SCHEMA = "service_template_schema";
    private static final String OTHER_SCHEMA = "other_service_schema";
    private static final String ADMIN_PASSWORD = "test-admin-password";
    private static final String MIGRATOR_PASSWORD = UUID.randomUUID().toString();
    private static final String APP_PASSWORD = UUID.randomUUID().toString();

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17.12")
                    .withDatabaseName("banking_test")
                    .withUsername("postgres")
                    .withPassword(ADMIN_PASSWORD)
                    .withEnv("SERVICE_TEMPLATE_MIGRATOR_PASSWORD", MIGRATOR_PASSWORD)
                    .withEnv("SERVICE_TEMPLATE_APP_PASSWORD", APP_PASSWORD);

    @BeforeAll
    static void provisionDatabaseAndRunFlyway() throws Exception {
        POSTGRES.start();
        String baseDirectory = System.getProperty("service.template.basedir");
        Path bootstrapPath = Path.of(
                baseDirectory,
                "database",
                "bootstrap",
                "service-template-ownership.sql");
        POSTGRES.copyFileToContainer(
                MountableFile.forHostPath(bootstrapPath.toString()),
                "/tmp/service-template-ownership.sql");
        var bootstrapResult = POSTGRES.execInContainer(
                "psql",
                "-v",
                "ON_ERROR_STOP=1",
                "--username",
                POSTGRES.getUsername(),
                "--dbname",
                POSTGRES.getDatabaseName(),
                "-f",
                "/tmp/service-template-ownership.sql");
        assertThat(bootstrapResult.getExitCode())
                .withFailMessage("Database ownership bootstrap failed: %s", bootstrapResult.getStderr())
                .isZero();

        try (Connection admin = adminConnection();
                Statement statement = admin.createStatement()) {
            statement.execute("CREATE SCHEMA " + OTHER_SCHEMA + " AUTHORIZATION postgres");
            statement.execute("CREATE TABLE " + OTHER_SCHEMA + ".private_probe (id BIGINT PRIMARY KEY)");
        }

        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), MIGRATOR_ROLE, MIGRATOR_PASSWORD)
                .schemas(OWNED_SCHEMA)
                .defaultSchema(OWNED_SCHEMA)
                .createSchemas(false)
                .locations("classpath:db/ownership-migrations")
                .load()
                .migrate();
    }

    @Test
    void runtimeRoleCanUseOnlyExplicitlyGrantedObjectsInItsSchema() throws Exception {
        try (Connection app = appConnection(); Statement statement = app.createStatement()) {
            statement.executeUpdate("INSERT INTO " + OWNED_SCHEMA + ".ownership_probe (value) VALUES ('owned')");
            var result = statement.executeQuery(
                    "SELECT value FROM " + OWNED_SCHEMA + ".ownership_probe");
            assertThat(result.next()).isTrue();
            assertThat(result.getString(1)).isEqualTo("owned");

            assertPermissionDenied(statement, "SELECT * FROM " + OTHER_SCHEMA + ".private_probe");
            assertPermissionDenied(statement, "CREATE TABLE " + OWNED_SCHEMA + ".app_created (id BIGINT)");
            assertPermissionDenied(statement, "SELECT * FROM " + OWNED_SCHEMA + ".flyway_schema_history");
        }
    }

    @Test
    void migrationRoleCannotCreateCrossSchemaForeignKeys() throws Exception {
        try (Connection migrator = migratorConnection(); Statement statement = migrator.createStatement()) {
            assertPermissionDenied(
                    statement,
                    "CREATE TABLE " + OWNED_SCHEMA
                            + ".cross_schema_reference (other_id BIGINT REFERENCES "
                            + OTHER_SCHEMA + ".private_probe(id))");
        }
    }

    @Test
    void flywayHistoryAndMigrationsAreOwnedByTheServiceSchema() throws Exception {
        try (Connection admin = adminConnection(); Statement statement = admin.createStatement()) {
            var ownedTables = statement.executeQuery(
                    "SELECT count(*) FROM information_schema.tables "
                            + "WHERE table_schema = '" + OWNED_SCHEMA
                            + "' AND table_name IN ('flyway_schema_history', 'ownership_probe', "
                            + "'reliability_probe', 'outbox_event', 'processed_event')");
            assertThat(ownedTables.next()).isTrue();
            assertThat(ownedTables.getLong(1)).isEqualTo(5);

            var result = statement.executeQuery(
                    "SELECT count(*) FROM pg_constraint constraint_row "
                            + "JOIN pg_class source_table ON source_table.oid = constraint_row.conrelid "
                            + "JOIN pg_namespace source_schema ON source_schema.oid = source_table.relnamespace "
                            + "JOIN pg_class target_table ON target_table.oid = constraint_row.confrelid "
                            + "JOIN pg_namespace target_schema ON target_schema.oid = target_table.relnamespace "
                            + "WHERE constraint_row.contype = 'f' "
                            + "AND source_schema.nspname = '" + OWNED_SCHEMA + "' "
                            + "AND target_schema.nspname <> source_schema.nspname");
            assertThat(result.next()).isTrue();
            assertThat(result.getLong(1)).isZero();
        }
    }

    @Test
    void outboxAndDomainChangeCommitOrRollbackTogether() throws Exception {
        UUID committedEvent = UUID.randomUUID();
        try (Connection app = appConnection()) {
            app.setAutoCommit(false);
            try (var insertProbe = app.prepareStatement(
                    "INSERT INTO " + OWNED_SCHEMA
                            + ".reliability_probe (event_id, value) VALUES (?, 'committed')");
                    var insertOutbox = app.prepareStatement(
                            "INSERT INTO " + OWNED_SCHEMA + ".outbox_event "
                                    + "(event_id, event_type, aggregate_type, aggregate_id, "
                                    + "correlation_id, payload, occurred_at) "
                                    + "VALUES (?, 'ReliabilityProbe', 'ReliabilityProbe', ?, "
                                    + "'correlation-1', '{}'::jsonb, CURRENT_TIMESTAMP)")) {
                insertProbe.setObject(1, committedEvent);
                insertProbe.executeUpdate();
                insertOutbox.setObject(1, committedEvent);
                insertOutbox.setString(2, committedEvent.toString());
                insertOutbox.executeUpdate();
                app.commit();
            }
        }

        UUID rolledBackEvent = UUID.randomUUID();
        try (Connection app = appConnection()) {
            app.setAutoCommit(false);
            try (var insertProbe = app.prepareStatement(
                    "INSERT INTO " + OWNED_SCHEMA
                            + ".reliability_probe (event_id, value) VALUES (?, 'rolled-back')");
                    var insertOutbox = app.prepareStatement(
                            "INSERT INTO " + OWNED_SCHEMA + ".outbox_event "
                                    + "(event_id, event_type, aggregate_type, aggregate_id, "
                                    + "payload, occurred_at) "
                                    + "VALUES (?, 'ReliabilityProbe', 'ReliabilityProbe', ?, "
                                    + "'{}'::jsonb, CURRENT_TIMESTAMP)")) {
                insertProbe.setObject(1, rolledBackEvent);
                insertProbe.executeUpdate();
                insertOutbox.setObject(1, rolledBackEvent);
                insertOutbox.setString(2, rolledBackEvent.toString());
                insertOutbox.executeUpdate();
                app.rollback();
            }
        }

        try (Connection app = appConnection(); Statement statement = app.createStatement()) {
            var committed = statement.executeQuery(
                    "SELECT count(*) FROM " + OWNED_SCHEMA
                            + ".reliability_probe WHERE event_id = '" + committedEvent + "'");
            assertThat(committed.next()).isTrue();
            assertThat(committed.getInt(1)).isOne();

            var committedOutbox = statement.executeQuery(
                    "SELECT count(*) FROM " + OWNED_SCHEMA
                            + ".outbox_event WHERE event_id = '" + committedEvent + "'");
            assertThat(committedOutbox.next()).isTrue();
            assertThat(committedOutbox.getInt(1)).isOne();

            var rolledBackProbe = statement.executeQuery(
                    "SELECT count(*) FROM " + OWNED_SCHEMA
                            + ".reliability_probe WHERE event_id = '" + rolledBackEvent + "'");
            assertThat(rolledBackProbe.next()).isTrue();
            assertThat(rolledBackProbe.getInt(1)).isZero();

            var rolledBackOutbox = statement.executeQuery(
                    "SELECT count(*) FROM " + OWNED_SCHEMA
                            + ".outbox_event WHERE event_id = '" + rolledBackEvent + "'");
            assertThat(rolledBackOutbox.next()).isTrue();
            assertThat(rolledBackOutbox.getInt(1)).isZero();
        }
    }

    @Test
    void processedEventMarkerPreventsDuplicateConsumerSideEffect() throws Exception {
        UUID eventId = UUID.randomUUID();
        try (Connection app = appConnection()) {
            app.setAutoCommit(false);
            try (var marker = app.prepareStatement(
                    "INSERT INTO " + OWNED_SCHEMA + ".processed_event "
                            + "(event_id, consumer_name, event_type) VALUES (?, 'reliability-test', "
                            + "'ReliabilityProbe')");
                    var sideEffect = app.prepareStatement(
                            "INSERT INTO " + OWNED_SCHEMA
                                    + ".reliability_probe (event_id, value) VALUES (?, 'applied-once')")) {
                marker.setObject(1, eventId);
                sideEffect.setObject(1, eventId);
                sideEffect.executeUpdate();
                marker.executeUpdate();
                app.commit();
            }
        }

        try (Connection app = appConnection()) {
            app.setAutoCommit(false);
            try (var marker = app.prepareStatement(
                    "INSERT INTO " + OWNED_SCHEMA + ".processed_event "
                            + "(event_id, consumer_name, event_type) VALUES (?, 'reliability-test', "
                            + "'ReliabilityProbe')");
                    var duplicateMarker = app.prepareStatement(
                            "SELECT count(*) FROM " + OWNED_SCHEMA
                                    + ".processed_event "
                                    + "WHERE event_id = ? AND consumer_name = 'reliability-test'");
                    var sideEffects = app.prepareStatement(
                            "SELECT count(*) FROM " + OWNED_SCHEMA
                                    + ".reliability_probe WHERE event_id = ?")) {
                marker.setObject(1, eventId);
                assertThatThrownBy(marker::executeUpdate)
                        .isInstanceOf(PSQLException.class)
                        .satisfies(exception ->
                                assertThat(((SQLException) exception).getSQLState())
                                        .isEqualTo("23505"));
                app.rollback();

                duplicateMarker.setObject(1, eventId);
                try (var result = duplicateMarker.executeQuery()) {
                    assertThat(result.next()).isTrue();
                    assertThat(result.getInt(1)).isOne();
                }
                sideEffects.setObject(1, eventId);
                try (var result = sideEffects.executeQuery()) {
                    assertThat(result.next()).isTrue();
                    assertThat(result.getInt(1)).isOne();
                }
            }
        }
    }

    private static void assertPermissionDenied(Statement statement, String sql) {
        assertThatThrownBy(() -> statement.execute(sql))
                .isInstanceOf(PSQLException.class)
                .satisfies(exception ->
                        assertThat(((SQLException) exception).getSQLState()).isEqualTo("42501"));
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

    private static Connection appConnection() throws SQLException {
        return DriverManager.getConnection(POSTGRES.getJdbcUrl(), APP_ROLE, APP_PASSWORD);
    }
}
