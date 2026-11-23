package com.banking.platform.corporate.application;

public class IneligibleApproverException extends RuntimeException {
    public IneligibleApproverException(String message) {
        super(message);
    }
}
