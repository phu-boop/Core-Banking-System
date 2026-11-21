# Payment Service

Provider-neutral interbank Payment boundary.

## Scope

- Persists external payment submissions and outcome history in `payment_schema`.
- Supports `PENDING_SUBMISSION`, `UNRESOLVED`, `SETTLED`, and `FAILED` states.
- Timeout or unavailable provider response remains `UNRESOLVED`.
- No provider protocol, Ledger posting, settlement finality, reversal or reconciliation policy is implemented.

## Verification

```bash
./mvnw -f services/payment-service/pom.xml verify
```
