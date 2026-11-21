package com.banking.platform.transfer.integration;

import com.banking.platform.transfer.domain.CorporateApprovalResponse;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class CorporateApprovalClient {
    private final RestClient restClient;

    public CorporateApprovalClient(@Value("${transfer.corporate.base-url}") String baseUrl,
                                   RestClient.Builder builder) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public CorporateApprovalResponse createRequest(UUID organizationId, String operationType,
                                                     String operationReference, String accessToken,
                                                     String correlationId) {
        try {
            return restClient.post()
                    .uri("/v1/organizations/{organizationId}/approval-requests", organizationId)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("X-Correlation-Id", correlationId)
                    .body(new CreateApprovalRequest(operationType, operationReference))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new RestClientResponseException(
                                "Corporate approval request failed",
                                response.getStatusCode().value(), "", null, null, null);
                    })
                    .body(CorporateApprovalResponse.class);
        } catch (RestClientResponseException ex) {
            throw ex;
        }
    }

    public CorporateApprovalResponse getRequest(UUID organizationId, UUID approvalId,
                                                String accessToken, String correlationId) {
        return restClient.get()
                .uri("/v1/organizations/{organizationId}/approval-requests/{approvalId}",
                        organizationId, approvalId)
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Correlation-Id", correlationId)
                .retrieve()
                .body(CorporateApprovalResponse.class);
    }

    private record CreateApprovalRequest(String operationType, String operationReference) {}
}
