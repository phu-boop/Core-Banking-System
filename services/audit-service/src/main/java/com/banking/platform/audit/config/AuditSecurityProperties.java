package com.banking.platform.audit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit.security")
public record AuditSecurityProperties(String issuerUri, String jwkSetUri, String audience) {

    public AuditSecurityProperties {
        issuerUri = requireValue(issuerUri, "KEYCLOAK_ISSUER_URI");
        jwkSetUri = requireValue(jwkSetUri, "KEYCLOAK_JWK_SET_URI");
        audience = requireValue(audience, "AUDIT_TOKEN_AUDIENCE");
    }

    private static String requireValue(String value, String environmentVariable) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable "
                    + environmentVariable + " is missing");
        }
        return value;
    }
}
