# Smoke Test Verdict

**Date**: 2026-11-21  
**Status**: PASS

## Command

```bash
./mvnw verify
```

## Root build

The workspace now uses a Maven reactor POM at the repository root.
The reactor includes:

- `services/service-template`
- `services/identity-service`
- `services/party-service`
- `services/branch-service`
- `services/corporate-service`
- `services/transfer-service`

## Evidence

| Module | Tests | Result |
|--------|-------:|--------|
| service-template | 16 | PASS |
| identity-service | 12 | PASS |
| party-service | 3 | PASS |
| branch-service | 4 | PASS |
| corporate-service | 21 | PASS |
| transfer-service | 8 | PASS |
| **Total** | **64** | **PASS** |

The Maven reactor completed with `BUILD SUCCESS`.

## Deferred scope

This verdict does not add Ledger, Payment, settlement, reversal, external interbank provider or unknown-outcome reconciliation behavior. Those remain explicitly deferred in [the transfer plan](../plan.md).
