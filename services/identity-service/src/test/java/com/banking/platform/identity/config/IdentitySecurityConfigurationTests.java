package com.banking.platform.identity.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

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
                        AUDIENCE,
                        false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("HTTPS");
        assertThatThrownBy(() -> new IdentitySecurityProperties(
                        ISSUER,
                        "https://idp.example/keys",
                        " ",
                        false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("IDENTITY_TOKEN_AUDIENCE");
    }

    @Test
    void acceptsHttpOnlyWhenExplicitlyAllowedAndRejectsInvalidOrCredentialedUris() {
        assertThat(new IdentitySecurityProperties(
                        "http://localhost:8180/realms/banking",
                        "http://localhost:8180/realms/banking/certs",
                        AUDIENCE,
                        true)
                .issuerUri())
                .isEqualTo("http://localhost:8180/realms/banking");
        assertThat(new IdentitySecurityProperties(
                        ISSUER, "https://idp.example/keys", AUDIENCE, true)
                .issuerUri())
                .isEqualTo(ISSUER);

        assertThat(new IdentitySecurityProperties(ISSUER, "https://idp.example/keys", AUDIENCE, false)
                        .issuerUri())
                .isEqualTo(ISSUER);
        assertThatThrownBy(() -> new IdentitySecurityProperties(
                        "/realms/banking", "https://idp.example/keys", AUDIENCE, true))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new IdentitySecurityProperties(
                        "http://idp.example/realms/banking",
                        "https://idp.example/keys",
                        AUDIENCE,
                        false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("HTTPS");
        assertThatThrownBy(() -> new IdentitySecurityProperties(
                        "https://user:password@idp.example/realms/banking",
                        "https://idp.example/keys",
                        AUDIENCE,
                        true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("without credentials");
    }

    @Test
    void jwtDecoderValidatesRsaSignature() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAKey signingKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID("identity-test-key")
                .build();
        var encoder = new NimbusJwtEncoder(
                new ImmutableJWKSet<SecurityContext>(new JWKSet(signingKey)));
        Instant now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject("subject")
                .audience(List.of(AUDIENCE))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .build();
        var header = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId("identity-test-key")
                .build();
        String signedToken = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic())
                .build();
        decoder.setJwtValidator(IdentitySecurityConfiguration.tokenValidator(properties()));
        assertThat(decoder.decode(signedToken).getSubject()).isEqualTo("subject");

        String[] parts = signedToken.split("\\.");
        String invalidSignature = (parts[2].startsWith("A") ? "B" : "A") + parts[2].substring(1);
        assertThatThrownBy(() -> decoder.decode(parts[0] + "." + parts[1] + "." + invalidSignature))
                .isInstanceOf(JwtException.class);
    }

    private static IdentitySecurityProperties properties() {
        return new IdentitySecurityProperties(ISSUER, "https://idp.example/keys", AUDIENCE, false);
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
