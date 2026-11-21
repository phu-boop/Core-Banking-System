package com.banking.platform.corporate.application;

import com.banking.platform.corporate.domain.MandateResponse;
import com.banking.platform.corporate.domain.MembershipResponse;
import com.banking.platform.corporate.persistence.MembershipRepository;
import com.banking.platform.corporate.web.CreateMandateRequest;
import com.banking.platform.corporate.web.CreateMembershipRequest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
    private static final Pattern CODE = Pattern.compile("[A-Z][A-Z0-9_]{0,63}");
    private final MembershipRepository repository;
    private final OrganizationAuthorizationService authorization;

    public MembershipService(MembershipRepository repository,
                             OrganizationAuthorizationService authorization) {
        this.repository = repository;
        this.authorization = authorization;
    }

    public MembershipResponse createMembership(UUID organizationId,
                                               CreateMembershipRequest request,
                                               Jwt jwt,
                                               String correlationId) {
        authorization.requireMembershipManagement(jwt, organizationId);
        if (request == null || request.subject() == null
                || request.subject().trim().isEmpty()) {
            throw new MembershipValidationException("subject is required");
        }
        String roleCode = normalizeCode(request.roleCode(), "roleCode");
        return repository.createMembership(
                organizationId, request.subject().trim(), roleCode,
                actor(jwt), correlationId);
    }

    public List<MembershipResponse> listMemberships(UUID organizationId, Jwt jwt) {
        authorization.requireMembershipManagement(jwt, organizationId);
        return repository.listMemberships(organizationId);
    }

    public MandateResponse createMandate(UUID organizationId, UUID membershipId,
                                         CreateMandateRequest request,
                                         Jwt jwt,
                                         String correlationId) {
        authorization.requireMembershipManagement(jwt, organizationId);
        if (request == null) {
            throw new MembershipValidationException("Mandate body is required");
        }
        String mandateScope = normalizeCode(request.mandateScope(), "mandateScope");
        return repository.createMandate(
                organizationId, membershipId, mandateScope,
                actor(jwt), correlationId);
    }

    private String normalizeCode(String value, String field) {
        if (value == null) {
            throw new MembershipValidationException(field + " is required");
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!CODE.matcher(normalized).matches()) {
            throw new MembershipValidationException(
                    field + " must be a code of 1 to 64 uppercase letters, digits, or underscores");
        }
        return normalized;
    }

    private String actor(Jwt jwt) {
        return jwt.getIssuer() + "/" + jwt.getSubject();
    }

    public static class MembershipValidationException extends RuntimeException {
        public MembershipValidationException(String message) {
            super(message);
        }
    }

    public static class MembershipNotFoundException extends RuntimeException {
        public MembershipNotFoundException(UUID membershipId) {
            super("Membership not found: " + membershipId);
        }
    }
}
