---
name: socialpilot-ai-frontend
description: Frontend product skill for SocialPilot AI. Use this when building the app shell, marketing pages, dashboard UI, content management screens, and AI workflow surfaces for a social media automation product.
---

# socialpilot-ai-frontend

This skill is for frontend work in the SocialPilot AI product. It is meant for building product screens, SaaS dashboard interfaces, content management UI, AI workflow status screens, and marketing surfaces that match a premium AI social content platform.

## Goal

Create interfaces that feel:
- modern and trustworthy
- productized and premium
- clear for creators and teams
- optimized for AI content operations and publishing workflows

## Product context

SocialPilot AI is a multi-platform social media automation product. The frontend must support a real operating model where users create content ideas, review AI-generated drafts, schedule posts, connect social platforms, and monitor publication across TikTok, Facebook, and YouTube.

The product architecture in this project is:
- dashboard for workspace and operations
- backend API in Node.js + TypeScript
- PostgreSQL for relational content and scheduling data
- Redis + BullMQ for queue and retry handling
- n8n for content and workflow orchestration
- platform adapters for TikTok, Facebook, and YouTube
- AI planner and production workflow stages

The frontend should support:
- workspace overview
- content library and review queue
- AI generation workflow status
- scheduling and publishing views
- social account management and OAuth state
- analytics / reporting screens
- marketing / landing page for product positioning
- approval flows before publishing

This skill should be used when the task is about UI, not backend logic.

## Product design direction

### Design tone
- modern SaaS
- premium but not overly flashy
- content-first and conversion-aware
- calm AI interface with strong hierarchy
- trust-driven and productivity-driven

### Visual style
- neutral background with one strong accent color
- clean cards, subtle shadows, clear spacing
- platform-friendly layout for creators and social teams
- strong dashboards with readable data density
- minimal but polished motion

### Recommended visual language
- backgrounds: slate, zinc, stone, white
- accent colors: blue, emerald, violet, orange, rose
- avoid generic AI purple overuse
- use strong contrast and readable text
- keep UI clean and operational

## Mandatory pre-flight for every coding task

Before any frontend implementation, read this skill and the relevant project references first. This check is required before writing or editing a single line of code.

1. Read the frontend skill first for the task type.
2. Read the relevant reference files in `skills/socialpilot-ai-frontend/references/` if the task touches design system, flows, or UI standards.
3. Inspect the exact app files involved in the feature before editing.
4. Confirm the product flow, status model, and layout pattern before implementing.
5. Keep the change scoped to the actual route/component and validate it afterward.

Do not skip this pre-flight step. Missing the skill review is how repo-specific rules and product expectations get overlooked.

## Core frontend principles

### 1. Design for creators, not designers only
The UI should feel like a creator workflow tool:
- simple content review flow
- clear approval states
- clear publishing status
- progress bars and step tracking
- low learning curve

### 2. Prioritize clarity over decoration
When in doubt:
- prefer simple cards over complex visual tricks
- prefer straightforward hierarchy over artistic noise
- prefer readable tables and sections over dramatic motion

### 3. Support AI workflow mental model
The interface should clearly communicate:
- draft content
- AI generation in progress
- video generation
- publishing in progress
- publish success or failure

### 4. Respect operational status states
For every workflow screen, show state transitions clearly:
- pending
- generating content
- generating video
- publishing
- completed
- failed

## Layout guidance

### Dashboard patterns
Use:
- header with workspace info and action buttons
- summary KPI cards
- content feed or table
- workflow activity panel
- scheduling overview
- recent AI runs and automation activity

### Product workflow screens
Because this product revolves around AI content automation, the UI should clearly show the product flow:
- AI Content Planner: generate topic and content roadmap
- Content Production: draft caption, script, hashtags, media assets
- Review and approval: human approval before publish
- Scheduler: publishing queue and upcoming jobs
- Publisher status: queued, processing, published, failed
- Monitoring and recovery: errors, retry, reauth, analytics summary

This means the product interface is not only a generic dashboard; it is a workflow UI that helps users see where content is in the pipeline.

### Content screens
Use:
- title, status badge, platform label
- caption / content preview
- media preview area
- AI prompt summary
- publish history or schedule details

### Marketing surfaces
Use:
- strong hero with product positioning
- social proof and trust cues
- value proposition sections
- feature grid
- CTA blocks

## Interaction guidance

- Use subtle transitions, not excessive motion
- Prefer progressive disclosure for details
- Use status badges and inline notifications consistently
- Avoid crowded multi-column layouts unless the data truly warrants it
- Keep CTA labels short and concrete: “Create Content”, “Review”, “Schedule”, “Publish”, “View Details”

## Component behavior

### Buttons
- One clear primary action per section
- Secondary actions for less urgent tasks
- Maintain visible hover and active states
- Use accessible contrast

### Cards
- Use cards for grouped status, content preview, or queue items
- Keep consistent spacing and radius
- Avoid decorating every card with heavy gradients

### Status badges
Use distinct but readable colors:
- draft: neutral
- generating: blue / cyan
- publishing: amber / yellow
- completed: green
- failed: red

### Forms and filters
- Label should be placed above input
- Use compact and readable forms
- Keep filters easy to scan
- Use clear empty states

## Technical expectations

When coding frontend for this repo or a future Next.js + TypeScript app:
- use Next.js App Router conventions by default
- keep components reusable and typed with TypeScript
- prefer server components for static or data-driven UI, and isolate client-only interactivity behind `"use client"`
- keep layout responsive from mobile to desktop
- avoid over-engineering early
- keep data shapes consistent with the backend domain
- do not mix frontend rendering with backend logic, API manipulation, or database rules
- keep business flow readable and predictable for content creation, review, scheduling, and publishing

## Next.js + TypeScript coding standards

### 1. Architecture
- prefer a clear folder structure such as `app/`, `components/`, `lib/`, `types/`, `hooks/`, and `services/`
- keep route-level UI in `app/(routes)/...` and shared UI in `components/`
- keep domain types centralized in `types/` or `lib/types.ts`
- separate server-only logic from client-only logic
- use route segment and layout composition to keep page structure maintainable

### 2. Server vs client boundaries
- use server components by default for reading data, rendering, and simple list/detail screens
- only mark a component as client when it needs `useState`, `useEffect`, browser events, or local UI state
- do not place heavy client logic in server components
- keep event handlers, forms, dialogs, and stateful UI inside dedicated client components

### 3. Type safety
- define explicit interfaces and types for API responses, task status payloads, platform metadata, and content records
- avoid `any` unless absolutely necessary and justifiable
- prefer discriminated unions for workflow states and status-based UI
- use strong prop types across reusable UI blocks

### 4. Clean component design
- keep components small and single-purpose
- pass typed props and avoid deep prop drilling
- keep page components thin; logic should live in small reusable blocks or hooks
- prefer composition: `Page -> Section -> Card -> Button` rather than giant monolithic components

### 5. Data and API boundaries
- treat backend API results as external data and map them to UI-friendly types in frontend code
- do not embed business rules in UI components
- do not mutate or invent backend states on the client unless the product explicitly requires it
- map task states into display states such as `pending`, `generating`, `publishing`, `completed`, `failed`

### 6. Styling and consistency
- prefer Tailwind CSS or a consistent utility style system if already adopted
- use a small, reusable design token set instead of ad-hoc hardcoded colors and spacing
- maintain component spacing rhythm and keep UI surfaces consistent across dashboard and workflow screens
- keep accessible contrast and readable typography at all screen sizes

### 7. Forms and UX quality
- use clear labels and placeholder text without confusion
- validate UI state before submitting actions
- handle loading, empty, error, and success states explicitly
- show task status and progress visibly, not just hidden in logs

### 8. Performance and maintainability
- avoid unnecessary re-renders and heavy client state overuse
- memoize only when there is a real benefit
- keep code readable and explicit over clever abstraction
- use small hooks and helpers to keep logic reusable and testable

## Safe working process for Next.js tasks

Follow:
1. Inspect the route and layout structure
2. Confirm whether the task is page, component, form, workflow UI, or marketing section
3. Prefer server-first architecture unless client behavior is required
4. Build a typed, scoped component solution
5. Check visuals, accessibility, and responsive behavior
6. Confirm that no backend logic or API behavior has been changed

## Prohibited behavior in Next.js Frontend work

Do not:
- add database logic or server-side business logic inside UI files
- bypass server/client separation without justification
- create giant page files with too many responsibilities
- use `any` casually or ignore TypeScript safety
- invent fake workflows or success states without confirmed backend status
- build UI that looks premium but hides important task or status information

## Safe working process

Follow:
1. Inspect existing structure
2. Identify UI requirement and product flow
3. Confirm whether it is dashboard, content, workflow, or marketing UI
4. Build a narrow, scoped UI solution
5. Check the result against accessibility and readability
6. Report what was implemented and what remains unverified

## Prohibited behavior

Do not:
- invent backend logic inside frontend code
- claim publishing is live without explicit backend status
- build fake success states when the workflow is not confirmed
- add heavy decorative design that hurts clarity
- create hidden or misleading statuses

## Quick references

When building UI for this project, inspect these files first:
- [../../README.md](../../README.md)
- [../../apps/api/prisma/schema.prisma](../../apps/api/prisma/schema.prisma)
- [../../apps/api/src/modules/content/content.controller.ts](../../apps/api/src/modules/content/content.controller.ts)
- [../../apps/api/src/modules/content/content.service.ts](../../apps/api/src/modules/content/content.service.ts)
- [../../apps/api/src/modules/webhooks/n8n-webhook.controller.ts](../../apps/api/src/modules/webhooks/n8n-webhook.controller.ts)

This skill is intentionally product-focused. It helps the agent create frontend experiences that fit a modern AI social-content platform without losing practical clarity.
