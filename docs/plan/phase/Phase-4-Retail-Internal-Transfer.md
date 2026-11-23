# Phase 4 - Retail Internal Transfer va Teller

## Scope

- Transfer owns orchestration and idempotency.
- Ledger owns posting, reversals and authoritative balances.
- Account owns lifecycle and eligibility.
- Teller deposit and withdrawal use the same internal posting boundary.
- Processing branch is an explicit transfer context.

## API

- `POST /v1/transfers/internal`
- `GET /v1/transfers/internal/{transferId}`
- Existing corporate transfer endpoints remain unchanged.

## Acceptance

- Successful posting returns `SETTLED`.
- Invalid currency, amount or account pair is rejected.
- Same key and same payload replay the prior transfer.
- Same key and different payload returns conflict.
- Ledger timeout leaves transfer `PROCESSING`, not confirmed failure.
