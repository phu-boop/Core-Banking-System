package com.banking.platform.transfer.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TransferSecurityPropertiesTests {
    @Test
    void requiresIssuerJwkSetAndAudience() {
        assertMissing(() -> new TransferSecurityProperties("", "https://idp.example/jwks", "transfer", false));
        assertMissing(() -> new TransferSecurityProperties(
                "https://idp.example", " ", "transfer", false));
        assertMissing(() -> new TransferSecurityProperties(
                "https://idp.example", "https://idp.example/jwks", "", false));
    }

    private void assertMissing(Runnable construction) {
        IllegalStateException error = assertThrows(IllegalStateException.class, construction::run);
        assertTrue(error.getMessage().contains("is missing"));
    }
}
