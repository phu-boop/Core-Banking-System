# SocialPilot AI Next.js + TypeScript coding standards

This is the implementation baseline for frontend work in SocialPilot AI when the project uses Next.js with TypeScript.

## Product assumptions

The frontend is for:
- workspace dashboards
- content review and approval screens
- workflow tracking and publishing status
- scheduling and calendar UI
- platform account management
- AI content generation monitoring

The frontend should look premium and professional, but it must remain clear, structured, and operational.

## Recommended project structure

```text
app/
  (dashboard)/
    dashboard/page.tsx
    content/page.tsx
    calendar/page.tsx
  globals.css
  layout.tsx
components/
  ui/
    Button.tsx
    Card.tsx
    Badge.tsx
    Input.tsx
    StatusPill.tsx
  dashboard/
  content/
  workflow/
lib/
  utils.ts
  format.ts
  constants.ts
services/
  content.ts
  platform.ts
types/
  content.ts
  workflow.ts
hooks/
  useWorkspace.ts
```

## Rules

### 1. App Router by default
- prefer route folders under `app/`
- use `layout.tsx` to share shell and navigation
- keep page-level files focused on composition
- avoid building logic-heavy pages without extracting smaller reusable blocks

### 2. Server-first rendering
- prefer server components for content lists, dashboard summaries, and static rendering
- use client components only when interactive UI is required
- do not render `useState`-driven logic in a server component without a clear client boundary

### 3. Type-safe domain models
Example domain types:

```ts
export type TaskStatus =
  | 'PENDING'
  | 'GENERATING_CONTENT'
  | 'GENERATING_VIDEO'
  | 'PUBLISHING'
  | 'COMPLETED'
  | 'FAILED';

export interface ContentTaskViewModel {
  id: string;
  topic: string;
  platform: string;
  status: TaskStatus;
  progress: number;
  stepMessage?: string;
  result?: string | null;
  error?: string | null;
  createdAt: string;
  updatedAt: string;
}
```

### 4. Clean component API design
- Prefer explicit props with minimal optional fields
- avoid “god props” objects with unrelated values
- use small interfaces for each component contract
- split complex components into smaller composable blocks

### 5. Backend contract separation
- frontend should map service data to UI model objects
- do not import Prisma or business-layer logic into UI components
- do not call raw database operations from the client side
- UI should consume typed API responses, not mutate server state directly

### 6. Status-driven UI patterns
For workflow screens, use explicit status mapping:

```ts
const statusMeta = {
  PENDING: { label: 'Pending', tone: 'neutral' },
  GENERATING_CONTENT: { label: 'Generating content', tone: 'info' },
  GENERATING_VIDEO: { label: 'Generating video', tone: 'info' },
  PUBLISHING: { label: 'Publishing', tone: 'warning' },
  COMPLETED: { label: 'Completed', tone: 'success' },
  FAILED: { label: 'Failed', tone: 'error' },
} as const;
```

This helps keep UI consistent with backend state transitions.

### 7. Form design and interaction
- label every form control
- provide inline validation messaging
- handle loading and disabled states visibly
- use short and precise CTA labels such as “Create Content”, “Review”, “Schedule”, “Publish”

### 8. Accessibility
- visible `focus-visible` rings
- proper contrast for text and buttons
- keep tap targets usable on mobile
- don’t rely on color alone to communicate state

### 9. Styling rules
- use a tokenized set of spacing, colors, radii, and typography
- keep surfaces neutral and readable
- use green as a primary accent, not as an everywhere color
- avoid over-designing with decorative gradients unless they truly support hierarchy

### 10. Performance conventions
- fetch only what is needed
- split heavy UI into smaller components
- avoid unnecessary re-renders from overuse of local state
- prefer static rendering when data is not interactive

## Clean implementation advice

When implementing a UI feature:
1. define the visible state and interaction model
2. map backend data to UI-friendly types
3. create a small component tree
4. keep business logic out of presentational files
5. verify loading, success, and failure states
6. check responsive behavior and accessibility

## In summary

The frontend should be clean, typed, predictable, and domain-aware. It should feel like a premium AI productivity product without becoming a decorative mock. The correct pattern is: product data in, typed UI components out, with backend concerns staying outside the frontend layer.
