---
name: vibe-code-remedy
description: Kole Jain's framework for eliminating "vibe coding" (superficial AI design without intent). Enforces Tesler's Law, purposeful onboarding, wiki-style interconnected linking, and strong product opinions.
---

# Anti-Vibe-Coding: Product Intentionality Framework

"Vibe coding" occurs when AI mimics surface-level UI trends without understanding user workflows or underlying engineering logic.

## Four Intentionality Principles

### 1. No Surface-Level Imitation
- Never add UI components (drop-down menus, modal popovers, accordions) just because other apps have them.
- Ask: *"What specific problem does this component solve at this exact step in the user flow?"*

### 2. Tesler's Law (Conservation of Complexity)
- Complexity cannot be destroyed; it can only be moved.
- **Rule:** Absorb complexity into system architecture, state machines, and backend logic. Never shift complexity onto the user by forcing extra clicks, manual configuration, or confusing options.

### 3. Purposeful Onboarding
- Ban passive modal carousels (*"Welcome! Click Next to see feature X"*).
- Build interactive onboarding that asks users their goals and dynamically configures their workspace on the fly.

### 4. Interconnected Context (The Wikipedia Model)
- Never create siloed, dead-end pages.
- Connect related data using inline links, hover cards, and breadcrumbs so users navigate fluidly without losing context.

### 5. Strong Product Opinion
- Default AI output is generic and median.
- Enforce an intentional design stance (bold typographic contrast, clear information hierarchy, high-conviction defaults).
