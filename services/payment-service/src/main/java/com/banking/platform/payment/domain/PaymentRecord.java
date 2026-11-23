package com.banking.platform.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRecord(
        UUID paymentId,
        UUID transferId,
        BigDecimal amount,
        String currency,
        String destinationReference,
        PaymentStatus status,
        String providerReference,
        String correlationId,
        Instant createdAt,
        Instant updatedAt) {}
