package com.banking.platform.account.integration;

public class IdentityServiceUnavailableException extends RuntimeException {
    public IdentityServiceUnavailableException(Throwable cause) {
        super("Identity Service is unavailable", cause);
    }

    public IdentityServiceUnavailableException() {
        super("Identity Service returned an invalid principal");
    }
}
