# Core Banking design system

Visual baseline for future Core Banking frontend work. Reuse the previous product's quiet, card-based layout and green success cues, replacing its creator-oriented palette and social workflow metaphors with a trustworthy banking interface.

## Design intent

- Make balances, transaction status, approvals, and account context easy to scan.
- Feel secure and professional without looking like a marketing site.
- Separate customer self-service from staff operations and corporate approvals.
- Never imply that a transaction succeeded until the backend confirms it.

## Color system

Use semantic tokens. Navy carries institutional identity; teal is reserved for primary actions; green denotes positive financial or transaction states, not general decoration.

```css
:root {
  --color-brand-primary: #123B5D;
  --color-brand-hover: #0C2E4A;
  --color-brand-pressed: #08243B;
  --color-accent: #0F766E;
  --color-accent-hover: #0B5F59;

  --color-text-primary: #172B3A;
  --color-text-secondary: #526779;
  --color-text-muted: #667085;

  --color-surface: #F4F7FA;
  --color-surface-elevated: #FFFFFF;
  --color-surface-subtle: #EAF0F5;
  --color-border: #D8E1E8;
  --color-border-strong: #B8C7D3;
  --color-focus-ring: rgba(15, 118, 110, 0.28);

  --color-success: #177245;
  --color-warning: #9A5B00;
  --color-error: #B42318;
  --color-info: #175CD3;

  --color-status-pending: #667085;
  --color-status-processing: #175CD3;
  --color-status-awaiting-approval: #9A5B00;
  --color-status-completed: #177245;
  --color-status-rejected: #B42318;
  --color-status-blocked: #667085;
}
```

### Usage

- Use navy for navigation, headings, and institutional identity.
- Use teal for the primary action in a section; avoid multiple competing primary buttons.
- Keep account, ledger, and report surfaces neutral. Reserve green for confirmed positive values and completed states.
- Red is for errors, rejected/failed actions, and destructive controls. Never use it as a generic accent.
- Status must include readable text or an icon, not color alone.
- Do not use color to indicate a balance is available unless the actual data supports that meaning.

## Typography and numbers

- Use a legible system sans-serif; use tabular numerals for currency, balances, and transaction amounts.
- Page title: 24–28px; section title: 18–20px; body: 14–16px; metadata: at least 12px.
- Use locale-aware currency and date formatting from one shared formatter.
- Distinguish debit and credit with explicit labels/signs and accessible text, not color alone.
- Mask account identifiers by default; reveal only in a deliberate, authorized context.

## Spacing and surfaces

Use an 8px rhythm (4, 8, 12, 16, 24, 32). Use 8–12px corner radii, thin neutral borders, white elevated surfaces, and restrained shadows. Keep transaction lists and tables denser than customer dashboard cards, but preserve comfortable row height and scanability.

## Components

- Buttons: primary teal with white text; secondary outlined/neutral; destructive clearly labeled and confirmed.
- Inputs: visible label, 40–44px control height, clear validation, focus ring, and formatted currency/identifier behavior.
- Status badge: neutral pending, blue processing, amber approval required, green completed, red rejected/failed, neutral blocked.
- Financial confirmation: show source, destination, amount, currency, fees if supplied, and a final confirmation step before submitting.
- Tables: align amounts consistently, keep column labels visible, support responsive horizontal scrolling or a deliberate mobile row layout.
- Avoid decorative gradients, ambiguous icons, and animation that obscures a changing balance or state.

## Responsive and accessibility

- Customer banking must work well on narrow mobile screens; staff workspaces may prioritize desktop but must remain usable on tablets.
- Meet WCAG AA contrast, support keyboard navigation and visible focus, and label controls for assistive technology.
- Respect reduced-motion preferences. Use short transitions (120–200ms) only for feedback.

## Safety boundary

Design changes must not change authorization, API contracts, transaction rules, approval policy, or financial state. Render only backend-confirmed states and values; do not fabricate dashboard totals, permissions, or success receipts.
