package com.banking.platform.transfer.application;

public final class TransferExceptions {
    private TransferExceptions() {}

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(java.util.UUID id) { super("Transfer not found: " + id); }
    }

    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) { super(message); }
    }

    public static class UnsupportedException extends RuntimeException {
        public UnsupportedException(String message) { super(message); }
    }

    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) { super(message); }
    }
}
