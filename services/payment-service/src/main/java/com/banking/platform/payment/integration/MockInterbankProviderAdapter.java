package com.banking.platform.payment.integration;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class MockInterbankProviderAdapter implements InterbankProviderAdapter {
    private final Map<UUID, String> outcomes = new ConcurrentHashMap<>();

    @Override
    public ProviderSubmissionResponse submit(UUID paymentId, String destinationReference,
                                             BigDecimal amount, String currency,
                                             String correlationId) {
        String normalizedDestination = destinationReference == null
                ? "" : destinationReference.trim().toUpperCase();
        if (normalizedDestination.equals("TIMEOUT")) {
            outcomes.put(paymentId, "UNRESOLVED");
            return new ProviderSubmissionResponse("UNRESOLVED", null);
        }
        if (normalizedDestination.equals("REJECT")) {
            outcomes.put(paymentId, "FAILED");
            return new ProviderSubmissionResponse("FAILED", "mock-ref-" + paymentId);
        }
        outcomes.put(paymentId, "SETTLED");
        return new ProviderSubmissionResponse(
                "PENDING_SUBMISSION", "mock-ref-" + paymentId);
    }

    @Override
    public PaymentOutcomeResponse query(UUID paymentId, String providerReference,
                                        String correlationId) {
        String outcome = outcomes.getOrDefault(paymentId, "UNRESOLVED");
        String reference = providerReference == null
                ? "mock-ref-" + paymentId : providerReference;
        return new PaymentOutcomeResponse(outcome, reference);
    }
}
