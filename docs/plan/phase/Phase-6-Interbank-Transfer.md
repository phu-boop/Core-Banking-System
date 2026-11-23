# Phase 6 - Provider-Neutral Interbank Transfer

## Sandbox scope

- Payment remains the provider boundary.
- The first executable provider is deterministic and provider-neutral.
- `TIMEOUT` produces `UNRESOLVED`.
- `REJECT` produces a verified `FAILED` outcome.
- Other destinations produce `PENDING_SUBMISSION`, then reconcile to `SETTLED`.
- No real network, posting, reversal or settlement finality is implemented.

## API

- `POST /internal/payments`
- `GET /internal/payments/{paymentId}`
- `POST /internal/payments/{paymentId}/outcomes`
- `POST /internal/payments/{paymentId}/reconcile`

## Acceptance

- Duplicate submission is replayed or conflicts by payload.
- Timeout remains unresolved.
- Duplicate or stale outcomes cannot change a terminal result.
- Reconciliation deduplicates outcome delivery.
- No cross-service schema access or Ledger side effect is introduced.
