---
name: grill-and-spec
description: Matt Pocock's strategic development workflow (Grill with Docs, ADR generation, parallel worktree implementation, merge-danger review, and session retro). Use when designing or implementing non-trivial features.
---

# Grill & Spec Strategic Development Workflow

Humans lead strategically (architecture, glossary, constraints); AI acts as the tactical implementer.

## The 5-Step Strategic Loop

### 1. Grill with Docs
Before writing any code:
- Interview the project requirements.
- Produce a **Domain Glossary** (defining project terms to prevent semantic drift).
- Record an **Architecture Decision Record (ADR)**:
  - *Context:* Why is this change needed?
  - *Decision:* What approach was chosen?
  - *Consequences:* What are the trade-offs and risks?

### 2. Specification & Tickets
- Break the feature into small, atomic tickets.
- Tag each ticket with status indicators: `[not-started]`, `[in-progress]`, `[completed]`.

### 3. Parallel Implementation
- Keep features compartmentalized. Use Git worktrees or isolated subagents for independent tasks.
- Build **Deep Modules**: Large, self-contained units with minimal, clean interfaces.

### 4. Adversarial Merge-Danger Review
Review AI output based on risk assessment rather than formatting:
- **One-Way Doors:** High risk, irreversible changes (database schemas, public API breaks, auth changes). Require rigorous human sign-off.
- **Two-Way Doors:** Low risk, easily reversible changes (internal refactors, CSS tweaks). Safe to merge after automated tests pass.

### 5. Retrospective
At the end of a session, document what failed, what the agent misjudged, and update lint rules or prompt constraints to prevent recurrence.
