package com.banking.platform.payment.integration;

import com.banking.platform.payment.domain.PaymentOutcomeRequest;
import java.util.Optional;
import java.util.UUID;

public interface OutcomeSource {
    Optional<PaymentOutcomeRequest> reconcile(UUID paymentId);
}
