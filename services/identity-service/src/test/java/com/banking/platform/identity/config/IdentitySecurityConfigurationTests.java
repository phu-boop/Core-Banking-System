package com.banking.platform.identity.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class IdentitySecurityConfigurationTests {

    private static final String ISSUER = "https://idp.example/realms/banking";
    private static final String AUDIENCE = "identity-api";

    @Test
    void acceptsTokenWithExpectedIssuerAudienceSubjectAndValidityWindow() {
        var validator = IdentitySecurityConfiguration.tokenValidator(properties());

        assertThat(validator.validate(jwt(ISSUER, List.of(AUDIENCE), "subject", Instant.now().plusSeconds(60)))
                        .hasErrors())
                .isFalse();
    }

    @Test
    void rejectsWrongIssuerAudienceMissingSubjectAndExpiredTokens() {
        var validator = IdentitySecurityConfiguration.tokenValidator(properties());

        assertThat(validator.validate(jwt("https://attacker.example", List.of(AUDIENCE), "subject",
                        Instant.now().plusSeconds(60)))
                        .hasErrors())
                .isTrue();
        assertThat(validator.validate(jwt(ISSUER, List.of("another-service"), "subject",
                        Instant.now().plusSeconds(60)))
                        .hasErrors())
                .isTrue();
        assertThat(validator.validate(jwt(ISSUER, List.of(AUDIENCE), " ",
                        Instant.now().plusSeconds(60)))
                        .hasErrors())
                .isTrue();
        assertThat(validator.validate(jwt(ISSUER, List.of(AUDIENCE), "subject",
                        Instant.now().minusSeconds(60)))
                        .hasErrors())
                .isTrue();
    }

    @Test
    void requiresHttpsProviderEndpointsAndConfiguredAudience() {
        assertThatThrownBy(() -> new IdentitySecurityProperties(
                        "http://idp.example/realms/banking",
                        "https://idp.example/keys",
                        AUDIENCE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("HTTPS");
        assertThatThrownBy(() -> new IdentitySecurityProperties(
                        ISSUER,
                        "https://idp.example/keys",
                        " "))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("IDENTITY_TOKEN_AUDIENCE");
    }

    private static IdentitySecurityProperties properties() {
        return new IdentitySecurityProperties(ISSUER, "https://idp.example/keys", AUDIENCE);
    }

    private static Jwt jwt(String issuer, List<String> audiences, String subject, Instant expiresAt) {
        Instant now = Instant.now();
        Instant issuedAt = expiresAt.isBefore(now) ? expiresAt.minusSeconds(120) : now.minusSeconds(60);
        return Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .issuer(issuer)
                .subject(subject)
                .audience(audiences)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();
    }
}
