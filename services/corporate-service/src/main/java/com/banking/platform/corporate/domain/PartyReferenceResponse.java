package com.banking.platform.corporate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PartyReferenceResponse(UUID partyId, PartyType partyType) {}
