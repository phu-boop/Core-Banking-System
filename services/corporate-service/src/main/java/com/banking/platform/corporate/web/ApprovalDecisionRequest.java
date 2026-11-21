package com.banking.platform.corporate.web;

public record ApprovalDecisionRequest(
        String decision,
        String reason,
        String idempotencyKey) {}
