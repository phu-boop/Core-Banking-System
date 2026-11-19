# Phase 2 — Decision Gate

**Status:** APPROVED BASELINE — see [ADR-012](../../adr/ADR-012-phase-2-identity-party-corporate-branch.md) for the canonical decision record. This document tracks contract implications and unresolved implementation details; it is not a substitute for the ADR.

## Purpose

Track the approved first-slice boundaries for Identity, Party, Corporate and Branch and the unresolved details that continue to gate specific migrations or APIs.

## Confirmed constraints

- Each context owns its schema: `identity_schema`, `party_schema`, `corporate_schema` and `branch_schema`.
- No cross-schema foreign keys, shared domain tables or direct cross-service table access.
- Identity & Access owns local user identity mappings and authorization grants; it does not own customer profiles, organizations, branches or accounts.
- Party owns person/customer identity and customer profile data.
- Corporate owns organizations, memberships, mandates, approval policies and approval history.
- Branch owns branch records and lifecycle.
- Resource-owning services enforce authorization for their resources; a gateway is not the sole authorization boundary.
- A managing branch and a transaction-processing branch are distinct. Teller access is branch-scoped.
- Keycloak/OIDC is the initial Identity Provider. Keycloak owns authentication and token issuance; no custom credential store or token issuer is introduced.
- REST is the synchronous internal API protocol for the first implementation slice; event transport remains OPEN.
- A customer has one primary managing branch at a time; reassignment is explicit and audited.
- Each Corporate Organization maps 1:1 to one Party of type `ORGANIZATION` through a logical reference.
- Party duplicate handling is soft-match with human review; no default National ID/phone/email uniqueness or automatic merge/delete.
- Idempotency, when used, is scoped by actor, operation and business scope, with 24-hour default retention.
- Sensitive audit records include actor, action, resource, result, timestamp and correlation ID; seven years is the project retention baseline subject to legal review.

These constraints follow [ADR-002](../../adr/ADR-002-retail-corporate-banking.md), [ADR-003](../../adr/ADR-003-multi-channel.md), [ADR-004](../../adr/ADR-004-shared-postgresql-schema-per-service.md), [ADR-009](../../adr/ADR-009-multi-branch-banking.md) and [Phase 2 plan](./Phase-2-Identity-Party-Corporate-Branch.md).

## Approved first-slice domain contract

The ownership and uniqueness boundaries below are approved. Exact schema fields, routes and DTOs remain subject to the unresolved-item gate.

| Context | Initial aggregate / record | Approved identity and uniqueness boundary |
|---|---|---|
| Identity & Access | User identity mapped to an external subject; grants; optional `partyId` in the trusted principal | Unique `(issuer, subject)`; opaque `userId`; Keycloak owns credentials and tokens |
| Party / Customer | Party with type/status and only approved individual profile fields | Opaque Party ID; single primary managing branch at a time; soft-match/manual review, no default National ID uniqueness |
| Corporate | Organization, membership, mandate and approval records | Opaque Organization ID; required 1:1 logical reference to one Party of type `ORGANIZATION` |
| Branch | Branch with code, name and lifecycle status | Opaque Branch ID; code unique within the bank |

### Approved authorization principal boundary

The internal, validated principal may include:

- `userId`: stable internal identity reference.
- `issuer` and `subject`: validated external identity reference, not caller-controlled body fields.
- `partyId`: optional link to a Party, if the user represents a customer.
- Roles and scoped grants: loaded/validated through the Identity boundary; resource owners make final authorization decisions.
- Organization, branch and channel context: validated references, not trusted solely because they appear in a token or request.

Use RBAC with `SELF`, `ORGANIZATION` and `BRANCH` scoped grants. Resource owners make final authorization decisions. Full role/permission vocabulary, grant inheritance, exact token claims and branch assignment lifecycle remain OPEN.

## API and event scope — exact contracts remain open

- Identity: resolve the Keycloak-validated external subject to an active internal `userId` and approved principal context. Token issuance and login stay in Keycloak.
- Party: create/read/update a customer Party and only approved individual profile fields, with resource-level authorization and optimistic version checks for updates. The first implementation is available in `party-service`; PII fields and match thresholds remain OPEN.
- Corporate: create/read an Organization and manage membership/mandate under organization-scoped permissions. Approval execution is not included in this first slice.
- Branch: create/read/list branch records and enforce branch-administration permissions. The first implementation is available in `branch-service`; claim mapping and lifecycle transitions remain OPEN.
- Events: `PartyCreated` remains a proposed event. Do not select or implement event transport until broker approval; synchronous owner APIs may be used where required by an approved flow.
- Each command API states whether it uses idempotency and defines its business scope. When used, apply actor + operation + business scope, 24-hour default retention, same-payload replay and different-payload `409` behavior. Key format/storage are not prescribed.

Exact routes, request/response fields, error mapping and event payloads remain subject to review in [API Contracts](../../api/API-CONTRACTS.md) and [Event Contracts](../../architecture/EVENT-CONTRACTS.md).

## Remaining OPEN implementation details

| Detail | Status | Blocks |
|---|---|---|
| Keycloak user populations, MFA/federation, hosting and environment configuration | OPEN | Identity deployment/config and production security |
| Identity lifecycle states and synchronization/disable behavior with Keycloak | OPEN | Identity mapping and lifecycle API/schema |
| Approved PII profile fields, match attributes, weights, thresholds and retention | OPEN | Party schema, validation and duplicate review |
| Jurisdiction-specific organization legal identifiers/uniqueness | OPEN | Corporate schema and onboarding |
| Full role catalog, permission vocabulary and grant inheritance | OPEN | Detailed grant model and endpoint authorization |
| Canonical Keycloak claim mapping for the first-slice Branch permissions and scoped grants | OPEN | Production authorization integration; current `branch_permissions` / `branch_grants` mapping is provisional |
| Managing-branch assignment timing, reassignment workflow/effective date | OPEN | Party schema constraints and branch-change API |
| Idempotency key format, storage and operation-specific business scopes | OPEN | Per-command schema/API implementation |
| Audit storage, tamper-evidence, read access matrix, retention enforcement and archival | OPEN | Durable audit implementation and production operations |
| Exact routes, DTOs, error codes, versioning and per-operation audit requirements | OPEN | Corresponding API implementation |
| Event broker, event transport, `PartyCreated` payload and Outbox publication | OPEN | Event-driven integration |
| Approval execution/policy snapshot and all Account/Ledger/Transfer behavior | OPEN / out of this slice | Later domain implementation |

## Gate outcome

2.1 can be marked complete when:

1. The project owner approves or explicitly defers each decision needed by the selected first implementation slice.
2. The approved domain boundaries and principal contract are reflected consistently in the bounded contexts, ERD, API contracts and ADR.
3. This approved decision baseline is complete. Detailed aggregate fields and per-operation constraints are specified immediately before the affected implementation.
4. No OPEN choice is silently treated as an implementation requirement.

The nine approved decisions authorize architecture/contract updates and implementation planning. They do not authorize a migration or implementation that relies on an unresolved item; that item blocks only its affected slice.
