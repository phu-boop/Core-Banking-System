package com.banking.platform.corporate.web;

import com.banking.platform.corporate.application.OrganizationService;
import com.banking.platform.corporate.domain.OrganizationResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(
            @RequestBody CreateOrganizationRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest servletRequest) {
        OrganizationResponse response = organizationService.create(
                request, jwt, (String) servletRequest.getAttribute(CorrelationIdFilter.CORRELATION_ID_ATTRIBUTE));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{organizationId}")
    public OrganizationResponse get(@PathVariable UUID organizationId,
                                    @AuthenticationPrincipal Jwt jwt) {
        return organizationService.get(organizationId, jwt);
    }
}
