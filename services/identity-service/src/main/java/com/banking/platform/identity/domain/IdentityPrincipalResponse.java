package com.banking.platform.identity.domain;

import java.util.List;
import java.util.UUID;

public record IdentityPrincipalResponse(UUID userId, UUID partyId, List<ScopedGrant> grants) {

    public IdentityPrincipalResponse {
        grants = List.copyOf(grants);
    }
}
