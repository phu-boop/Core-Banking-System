# SocialPilot AI product flows

This file captures the expected frontend flow for the product based on the backend and workflow contracts currently in the repo.

## 1. Create content flow

User journey:
1. User opens workspace
2. Clicks create content
3. Provides topic and platform
4. App triggers backend `/api/v1/content/run`
5. UI shows task created with pending state
6. UI polls status or listens to updates
7. UI displays content generation progress
8. UI shows publishing status and final result

For SocialPilot AI, this is not just CRUD. It is a human-in-the-loop workflow:
- AI plan topic and script
- review and approval
- schedule and queue
- publish to multiple platforms
- report final result and errors

## 2. Workflow tracking UX

The UI should render status transitions clearly:
- pending
- generating content
- generating video
- publishing
- completed
- failed

The interface should show both:
- the stage label
- progress percentage
- last step message
- optional error details

For this product, the UI should also reflect the higher-level workflow stages mentioned in the project overview:
- AI Content Planner
- Content Production
- Publishing Scheduler
- Monitoring & Recovery

This allows users to understand not only the current content task, but also which operational layer is active.

## 3. Content review UX

After generation, the user may need to:
- review title and caption
- preview media assets
- approve content
- schedule it
- edit prompt or output

## 4. Publishing UX

The scheduling and publishing section should clearly show:
- target platform
- scheduled time
- status
- retry attempts if any
- final publish result or error

## 5. Future dashboard UX

A more complete UI could include:
- active workspaces
- content counts by platform
- schedules due this week
- task load and recent activity
- health and workflow summary

These views are future-facing and should be introduced only when the underlying backend supports them.
