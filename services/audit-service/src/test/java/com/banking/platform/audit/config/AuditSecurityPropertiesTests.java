package com.banking.platform.audit.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AuditSecurityPropertiesTests {
    @Test
    void requiresIssuerAndJwkSet() {
        assertMissing(() -> new AuditSecurityProperties("", "https://idp.example/jwks", "audit"));
        assertMissing(() -> new AuditSecurityProperties(
                "https://idp.example", " ", "audit"));
    }

    private void assertMissing(Runnable construction) {
        IllegalStateException error = assertThrows(IllegalStateException.class, construction::run);
        assertTrue(error.getMessage().contains("is missing"));
    }
}
