package com.banking.platform.corporate.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "corporate.security")
public record CorporateSecurityProperties(
        String issuerUri,
        String jwkSetUri,
        String audience,
        String partyServiceBaseUrl,
        String partyClientId,
        String partyClientSecret,
        String tokenUri) {

    public CorporateSecurityProperties {
        issuerUri = requireHttps(issuerUri, "KEYCLOAK_ISSUER_URI");
        jwkSetUri = requireHttps(jwkSetUri, "KEYCLOAK_JWK_SET_URI");
        audience = requireValue(audience, "CORPORATE_TOKEN_AUDIENCE");
        partyServiceBaseUrl = requireHttpUrl(partyServiceBaseUrl, "PARTY_SERVICE_BASE_URL");
        partyClientId = requireValue(partyClientId, "CORPORATE_PARTY_CLIENT_ID");
        partyClientSecret = requireValue(partyClientSecret, "CORPORATE_PARTY_CLIENT_SECRET");
        tokenUri = requireHttps(tokenUri, "KEYCLOAK_TOKEN_URI");
    }

    private static String requireHttps(String value, String name) {
        String configured = requireValue(value, name);
        URI uri = parseUri(configured, name);
        if (!uri.isAbsolute() || uri.getHost() == null
                || !"https".equalsIgnoreCase(uri.getScheme()) || uri.getUserInfo() != null) {
            throw new IllegalStateException(name + " must be an absolute HTTPS URI");
        }
        return configured;
    }

    private static String requireHttpUrl(String value, String name) {
        String configured = requireValue(value, name);
        URI uri = parseUri(configured, name);
        if (!uri.isAbsolute() || uri.getHost() == null
                || !("https".equalsIgnoreCase(uri.getScheme())
                        || "http".equalsIgnoreCase(uri.getScheme()))
                || uri.getUserInfo() != null) {
            throw new IllegalStateException(name + " must be an absolute HTTP(S) URL without credentials");
        }
        return configured;
    }

    private static URI parseUri(String value, String name) {
        try {
            return URI.create(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(name + " must be a valid URI");
        }
    }

    private static String requireValue(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable " + name + " is missing");
        }
        return value;
    }

    @Override
    public String toString() {
        return "CorporateSecurityProperties[issuerUri=<configured>, jwkSetUri=<configured>, "
                + "audience=<configured>, partyServiceBaseUrl=<configured>, "
                + "partyClientId=<configured>, partyClientSecret=<redacted>, tokenUri=<configured>]";
    }
}
