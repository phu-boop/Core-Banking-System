package com.banking.platform.account.persistence;

import com.banking.platform.account.domain.AccountResponse;
import com.banking.platform.account.domain.AccountStatus;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    AccountResponse create(UUID accountId, String accountNumber, UUID partyId);

    Optional<AccountResponse> findById(UUID accountId);

    boolean transition(UUID accountId, AccountStatus fromStatus, AccountStatus toStatus,
                       String reason, String actor, String correlationId);
}
