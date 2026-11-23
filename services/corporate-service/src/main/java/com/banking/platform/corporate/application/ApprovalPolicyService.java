package com.banking.platform.corporate.application;

import com.banking.platform.corporate.domain.ApprovalPolicyResponse;
import com.banking.platform.corporate.persistence.ApprovalPolicyRepository;
import com.banking.platform.corporate.web.ApprovalPolicyLevelRequest;
import com.banking.platform.corporate.web.CreateApprovalPolicyRequest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class ApprovalPolicyService {
    private static final Pattern CODE = Pattern.compile("[A-Z][A-Z0-9_]{0,63}");
    private static final Pattern OPERATION_TYPE = Pattern.compile("[^\\s]{1,64}");

    private final ApprovalPolicyRepository repository;
    private final OrganizationAuthorizationService authorization;

    public ApprovalPolicyService(ApprovalPolicyRepository repository,
                                 OrganizationAuthorizationService authorization) {
        this.repository = repository;
        this.authorization = authorization;
    }

    public ApprovalPolicyResponse create(UUID organizationId, CreateApprovalPolicyRequest request,
                                         Jwt jwt, String correlationId) {
        authorization.requireApprovalPolicyManagement(jwt, organizationId);
        if (request == null) {
            throw new InvalidApprovalPolicyException("Approval policy body is required");
        }
        String operationType = validateOperationType(request.operationType());
        if (request.levels() == null || request.levels().isEmpty()) {
            throw new InvalidApprovalPolicyException("At least one approval level is required");
        }
        List<ApprovalPolicyLevelRequest> levels = request.levels().stream()
                .map(this::normalizeLevel)
                .toList();
        try {
            return repository.create(UUID.randomUUID(), organizationId, operationType, levels,
                    actor(jwt), correlationId);
        } catch (DuplicateKeyException exception) {
            throw new ApprovalPolicyConflictException(
                    "A policy version could not be allocated; retry the request");
        }
    }

    public List<ApprovalPolicyResponse> list(UUID organizationId, String operationType, Jwt jwt) {
        authorization.requireApprovalPolicyManagement(jwt, organizationId);
        String normalizedOperationType = operationType == null
                ? null : validateOperationType(operationType);
        return repository.list(organizationId, normalizedOperationType);
    }

    public ApprovalPolicyResponse activate(UUID organizationId, UUID policyId,
                                           Jwt jwt, String correlationId) {
        authorization.requireApprovalPolicyManagement(jwt, organizationId);
        try {
            return repository.activate(organizationId, policyId, actor(jwt), correlationId);
        } catch (DuplicateKeyException exception) {
            throw new ApprovalPolicyConflictException(
                    "Another policy version is already active for this operation type");
        }
    }

    private ApprovalPolicyLevelRequest normalizeLevel(ApprovalPolicyLevelRequest level) {
        if (level == null) {
            throw new InvalidApprovalPolicyException("Approval levels must not contain null entries");
        }
        return new ApprovalPolicyLevelRequest(
                normalizeCode(level.roleCode(), "roleCode"),
                normalizeCode(level.mandateScope(), "mandateScope"));
    }

    private String validateOperationType(String value) {
        if (value == null || !OPERATION_TYPE.matcher(value).matches()) {
            throw new InvalidApprovalPolicyException(
                    "operationType must contain 1 to 64 non-whitespace characters");
        }
        return value;
    }

    private String normalizeCode(String value, String field) {
        if (value == null) {
            throw new InvalidApprovalPolicyException(field + " is required");
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!CODE.matcher(normalized).matches()) {
            throw new InvalidApprovalPolicyException(field + " must be a code of 1 to 64 uppercase letters, digits, or underscores");
        }
        return normalized;
    }

    private String actor(Jwt jwt) {
        return jwt.getIssuer() + "/" + jwt.getSubject();
    }

    public static class InvalidApprovalPolicyException extends RuntimeException {
        public InvalidApprovalPolicyException(String message) {
            super(message);
        }
    }

    public static class ApprovalPolicyConflictException extends RuntimeException {
        public ApprovalPolicyConflictException(String message) {
            super(message);
        }
    }

    public static class ApprovalPolicyNotFoundException extends RuntimeException {
        public ApprovalPolicyNotFoundException(UUID policyId) {
            super("Approval policy not found: " + policyId);
        }
    }
}
