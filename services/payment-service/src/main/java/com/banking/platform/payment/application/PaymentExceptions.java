package com.banking.platform.payment.application;

import java.util.UUID;

public final class PaymentExceptions {
    private PaymentExceptions() {}

    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) { super(message); }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(UUID paymentId) {
            super("Payment not found: " + paymentId);
        }
    }
}
