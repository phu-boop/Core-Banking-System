# ADR-014: Role, Permission and Identity Grant Framework

- **Status:** ACCEPTED for the initial shared Role and Permission Catalog
- **Decision date:** 2026-11-26
- **Approval authority:** Project Owner
- **Approval record:** The Project Owner approved the implementation plan, canonical-code policy, legacy-grant handling and initial Role matrix on 2026-11-26.
- **Scope:** Identity authorization catalog and user grant provenance

## Context

[ADR-012](./ADR-012-phase-2-identity-party-corporate-branch.md) assigns identity mappings and grants to Identity & Access and requires resource-owning services to enforce authorization. [ADR-013](./ADR-013-identity-authoritative-user-authorization.md) makes Identity the source of user grants for the first customer account-read slice. Other resource-service operations still use transitional service-specific JWT claims.

Before this decision was implemented, the Identity schema contained only `user_identity`, `authorization_grant`, and `identity_audit`. Migrations V3/V4 now add the shared catalog, version, assignment, and seed model described below. The runtime Identity database role remains read-only. Corporate separately owns organization Membership and Mandate records used by approval workflows.

## Decisions

1. **Extend Identity Service; do not create another authorization service.** Identity owns the shared Permission Catalog, shared Role Catalog, Role versions, Role Assignments, and effective user grants.
2. **Use one shared Role Catalog in the initial scope.** Organization-specific Corporate Roles are not included. This constraint was selected by the Project Owner on 2026-11-26. Corporate continues to own organization Membership, `role_code`, and Mandate; these remain distinct from global Identity Roles.
3. **Keep effective grants as the runtime authorization representation.** Resource services consume grants and remain responsible for resource ownership, organization/branch isolation, transaction state, and other domain rules. Identity does not make resource-level decisions.
4. **Treat published Role versions as immutable.** A permission change creates a new version. Existing assignments do not silently acquire or lose permissions; migration between versions is explicit and audited.
5. **Treat each Role Assignment as the source for its derived grants.** Resolve grants from Role Assignment → published Role Version → Permission, deduplicating only the returned permission/scope tuple. Keep each assignment row independent; revoking or expiring one assignment must not remove a permission still supplied by another active assignment. Role-derived grants are not written into the legacy `authorization_grant` table.
6. **Preserve the existing scope vocabulary:** `SELF`, `ORGANIZATION`, and `BRANCH`. Do not add `REGION`, `GLOBAL`, inheritance, or implicit scope expansion in this decision.
7. **Fail closed.** Disabled principals, revoked assignments, unsupported scopes, and expired grant sources are not effective. Expiration must be checked when resolving permissions; a scheduled status update is operational cleanup, not the authorization boundary.
8. **Use explicit Permission lifecycle status.** Only `ACTIVE` permissions from published Role Versions are returned. `PENDING_IMPLEMENTATION` and `RESTRICTED` permissions are not effective. Do not promote a permission based only on a JWT claim or a database row.
9. **Keep provisioning controlled.** This implementation adds no public assignment or direct-grant API. The runtime read-only database role remains read-only; any future provisioning workflow needs a separately approved authority model, durable audit, and least-privilege writer identity.
10. **Preserve legacy grants without silently activating unknown codes.** Do not update or delete existing `authorization_grant` rows. Seed previously unknown codes as `PENDING_IMPLEMENTATION`; grant resolution returns a legacy row only when its catalog status is `ACTIVE`.
11. **Do not infer principal type from role category.** `CUSTOMER`, `EMPLOYEE`, `CORPORATE`, and `SYSTEM` are descriptive metadata only. Reliable principal classification and employee/branch associations require approved onboarding and source-of-truth contracts.

## Implemented logical model

The implemented fields and constraints are recorded in [Authorization Data Dictionary](../database/AUTHORIZATION-DATA-DICTIONARY.md). The model extends the existing Identity schema; it does not recreate `user_identity` or `authorization_grant`.

- `permission_catalog`: stable code and explicit `ACTIVE`, `PENDING_IMPLEMENTATION`, or `RESTRICTED` status.
- `role_catalog`: globally reusable Role definitions; no organization-owned Identity Roles.
- `role_version`: draft or published version; published versions are immutable.
- `role_version_permission`: permissions included in a specific Role version.
- `role_assignment`: user, published Role version, supported scope, assigner and effective/revocation lifecycle.
- `authorization_grant`: existing legacy grant table; preserved, not used to materialize Role-derived grants.
- Role Assignment rows remain separate provenance sources; Identity resolves and deduplicates their effective tuples at read time.

The initial catalog includes all ten requested global roles. Only `RETAIL_CUSTOMER` version 1 is published, containing `account:read` (`SELF`); `RETAIL_CUSTOMER` version 2 and the other initial versions remain `DRAFT` because their permissions are not yet enforced through Identity. No Role Assignment is seeded. The approved candidate matrix and permission lifecycle inventory are recorded in the data dictionary.

The `user_identity.party_id` relationship and scope IDs remain logical cross-service references without cross-schema foreign keys. Scope IDs must be validated against an authoritative owner API or approved provisioning workflow, not trusted solely because they appear in a request.

## Corporate approval boundary

Identity Roles may gate access to a general function, but do not grant Corporate Membership or approval eligibility. Corporate continues to verify active Membership and Mandate, the current approval step, and maker/checker separation. This implementation does not add custom organization Roles or replace Corporate's `role_code` and `mandate_scope` model.

## Consequences

- Permission definitions and role assignment become centralized without moving resource authorization out of the owning services.
- Existing legacy Identity grants remain stored. Only catalog entries with `ACTIVE` status are returned; unknown existing codes are preserved as `PENDING_IMPLEMENTATION`.
- Role Assignment rows provide grant provenance, so overlapping assignments can be revoked independently.
- Existing service-specific JWT claims remain transitional until each operation has a tested Identity-based replacement. No claim is removed.
- Role categories do not prove a user's principal type, and the absence of `GLOBAL` means security/platform roles cannot yet be assigned system-wide.

## Not decided by this ADR

- Who may create/publish roles, assign/revoke roles, or issue direct grants, and whether approvals or separation of duties are required for those operations.
- Whether direct grants should be supported in a future controlled workflow, including lifetime and audit requirements.
- Role-version migration policy for existing assignments.
- Authoritative customer/employee/corporate principal classification and employee-to-branch data source.
- Exact API contracts, operator workflow, idempotency requirements, and error responses.
- Grant cache policy, revocation latency, outage behavior for migrated services, and any event consumers.
- Whether `identity_audit` needs structured before/after state and reason fields.

## Implementation boundary

The initial catalog and resolver are implemented by Identity migrations V3/V4. No default user is assigned a Role. No public catalog, assignment or direct-grant API is included. Resource-service JWT authorization, missing resource ownership checks, and Corporate authorization remain outside this implementation and are tracked as pending or restricted permissions.

## Related decisions

- [ADR-012](./ADR-012-phase-2-identity-party-corporate-branch.md) — Identity ownership, scopes, and resource-service enforcement.
- [ADR-013](./ADR-013-identity-authoritative-user-authorization.md) — Identity-authoritative grants for the initial Account read slice.
- [Logical ERD](../database/ERD.md) and [Authorization Data Dictionary](../database/AUTHORIZATION-DATA-DICTIONARY.md).
