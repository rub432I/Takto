# 13: Preventing AI Distraction & Eliminating Side Quests

**Source:** `youtube_pl.txt` (Lines 358-396)  

---

## Overview
A "side quest" occurs when an AI agent gets distracted by outdated documentation, obsolete specifications, or stale code comments and begins "fixing" or refactoring things the user never asked for (1:48).

---

## Why Side Quests Happen (0:52 - 3:20)
1. **Context Confusion:** Agents read all files in a project indiscriminately. They treat an old note from months ago as an active directive.
2. **Helpfulness Bias:** Models try to be helpful by resolving perceived inconsistencies, even when those issues were intentionally left alone or already dropped.
3. **Severe Impact:** Sessions with side quests waste tokens, take 3x longer, and introduce project drift.

---

## Structural Countermeasures (9:20 - 12:50)

```
┌────────────────────────────────────────────────────────────────────────┐
│ 1. Codebase is Primary Ground Truth (9:39 - 9:58)                      │
│    - Never trust outdated docs over running code                       │
├────────────────────────────────────────────────────────────────────────┤
│ 2. Strict Scope Boundary (10:13 - 10:29)                               │
│    - Ignore anything not explicitly requested; zero unrequested scope  │
├────────────────────────────────────────────────────────────────────────┤
│ 3. Status Tag Taxonomy (9:20 - 9:37)                                   │
│    - Tag every task: [completed], [in-progress], [not-started]         │
├────────────────────────────────────────────────────────────────────────┤
│ 4. Fresh Sessions (11:11 - 11:21)                                      │
│    - Start a clean conversation turn/session for every distinct task   │
├────────────────────────────────────────────────────────────────────────┤
│ 5. The "Sweep" Archiving Protocol (11:28 - 12:50)                      │
│    - Move stale notes and old plans into an archive/ directory         │
│    - Use a hook/rule to block the agent from indexing archived files   │
└────────────────────────────────────────────────────────────────────────┘
```
