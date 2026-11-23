# Phase 3 - Account va Ledger

## Pham vi

- `account-service` owns `account_schema` and payment-account lifecycle.
- `ledger-service` owns `ledger_schema` and double-entry journal posting.
- Both services use VND and reject floating-point monetary inputs.

## API

- `POST /v1/accounts`
- `GET /v1/accounts/{accountId}`
- `POST /v1/accounts/{accountId}/block`
- `POST /v1/accounts/{accountId}/unblock`
- `POST /internal/ledger/journals`
- `POST /internal/ledger/journals/{journalId}/reversals`
- `GET /internal/ledger/journals/{journalId}`
- `GET /internal/ledger/accounts/{accountId}/balance`

## Acceptance

- Debit equals Credit.
- Posted journals are immutable.
- Reversal creates balanced offset entries.
- Operation-key replay cannot create duplicate posts.
- Concurrent balance changes cannot double-spend.
