package com.banking.platform.party.domain;

import java.time.LocalDate;
import java.util.UUID;

public record IndividualProfileResponse(
        UUID partyId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        boolean nationalIdPresent) {}
