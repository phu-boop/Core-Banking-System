package com.banking.platform.ledger.application;

import com.banking.platform.ledger.domain.BalanceResponse;
import com.banking.platform.ledger.domain.EntrySide;
import com.banking.platform.ledger.domain.JournalEntryRequest;
import com.banking.platform.ledger.domain.JournalRequest;
import com.banking.platform.ledger.domain.JournalResponse;
import com.banking.platform.ledger.domain.ReversalRequest;
import com.banking.platform.ledger.persistence.LedgerRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerService {
    private final LedgerRepository repository;

    public LedgerService(LedgerRepository repository) {
        this.repository = repository;
    }

    public JournalResponse post(JournalRequest request, String actor, String correlationId) {
        validate(request);
        var replay = repository.findByOperationKey(request.operationKey());
        if (replay.isPresent()) {
            return replay.get();
        }
        return repository.post(UUID.randomUUID(), request.transactionId(),
                request.operationKey(), request.entries(), request.currency(),
                actor, correlationId);
    }

    public JournalResponse reverse(UUID journalId, ReversalRequest request,
                                   String actor, String correlationId) {
        JournalResponse original = repository.findById(journalId)
                .orElseThrow(() -> new JournalNotFoundException(journalId));
        if (repository.findReversal(journalId).isPresent()) {
            throw new JournalAlreadyReversedException(journalId);
        }
        if (request == null || request.transactionId() == null
                || request.reasonCode() == null || request.reasonCode().isBlank()
                || request.operationKey() == null || request.operationKey().isBlank()) {
            throw new InvalidJournalException("transactionId, reasonCode and operationKey are required");
        }
        List<JournalEntryRequest> reversed = original.entries().stream()
                .map(entry -> new JournalEntryRequest(
                        entry.accountId(),
                        entry.side() == EntrySide.DEBIT ? EntrySide.CREDIT : EntrySide.DEBIT,
                        entry.amount()))
                .toList();
        try {
            return repository.reverse(UUID.randomUUID(), request.transactionId(),
                    request.operationKey(), journalId, reversed,
                    request.reasonCode(), actor, correlationId);
        } catch (DuplicateKeyException exception) {
            return repository.findReversal(journalId).orElseThrow();
        }
    }

    @Transactional(readOnly = true)
    public JournalResponse get(UUID journalId) {
        return repository.findById(journalId).orElseThrow(() -> new JournalNotFoundException(journalId));
    }

    @Transactional(readOnly = true)
    public BalanceResponse balance(UUID accountId) {
        return repository.getBalance(accountId);
    }

    private void validate(JournalRequest request) {
        if (request == null || request.transactionId() == null
                || request.operationKey() == null || request.operationKey().isBlank()
                || !"VND".equals(request.currency())
                || request.entries() == null || request.entries().size() < 2) {
            throw new InvalidJournalException("A VND journal requires transactionId, operationKey and at least two entries");
        }
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal credit = BigDecimal.ZERO;
        for (JournalEntryRequest entry : request.entries()) {
            if (entry.accountId() == null || entry.side() == null
                    || entry.amount() == null || entry.amount().compareTo(BigDecimal.ZERO) <= 0
                    || entry.amount().scale() > 2) {
                throw new InvalidJournalException("Every entry requires an account, side and positive amount");
            }
            if (entry.side() == EntrySide.DEBIT) {
                debit = debit.add(entry.amount());
            } else {
                credit = credit.add(entry.amount());
            }
        }
        if (debit.compareTo(credit) != 0) {
            throw new UnbalancedJournalException();
        }
    }

    public static class InvalidJournalException extends RuntimeException {
        public InvalidJournalException(String message) { super(message); }
    }
    public static class UnbalancedJournalException extends RuntimeException {}
    public static class JournalNotFoundException extends RuntimeException {
        public JournalNotFoundException(UUID id) { super("Journal not found: " + id); }
    }
    public static class JournalAlreadyReversedException extends RuntimeException {
        public JournalAlreadyReversedException(UUID id) { super("Journal already reversed: " + id); }
    }
}
