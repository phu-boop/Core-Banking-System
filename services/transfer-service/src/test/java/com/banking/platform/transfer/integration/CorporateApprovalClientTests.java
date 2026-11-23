package com.banking.platform.transfer.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.banking.platform.transfer.domain.CorporateApprovalResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CorporateApprovalClientTests {
    private CorporateApprovalClient client;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new CorporateApprovalClient("http://corporate.test", builder);
    }

    @Test
    void createsApprovalRequestWithCorrelationAndBearerToken() {
        UUID organizationId = UUID.randomUUID();
        UUID approvalId = UUID.randomUUID();
        server.expect(requestTo("http://corporate.test/v1/organizations/"
                        + organizationId + "/approval-requests"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer access-token"))
                .andExpect(header("X-Correlation-Id", "correlation-1"))
                .andRespond(withSuccess("""
                        {
                          "approvalId":"%s",
                          "organizationId":"%s",
                          "operationType":"CORPORATE_OUTBOUND",
                          "operationReference":"operation-1",
                          "policyVersion":1,
                          "status":"PENDING",
                          "currentStep":1,
                          "submittedAt":"2026-11-21T00:00:00Z",
                          "completedAt":null
                        }
                        """.formatted(approvalId, organizationId), MediaType.APPLICATION_JSON));

        CorporateApprovalResponse response = client.createRequest(
                organizationId, "CORPORATE_OUTBOUND", "operation-1",
                "access-token", "correlation-1");

        assertThat(response.approvalId()).isEqualTo(approvalId);
        assertThat(response.status()).isEqualTo("PENDING");
        server.verify();
    }

    @Test
    void readsApprovalRequestUsingOpaqueApprovalId() {
        UUID organizationId = UUID.randomUUID();
        UUID approvalId = UUID.randomUUID();
        server.expect(requestTo("http://corporate.test/v1/organizations/"
                        + organizationId + "/approval-requests/" + approvalId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer access-token"))
                .andRespond(withSuccess("""
                        {
                          "approvalId":"%s",
                          "organizationId":"%s",
                          "operationType":"CORPORATE_OUTBOUND",
                          "operationReference":"operation-1",
                          "policyVersion":1,
                          "status":"APPROVED",
                          "currentStep":2,
                          "submittedAt":"2026-11-21T00:00:00Z",
                          "completedAt":"2026-11-21T00:01:00Z"
                        }
                        """.formatted(approvalId, organizationId), MediaType.APPLICATION_JSON));

        CorporateApprovalResponse response = client.getRequest(
                organizationId, approvalId, "access-token", "correlation-2");

        assertThat(response.status()).isEqualTo("APPROVED");
        server.verify();
    }
}
