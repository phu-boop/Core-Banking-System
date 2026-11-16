# SocialPilot AI front-end UI guidelines

These guidelines are tailored to the SocialPilot AI product direction and backend domain already present in the repo.

## Product goals

The frontend should help users:
- create content ideas
- review AI-generated drafts
- manage scheduled content
- publish across social platforms
- monitor generation and publishing status
- understand workspace performance
- handle platform account connection, approval, and error recovery

This is a product in which workflow clarity matters as much as aesthetics. The user is not just browsing a page; they are operating an automation system.

## Core interface patterns

### 1. Workspace shell
The top-level app likely needs:
- workspace switcher
- navigation items: Dashboard, Content, Calendar, Accounts, Settings
- user profile / workspace context
- global action button for creating content
- quick access to queue, automation runs, and approvals

### 2. Multi-platform content workspace
Because the repo targets TikTok, Facebook, and YouTube, the UI should often include platform-oriented views:
- platform chips or filters
- per-account status and OAuth health
- per-platform publish queue
- platform-specific metadata handling

### 3. Review and approval flow
The human approval layer is important to the product concept. The UI should support:
- review content draft
- approve or reject
- edit caption and hashtags
- schedule or publish manually when authorized

### 2. Content management
The content list should show:
- title
- status badge
- platform badge
- date or schedule
- preview or summary
- actions like edit, review, publish

### 3. Workflow status panel
This is critical because the backend already tracks content task states:
- pending
- generating content
- generating video
- publishing
- completed
- failed

The UI should visualize these states in a clear, calm, production-friendly manner.

### 4. Publishing / calendar view
The product should surface:
- queue and scheduled items
- publish time
- target platform
- campaign or workspace context

## Layout principles

- use clear whitespace and consistent rhythm
- avoid visual overload
- prefer high hierarchy over decorative complexity
- keep important actions visible near the top or within context
- use compact data visualization for workflow and content health

## Visual tone

Recommended feel:
- premium SaaS
- creator-focused
- calm and actionable
- professional enough for business teams
- not too flashy or gadget-heavy

## Accessibility basics

- strong text contrast
- obvious focus states
- readable status color mapping
- proper labels on forms and filters
- buttons with short clear labels

## Design system suggestions

If a future frontend stack is introduced, the safest starting direction is:
- React + Next.js or a similar frontend app
- Tailwind or equivalent utility styling
- simple component library for cards, tables, badges, buttons
- status colors mapped to backend task states

For the actual visual baseline, use the Upwork-inspired token system in [design-system.md](design-system.md), which defines the core palette, typography, component patterns, and accessibility rules for SocialPilot AI.

Avoid heavy over-designed styling in early product versions. Clarity should come first.
