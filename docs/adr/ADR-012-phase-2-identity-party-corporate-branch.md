# ADR-012: Phase 2 Identity, Party, Corporate and Branch Decisions

- **Status:** ACCEPTED
- **Decision date:** 2026-11-19
- **Approval authority:** Project Owner
- **Approval record:** Nine decisions approved on 2026-11-19. This ADR records the approval; it is not a cryptographic signature or evidence of deployment.

## Context

Phase 2 requires approved identity, authorization, party, organization, branch, idempotency, audit and synchronous communication boundaries before service implementation. Existing ADRs establish the overall domain scope and service/schema ownership but leave these first-slice choices open.

## Decisions

### 1. Identity Provider

Use Keycloak as the initial Identity Provider and OpenID Connect (OIDC) for authentication.

- Use Authorization Code with PKCE for interactive user applications.
- Use Client Credentials for service-to-service machine identities.
- Identity & Access maps a validated `(issuer, subject)` pair to an opaque internal `userId`; the pair is unique in the identity mapping.
- Keycloak owns login, password management and token issuance. Identity & Access does not store passwords or mint tokens.
- This provider selection does not approve hosting/deployment topology or environment-specific configuration; define those separately.

### 2. Customer managing branch

A customer has one primary managing branch at a time. `managingBranchId` is distinct from `transactionBranchId`, the branch where a transaction is processed.

- Changing a customer's managing branch is an explicit, audited operation.
- Teller access is branch-scoped and is not inferred from the customer's managing branch.
- Cross-branch processing requires explicit authorization.

### 3. Organization-to-Party mapping

Corporate owns the Organization aggregate. Each Organization references exactly one Party using a logical `partyId`.

- In the first implementation slice, one Organization maps to one Party of type `ORGANIZATION`.
- The reference is not a cross-schema foreign key; Corporate validates it through an owner-controlled API or approved integration contract.
- Corporate owns Organization, membership and mandate data; Party owns shared party identity and approved profile data.
- Lifecycle synchronization is a separate contract concern if needed.

### 4. Customer Party link

`partyId` is optional in the internal identity principal. Staff, administrators and service identities may exist without a customer Party; corporate users may be linked to an individual Party without conflating that link with Corporate membership.

### 5. PII duplicate policy

Use soft-match duplicate detection with human review.

- National ID is not a default unique key; phone number and email are not assumed globally unique.
- Duplicate candidates may be reviewed but are not automatically merged or deleted.
- Organization legal-identity uniqueness requires a separately approved jurisdiction and identifier policy.
- PII fields, match attributes, weights, thresholds and retention remain open; this ADR authorizes no National ID uniqueness constraint.

### 6. Authorization

Use RBAC combined with scoped grants and resource-level authorization. Initial scope vocabulary is `SELF`, `ORGANIZATION` and `BRANCH`.

- Identity & Access owns user identity mappings and grants.
- Each resource-owning service enforces authorization for its own resources.
- Authorization considers a validated principal, required permission, applicable scope, validated organization/branch context and resource-specific rules.
- Client-supplied permissions, organization or branch context are not trusted as authorization facts.
- Grant inheritance, the full role catalog and detailed permission vocabulary remain open.

### 7. Idempotency

Where an API uses idempotency, scope it by actor, operation and business scope. The default retention is 24 hours.

- Same key and same request payload returns the previously recorded outcome.
- Same key and different payload returns `409 Conflict`.
- A retry must not create a duplicate business resource.
- Each command documents whether idempotency is required and its business scope. This ADR does not prescribe a global key or shared idempotency table.
- Key format, storage schema and operation-specific scopes remain open.

### 8. Audit

Audit records for sensitive operations capture at least actor, action, resource type and identifier, result, timestamp and correlation ID. Include organization, channel and branch context where applicable.

- Ordinary business APIs cannot edit audit records; audit reads require separate authorization.
- Never record passwords, access tokens or refresh tokens; avoid unnecessary PII.
- Seven years is the project retention baseline, subject to jurisdiction-specific legal and compliance review before production.
- Storage, tamper-evidence, retention enforcement and archival remain implementation details or separate decisions.

### 9. Internal API protocol

Use REST for synchronous service-to-service commands and queries in the first implementation slice. gRPC is not included in that slice.

- Contracts specify HTTP methods, paths, request/response schemas and error behavior.
- Clients use explicit timeouts and error handling.
- Event contracts remain transport-independent. Broker selection, event transport and Outbox implementation remain open.

## Consequences

- The approved boundaries permit architecture, ERD and API contract updates and implementation planning for the approved first slice.
- No migration or implementation may assume unresolved profile fields, matching thresholds, legal identifiers, routes/DTOs, role catalog, grant inheritance, idempotency key format/scope, audit storage/access, event transport or onboarding workflow.
- Before a migration, each aggregate must have ownership, identity/uniqueness rules, required fields, relationship strategy, authorization, idempotency and audit requirements defined as applicable.
- An unresolved item blocks only the implementation slice it affects; it does not invalidate these accepted decisions.

## Dependencies

- [ADR-002](./ADR-002-retail-corporate-banking.md), [ADR-003](./ADR-003-multi-channel.md), [ADR-004](./ADR-004-shared-postgresql-schema-per-service.md), and [ADR-009](./ADR-009-multi-branch-banking.md).
- [Phase 2 implementation plan](../plan/phase/Phase-2-Identity-Party-Corporate-Branch.md).
- [Phase 2 decision gate](../plan/phase/Phase-2-Decision-Gate.md).
- API Gateway technology, production Keycloak hosting, detailed permissions, event broker and exact production security/compliance requirements remain outside this decision.
