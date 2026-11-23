# 14: Strategic Programming & The Agent Development Loop

**Source:** `youtube_pl.txt` (Lines 398-449)  
**Authors:** Matt Pocock (Total TypeScript, AI Hero) & NetworkChuck  

---

## Overview
Coding with AI is less about typing syntax and more about **strategic leadership**. The human acts as the strategic architect; the AI acts as the tactical implementer (5:03).

---

## The Strategic Development Loop

```
[1. Grill with Docs] ──▶ [2. Spec & Tickets] ──▶ [3. Parallel Worktrees]
         │                                                 │
[5. Retrospective]  ◀── [4. Adversarial Review] ◀─────────┘
```

### 1. Strategic Alignment: "Grill with Docs" (12:35 - 17:49)
- Before writing code, grill the agent to ensure it understands the project's domain vocabulary.
- Build a **Domain Glossary** and log an **Architecture Decision Record (ADR)** to lock in technical agreements.

### 2. Spec and Tickets (25:18 - 34:35)
- Create a crisp technical specification.
- Break the work down into granular, independently executable tickets.

### 3. Parallel Execution via Worktrees (34:35 - 40:20)
- Deploy sub-agents in parallel Git worktrees to implement separate tickets without branch collisions.

### 4. Adversarial Code Review: Merge Danger (40:20 - 42:51)
- Do not review AI code line-by-line (exhausting and ineffective).
- Review based on **Merge Danger**:
  - *One-way doors:* Irreversible database changes, security architecture, breaking API changes.
  - *Two-way doors:* Easily reversible UI styling or internal helper refactors.

### 5. Retro & Feedback (42:34 - 44:52)
- At the end of each session, run a retrospective to evaluate agent errors and update system constraints.

---

## Deep Modules vs. Shallow Modules (52:31)
- **Shallow Modules:** Thousands of tiny files with complex interdependencies. AI agents get lost and hallucinate imports.
- **Deep Modules:** Large, well-encapsulated modules with simple, clear interfaces. Extremely easy for AI to navigate and reason about.
