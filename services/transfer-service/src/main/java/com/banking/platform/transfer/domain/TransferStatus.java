package com.banking.platform.transfer.domain;

public enum TransferStatus {
    ACCEPTED,
    PROCESSING,
    SETTLED,
    FAILED,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    EXECUTION_REQUESTED,
    PENDING_INTERBANK,
    UNRESOLVED_INTERBANK,
    INTERBANK_SETTLED,
    INTERBANK_FAILED
}
