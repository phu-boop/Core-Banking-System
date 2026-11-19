package com.banking.platform.party.web;

import com.banking.platform.party.domain.PartyType;
import java.time.LocalDate;
import java.util.UUID;

public record CreatePartyRequest(
        PartyType partyType,
        String displayName,
        UUID managingBranchId,
        IndividualProfileRequest individualProfile) {
    public record IndividualProfileRequest(
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String nationalIdHash) {}
}
