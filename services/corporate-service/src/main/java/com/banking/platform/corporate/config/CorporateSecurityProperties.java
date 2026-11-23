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
        String tokenUri,
        boolean allowHttp) {

    public CorporateSecurityProperties {
        issuerUri = requireUri(issuerUri, "KEYCLOAK_ISSUER_URI", allowHttp);
        jwkSetUri = requireUri(jwkSetUri, "KEYCLOAK_JWK_SET_URI", allowHttp);
        audience = requireValue(audience, "CORPORATE_TOKEN_AUDIENCE");
        partyServiceBaseUrl = requireHttpUrl(partyServiceBaseUrl, "PARTY_SERVICE_BASE_URL");
        partyClientId = requireValue(partyClientId, "CORPORATE_PARTY_CLIENT_ID");
        partyClientSecret = requireValue(partyClientSecret, "CORPORATE_PARTY_CLIENT_SECRET");
        tokenUri = requireUri(tokenUri, "KEYCLOAK_TOKEN_URI", allowHttp);
    }

    private static String requireUri(String value, String name, boolean allowHttp) {
        String configured = requireValue(value, name);
        URI uri = parseUri(configured, name);
        if (!uri.isAbsolute() || uri.getHost() == null
                || (!"https".equalsIgnoreCase(uri.getScheme())
                        && !(allowHttp && "http".equalsIgnoreCase(uri.getScheme())))
                || uri.getUserInfo() != null) {
            throw new IllegalStateException(name + " must be an absolute " + (allowHttp ? "HTTP(S)" : "HTTPS") + " URI without credentials");
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
