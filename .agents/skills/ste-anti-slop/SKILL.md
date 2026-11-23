---
name: ste-anti-slop
description: ASD-STE100 Simplified Technical English standard and anti-slop writing guidelines. Use when writing technical documentation, pull requests, commit messages, code comments, or user-facing guides to eliminate wordiness and ambiguity.
---

# ASD-STE100 Anti-Slop Writing Standard

Originally created in 1986 for aerospace maintenance, ASD-STE100 eliminates ambiguity, filler, and generic "AI slop" through mechanical linguistic constraints.

## The Six Prohibited Habits of AI Slop

| Habit | Violation | Mandatory Correction |
| :--- | :--- | :--- |
| **1. Synonym Rotation** | Using different words for the same item | Assign **one** approved term per object. Stick to it throughout. |
| **2. Hedging** | *"It is important to note that..."* | State the requirement directly: *"Do X."* |
| **3. Frozen Verbs** | *"Perform an analysis of the log"* | Use active verbs: *"Analyze the log."* |
| **4. Marketing Buzzwords** | *"Seamless, robust, cutting-edge"* | Remove completely. Use objective technical descriptors. |
| **5. Run-On Sentences** | Sentences exceeding 25 words or using semicolons | Cap instructions at **20 words**, descriptions at **25 words**. Ban semicolons. |
| **6. Phrasal Verbs** | *"Take off the lid"*, *"look into the bug"* | Use direct verbs: *"Remove the lid"*, *"Investigate the bug"*. |

## Execution Checklist for Agents
- [ ] Are sentences concise (≤ 20 words for steps)?
- [ ] Is active voice used (*"The compiler checks..."* not *"Checks are performed by the compiler"* )?
- [ ] Are all hype adjectives (*seamless, robust, game-changing*) deleted?
- [ ] Is every technical component referred to by a single, consistent name?
