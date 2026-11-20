package com.banking.platform.identity.application;

import com.banking.platform.identity.domain.IdentityPrincipalResponse;
import com.banking.platform.identity.domain.IdentityStatus;
import com.banking.platform.identity.persistence.IdentityMappingRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdentityPrincipalService {

    private final IdentityMappingRepository identityMappingRepository;

    public IdentityPrincipalService(IdentityMappingRepository identityMappingRepository) {
        this.identityMappingRepository = identityMappingRepository;
    }
    
    @Transactional(readOnly = true)
    public IdentityPrincipalResponse resolve(String issuer, String subject) {
        var mapping = identityMappingRepository.findByIssuerAndSubject(issuer, subject)
                .filter(identity -> identity.status() == IdentityStatus.ACTIVE)
                .orElseThrow(() -> new AccessDeniedException("Identity mapping is not active"));

        return new IdentityPrincipalResponse(
                mapping.userId(),
                mapping.partyId(),
                identityMappingRepository.findGrantsByUserId(mapping.userId()));
    }
}
