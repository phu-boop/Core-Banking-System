# Core Banking UI guidelines

These guidelines prepare the interface for the Java/Spring Boot Core Banking services in this repository. There is not yet a frontend app or an agreed frontend framework; treat these as design guidance, not an instruction to scaffold a stack.

## Product and user surfaces

Provide separate navigation contexts for:

- **Customer Internet Banking:** accounts and balances, transaction history, transfers, payment activity, profile/security.
- **Teller/Operations:** customer and account lookup, branch context, permitted deposit/withdrawal and transfer operations, transaction status.
- **Corporate Banking:** organization context, member/mandate views, transfer initiation, approval inbox and immutable decision history.
- **Administration/Control:** identity and branch administration, audit search and reporting, limited by granted permissions.

Use a role- and permission-aware shell. Do not expose navigation or actions as authorization; every action must still be checked by the backend.

## Banking workspace shell

- Persistent sidebar on desktop with institution/product mark, role-scoped navigation, and clear current location.
- Header with page title, authenticated user, active organization or branch context, and session/security actions.
- On mobile, use a compact header and accessible bottom navigation or drawer for customer tasks.
- Keep the active account/organization context visible before showing financial data or starting a transaction.
- Use a global notification area for backend-confirmed alerts; do not silently imply delivery or completion.

## Page patterns

### Overview

Prefer a restrained summary: selected account balances, recent transactions, pending approvals/tasks, and service messages. Only render metrics supported by actual API responses; no mock totals or invented trend charts.

### Accounts and transactions

Show account label/type, masked identifier, currency, available/current balance only when the API distinguishes them, and transaction rows with date, description, debit/credit, amount, and authoritative status. Provide filters with labels and explicit empty/error/loading states.

### Transfer and payment

Use a focused, stepwise form: source, beneficiary/destination, amount/currency, optional reference, review, then explicit confirmation. Show validation and server-side rejection without losing user context. After submission, distinguish accepted/processing from completed; present a reference/receipt only when returned by the service.

### Corporate approval

Show the requested operation, amount, organization, initiator, and approval requirements. Make approve/reject decisions deliberate and auditable; display the recorded decision and history. Enforce maker-checker through backend behavior, never a UI-only rule.

### Teller and staff operations

Keep customer, account, branch, and amount context visible throughout the flow. Require review before a financial mutation and make branch scope explicit. Do not offer unsupported operations such as reversal or limits management without a corresponding backend contract.

### Audit and reporting

Use searchable, filterable tables with date range and scope. Respect data minimization: mask personal/account identifiers unless visibility is required and authorized. Clearly distinguish operational reports from ledger-authoritative balances.

## Interaction and states

- Use concrete labels such as “Review transfer”, “Submit for approval”, “Approve”, and “Reject”.
- Confirm irreversible/high-impact actions and prevent duplicate submissions while pending.
- Show pending, processing, awaiting approval, completed, rejected, and failed states only when returned or derived from documented service contracts.
- Provide loading, empty, validation, unauthorized, unavailable, and success states.
- Keep status labels accessible and include reference IDs in copyable, non-secret form.

## Frontend/API boundary

Backend services currently cover Identity, Party, Branch, Corporate approvals/memberships, Account, Ledger, Transfer, Payment, Audit, Notification, and Reporting. API Gateway and some production/channel decisions remain open in [PROJECT_ROADMAP.md](../../../PROJECT_ROADMAP.md). Build screens against confirmed endpoints and contracts; do not assume one dashboard endpoint or invent service capabilities.

Use the shared palette and component rules in [design-system.md](design-system.md). Reuse the old calm, card-based visual structure, but replace social platform/content concepts with banking information architecture.
