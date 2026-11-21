package com.banking.platform.ledger.domain;

import java.util.UUID;

public record ReversalRequest(UUID transactionId, String reasonCode, String operationKey) {
}
