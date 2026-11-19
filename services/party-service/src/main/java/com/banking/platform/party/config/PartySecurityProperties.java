package com.banking.platform.party.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "party.security")
public record PartySecurityProperties(String issuerUri, String jwkSetUri, String audience) {
    public PartySecurityProperties {
        issuerUri = requireHttps(issuerUri, "KEYCLOAK_ISSUER_URI");
        jwkSetUri = requireHttps(jwkSetUri, "KEYCLOAK_JWK_SET_URI");
        audience = requireValue(audience, "PARTY_TOKEN_AUDIENCE");
    }

    private static String requireHttps(String value, String name) {
        String configured = requireValue(value, name);
        URI uri = URI.create(configured);
        if (!uri.isAbsolute() || uri.getHost() == null
                || !"https".equalsIgnoreCase(uri.getScheme()) || uri.getUserInfo() != null) {
            throw new IllegalStateException(name + " must be an absolute HTTPS URI");
        }
        return configured;
    }

    private static String requireValue(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable " + name + " is missing");
        }
        return value;
    }

    @Override
    public String toString() {
        return "PartySecurityProperties[issuerUri=<configured>, jwkSetUri=<configured>, audience=<configured>]";
    }
}
