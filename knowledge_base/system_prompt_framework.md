# System Prompt Architectural Framework

**Reference:** `youtube_pl.txt` (Lines 23-31, Video 04)  

---

## What is a System Prompt?
A system prompt is a foundational set of instructions given to an AI model before it begins interacting with a user. It functions as the AI's **rulebook and operating manual**, defining its persona, boundaries, reasoning patterns, and operational constraints.

---

## The Four Core Functions

```
                     ┌─────────────────────────────────────────┐
                     │          SYSTEM PROMPT PILLARS          │
                     └────────────────────┬────────────────────┘
                                          │
       ┌──────────────────┬───────────────┴───────────────┬──────────────────┐
       ▼                  ▼                               ▼                  ▼
┌──────────────┐   ┌──────────────┐               ┌──────────────┐   ┌──────────────┐
│  1. IDENTITY │   │2. CONSTRAINTS│               │ 3. BEHAVIOR  │   │  4. CONTEXT  │
└──────┬───────┘   └──────┬───────┘               └──────┬───────┘   └──────┬───────┘
       │                  │                              │                  │
 • Who the AI is    • Hard limits                  • Formatting       • Project scope
 • Expertise level  • Banned tools                 • Tool usage       • User background
 • Tone & posture   • Security gates               • Reasoning mode   • Active intent
```

### 1. Defining Identity
- Establishes role and authority: *"You are an elite systems engineer and adversarial critic."*
- Establishes communication posture: Aircraft repair manual English, concise, objective.

### 2. Setting Constraints
- Establishes non-negotiable boundaries:
  - *"Never alter existing code unless explicitly requested."*
  - *"Ban marketing adjectives and frozen verbs (ASD-STE100)."*
  - *"Do not bypass tests or execute unverified side quests."*

### 3. Guiding Behavior
- Defines step-by-step reasoning logic:
  - *"For complex tasks, generate an Architecture Decision Record (ADR) first."*
  - *"Format output with Mermaid diagrams and structured tables."*
  - *"Allocate 70% of effort to runtime verification."*

### 4. Establishing Context
- Frames the environment: Operating system (Termux/Android), workspace paths, active Git branches, available tools, and domain glossaries.
