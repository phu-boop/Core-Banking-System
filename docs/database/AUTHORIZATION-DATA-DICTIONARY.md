# Authorization Data Dictionary

**Status:** Implemented for the initial shared Role and Permission Catalog under [ADR-014](../adr/ADR-014-role-permission-and-identity-grant-framework.md). Catalog and assignment administration APIs remain deferred.

## Identity schema

| Table | Columns and constraints | Runtime behavior |
|---|---|---|
| `identity_schema.user_identity` | `user_id UUID` PK; non-empty `issuer`/`subject`; optional `party_id`; status `ACTIVE`/`DISABLED`; unique `(issuer, subject)` | Only active mappings resolve. Party reference remains logical; no cross-schema FK. |
| `identity_schema.authorization_grant` | PK `(user_id, permission, scope_type, scope_id)`; scopes limited to `SELF`, `ORGANIZATION`, `BRANCH` | Existing grant rows are preserved. A row is returned only when its permission has catalog status `ACTIVE`. Role-derived grants are not written here. |
| `identity_schema.identity_audit` | `audit_id`, actor/action/resource/result/time/correlation ID | Current resolution audit; not yet a controlled Role-assignment audit workflow. |
| `identity_schema.permission_catalog` | Stable `permission_code` PK; status `ACTIVE`, `PENDING_IMPLEMENTATION`, or `RESTRICTED`; description and creation time | Only `ACTIVE` permissions can be effective. No wildcard or implicit permission inheritance. |
| `identity_schema.role_catalog` | Stable `role_code` PK; display name, descriptive category, description, status `ACTIVE`/`INACTIVE` | Shared global roles. Category is descriptive metadata, not proof of customer, employee, corporate-user, or system identity. |
| `identity_schema.role_version` | UUID `role_version_id` PK; role FK; positive version; `DRAFT`/`PUBLISHED`; created/published actor and time; unique `(role_code, version)` | DB triggers permit only DRAFT-to-PUBLISHED and reject updates/deletes after publication. |
| `identity_schema.role_version_permission` | PK `(role_version_id, permission_code)`; FKs to Role Version and Permission | Changes are rejected for a published Role Version. |
| `identity_schema.role_assignment` | UUID assignment PK; `user_id` FK; published `role_version_id` FK; scope type/id; status `ACTIVE`/`REVOKED`; `assigned_by`, `assigned_at`, `valid_from`, optional `expires_at`, revocation actor/time/reason | Scope constraint accepts only `SELF`, `ORGANIZATION`, `BRANCH`; assignment trigger rejects draft/inactive Role Versions. Expired/revoked assignments are ignored at resolution. |

The Identity runtime role is granted `SELECT` on the catalog and assignment tables. It cannot write these tables. `identity_service_migrator` owns schema changes and catalog seed operations.

## Seeded Role Versions and matrix

V4 seeds these ten shared Roles. No Role Assignment is seeded, and no default user is assigned a privileged Role.

| Role | Version 1 / candidate permission set | State and intended scope |
|---|---|---|
| `RETAIL_CUSTOMER` | `account:read` | Published; `SELF` only in the currently verified Account read flow. |
| `RETAIL_CUSTOMER` | `account:read`, `transfer:submit` | Version 2 is DRAFT; proposed `SELF` scope, pending Transfer authorization. |
| `CORPORATE_USER` | `organization:read`, `approval-request:submit` | DRAFT; proposed `ORGANIZATION` scope. This does not grant Membership, Mandate, or approval decisions. |
| `BANK_TELLER` | `branch:read`, `account:create`, `transfer:submit` | DRAFT; proposed `BRANCH` scope. |
| `BRANCH_MANAGER` | `branch:read`, `branch:admin`, `account:manage`, `transfer:execute` | DRAFT; proposed `BRANCH` scope; transfer execution is RESTRICTED. |
| `CUSTOMER_SUPPORT` | `party:read`, `party:update` | DRAFT; resource ownership enforcement is incomplete. |
| `OPERATIONS_OFFICER` | `transfer:read`, `payment:reconcile`, `ledger:post`, `ledger:reverse` | DRAFT; ledger posting/reversal and payment reconciliation are RESTRICTED. |
| `PAYMENT_OPERATOR` | `payment:submit`, `payment:reconcile`, `payment:outcome:read` | DRAFT; payment submission and reconciliation are RESTRICTED. |
| `BANK_AUDITOR` | `audit:read`, `report:account:read`, `report:transaction:read`, `report:audit:read`, `report:operational:read`, `ledger:balance:read` | DRAFT; proposed branch-scoped access only. |
| `SECURITY_ADMIN` | `identity:principal:read`, `identity:role:read/manage/assign/revoke`, `identity:grant:read/manage` | DRAFT; role/grant management is RESTRICTED. |
| `PLATFORM_ADMIN` | No permissions | DRAFT. No wildcard or system-wide authority is inferred. |

Only permissions with status `ACTIVE` are returned by Identity. DRAFT versions cannot be assigned. A permission becoming `ACTIVE` does not publish a DRAFT Role Version; permissions must be reviewed and a version explicitly published. Published versions are immutable; permission-set changes require a new version.

## Permission status inventory

Statuses reflect Identity-backed authorization, not merely a JWT claim, API route, or existing database row.

| Status | Canonical codes | Evidence / limitation |
|---|---|---|
| `ACTIVE` | `account:read` | Verified only for a `SELF` grant whose `scopeId` matches the mapped Party and whose Party owns the Account. |
| `PENDING_IMPLEMENTATION` | `identity:principal:read`, `identity:role:read`, `identity:grant:read`; `party:create`, `party:read`, `party:update`, `party:self`; `account:create`, `account:manage`; `branch:read`, `branch:update`, `branch:admin`, `branch:manage`; `transfer:read`, `transfer:create`, `transfer:submit`; `payment:outcome:read`; `ledger:balance:read`; `organization:create`, `organization:read`, `approval-request:submit`; `audit:read`; `report:account:read`, `report:transaction:read`, `report:audit:read`, `report:operational:read` | Current code uses JWT claims, has incomplete resource checks, or has no corresponding operation. Identity does not yet back these APIs. |
| `RESTRICTED` | `identity:role:manage`, `identity:role:assign`, `identity:role:revoke`, `identity:grant:manage`; `transfer:execute`; `payment:submit`, `payment:reconcile`; `ledger:post`, `ledger:reverse`; `membership:manage`, `approval-policy:manage`; `audit:write` | Sensitive operations require a separately controlled workflow. These permissions are never effective through the initial generic Role resolution. |

Existing runtime codes are kept canonical. Semantic aliases are not duplicated: Branch creation continues to use `branch:admin` rather than adding `branch:create`; Corporate continues to use `organization:read` and `membership:manage` rather than duplicate `corporate:*` aliases; Reporting uses the four supported `report:<type>:read` codes rather than a blanket `reporting:read`. The codebase also has `customer:read` in an Identity test fixture only, not as a runtime authorization code.

## Grant resolution and provenance

1. Identity returns the union of active legacy grants and active Role-derived grants, removing duplicate `(permission, scope type, scope ID)` tuples from the response.
2. A legacy grant is returned only if its Permission status is `ACTIVE`. V4 preserves existing grant rows and inserts unknown legacy codes into the catalog as `PENDING_IMPLEMENTATION`; it does not delete or update the grant rows.
3. A Role-derived grant requires an active `user_identity`, active Role, published Role Version, `ACTIVE` Permission, active assignment, `valid_from <= now`, no revocation, and either no expiry or `now < expires_at`.
4. Each `role_assignment` row is an independent provenance source. Resolution does not materialize Role grants into `authorization_grant`; revoking/expiring one assignment therefore leaves an identical grant supplied by another active assignment.
5. No direct-grant API or provisioning workflow is implemented. No cross-schema foreign keys, global scope, inheritance, or implicit scope expansion are added.

## Corporate authorization boundary

Corporate Membership, `role_code`, Mandate, Maker/Checker and Approval Policy remain Corporate-owned. A global Role never independently establishes organization membership, mandate authority, or approval eligibility. Corporate still enforces active Membership/Mandate and its approval workflow.

## Migration and initialization

- V1/V2 are unchanged. V3 creates the additive catalog/version/assignment schema and runtime `SELECT` grants; V4 idempotently seeds Permissions and Roles.
- On a fresh database, run `database/bootstrap/identity-service-ownership.sql` once, then apply Flyway using `identity_service_migrator`.
- On an existing database, do not rerun ownership bootstrap; apply pending Flyway migrations using the migrator credentials. Do not use `identity_service_app` for migrations or provisioning.
- Application startup leaves Flyway disabled. See [Identity Service README](../../services/identity-service/README.md) for build and migration commands.
