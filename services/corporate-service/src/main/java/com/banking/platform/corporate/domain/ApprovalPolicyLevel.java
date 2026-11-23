package com.banking.platform.corporate.domain;

public record ApprovalPolicyLevel(
        int stepNumber,
        String roleCode,
        String mandateScope) {}
