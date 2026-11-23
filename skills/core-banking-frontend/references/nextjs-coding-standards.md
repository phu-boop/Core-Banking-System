# Core Banking frontend implementation standards

Framework-neutral preparation for a future frontend. The repository currently has no frontend package or confirmed framework. If Next.js + TypeScript is chosen, use the conventions below without treating the example structure as a required scaffold.

## Suggested structure

```text
app/
  (customer)/
  (operations)/
  (corporate)/
  globals.css
  layout.tsx
components/
  ui/
  navigation/
  accounts/
  transactions/
  approvals/
lib/
  api/
  format/
  permissions/
types/
```

Keep customer, operations, and corporate route groups distinct where their navigation and access contexts differ. Reuse visual primitives, not authorization assumptions.

## Rendering and data

- Prefer server rendering for read-only pages; use client components only for interactive state.
- Treat every service response as untrusted input and validate/map it at the API boundary.
- Use typed request/response models based on actual OpenAPI or accepted contracts.
- Keep credentials, tokens, and privileged actions out of browser storage and logs; follow the selected auth architecture.
- Never access service databases directly from the frontend.
- Do not fabricate balance totals, status transitions, permissions, fee calculations, or successful receipts.

## Financial UI types

Represent currency amounts without floating-point arithmetic (e.g. decimal string plus ISO currency code). Keep display formatting separate from raw values.

```ts
export type TransactionState =
  | 'PENDING'
  | 'PROCESSING'
  | 'AWAITING_APPROVAL'
  | 'COMPLETED'
  | 'REJECTED'
  | 'FAILED';

export interface MoneyView {
  amount: string;
  currency: string;
}
```

Only include enum values present in the relevant backend contract; adapt this illustrative union to the API before implementation.

## Forms and transaction safety

- Label every control and validate format locally; server validation remains authoritative.
- For financial mutations, show a review step and guard against duplicate submission.
- Distinguish pending/processing from completed and preserve the returned transaction reference.
- Confirm destructive or irreversible actions; do not hide errors behind success-shaped UI.
- Mask personal and account identifiers by default.

## Accessibility and visual consistency

- Use semantic HTML, keyboard support, visible `focus-visible` styles, and WCAG AA contrast.
- Pair status colors with text/icon labels; support reduced motion and responsive layouts.
- Use design tokens from [design-system.md](design-system.md), not ad hoc hex values.
- Format currency/date/time using a shared locale-aware formatter.

## Verification

Before delivery, verify against the real service contract, roles/scopes, loading/error/empty states, keyboard navigation, narrow viewport behavior, and financial confirmation paths. Browser tests should assert backend-confirmed outcomes, not visual mock success.
