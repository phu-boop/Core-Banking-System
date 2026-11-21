package com.banking.platform.ledger.persistence;

import com.banking.platform.ledger.domain.BalanceResponse;
import com.banking.platform.ledger.domain.JournalEntryRequest;
import com.banking.platform.ledger.domain.JournalResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LedgerRepository {
    Optional<JournalResponse> findByOperationKey(String operationKey);
    JournalResponse post(UUID journalId, UUID transactionId, String operationKey,
                         List<JournalEntryRequest> entries, String currency,
                         String actor, String correlationId);
    Optional<JournalResponse> findById(UUID journalId);
    Optional<JournalResponse> findReversal(UUID originalJournalId);
    JournalResponse reverse(UUID reversalJournalId, UUID transactionId, String operationKey,
                            UUID originalJournalId, List<JournalEntryRequest> entries,
                            String reasonCode, String actor, String correlationId);
    BalanceResponse getBalance(UUID accountId);
}
