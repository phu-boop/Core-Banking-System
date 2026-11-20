package com.banking.platform.corporate.web;

import com.banking.platform.corporate.application.ApprovalPolicyService;
import com.banking.platform.corporate.domain.ApprovalPolicyResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/v1/organizations/{organizationId}/approval-policies")
public class ApprovalPolicyController {
    private final ApprovalPolicyService service;

    public ApprovalPolicyController(ApprovalPolicyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApprovalPolicyResponse> create(
            @PathVariable UUID organizationId,
            @RequestBody(required = false) CreateApprovalPolicyRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest servletRequest) {
        ApprovalPolicyResponse policy = service.create(
                organizationId, request, jwt, correlationId(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(policy);
    }

    @GetMapping
    public List<ApprovalPolicyResponse> list(
            @PathVariable UUID organizationId,
            @RequestParam(required = false) String operationType,
            @AuthenticationPrincipal Jwt jwt) {
        return service.list(organizationId, operationType, jwt);
    }

    @PostMapping("/{policyId}/activate")
    public ApprovalPolicyResponse activate(
            @PathVariable UUID organizationId,
            @PathVariable UUID policyId,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest servletRequest) {
        return service.activate(organizationId, policyId, jwt, correlationId(servletRequest));
    }

    private String correlationId(HttpServletRequest request) {
        Object correlationId = request.getAttribute(CorrelationIdFilter.CORRELATION_ID_ATTRIBUTE);
        if (!(correlationId instanceof String value)) {
            throw new IllegalStateException("Correlation ID was not initialized by the request filter");
        }
        return value;
    }
}
