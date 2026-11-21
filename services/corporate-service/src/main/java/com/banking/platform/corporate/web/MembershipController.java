package com.banking.platform.corporate.web;

import com.banking.platform.corporate.application.MembershipService;
import com.banking.platform.corporate.domain.MandateResponse;
import com.banking.platform.corporate.domain.MembershipResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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
@RequestMapping("/v1/organizations/{organizationId}")
public class MembershipController {
    private final MembershipService service;

    public MembershipController(MembershipService service) {
        this.service = service;
    }

    @PostMapping("/members")
    public ResponseEntity<MembershipResponse> createMembership(
            @PathVariable UUID organizationId,
            @RequestBody(required = false) CreateMembershipRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest servletRequest) {
        MembershipResponse response = service.createMembership(
                organizationId, request, jwt, correlationId(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/members")
    public List<MembershipResponse> listMemberships(
            @PathVariable UUID organizationId,
            @AuthenticationPrincipal Jwt jwt) {
        return service.listMemberships(organizationId, jwt);
    }

    @PostMapping("/members/{membershipId}/mandates")
    public ResponseEntity<MandateResponse> createMandate(
            @PathVariable UUID organizationId,
            @PathVariable UUID membershipId,
            @RequestBody(required = false) CreateMandateRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest servletRequest) {
        MandateResponse response = service.createMandate(
                organizationId, membershipId, request, jwt,
                correlationId(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private String correlationId(HttpServletRequest request) {
        Object value = request.getAttribute(CorrelationIdFilter.CORRELATION_ID_ATTRIBUTE);
        return value instanceof String correlationId ? correlationId : "";
    }
}
