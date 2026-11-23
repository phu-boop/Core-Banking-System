# ADR-013: Identity-Authoritative User Authorization

- **Status:** ACCEPTED for the first customer account-read slice
- **Decision date:** 2026-11-23
- **Approval authority:** Project Owner
- **Approval record:** The project owner selected Identity as the source of user grants and approved implementation of the first vertical slice on 2026-11-23.

## Context

ADR-012 assigns identity mappings and grants to Identity & Access and requires each resource-owning service to enforce authorization. Current service implementations also contain provisional, service-specific permission claims in Keycloak access tokens. Identity's `/v1/identity/me` returns grants, but resource services did not yet use them.

## Decisions

1. Identity Service is the authoritative source for the validated internal principal and its scoped grants. Keycloak remains responsible for authentication and token issuance.
2. A resource service validates its own access token, then forwards that bearer token to Identity's `GET /v1/identity/me`. The token audience must include both `identity-service` and the resource service.
3. Each resource-owning service makes its own resource-level decision from the returned principal and grants. The frontend and request-body values are never authorization authorities.
4. Identity lookup failure is fail-closed. A missing or inactive mapping is denied; an unavailable Identity Service does not cause a success-shaped fallback. The first implementation does not cache principals or grants.
5. For customer account reads, an `account:read` grant with `scopeType=SELF` and `scopeId=<partyId>` permits reads only when the account's owner `partyId` equals the mapped principal's `partyId`.
6. This first slice covers only customer account reads. Existing service-specific JWT-claim checks for other operations are transitional and remain outside this decision's implementation scope; they must not be interpreted as proof that all authorization paths have migrated to Identity.
7. Identity mapping and grant provisioning remains restricted to an explicitly controlled, audited operator process. This ADR does not authorize self-registration, a public provisioning endpoint, or use of the runtime database role for writes.

## Consequences

- User-facing OIDC clients calling a resource service must receive the audience required by both that service and Identity.
- Resource services gain a synchronous dependency on Identity for the migrated authorization paths and must set bounded timeouts and fail closed.
- Permission vocabulary, role catalog, grant lifecycle, and audited provisioning workflow remain separate decisions before migrating staff, administrator, corporate, and write operations.
- Service-specific JWT claims are not removed until each associated operation has a tested replacement authorization path.

## Initial scope

- Account Service `GET /v1/accounts/{accountId}` resolves the principal and grants through Identity and enforces the self-party ownership check.
- Teller branch authorization, privileged administration, customer write operations, and financial commands are not included.
