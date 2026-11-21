package com.banking.platform.corporate.application;

import java.util.UUID;

public class ApprovalRequestNotFoundException extends RuntimeException {
    public ApprovalRequestNotFoundException(UUID approvalId) {
        super("Approval request not found: " + approvalId);
    }
}
