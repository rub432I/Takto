# Matt Pocock's Merge Danger Review Protocol

**Reference:** `youtube_pl.txt` (Lines 403, 422, 434, 445)  
**Author:** Matt Pocock (Total TypeScript, AI Hero)  

---

## Strategic Code Review Philosophy
> *"Do not review AI code line-by-line. Instead, review code based on Merge Danger: distinguishing between One-Way Doors and Two-Way Doors."*

Line-by-line review of agent code is exhausting and produces diminishing returns. Developers must instead assess the blast radius and reversibility of each proposed PR.

---

## One-Way Doors vs. Two-Way Doors

```
                     ┌─────────────────────────────────────────┐
                     │          EVALUATE MERGE DANGER          │
                     └────────────────────┬────────────────────┘
                                          │
                   ┌──────────────────────┴──────────────────────┐
                   ▼                                             ▼
       ┌───────────────────────┐                     ┌───────────────────────┐
       │     ONE-WAY DOORS     │                     │     TWO-WAY DOORS     │
       │ (High Danger / Hard)  │                     │  (Low Danger / Easy)  │
       └───────────┬───────────┘                     └───────────┬───────────┘
                   │                                             │
      • Database migrations                         • UI cosmetic styling
      • Authentication logic                        • Internal helper refactors
      • Public API contracts                        • Documentation updates
      • Payment / billing flows                     • Test coverage expansions
                   │                                             │
                   ▼                                             ▼
       MANDATORY HUMAN AUDIT                         AUTO-MERGE ON GREEN CI
```

### Protocol Checklist for Code Reviewers
1. **Identify Door Type:** If the change touches schemas, security, or public endpoints, mark as `[one-way-door]`. Require manual verification.
2. **Automate Two-Way Doors:** If the change touches only internal implementation details and tests pass, approve immediately.
3. **Check Invariants:** Does the PR violate any architectural rules (e.g. Dune thread boundaries)?
4. **Session Retro:** Log any mistakes into the project glossary or lint rules to prevent the agent from repeating them.
