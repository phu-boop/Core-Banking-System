package com.banking.platform.ledger.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BalanceResponse(UUID accountId, String currency, BigDecimal balance, OffsetDateTime asOf) {
}
