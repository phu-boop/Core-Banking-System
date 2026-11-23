package com.banking.platform.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.security")
public record PaymentSecurityProperties(String issuerUri, String jwkSetUri, String audience) {

    public PaymentSecurityProperties {
        issuerUri = requireValue(issuerUri, "KEYCLOAK_ISSUER_URI");
        jwkSetUri = requireValue(jwkSetUri, "KEYCLOAK_JWK_SET_URI");
        audience = requireValue(audience, "PAYMENT_TOKEN_AUDIENCE");
    }

    private static String requireValue(String value, String environmentVariable) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable "
                    + environmentVariable + " is missing");
        }
        return value;
    }
}
