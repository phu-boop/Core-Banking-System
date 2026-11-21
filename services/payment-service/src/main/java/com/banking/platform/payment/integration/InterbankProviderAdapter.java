package com.banking.platform.payment.integration;

import java.math.BigDecimal;
import java.util.UUID;

public interface InterbankProviderAdapter {
    ProviderSubmissionResponse submit(UUID paymentId, String destinationReference,
                                      BigDecimal amount, String currency,
                                      String correlationId);

    PaymentOutcomeResponse query(UUID paymentId, String providerReference,
                                 String correlationId);

    record ProviderSubmissionResponse(String status, String providerReference) {}

    record PaymentOutcomeResponse(String outcome, String providerReference) {}
}
