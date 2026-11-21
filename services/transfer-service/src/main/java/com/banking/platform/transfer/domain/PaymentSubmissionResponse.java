package com.banking.platform.transfer.domain;

import java.util.UUID;

public record PaymentSubmissionResponse(
        UUID paymentId,
        String status,
        String providerReference,
        String correlationId) {}
