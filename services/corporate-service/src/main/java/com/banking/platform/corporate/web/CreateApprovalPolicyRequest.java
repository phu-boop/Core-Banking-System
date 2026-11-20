package com.banking.platform.corporate.web;

import java.util.List;

public record CreateApprovalPolicyRequest(
        String operationType,
        List<ApprovalPolicyLevelRequest> levels) {}
