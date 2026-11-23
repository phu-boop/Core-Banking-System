package com.banking.platform.corporate.application;

import java.util.UUID;

public interface ApproverEligibilityClient {
    boolean isEligible(UUID organizationId, String subject,
                       String roleCode, String mandateScope);
}
