# API Contracts

**Status:** tất cả endpoint dưới đây là **PROPOSED** design contracts, không phải API đã tồn tại. Không có source code để xác minh implementation. REST is proposed; gateway, Identity Provider and internal REST-vs-gRPC are OPEN. Request/response examples are schematic; enum values, limits and detailed field rules require domain review.

## Conventions

- JSON over HTTPS is **PROPOSED**. IDs are opaque. Timestamps use RFC-3339 UTC.
- Monetary amounts are decimal strings, e.g. `"1250.00"`; currency in MVP is `"VND"`. Java must use `BigDecimal`; never parse as floating point.
- Error envelope (PROPOSED):

```json
{
  "code": "ACCOUNT_NOT_ELIGIBLE",
  "message": "Request cannot be completed.",
  "correlationId": "request-id",
  "details": []
}
```

- Common HTTP errors: `400` invalid input, `401` unauthenticated, `403` unauthorized, `404` resource absent/not visible, `409` state or idempotency conflict, `422` business validation failure, `429` rate limit, `5xx` transient/system failure. Exact mapping is PROPOSED.
- Each service enforces authorization for its own resource and business operation. API Gateway must not be the only authorization layer.
- `Idempotency-Key` behavior requires a defined scope/retention policy; values are not yet specified. See [Open Decisions](../architecture/OPEN-DECISIONS.md).

## Authentication — Identity & Access

| Method / endpoint (PROPOSED) | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/auth/token` | OAuth-style credential/grant request → `accessToken`, `expiresIn`, `tokenType`; exact protocol/provider OPEN | Validate grant/client and required fields at chosen IdP | Public client subject to client authentication and flow policy; Identity Provider TBD | invalid credentials/grant, locked user, invalid client, throttled provider | Not applicable to token issuance; provider replay/refresh controls TBD |
| `GET /v1/identity/me` | none → `userId`, authorized scopes/claims | Validate active subject | Authenticated principal | unauthenticated, inactive identity | Not applicable |

These are illustrative app-facing contracts only. Do not implement custom credential storage or token issuance before Identity Provider decision.

## Customer — Party / Customer

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/customers` | `partyType`, type-specific profile, optional contact data → `partyId`, status | Required fields by party type; format/duplicate checks depend on approved customer rules | Customer-creation permission for channel/actor; staff scope where relevant | invalid profile, duplicate identity, forbidden scope, unavailable verification | Key recommended for create; scope/retention OPEN |
| `GET /v1/customers/{partyId}` | none → customer profile and status | Valid ID | Customer data permission plus organization/branch scope as applicable | not found/not visible, forbidden | Not applicable |
| `PATCH /v1/customers/{partyId}` | allowed profile fields + version → updated profile/version | Only mutable fields; concurrency/version check | Same resource-scoped permission as read plus update grant | invalid field, stale version, forbidden, not found | Not applicable; use optimistic version to avoid lost update |

## Corporate

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/organizations` | `partyId`, organization profile → `organizationId`, status | Party exists/eligible via Party API; required legal data TBD | Corporate onboarding permission | invalid profile, party absent, duplicate organization, forbidden | Key recommended for create; scope/retention OPEN |
| `POST /v1/organizations/{organizationId}/members` | `userId`, membership role/scope → `membershipId`, status | Organization/user active; role/scope allowed by policy | Organization admin/mandate grant, checked by Corporate | org/user absent, duplicate membership, insufficient mandate, forbidden | Key recommended |
| `GET /v1/organizations/{organizationId}` | none → organization profile and status | Valid ID | Organization-level authorization | not found/not visible, forbidden | Not applicable |

## Branch

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/branches` | `code`, `name`, optional approved metadata → `branchId`, status | Unique code; required fields | Branch administration permission | duplicate code, invalid data, forbidden | Key recommended |
| `GET /v1/branches/{branchId}` | none → branch metadata/status | Valid ID | Branch visibility scope | not found/not visible, forbidden | Not applicable |
| `GET /v1/branches` | filters/page → branch page | Validate filters/page bounds after policy chosen | Branch-scoped permission | invalid filter, forbidden | Not applicable |

## Account

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/accounts` | `partyId`, `accountType=PAYMENT`, `currency=VND`, optional `managingBranchId` → `accountId`, status | Customer/branch references valid through owners; only payment account in MVP; VND only | Customer self-service or authorized Teller/Admin; enforce channel, party and branch scope | invalid/unsupported type or currency, customer/branch ineligible, duplicate request, forbidden | Required for account opening; retention/scope OPEN |
| `GET /v1/accounts/{accountId}` | none → account metadata/status (no authoritative balance) | Valid ID | Account ownership/org/branch/channel scope | not found/not visible, forbidden | Not applicable |
| `POST /v1/accounts/{accountId}/blocks` | `reasonCode`, `requestId` → account status/version | Valid transition and reason per approved policy | Account-block permission; branch/org scope checked | invalid transition, not found, forbidden, concurrency conflict | Required for retryable command; key scope OPEN |

No endpoint may directly change an authoritative balance; financial posting belongs to Ledger.

## Transfer

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/transfers` | `sourceAccountId`, destination type/reference, `amount`, `currency=VND`, `channel`, optional requested processing branch → `transferId`, `transactionId`, status, correlation ID | Positive amount; VND only; account eligibility; internal/interbank route; limits/business-day rules remain OPEN | Account/org/channel/branch scope; Corporate policy/Maker–Checker when applicable | malformed/unsupported currency, ineligible account, approval required, duplicate key with different payload, conflict; external outcome may be unknown | Required; bind key to canonical payload and scope; retention OPEN |
| `GET /v1/transfers/{transferId}` | none → transfer/transaction status, timestamps, references | Valid ID | Source/destination account or organization/branch visibility scope | not found/not visible, forbidden | Not applicable |
| `POST /v1/transfers/{transferId}/cancel-requests` | `reason` → cancellation request/status | Only if cancellation transition is permitted; cancellation rules OPEN | Same transaction scope plus cancel permission | already settled, unsupported transition, forbidden, outcome unknown | Required for command retries; scope OPEN |

Timeouts must not be returned as definitive failure without verified outcome; expose an unresolved/processing state.

## Ledger

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /internal/ledger/journals` | `transactionId`, `currency`, `entries[{accountId, side, amount}]` → `journalId`, `POSTED`, `postedAt` | Positive amounts; supported VND; at least required entries; total Debit = total Credit; references validated; balanced posting | Service identity allowed to post this operation; Ledger enforces posting policy | unbalanced journal, invalid entry, account/reference failure, duplicate request, concurrency conflict | Required; unique operation key, replay returns same result |
| `POST /internal/ledger/journals/{journalId}/reversals` | `reasonCode`, `transactionId` → reversal journal reference/status | Original journal posted and not already reversed per policy; balanced reverse entries | Authorized service role; reason/actor audited | not found, already reversed, invalid state, forbidden | Required; one effective reversal per authorized reversal command |
| `GET /internal/ledger/journals/{journalId}` | none → journal header and entries | Valid ID | Ledger service-to-service identity; data scope enforced | not found, forbidden | Not applicable |
| `GET /internal/ledger/accounts/{accountId}/balance` | optional `asOf` → `accountId`, `currency`, `balance`, `asOf` | Valid account reference; VND only; consistency/as-of semantics must be defined | Authorized internal caller or explicitly authorized channel path; Ledger checks account scope | account unknown, forbidden, projection lag/unavailable | Not applicable; read only |

Posting must be atomic inside Ledger. Posted entries are immutable; no update/delete API is proposed.

## Payment

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /internal/payments` | `transferId`, destination reference, `amount`, `currency=VND`, `correlationId` → `paymentId`, submission status | Transfer is eligible for external submission; external fields per provider contract (OPEN) | Transfer service identity; Payment validates caller and operation | invalid request, provider rejection, unavailable provider, timeout/outcome unknown | Required; dedupe by business operation/provider submission scope |
| `GET /internal/payments/{paymentId}` | none → status, external reference if present, timestamps | Valid ID | Authorized internal caller and transfer scope | not found, forbidden, unresolved external outcome | Not applicable |

Provider/protocol-specific routes, callbacks and reconciliation API are intentionally unspecified.

## Approval — Corporate

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `GET /v1/organizations/{organizationId}/approval-policies` | filters → policy/version summary | Organization exists; page/filter valid | Organization admin or authorized member | not found/not visible, forbidden | Not applicable |
| `POST /v1/organizations/{organizationId}/transfers/{transferId}/approval-decisions` | `decision`, `reason`, optional `step` → `approvalId`, decision/status, history reference | Organization owns transfer; pending step exists; policy version selected; maker differs from checker where required; policy snapshot semantics OPEN | Corporate verifies organization membership, mandate, role, step and actor | organization/transfer mismatch, no pending approval, unauthorized actor, self-approval, stale policy, duplicate/conflicting decision | Required; duplicate same decision returns prior result; conflicting replay is `409` |

Approval decision is not itself a Ledger posting or settlement.

## Transaction

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `GET /v1/transactions/{transactionId}` | none → generic operation state, status history and references | Valid ID | Resource scope inherited from owning operation; enforce caller/channel/org/branch | not found/not visible, forbidden | Not applicable |
| `POST /internal/transactions` | `operationType`, `idempotencyKey`, `correlationId`, request reference → `transactionId`, initial status | Supported operation type; idempotency key and payload consistent | Authorized Transfer/Payment service identity | invalid type, duplicate conflict, forbidden | Required; same key/same payload returns existing transaction |

Transaction is proposed as the generic lifecycle record; it must not become a second source of truth for transfer-specific state.

## Audit

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /internal/audit/records` | actor, action, resource, result, correlation, timestamp → `auditRecordId`, recorded status | Required fields; redact secrets; timestamp format | Authenticated service identity; producer scope | invalid record, forbidden producer, storage unavailable | Required/deduplicated by stable event or command ID |
| `GET /v1/audit/records` | resource/actor/time filters + page → audit record page | Validate allowed filters and range | Explicit audit-read permission; tenant/org/branch scope as applicable | invalid filter, forbidden, out-of-range query | Not applicable |

If audit persistence is mandatory for an action, caller must not report success until the required durable audit write contract succeeds.

## Reporting

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `GET /v1/reports/{reportType}` | report filters/date range/page → rows, generated-at, source watermark if available | Allowlisted report/filter; validate dates and page bounds; business-day rules OPEN | Role plus organization/account/branch/channel scope; enforce in Reporting | unknown report, invalid range, forbidden, projection lag/unavailable | Not applicable for read |
| `POST /v1/reports/{reportType}/exports` | filters and output format → `reportJobId`, accepted status | Allowed report/format; bounded range per future policy | Same report-specific authorization | invalid filter/format, forbidden, capacity failure | Recommended for duplicate export jobs |

Reporting is a projection/read model, not an authoritative source for account balance or Ledger data.

## Cross-service contract requirements

- Service-to-service authentication and authorization are mandatory; protocol choice remains OPEN.
- Forward `correlationId` through synchronous calls and events.
- Never trust caller-supplied role, organization or branch scope without owner-side authorization checks.
- Do not return or log secrets, access tokens, or unnecessary PII.
- Error codes, pagination, API versioning, rate limits, idempotency scope/retention, request signing and sensitive-data classification require approval before implementation.
- See [Business Rules](../domain/BUSINESS-RULES.md), [Bounded Contexts](../domain/BOUNDED_CONTEXTS.md), [Event Contracts](../architecture/EVENT-CONTRACTS.md) and [Open Decisions](../architecture/OPEN-DECISIONS.md).
