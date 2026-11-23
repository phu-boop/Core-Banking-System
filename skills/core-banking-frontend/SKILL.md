---
name: core-banking-frontend
description: Frontend design and implementation guidance for the Core Banking System customer, teller, corporate, and operations interfaces.
---

# Core Banking frontend

Use this guidance for frontend design and implementation in this repository. The backend is a Java/Spring Boot microservice system. The initial frontend lives in `frontend/` and uses Next.js + TypeScript.

## Product context

The Core Banking MVP covers Identity, Party, Organization/Corporate, Branch, Account, Ledger, Transfer, Payment, Audit, Notification, and Reporting. Loan is outside the MVP. API Gateway and some production/channel decisions remain open; see [PROJECT_ROADMAP.md](../../PROJECT_ROADMAP.md).

## Visual direction

Reuse the previous design's calm, readable, neutral surfaces and card/table patterns. Adapt it to banking:

- Navy conveys institutional trust and anchors navigation.
- Teal is the restrained primary action color.
- Green indicates a confirmed positive/completed state, not generic branding.
- Neutral backgrounds preserve contrast for financial data.
- Amber/red are reserved for approval attention and errors/rejections.

Use [references/design-system.md](references/design-system.md) for color tokens, typography, components, accessibility, and responsive behavior.

## Product surfaces

Design distinct but visually related experiences for:

- Customer Internet Banking
- Teller/Operations
- Corporate Banking and maker-checker approvals
- Administration, Audit, and Reporting

Follow [references/ui-guidelines.md](references/ui-guidelines.md) and [references/product-flows.md](references/product-flows.md). Navigation and UI visibility are not authorization; backend permissions remain authoritative.

## Required preflight

Before implementing frontend code:

1. Inspect this skill and the relevant references.
2. Confirm whether a frontend package and framework now exist; do not scaffold based on an assumption.
3. Inspect the actual service OpenAPI/API contract, role/scopes, and user flow involved.
4. Design explicit loading, empty, error, unauthorized, pending, and confirmed-result states.
5. Keep changes scoped to frontend code; do not alter backend business rules or APIs to fit a mock.

## Banking safety and UX

- Mask account/customer identifiers by default.
- Format money with decimal-safe values and currency codes; never use floating-point arithmetic for financial decisions.
- Require a review/confirmation step for financial mutations.
- Prevent duplicate submits and distinguish pending/processing from completed.
- Show only backend-confirmed balances, statuses, fees, permissions, and receipts.
- Do not invent dashboard metrics or unsupported banking actions.
- Keep audit history and corporate approval decisions clear and non-editable when the service treats them as immutable.
- Use accessible text/icon labels in addition to status color; support keyboard navigation and mobile layouts.

## Technical guidance

Use Next.js App Router and TypeScript conventions from [references/nextjs-coding-standards.md](references/nextjs-coding-standards.md). Keep typed API boundaries, server/client responsibilities explicit, and design values tokenized. Avoid additional dependencies unless a feature requires them.
