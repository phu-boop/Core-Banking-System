package com.banking.platform.payment.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record SubmitPaymentRequest(
        UUID transferId,
        String destinationReference,
        BigDecimal amount,
        String currency,
        String correlationId) {}
