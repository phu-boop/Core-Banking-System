package com.banking.platform.servicetemplate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service-template.database")
public record DatabaseConnectionProperties(String url, String username, String password) {

    public DatabaseConnectionProperties {
        url = requireValue(url, "SPRING_DATASOURCE_URL");
        username = requireValue(username, "SPRING_DATASOURCE_USERNAME");
        password = requireValue(password, "SPRING_DATASOURCE_PASSWORD");
    }

    private static String requireValue(String value, String environmentVariable) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable " + environmentVariable + " is missing");
        }
        return value;
    }

    @Override
    public String toString() {
        return "DatabaseConnectionProperties[url=<configured>, username=<configured>, password=<redacted>]";
    }
}
