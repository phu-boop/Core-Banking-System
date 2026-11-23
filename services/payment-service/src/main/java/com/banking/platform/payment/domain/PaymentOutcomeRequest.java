package com.banking.platform.payment.domain;

public record PaymentOutcomeRequest(
        String outcome,
        String outcomeSource,
        String providerReference,
        String correlationId) {}
