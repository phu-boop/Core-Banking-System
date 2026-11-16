# ADR-008: Loan Excluded from MVP

- **Status:** ACCEPTED; Loan scope is **DEFERRED** to a later phase.
- **Date:** Not recorded; historical approval date was not supplied.

## Context

MVP scope must remain focused and avoid assuming lending requirements.

## Decision

**Loan is excluded from MVP and planned for a later phase.**

## Alternatives

- Include loan products and servicing in MVP.
- Make a loan model a required part of the initial account domain.

## Consequences

- Do not create a Loan service, loan workflow or Loan Account MVP requirement.
- Future loan capabilities require separate business requirements and architecture decisions.
- The decision does not specify when or how a later loan phase will be delivered.

## Dependencies

- ADR-002 retains Retail and Corporate scope without implying lending.
- Account MVP is focused on payment accounts; see [Business Rules](../domain/BUSINESS-RULES.md).
