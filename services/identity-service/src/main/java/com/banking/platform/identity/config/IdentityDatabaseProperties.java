package com.banking.platform.identity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identity.database")
public record IdentityDatabaseProperties(String url, String username, String password) {

    public IdentityDatabaseProperties {
        url = requireValue(url, "IDENTITY_DATASOURCE_URL");
        username = requireValue(username, "IDENTITY_DATASOURCE_USERNAME");
        password = requireValue(password, "IDENTITY_DATASOURCE_PASSWORD");
    }

    private static String requireValue(String value, String environmentVariable) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable "
                    + environmentVariable + " is missing");
        }
        return value;
    }

    @Override
    public String toString() {
        return "IdentityDatabaseProperties[url=<configured>, username=<configured>, password=<redacted>]";
    }
}
