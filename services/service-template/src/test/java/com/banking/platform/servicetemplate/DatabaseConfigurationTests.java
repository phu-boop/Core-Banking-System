package com.banking.platform.servicetemplate;

import static org.assertj.core.api.Assertions.assertThat;

import com.banking.platform.servicetemplate.config.DatabaseConnectionProperties;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class DatabaseConfigurationTests {

    private static final Map<String, String> DATABASE_ENVIRONMENT = Map.of(
            "SPRING_DATASOURCE_URL", "not-a-jdbc-url",
            "SPRING_DATASOURCE_USERNAME", "test-user",
            "SPRING_DATASOURCE_PASSWORD", "test-password");

    @Test
    void databaseConnectionPropertiesRedactConfiguredValues() {
        var properties = new DatabaseConnectionProperties(
                "jdbc:postgresql://db.example/banking", "service-user", "test-password");

        assertThat(properties.toString())
                .doesNotContain("jdbc:postgresql://db.example/banking")
                .doesNotContain("service-user")
                .doesNotContain("test-password")
                .contains("<redacted>");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "SPRING_DATASOURCE_URL",
        "SPRING_DATASOURCE_USERNAME",
        "SPRING_DATASOURCE_PASSWORD"
    })
    void missingRequiredDatabaseEnvironmentVariableFailsFast(
            String missingVariable, CapturedOutput output) {
        var originalValues = new HashMap<String, String>();
        DATABASE_ENVIRONMENT.forEach((key, value) -> {
            if (!key.equals(missingVariable)) {
                originalValues.put(key, System.getProperty(key));
                System.setProperty(key, value);
            }
        });

        var application = new SpringApplication(ServiceTemplateApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setRegisterShutdownHook(false);

        Throwable failure;
        try {
            failure = catchStartupFailure(
                    application,
                    "--spring.main.banner-mode=off",
                    "--spring.main.web-application-type=none");
        } finally {
            originalValues.forEach((key, value) -> {
                if (value == null) {
                    System.clearProperty(key);
                } else {
                    System.setProperty(key, value);
                }
            });
        }

        assertThat(failure).hasStackTraceContaining(missingVariable);
        assertThat(failure).hasStackTraceContaining("is missing");
        assertThat(failure.getMessage()).doesNotContain("test-password");
        assertThat(output.getAll()).doesNotContain("test-password");
    }

    private static Throwable catchStartupFailure(SpringApplication application, String... arguments) {
        try {
            application.run(arguments).close();
            throw new AssertionError("Application unexpectedly started without " + arguments);
        } catch (RuntimeException exception) {
            return exception;
        }
    }
}
