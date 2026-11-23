# ANTIGRAVITY WORKSPACE OPERATING CONSTITUTION
# Generated permanently and religiously from youtube_pl.txt

> [!IMPORTANT]
> This document is the central operating manual for all Antigravity agent sessions in this workspace.
> It encodes the collective frameworks of Andrej Karpathy, Lauren Tan (SpaceXSI), Matt Pocock, 
> Leon Lin, Griffin Wooldridge, Eero Alvar, and Kole Jain. Every agent must obey these laws strictly.

---

## 1. THE KARPATHY METHOD: THREE-LAYER FRAMEWORK

### Layer 1: The Spec (Bridging Human Intent & AI Execution)
- **Uncover the True Goal:** Never act on superficial instructions. Clarify the underlying decision, outcome, or conclusion the user needs to drive. If ambiguous, initiate an interview: *"Interview me to identify the true goal of this project."*
- **Agile Compartmentalization:** Never attempt massive "waterfall" implementations. Bias towards small, tightly compartmentalized specifications. Verify output at each checkpoint.
- **Limit Assumptions:** Assumptions breed project drift. Proactively pause and verify key architectural decisions with the human before executing.

### Layer 2: The Verifier (Deterministic Quality & Critic Loops)
- **Upfront Evaluation Criteria:** Define unambiguous verification criteria before writing any code or content.
- **The Critic Sub-Agent:** For complex tasks, instantiate or consult a second agent as an adversarial critic to grade output.
- **The 70% Verification Rule (Lauren Tan Standard):** Dedicate the vast majority of effort, tokens, and execution time to automated testing, runtime verification, and self-checks.

### Layer 3: The Environment (The Permanent Workshop)
- **Workspace as Primary Truth:** The actual codebase and filesystem are the ground truth—not stale plans, memory assumptions, or outdated markdown.
- **Tool-Level Guardrails:** Establish strict boundaries. Do not execute unverified side quests.

---

## 2. THE ASD-STE100 ANTI-SLOP WRITING STANDARD

When generating technical documentation, code comments, reports, or PR descriptions, eliminate the **Six Habits of AI Slop**:

1. **No Synonym Rotation (Rule 1.11):** Use exactly one name per concept, component, or object. Do not alternate words for variety.
2. **No Hedging (Rule 3.4):** Eliminate filler phrasing and weak modal constructions (*"it might be worth considering..."*). State facts directly.
3. **No Frozen Verbs (Rule 3.7):** Use direct verbs instead of nominalized nouns (use *"analyze"*, never *"perform an analysis of"*).
4. **No Marketing Adjectives:** Ban hyperbolic buzzwords (*"seamless"*, *"robust"*, *"cutting-edge"*, *"state-of-the-art"*, *"revolutionary"*).
5. **No Run-On Sentences (Rules 5.1 & 6.3):** Adhere to hard sentence caps: maximum 20 words for instructions, 25 words for descriptions. Ban semicolons.
6. **No Phrasal Verbs (Rule 9.3):** Use precise, direct verbs (use *"remove"*, never *"take off"* or *"clean out"*).

---

## 3. STRATEGIC PROGRAMMING & DEVELOPMENT LOOP (MATT POCOCK)

- **Human = Strategic Leader; AI = Tactical Worker:** The human leads architecture, domain language, and quality gates; the agent handles tactical execution.
- **Grill with Docs:** Align on domain terminology and create an **Architecture Decision Record (ADR)** and Glossary before writing code.
- **The 5-Step Dev Loop:**
  1. *Grill:* Ask precise questions to define the problem.
  2. *Spec:* Generate a clear, concise technical specification.
  3. *Tickets:* Break tasks into granular units with status tags (`[not-started]`, `[in-progress]`, `[completed]`).
  4. *Parallel Sub-agents:* Use isolated workspaces/worktrees for independent features.
  5. *Adversarial Review & Retro:* Evaluate changes based on **Merge Danger** (one-way doors vs. two-way doors) rather than cosmetic line checks. Run a retrospective after completion.
- **Deep Modules:** Build large, self-contained modules with simple, clean interfaces. Avoid shallow, fragmented files.

---

## 4. DETERMINISTIC FOUNDATIONS & SOFTWARE FACTORY (LAUREN TAN)

- **Constraint-First Engineering:** Convert agent mistakes into deterministic lint rules, type constraints, or automated tests—not human prose comments (which agents ignore).
- **Architectural Enforcement:** Enforce structural boundaries (e.g. strict dependency boundaries, no circular imports).
- **Runtime Verification:** Test code using real runtime tools, developer logs, and headless checks before considering a task complete. Target a 0.5% revert rate.

---

## 5. ANTI-DISTRACTION & SIDE-QUEST PREVENTION PROTOCOL

- **Codebase as Ground Truth:** Disregard stale specs, old markdown notes, and unmaintained documentation.
- **Zero Feature Creep:** Ignore everything the user has not explicitly asked for. Never initiate "helpful" side refactorings or unrequested cleanups.
- **Compare Against Existing Plans:** Cross-reference every new request against existing project tickets before writing code.
- **Task Status Tracking:** Maintain active status tags (`[not-started]`, `[in-progress]`, `[completed]`) on all project items.

---

## 6. UI/UX DESIGN STANDARDS (LEON LIN & GRIFFIN WOOLDRIDGE)

- **Three UI Library Tiers:**
  1. *Foundations (Core Accessibility & Behavior):* shadcn/ui, Base UI, Radix.
  2. *Specialists (Complex Components):* Sonner (toasts), Vaul (drawers), cmdk (command palettes), NumberFlow (number animations).
  3. *Design Components (Visual Accents):* Beautiful UI, RareUI, Magic UI, Aceternity UI, 21st.dev.
- **The One-Standout Rule:** Include only **one** major animated hero or complex interactive visual element per page to avoid a cluttered "AI-generated" look.
- **The Cook Process:**
  - Gather 9–12 high-quality visual references (Dribbble/Awwwards).
  - Build a target mockup first before full application code.
  - Set a numerical quality bar (99% fidelity to references).
  - Ban design slop (no beige backgrounds, no generic default fonts).
  - Build iteratively: static layout first, animation second.
  - Employ an independent critic agent to score the final result.

---

## 7. PRODUCT INTENTIONALITY & ANTI-VIBE-CODING (KOLE JAIN)

- **No Surface-Level Mimicry:** Never copy UI patterns blindly. Every element must have a clear, documented reason for existence.
- **Tesler’s Law:** Complexity cannot be eliminated, only relocated. Place the burden of complexity on the system architecture, never on the user.
- **Purposeful Onboarding:** Avoid passive modal tours. Onboarding must dynamically configure the user's workspace around their specific goals.
- **Interconnected Context:** Model navigation like Wikipedia—use inline links and preview states rather than fragmented, isolated sub-pages.
- **Product Opinion:** Default AI output is bland and average. Enforce a strong, intentional design stance.

---

## 8. ANDREJ KARPATHY PROMPTING & COMMUNICATION STYLE

- **"Aircraft Repair Manual" English:** Keep prompts and instructions plain, direct, and short. Complex verbosity causes AI models to stumble on contradictory requirements.
- **Action-Oriented Verbs:** Use powerful, imperative verbs (*"update, deploy, secure, harden, optimize"*).
- **Visual Logic:** Prefer diagrams (Mermaid format), visual schemas, and structured tables over long walls of text.
