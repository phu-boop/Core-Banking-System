package com.banking.platform.transfer.integration;

import com.banking.platform.transfer.domain.PaymentSubmissionResponse;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentSubmissionClient {
    private final RestClient restClient;

    public PaymentSubmissionClient(@Value("${transfer.payment.base-url:http://localhost:8086}") String baseUrl,
                                   RestClient.Builder builder) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public PaymentSubmissionResponse submit(UUID transferId, String destinationReference,
                                            BigDecimal amount, String currency,
                                            String accessToken, String correlationId) {
        return restClient.post()
                .uri("/internal/payments")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Correlation-Id", correlationId)
                .body(new SubmitPaymentRequest(transferId, destinationReference, amount, currency, correlationId))
                .retrieve()
                .body(PaymentSubmissionResponse.class);
    }

    public PaymentSubmissionResponse get(UUID paymentId, String accessToken, String correlationId) {
        return restClient.get()
                .uri("/internal/payments/{paymentId}", paymentId)
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Correlation-Id", correlationId)
                .retrieve()
                .body(PaymentSubmissionResponse.class);
    }

    private record SubmitPaymentRequest(UUID transferId, String destinationReference,
                                        BigDecimal amount, String currency, String correlationId) {}
}
