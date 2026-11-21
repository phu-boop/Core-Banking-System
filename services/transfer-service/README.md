# Transfer Service

Independent Spring Boot service that owns corporate transfer state and enforces the Corporate Approval result.

## Scope

- Creates durable pending transfers and submits approval requests to Corporate.
- Stores opaque Corporate approval request and policy references.
- Blocks execution for pending or rejected approval.
- Provides idempotent submission replay and conflicting-payload rejection.
- Keeps Ledger, Payment, interbank, settlement and reversal behavior unresolved.

## Run

```bash
./mvnw -f services/transfer-service/pom.xml verify
```

## Verification

- 8 Transfer tests pass, including pending/rejected execution gates, idempotency replay/conflict,
  Mock Corporate REST create/read calls and PostgreSQL persistence.
