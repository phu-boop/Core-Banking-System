package com.banking.platform.corporate.application;

import com.banking.platform.corporate.domain.ApprovalDecision;
import com.banking.platform.corporate.domain.ApprovalDecisionResponse;
import com.banking.platform.corporate.domain.ApprovalHistoryResponse;
import com.banking.platform.corporate.domain.ApprovalRequestResponse;
import com.banking.platform.corporate.persistence.ApprovalRepository;
import com.banking.platform.corporate.web.ApprovalDecisionRequest;
import com.banking.platform.corporate.web.CreateApprovalRequest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class ApprovalWorkflowService {
    private final ApprovalRepository repository;
    private final OrganizationAuthorizationService authorization;

    public ApprovalWorkflowService(ApprovalRepository repository,
                                   OrganizationAuthorizationService authorization) {
        this.repository = repository;
        this.authorization = authorization;
    }

    public ApprovalRequestResponse create(UUID organizationId,
                                          CreateApprovalRequest request,
                                          Jwt jwt,
                                          String correlationId) {
        authorization.requireApprovalRequestSubmission(jwt, organizationId);
        if (request == null) {
            throw new ApprovalRequestValidationException("Approval request body is required");
        }
        String operationType = request.operationType();
        if (operationType == null || operationType.trim().isEmpty()) {
            throw new ApprovalRequestValidationException("operationType is required");
        }
        if (operationType.trim().length() > 64) {
            throw new ApprovalRequestValidationException("operationType must not exceed 64 characters");
        }
        return repository.create(
                organizationId,
                operationType.trim(),
                request.operationReference(),
                actorSubject(jwt),
                correlationId);
    }

    public ApprovalRequestResponse get(UUID organizationId, UUID approvalId, Jwt jwt) {
        if (!authorization.canRead(jwt, organizationId)
                && !authorization.hasApprovalPolicyManagement(jwt, organizationId)
                && !authorization.hasApprovalRequestSubmission(jwt, organizationId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Approval request read permission is required");
        }
        return repository.get(organizationId, approvalId);
    }

    public ApprovalDecisionResponse decide(UUID organizationId, UUID approvalId,
                                           ApprovalDecisionRequest request,
                                           String headerIdempotencyKey,
                                           Jwt jwt,
                                           String correlationId) {
        if (request == null) {
            throw new ApprovalRequestValidationException("Decision body is required");
        }
        ApprovalDecision decision;
        try {
            decision = ApprovalDecision.valueOf(
                    request.decision() == null
                            ? "" : request.decision().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ApprovalRequestValidationException("decision must be APPROVE or REJECT");
        }
        if (request.reason() != null && request.reason().length() > 1024) {
            throw new ApprovalRequestValidationException("reason must not exceed 1024 characters");
        }
        String idempotencyKey = headerIdempotencyKey;
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            idempotencyKey = request.idempotencyKey();
        }
        return repository.decide(
                organizationId, approvalId, actorSubject(jwt),
                decision, request.reason(), idempotencyKey, correlationId);
    }

    public List<ApprovalHistoryResponse> history(UUID organizationId, UUID approvalId, Jwt jwt) {
        if (!authorization.hasApprovalPolicyManagement(jwt, organizationId)
                && !authorization.canRead(jwt, organizationId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Approval history read permission is required");
        }
        repository.get(organizationId, approvalId);
        return repository.history(organizationId, approvalId);
    }

    private String actorSubject(Jwt jwt) {
        return jwt.getIssuer() + "/" + jwt.getSubject();
    }
}
