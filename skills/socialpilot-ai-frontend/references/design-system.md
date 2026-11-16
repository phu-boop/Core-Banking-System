# SocialPilot AI design system

This design system is the visual baseline for future frontend work in SocialPilot AI. It follows the product architecture already present in this repo and uses an Upwork-inspired design language: professional, trustworthy, calm, and operationally clear.

## Design intent

The product is not a generic SaaS landing page. It is an AI workflow and publishing system for creators and teams. The UI should feel:
- premium but not flashy
- confident and trustworthy
- highly readable at a glance
- optimized for operational decisions
- calm under heavy task and status information

## Color system

Use a semantic token structure so the design remains consistent and easy to extend.

### Core palette
- Brand green: `#14A800`
- Dark green: `#108A00`
- Primary text: `#181818`
- Secondary text: `#5E6D55`
- Border: `#D5D8DC`
- Surface: `#F7F7F7`
- Surface elevated: `#FFFFFF`
- Success: `#1F9D61`
- Warning: `#C77900`
- Error: `#D92D20`
- Info: `#2F6FED`

### Semantic tokens
```css
:root {
  --color-brand-primary: #14A800;
  --color-brand-hover: #108A00;
  --color-brand-pressed: #0D7600;

  --color-text-primary: #181818;
  --color-text-secondary: #5E6D55;
  --color-text-muted: #697586;

  --color-surface: #F7F7F7;
  --color-surface-elevated: #FFFFFF;
  --color-surface-subtle: #EEF2F6;

  --color-border: #D5D8DC;
  --color-border-strong: #B9C3CC;
  --color-focus-ring: rgba(20, 168, 0, 0.25);

  --color-success: #1F9D61;
  --color-warning: #C77900;
  --color-error: #D92D20;
  --color-info: #2F6FED;

  --color-status-pending: #6B7280;
  --color-status-generating: #2F6FED;
  --color-status-publishing: #C77900;
  --color-status-completed: #1F9D61;
  --color-status-failed: #D92D20;
}
```

### Usage rules
- Reserve green for primary actions and important highlights.
- Use neutral backgrounds for most surfaces.
- Use semantic colors only for task status, validation, and action emphasis.
- Avoid applying green to every panel; otherwise the interface loses hierarchy.
- Keep text and controls in accessible contrast ranges.

## Typography

Use a clean sans-serif system with strong hierarchy and controlled density.

### Suggested scale
- Display / hero: 32–40px, 700–800 weight, tight but readable tracking
- Page title: 24–28px, 700 weight
- Section heading: 18–20px, 600 weight
- Card title: 16–18px, 600 weight
- Body text: 14–16px, 400–500 weight
- Small metadata: 12–13px, 500 weight
- Helper text: 12px, 400–500 weight

### Rules
- Keep line height around 1.4–1.6 for body text.
- Avoid overusing bold and uppercase.
- Use text weight intentionally to signal hierarchy.
- Keep metadata and status labels visually quiet but legible.

## Spacing and structure

Use a consistent 8px spacing scale.

- 4px: tight micro spacing
- 8px: baseline spacing
- 12px: compact UI gaps
- 16px: standard component padding
- 24px: section spacing
- 32px: larger layout rhythm

### Card styling
- radius: 12–16px
- border: 1px solid `--color-border`
- background: `--color-surface-elevated`
- subtle shadow: low-opacity neutral shadow only
- keep cards clean and readable, not decorative

## Component patterns

### Buttons
- Primary: green background, white text, subtle hover and active states
- Secondary: white or soft neutral background, dark text, border
- Tertiary: transparent background with visible hover treatment
- Destructive: red foreground treatment, clear label, strong contrast
- Disabled: muted background with reduced contrast and no hover effect

Recommended button height:
- standard: 40–44px
- compact: 32–36px

### Inputs and form fields
- height: 40–44px
- border: 1px solid `--color-border`
- radius: 10–12px
- background: white or very light neutral surface
- focus ring: 2px emphasis with `--color-focus-ring`
- error states: red border + red message text
- helper text: muted gray or semantic label

### Status badges
Use badges for workflow states and platform chips.
- pending: neutral gray
- generating: blue
- publishing: amber
- completed: green
- failed: red

### Layout behavior
- prefer clear page sections over decorative framing
- use predictable tables and list rows
- keep primary actions close to related content
- use subtle dividers instead of heavy borders

## Interaction and states

### States to support
- hover
- active/pressed
- focus-visible
- disabled
- loading
- success
- warning
- error

### Motion
- use light transitions: 120–200ms ease-out
- focus on comfort and clarity, not spectacle
- avoid high-energy or excessive animation

## Accessibility

- maintain contrast for body text and controls
- ensure every interactive element has a visible focus state
- don’t rely on color alone to communicate status
- keep touch targets large enough on mobile
- avoid hidden text or low-contrast overlays

## Responsive guidance

- preserve the existing information architecture
- prevent overflow in tables and forms
- stack secondary actions cleanly on smaller screens
- keep status and CTA areas obvious at mobile sizes

## Safe implementation policy

This design system is for presentation only. It should never change:
- workflow logic
- API contracts
- database structure
- page hierarchy
- business rules

The system should improve how the app looks and feels without changing what it does.
