---
name: karpathy-method
description: Three-layer framework for AI agent engineering (Spec, Verifier, Environment) as formulated by Andrej Karpathy. Use when planning, verifying, or structuring AI development projects.
---

# Karpathy Method: 3-Layer Engineering Framework

This skill operationalizes Andrej Karpathy's three-layer framework for building projects with AI 10x faster and with near-zero error rates.

```
┌────────────────────────────────────────────────────────┐
│  LAYER 1: THE SPEC (Context & Goal Alignment)          │
│  - "Interview me to uncover the true goal"             │
│  - Agile, compartmentalized specifications             │
│  - Verify key decisions; eliminate assumptions         │
├────────────────────────────────────────────────────────┤
│  LAYER 2: THE VERIFIER (Deterministic Quality Control) │
│  - Upfront evaluation criteria & tests                 │
│  - Critic agent adversarial review                     │
│  - 70% token budget dedicated to verification          │
├────────────────────────────────────────────────────────┤
│  LAYER 3: THE ENVIRONMENT (The Operating Workshop)     │
│  - GEMINI.md / CLAUDE.md auto-injected rules           │
│  - Knowledge base & custom reusable skills             │
│  - Hard tool-level guardrails                          │
└────────────────────────────────────────────────────────┘
```

## Layer 1: The Spec
When beginning any task or project:
1. **Interview the User:** If requirements are high-level, ask: *"What is the exact decision, output, or conclusion this project must drive?"* Ask 3-5 clarifying questions before writing code.
2. **Compartmentalize:** Never execute a monolithic spec. Break the work into small, independently testable milestone tickets.
3. **Verify Assumptions:** Stop and present key architectural decisions to the user for explicit confirmation.

## Layer 2: The Verifier
Before submitting any work:
1. **Establish Evaluation Criteria:** Define tests and expected outcomes prior to implementation.
2. **Deploy the Critic:** Run a verification check or spawn a critic agent to review the solution against edge cases.
3. **External Signals:** Verify against actual terminal execution, linters, and compilers—never assume code works based on syntax appearance alone.

## Layer 3: The Environment
1. Keep the workspace clean and well-documented.
2. Maintain strict tool guardrails to prevent unrequested modifications.
