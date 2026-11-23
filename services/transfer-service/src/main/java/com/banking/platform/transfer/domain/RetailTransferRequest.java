package com.banking.platform.transfer.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record RetailTransferRequest(
        RetailTransferType operationType,
        UUID sourceAccountId,
        UUID destinationAccountId,
        UUID processingBranchId,
        BigDecimal amount,
        String currency) {
}
