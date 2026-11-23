package com.banking.platform.reporting.application;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

class ReportingAuthorizationServiceTests {
    private final ReportingAuthorizationService authorization =
            new ReportingAuthorizationService();

    @Test
    void deniesMissingReportPermissionAndOutOfScopeOrganization() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("reporter")
                .claims(claims -> claims.putAll(Map.of(
                        "report_permissions", List.of("report:account:read"),
                        "organizations", List.of("org-1"),
                        "accounts", List.of("acc-1"),
                        "branches", List.of("branch-1"))))
                .build();

        assertThrows(AccessDeniedException.class,
                () -> authorization.requireRead(jwt, "audit"));
        assertThrows(AccessDeniedException.class,
                () -> authorization.requireScope(jwt, "org-2", null, null));
    }

    @Test
    void permitsAllowedReportAndScopes() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("reporter")
                .claims(claims -> claims.putAll(Map.of(
                        "report_permissions", List.of("report:account:read"),
                        "organizations", List.of("org-1"),
                        "accounts", List.of("acc-1"),
                        "branches", List.of("branch-1"))))
                .build();

        authorization.requireRead(jwt, "account");
        authorization.requireScope(jwt, "org-1", "acc-1", "branch-1");
    }
}
