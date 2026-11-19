package com.banking.platform.identity.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identity.security")
public record IdentitySecurityProperties(String issuerUri, String jwkSetUri, String audience) {

    public IdentitySecurityProperties {
        issuerUri = requireAbsoluteHttpsUri(issuerUri, "KEYCLOAK_ISSUER_URI");
        jwkSetUri = requireAbsoluteHttpsUri(jwkSetUri, "KEYCLOAK_JWK_SET_URI");
        audience = requireValue(audience, "IDENTITY_TOKEN_AUDIENCE");
    }

    private static String requireAbsoluteHttpsUri(String value, String environmentVariable) {
        String configuredValue = requireValue(value, environmentVariable);
        URI uri;
        try {
            uri = URI.create(configuredValue);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "Environment variable " + environmentVariable + " must be an absolute HTTP(S) URI");
        }
        if (!uri.isAbsolute()
                || uri.getHost() == null
                || !"https".equalsIgnoreCase(uri.getScheme())
                || uri.getUserInfo() != null) {
            throw new IllegalStateException(
                    "Environment variable " + environmentVariable + " must be an absolute HTTPS URI without credentials");
        }
        return configuredValue;
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
        return "IdentitySecurityProperties[issuerUri=<configured>, jwkSetUri=<configured>, audience=<configured>]";
    }
}
