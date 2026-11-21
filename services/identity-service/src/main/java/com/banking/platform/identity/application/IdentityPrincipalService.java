package com.banking.platform.identity.application;

import com.banking.platform.identity.domain.IdentityPrincipalResponse;
import com.banking.platform.identity.domain.IdentityMapping;
import com.banking.platform.identity.domain.IdentityStatus;
import com.banking.platform.identity.persistence.IdentityMappingRepository;
import com.banking.platform.identity.persistence.JdbcIdentityMappingRepository;
import org.slf4j.MDC;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdentityPrincipalService {

    private final IdentityMappingRepository identityMappingRepository;

    public IdentityPrincipalService(IdentityMappingRepository identityMappingRepository) {
        this.identityMappingRepository = identityMappingRepository;
    }
    
    @Transactional(noRollbackFor = AccessDeniedException.class)
    public IdentityPrincipalResponse resolve(String issuer, String subject) {
        String resourceId = JdbcIdentityMappingRepository.opaquePrincipalReference(issuer, subject);
        String correlationId = MDC.get("correlationId");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        var mapping = identityMappingRepository.findByIssuerAndSubject(issuer, subject);
        if (mapping.isEmpty() || mapping.get().status() != IdentityStatus.ACTIVE) {
            identityMappingRepository.recordResolutionAudit(
                    "UNKNOWN",
                    "IDENTITY_PRINCIPAL_RESOLVE",
                    resourceId,
                    "DENIED",
                    correlationId);
            throw new AccessDeniedException("Identity mapping is not active");
        }

        IdentityMapping active = mapping.get();
        identityMappingRepository.recordResolutionAudit(
                active.userId().toString(),
                "IDENTITY_PRINCIPAL_RESOLVE",
                resourceId,
                "SUCCESS",
                correlationId);
        return new IdentityPrincipalResponse(
                active.userId(),
                active.partyId(),
                identityMappingRepository.findGrantsByUserId(active.userId()));
    }
}
