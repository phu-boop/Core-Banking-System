package com.banking.platform.transfer.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record SubmitTransferRequest(
        UUID organizationId,
        String operationType,
        String operationReference,
        BigDecimal amount,
        String currency) {}
