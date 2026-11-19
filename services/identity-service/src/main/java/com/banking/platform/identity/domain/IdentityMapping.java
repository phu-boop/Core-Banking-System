package com.banking.platform.identity.domain;

import java.util.UUID;

public record IdentityMapping(UUID userId, UUID partyId, IdentityStatus status) {}
