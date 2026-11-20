package com.banking.platform.corporate.application;

import com.banking.platform.corporate.domain.OrganizationResponse;
import com.banking.platform.corporate.domain.PartyType;
import com.banking.platform.corporate.integration.OidcPartyReferenceClient.PartyServiceUnavailableException;
import com.banking.platform.corporate.integration.PartyReferenceClient;
import com.banking.platform.corporate.persistence.OrganizationRepository;
import com.banking.platform.corporate.web.CreateOrganizationRequest;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {
    private final OrganizationRepository repository;
    private final PartyReferenceClient partyReferenceClient;
    private final OrganizationAuthorizationService authorization;

    public OrganizationService(OrganizationRepository repository,
                               PartyReferenceClient partyReferenceClient,
                               OrganizationAuthorizationService authorization) {
        this.repository = repository;
        this.partyReferenceClient = partyReferenceClient;
        this.authorization = authorization;
    }

    public OrganizationResponse create(CreateOrganizationRequest request, Jwt jwt, String correlationId) {
        authorization.requireCreate(jwt);
        if (request == null || request.partyId() == null) {
            throw new InvalidOrganizationException("partyId is required");
        }
        var party = partyReferenceClient.findById(request.partyId())
                .orElseThrow(() -> new InvalidOrganizationException("Referenced Party does not exist"));
        if (!request.partyId().equals(party.partyId())) {
            throw new PartyServiceUnavailableException();
        }
        if (party.partyType() != PartyType.ORGANIZATION) {
            throw new InvalidOrganizationException("partyId must reference a Party of type ORGANIZATION");
        }
        UUID organizationId = UUID.randomUUID();
        try {
            return repository.create(organizationId, party.partyId(), actor(jwt), correlationId);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateOrganizationPartyException(party.partyId());
        }
    }

    public OrganizationResponse get(UUID organizationId, Jwt jwt) {
        if (!authorization.canRead(jwt, organizationId)) {
            throw new OrganizationNotFoundException(organizationId);
        }
        return repository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
    }

    private String actor(Jwt jwt) {
        return jwt.getIssuer() + "/" + jwt.getSubject();
    }

    public static class InvalidOrganizationException extends RuntimeException {
        public InvalidOrganizationException(String message) {
            super(message);
        }
    }

    public static class DuplicateOrganizationPartyException extends RuntimeException {
        public DuplicateOrganizationPartyException(UUID partyId) {
            super("A Corporate Organization already references Party " + partyId);
        }
    }

    public static class OrganizationNotFoundException extends RuntimeException {
        public OrganizationNotFoundException(UUID organizationId) {
            super("Organization not found: " + organizationId);
        }
    }
}
