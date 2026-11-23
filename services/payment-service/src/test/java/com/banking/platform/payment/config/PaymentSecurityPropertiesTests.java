package com.banking.platform.payment.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PaymentSecurityPropertiesTests {
    @Test
    void requiresIssuerJwkSetAndAudience() {
        assertMissing(() -> new PaymentSecurityProperties("", "https://idp.example/jwks", "payment"));
        assertMissing(() -> new PaymentSecurityProperties(
                "https://idp.example", " ", "payment"));
        assertMissing(() -> new PaymentSecurityProperties(
                "https://idp.example", "https://idp.example/jwks", ""));
    }

    private void assertMissing(Runnable construction) {
        IllegalStateException error = assertThrows(IllegalStateException.class, construction::run);
        assertTrue(error.getMessage().contains("is missing"));
    }
}
