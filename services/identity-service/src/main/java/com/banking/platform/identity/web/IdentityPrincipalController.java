package com.banking.platform.identity.web;

import com.banking.platform.identity.application.IdentityPrincipalService;
import com.banking.platform.identity.domain.IdentityPrincipalResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdentityPrincipalController {

    private final IdentityPrincipalService identityPrincipalService;

    public IdentityPrincipalController(IdentityPrincipalService identityPrincipalService) {
        this.identityPrincipalService = identityPrincipalService;
    }

    @GetMapping("/v1/identity/me")
    public IdentityPrincipalResponse currentPrincipal(@AuthenticationPrincipal Jwt jwt) {
        return identityPrincipalService.resolve(jwt.getIssuer().toString(), jwt.getSubject());
    }
}
