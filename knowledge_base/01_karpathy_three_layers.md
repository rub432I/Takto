# 01: The Karpathy Method (Three-Layer AI Engineering)

**Source:** `youtube_pl.txt` (Lines 1-22, 451-488)  
**Core Authors:** Andrej Karpathy (former OpenAI & Tesla Director of AI)  

---

## Overview
The Karpathy Method is an engineering framework designed to utilize AI tools like Claude Code 10x faster while virtually eliminating AI hallucinations, assumptions, and drift.

```
┌────────────────────────────────────────────────────────┐
│  LAYER 1: THE SPEC (0:28 - 3:34)                       │
│  - Bridge human context with AI compute                │
│  - "Interview me to identify project goals"            │
│  - Agile, compartmentalized specifications             │
│  - Make human verify key decisions                     │
├────────────────────────────────────────────────────────┤
│  LAYER 2: THE VERIFIER (3:34 - 8:48)                   │
│  - Pre-defined evaluation criteria                     │
│  - Second AI model as an adversarial critic            │
│  - Connect to external systems & real runtime signals  │
├────────────────────────────────────────────────────────┤
│  LAYER 3: THE ENVIRONMENT (8:48 - 12:36)               │
│  - Operating workshop with CLAUDE.md / GEMINI.md rules │
│  - LLM knowledge base & reusable custom skills         │
│  - Rule-based guardrails at tool level                 │
└────────────────────────────────────────────────────────┘
```

---

## Detailed Layer Breakdown

### Layer 1: The Spec (Specification Engineering)
1. **Uncover Your True Goal (1:48 - 2:09):**
   - Don't just assign a generic task (e.g. *"write a report"*). Define the underlying decision or conclusion that output must drive.
   - Prompt Directive: *"Interview me to identify the goal of this project."*
2. **Be Agile with Speccing (2:13 - 2:48):**
   - Avoid waterfall specification dumps. Break tasks into smaller, manageable buckets.
   - Prompt Directive: *"Bias towards smaller and more compartmentalized specs."* Review output at each milestone.
3. **Be Precise to Limit Assumptions (2:54 - 3:12):**
   - Every AI assumption introduces potential drift.
   - Prompt Directive: *"Make me verify key decisions to ensure nothing is missed."*

### Layer 2: The Verifier (Deterministic Quality Assurance)
1. **Upfront Evaluation Criteria:** Never start coding or generating without specifying what success looks like.
2. **The Critic AI:** Use an independent model/session to grade the generator model's output objectively.
3. **External Truth:** Verify code by running it against real linters, compilers, and test suites rather than relying on LLM visual self-inspection.

### Layer 3: The Environment (The AI Workshop)
1. **Auto-Injected Instructions:** Maintain a `GEMINI.md` or `CLAUDE.md` file loaded into every session.
2. **Knowledge Base:** Maintain a structured, markdown-based knowledge repository.
3. **Custom Skills:** Package repeatable procedures into modular skills.
4. **Tool Guardrails:** Restrict AI tools to authorized boundaries to prevent destructive side quests.
