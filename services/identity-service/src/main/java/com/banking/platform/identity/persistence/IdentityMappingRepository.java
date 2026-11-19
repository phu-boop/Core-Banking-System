package com.banking.platform.identity.persistence;

import com.banking.platform.identity.domain.IdentityMapping;
import com.banking.platform.identity.domain.ScopedGrant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IdentityMappingRepository {

    Optional<IdentityMapping> findByIssuerAndSubject(String issuer, String subject);

    List<ScopedGrant> findGrantsByUserId(UUID userId);
}
