package com.banking.platform.account.integration;

import java.util.List;
import java.util.UUID;

public record IdentityPrincipal(UUID userId, UUID partyId, List<IdentityGrant> grants) {
    public IdentityPrincipal {
        grants = List.copyOf(grants);
    }
}
