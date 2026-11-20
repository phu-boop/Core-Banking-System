package com.banking.platform.corporate.integration;

import com.banking.platform.corporate.domain.PartyReferenceResponse;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class OidcPartyReferenceClient implements PartyReferenceClient {
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final RestClient restClient;

    public OidcPartyReferenceClient(OAuth2AuthorizedClientManager authorizedClientManager,
                                    RestClient partyServiceRestClient) {
        this.authorizedClientManager = authorizedClientManager;
        this.restClient = partyServiceRestClient;
    }

    @Override
    public Optional<PartyReferenceResponse> findById(UUID partyId) {
        try {
            var request = OAuth2AuthorizeRequest.withClientRegistrationId("party-service")
                    .principal("corporate-service")
                    .build();
            var authorizedClient = authorizedClientManager.authorize(request);
            if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
                throw new PartyServiceUnavailableException();
            }
            PartyReferenceResponse party = restClient.get()
                    .uri("/v1/customers/{partyId}", partyId)
                    .headers(headers -> headers.setBearerAuth(
                            authorizedClient.getAccessToken().getTokenValue()))
                    .retrieve()
                    .body(PartyReferenceResponse.class);
            if (party == null) {
                throw new PartyServiceUnavailableException();
            }
            return Optional.of(party);
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        } catch (OAuth2AuthorizationException | RestClientException exception) {
            throw new PartyServiceUnavailableException(exception);
        }
    }

    public static class PartyServiceUnavailableException extends RuntimeException {
        public PartyServiceUnavailableException() {
            super("Party service could not validate the reference");
        }

        public PartyServiceUnavailableException(Throwable cause) {
            super("Party service could not validate the reference", cause);
        }
    }
}
