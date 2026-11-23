package com.banking.platform.corporate.application;

public class ApprovalRequestConflictException extends RuntimeException {
    public ApprovalRequestConflictException(String message) {
        super(message);
    }
}
