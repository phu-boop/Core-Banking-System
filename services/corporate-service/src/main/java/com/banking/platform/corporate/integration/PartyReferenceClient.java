package com.banking.platform.corporate.integration;

import com.banking.platform.corporate.domain.PartyReferenceResponse;
import java.util.Optional;
import java.util.UUID;

public interface PartyReferenceClient {
    Optional<PartyReferenceResponse> findById(UUID partyId);
}
