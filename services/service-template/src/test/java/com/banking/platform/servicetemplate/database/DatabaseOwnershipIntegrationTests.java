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
                            + "' AND table_name IN ('flyway_schema_history', 'ownership_probe')");
            assertThat(ownedTables.next()).isTrue();
            assertThat(ownedTables.getLong(1)).isEqualTo(2);

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
