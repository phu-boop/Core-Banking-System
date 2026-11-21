package com.banking.platform.corporate.web;

public record CreateApprovalRequest(
        String operationType,
        String operationReference) {}
