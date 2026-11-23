package com.banking.platform.account.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "account.identity")
public record IdentityClientProperties(String baseUrl, boolean allowHttp) {

    public IdentityClientProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Required environment variable IDENTITY_SERVICE_BASE_URL is missing");
        }

        URI uri;
        try {
            uri = URI.create(baseUrl);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("IDENTITY_SERVICE_BASE_URL must be an absolute HTTP(S) URI");
        }

        boolean localHttp = allowHttp
                && "http".equalsIgnoreCase(uri.getScheme())
                && ("localhost".equalsIgnoreCase(uri.getHost())
                        || "127.0.0.1".equals(uri.getHost())
                        || "[::1]".equals(uri.getHost()));
        if (!uri.isAbsolute()
                || uri.getHost() == null
                || (!"https".equalsIgnoreCase(uri.getScheme()) && !localHttp)
                || uri.getUserInfo() != null
                || uri.getQuery() != null
                || uri.getFragment() != null) {
            throw new IllegalStateException(
                    "IDENTITY_SERVICE_BASE_URL must be an absolute HTTPS URI without credentials");
        }

        baseUrl = uri.toString().replaceAll("/+$", "");
    }
}
