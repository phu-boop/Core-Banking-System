package com.banking.platform.account.application;

import com.banking.platform.account.domain.AccountResponse;
import com.banking.platform.account.domain.AccountStatus;
import com.banking.platform.account.persistence.AccountRepository;
import com.banking.platform.account.web.CreateAccountRequest;
import com.banking.platform.account.web.StateChangeRequest;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request, String actor, String correlationId) {
        if (request == null || request.partyId() == null
                || request.accountNumber() == null
                || !request.accountNumber().matches("[A-Za-z0-9-]{6,34}")) {
            throw new InvalidAccountException("partyId and a valid accountNumber are required");
        }
        try {
            return repository.create(UUID.randomUUID(), request.accountNumber().trim(), request.partyId());
        } catch (DuplicateKeyException exception) {
            throw new DuplicateAccountException();
        }
    }

    @Transactional(readOnly = true)
    public AccountResponse get(UUID accountId) {
        return repository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Transactional
    public AccountResponse block(UUID accountId, StateChangeRequest request,
                                 String actor, String correlationId) {
        return transition(accountId, AccountStatus.ACTIVE, AccountStatus.BLOCKED, request, actor, correlationId);
    }

    @Transactional
    public AccountResponse unblock(UUID accountId, StateChangeRequest request,
                                   String actor, String correlationId) {
        return transition(accountId, AccountStatus.BLOCKED, AccountStatus.ACTIVE, request, actor, correlationId);
    }

    private AccountResponse transition(UUID accountId, AccountStatus from, AccountStatus to,
                                       StateChangeRequest request, String actor, String correlationId) {
        AccountResponse account = get(accountId);
        if (account.status() != from) {
            throw new InvalidAccountTransitionException(account.status(), to);
        }
        String reason = request == null || request.reason() == null || request.reason().isBlank()
                ? "No reason provided" : request.reason().trim();
        if (!repository.transition(accountId, from, to, reason, actor, correlationId)) {
            throw new InvalidAccountTransitionException(account.status(), to);
        }
        return get(accountId);
    }

    public static class InvalidAccountException extends RuntimeException {
        public InvalidAccountException(String message) { super(message); }
    }
    public static class DuplicateAccountException extends RuntimeException {}
    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(UUID id) { super("Account not found: " + id); }
    }
    public static class InvalidAccountTransitionException extends RuntimeException {
        public InvalidAccountTransitionException(AccountStatus from, AccountStatus to) {
            super("Invalid account transition from " + from + " to " + to);
        }
    }
}
