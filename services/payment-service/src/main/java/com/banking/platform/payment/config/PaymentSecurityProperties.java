package com.banking.platform.payment.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.security")
public record PaymentSecurityProperties(
        String issuerUri,
        String jwkSetUri,
        String audience,
        boolean allowHttp) {

    public PaymentSecurityProperties {
        issuerUri = requireUri(issuerUri, "KEYCLOAK_ISSUER_URI", allowHttp);
        jwkSetUri = requireUri(jwkSetUri, "KEYCLOAK_JWK_SET_URI", allowHttp);
        audience = requireValue(audience, "PAYMENT_TOKEN_AUDIENCE");
    }

    private static String requireUri(String value, String name, boolean allowHttp) {
        String configured = requireValue(value, name);
        URI uri;
        try {
            uri = URI.create(configured);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(name + " must be an absolute HTTP(S) URI");
        }
        if (!uri.isAbsolute() || uri.getHost() == null
                || (!"https".equalsIgnoreCase(uri.getScheme())
                        && !(allowHttp && "http".equalsIgnoreCase(uri.getScheme())))
                || uri.getUserInfo() != null) {
            throw new IllegalStateException(name + " must be an absolute " + (allowHttp ? "HTTP(S)" : "HTTPS") + " URI without credentials");
        }
        return configured;
    }

    private static String requireValue(String value, String environmentVariable) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable "
                    + environmentVariable + " is missing");
        }
        return value;
    }
}
