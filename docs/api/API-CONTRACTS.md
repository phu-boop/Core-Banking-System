# API Contracts

**Status:** tài liệu này là design contract; các mục chưa triển khai vẫn là **PROPOSED**. First slices của Party, Branch và Corporate đã có business API tương ứng trong repository; Identity, Membership, Mandate, approval và các domain khác vẫn là thiết kế. REST is accepted for synchronous service-to-service calls in the first implementation slice by [ADR-012](../adr/ADR-012-phase-2-identity-party-corporate-branch.md); API Gateway technology remains OPEN. Keycloak/OIDC is the initial Identity Provider. Request/response examples are schematic; enum values, limits, routes and detailed field rules require domain review.

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
- For an API that uses idempotency, scope it by actor, operation and business scope; default retention is 24 hours. Same key/same payload replays the prior result; same key/different payload returns `409 Conflict`. Each command must state if idempotency applies and define its business scope; key format/storage are not prescribed. See [ADR-012](../adr/ADR-012-phase-2-identity-party-corporate-branch.md).

## Authentication — Identity & Access

| Method / endpoint (PROPOSED) | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `GET /v1/identity/me` | none → validated internal principal (`userId`, optional `partyId`, approved context) | Validate Keycloak OIDC token and resolve active `(issuer, subject)` mapping | Authenticated principal; only trusted grants/context returned | invalid/expired token, inactive identity, forbidden | Not applicable |

Keycloak owns login and token issuance. Use Authorization Code + PKCE for interactive user applications and Client Credentials for machine identities. Identity & Access must not expose a custom token-issuance endpoint or trust client-submitted permissions, organization or branch context. Exact claims and routes remain to be specified.

## Customer — Party / Customer

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/customers` | `partyType`, approved type-specific profile → `partyId`, status | Required fields depend on approved customer rules; duplicate candidates use soft matching and human review; no default National ID/phone/email uniqueness | Customer-creation permission for channel/actor; staff scope where relevant | invalid profile, duplicate candidate requiring review, forbidden scope, unavailable verification | If enabled, actor + operation + customer business scope; 24-hour default; exact scope and key storage are per-operation decisions |
| `GET /v1/customers/{partyId}` | none → customer profile and status | Valid ID | Customer data permission plus organization/branch scope as applicable | not found/not visible, forbidden | Not applicable |
| `PATCH /v1/customers/{partyId}` | allowed profile fields + version → updated profile/version | Only mutable fields; concurrency/version check; one primary managing branch at a time, reassignment is audited | Same resource-scoped permission as read plus update grant | invalid field, stale version, forbidden, not found | Optimistic version prevents lost update; if command idempotency is used, apply the ADR-012 scope/retention rule |

## Corporate

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/organizations` | `partyId` → `organizationId`, status | Party must be type `ORGANIZATION`; exactly one logical Party reference per Organization and one-to-one mapping; legal profile fields TBD | Corporate onboarding permission | invalid/missing Party, wrong Party type, duplicate organization, forbidden | If enabled, actor + operation + organization business scope; 24-hour default; exact scope and key storage are per-operation decisions |
| `POST /v1/organizations/{organizationId}/members` | `userId`, membership role/scope → `membershipId`, status | Organization/user active; role/scope allowed by policy | Organization admin/mandate grant, checked by Corporate | org/user absent, duplicate membership, insufficient mandate, forbidden | Key recommended |
| `GET /v1/organizations/{organizationId}` | none → organization profile and status | Valid ID | Organization-level authorization | not found/not visible, forbidden | Not applicable |

The Corporate Organization first slice implements `partyId` and `ACTIVE` status. Corporate now also implements policy creation, listing and activation; legal profile data, Membership, Mandate, approval requests and decisions remain excluded. Corporate validates the logical Party reference by calling Party's owner API with a Keycloak Client Credentials token. The service client must be configured for the Party API audience and `party:read`; exact Keycloak client/claim mapping remains OPEN.

## Branch

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/branches` | `code`, `name`, optional approved metadata → `branchId`, status | Unique code; required fields | Branch administration permission | duplicate code, invalid data, forbidden | Key recommended |
| `GET /v1/branches/{branchId}` | none → branch metadata/status | Valid ID | Branch visibility scope | not found/not visible, forbidden | Not applicable |
| `GET /v1/branches` | optional `status`, `page` (default `0`), `size` (default `20`) → branch page | `page >= 0`; `1 <= size <= 100`; status is `ACTIVE` or `INACTIVE` | Branch-scoped permission; list includes only branches visible to the caller unless caller has global branch-read permission | invalid filter/page, forbidden | Not applicable |

The Branch first slice uppercases trimmed codes, trims names, and treats codes as case-insensitively unique. Its current `branch_permissions` / `branch_grants` JWT claim mapping is provisional; the canonical permission catalog and Identity/Keycloak claim mapping remain OPEN. Branch status transitions are not implemented in this slice.

## Account

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/accounts` | `partyId`, `accountType=PAYMENT`, `currency=VND` → `accountId`, status | Customer reference valid through its owner; only payment account in MVP; VND only | Customer self-service or authorized Teller/Admin; enforce channel, party and branch scope | invalid/unsupported type or currency, customer ineligible, duplicate request, forbidden | Required for account opening; actor + operation + business scope, 24-hour default; exact scope to define |
| `GET /v1/accounts/{accountId}` | none → account metadata/status (no authoritative balance) | Valid ID | Account ownership/org/branch/channel scope | not found/not visible, forbidden | Not applicable |
| `POST /v1/accounts/{accountId}/blocks` | `reasonCode`, `requestId` → account status/version | Valid transition and reason per approved policy | Account-block permission; branch/org scope checked | invalid transition, not found, forbidden, concurrency conflict | Required for retryable command; actor + operation + business scope, 24-hour default; exact scope to define |

No endpoint may directly change an authoritative balance; financial posting belongs to Ledger.

## Transfer

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `POST /v1/transfers` | `sourceAccountId`, destination type/reference, `amount`, `currency=VND`, `channel`, optional requested processing branch → `transferId`, `transactionId`, status, correlation ID | Positive amount; VND only; account eligibility; internal/interbank route; limits/business-day rules remain OPEN | Account/org/channel/branch scope; Corporate policy/Maker–Checker when applicable | malformed/unsupported currency, ineligible account, approval required, duplicate key with different payload, conflict; external outcome may be unknown | Required; actor + operation + business scope, 24-hour default; exact scope to define |
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

Provider/protocol-specific routes, callbacks and reconciliation API are intentionally unspecified. REST is the synchronous internal API default for the first implementation slice; event transport remains OPEN.

## Approval — Corporate core

The Corporate Approval core accepts an opaque operation type/reference from the owning service and does not execute the operation. Policy writes require an organization-scoped `approval-policy:manage` grant represented by the existing provisional `corporate_grants` convention (`permission`, `scopeType: "ORGANIZATION"`, `scopeId`). Checker decisions require an active Membership and Mandate matching the current level's role/mandate scope. Canonical Keycloak claim mapping and the owner-service request permission remain subject to Identity integration review. Policy administration endpoints below are implemented; approval request, decision and history-read endpoints remain proposed.

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `GET /v1/organizations/{organizationId}/approval-policies` **(implemented)** | optional `operationType` → policy/version list | Organization exists; operation type is an opaque non-whitespace value of 1–64 characters | Organization-scoped `approval-policy:manage` grant | organization not found, invalid filter, forbidden | Not applicable |
| `POST /v1/organizations/{organizationId}/approval-policies` **(implemented)** | `operationType`, ordered `levels[]` → `policyId`, `version`, `status=DRAFT` | Operation type is opaque and 1–64 characters; at least one level with a valid role code and mandate scope; one active version per organization/operation type | Organization-scoped `approval-policy:manage` grant | invalid policy (`422`), forbidden (`403`), conflict (`409`), organization not found (`404`) | Not implemented |
| `POST /v1/organizations/{organizationId}/approval-policies/{policyId}/activate` **(implemented)** | none → policy/version/status | Draft or inactive policy belongs to organization; activation replaces prior active version for that operation type; active policy version fields and levels are immutable | Organization-scoped `approval-policy:manage` grant | not found (`404`), invalid lifecycle transition (`409`), forbidden (`403`) | Repeated activation of the active version is idempotent |
| `POST /v1/organizations/{organizationId}/approval-requests` **(proposed)** | owner-supplied `operationType`, `operationReference` → `approvalId`, pinned `policyVersion`, status | Active policy exists for operation type; request ID/reference valid | Authorized owning service / organization context; exact claim mapping remains OPEN | no active policy, invalid reference, forbidden | Operation-scoped key to be finalized |
| `POST /v1/organizations/{organizationId}/approval-requests/{approvalId}/decisions` **(proposed)** | `decision`, optional `reason` → status, current level, history reference | Request pending; current level only; active policy version is pinned; rejection is terminal | Corporate verifies active Membership/Mandate, role/mandate scope, and Maker–Checker | not found/not visible, out of order, unauthorized actor, self-approval, terminal request, conflicting replay | Required; same decision replays; conflicting replay is `409` |
| `GET /v1/organizations/{organizationId}/approval-requests/{approvalId}/history` **(proposed)** | none → append-only decision history | Request belongs to organization | Organization-scoped history-read permission to be finalized | not found/not visible, forbidden | Not applicable |

Implemented policy creation and activation are audited in `organization_audit` in the same transaction. The database runtime role cannot update policy version fields or policy-level rows; it can only change a policy's lifecycle status and activation timestamp.

The transfer-specific route below is a deferred integration proposal, not part of the current Corporate core implementation slice.

| Method / endpoint | Request → response | Validation | Authorization | Error cases | Idempotency |
|---|---|---|---|---|---|
| `GET /v1/organizations/{organizationId}/approval-policies` | filters → policy/version summary | Organization exists; page/filter valid | Organization admin or authorized member | not found/not visible, forbidden | Not applicable |
| `POST /v1/organizations/{organizationId}/transfers/{transferId}/approval-decisions` | `decision`, `reason`, optional `step` → `approvalId`, decision/status, history reference | Organization owns transfer; pending step exists; request remains bound to policy version selected at submission; sequential levels; maker differs from checker | Corporate verifies organization membership, mandate, role, step and actor | organization/transfer mismatch, no pending step, unauthorized actor, self-approval, terminal rejection, duplicate/conflicting decision | Required; duplicate same decision returns prior result; conflicting replay is `409` |

Per [ADR-007](../adr/ADR-007-configurable-corporate-approval.md), approval levels are sequential and all required levels must approve; rejection is terminal, resubmission creates a new request ID, and expiry/delegation are excluded from the MVP. Requests pin the immutable policy version selected at submission. Policy lifecycle and approver eligibility remain subject to the feature-spec clarifications. Approval decision is not itself a Ledger posting or settlement; Transfer integration is outside the Corporate Approval core slice.

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
