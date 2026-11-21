package com.banking.platform.corporate.web;

import com.banking.platform.corporate.application.ApprovalWorkflowService;
import com.banking.platform.corporate.domain.ApprovalDecisionResponse;
import com.banking.platform.corporate.domain.ApprovalHistoryResponse;
import com.banking.platform.corporate.domain.ApprovalRequestResponse;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/organizations/{organizationId}/approval-requests")
public class ApprovalDecisionController {
    private final ApprovalWorkflowService service;

    public ApprovalDecisionController(ApprovalWorkflowService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApprovalRequestResponse> create(
            @PathVariable UUID organizationId,
            @RequestBody(required = false) CreateApprovalRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest servletRequest) {
        ApprovalRequestResponse response = service.create(
                organizationId, request, jwt, correlationId(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{approvalId}")
    public ApprovalRequestResponse get(
            @PathVariable UUID organizationId,
            @PathVariable UUID approvalId,
            @AuthenticationPrincipal Jwt jwt) {
        return service.get(organizationId, approvalId, jwt);
    }

    @PostMapping("/{approvalId}/decisions")
    public ApprovalDecisionResponse decide(
            @PathVariable UUID organizationId,
            @PathVariable UUID approvalId,
            @RequestBody(required = false) ApprovalDecisionRequest request,
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest servletRequest) {
        return service.decide(
                organizationId, approvalId, request, idempotencyKey,
                jwt, correlationId(servletRequest));
    }

    @GetMapping("/{approvalId}/history")
    public List<ApprovalHistoryResponse> history(
            @PathVariable UUID organizationId,
            @PathVariable UUID approvalId,
            @AuthenticationPrincipal Jwt jwt) {
        return service.history(organizationId, approvalId, jwt);
    }

    private String correlationId(HttpServletRequest request) {
        Object value = request.getAttribute(CorrelationIdFilter.CORRELATION_ID_ATTRIBUTE);
        return value instanceof String correlationId ? correlationId : "";
    }
}
