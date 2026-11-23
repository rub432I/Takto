---
name: prevent-sidequests
description: Operational protocol to keep AI agents strictly on-mission, prevent side quests, enforce codebase-as-truth, and manage task status tags. Use on multi-turn, complex development projects.
---

# Side-Quest Prevention & Focus Protocol

AI agents frequently drift into "side quests"—fixing obsolete bugs, rewriting deprecated features, or refactoring unrequested files because they treat old documentation as truth.

## Core Directives

1. **Codebase is Ground Truth:**
   - Always trust actual source code over old markdown notes, stale tickets, or archived plans.
   - If documentation contradicts code, update or archive the documentation—never alter working code to match old notes.

2. **Strict Scope Enforcement:**
   - Ignore any issue, feature, or cleanup that was not explicitly requested by the user.
   - Never perform "courtesy" refactoring of adjacent files.

3. **Task Status Tracking:**
   Every ticket or roadmap item must bear an unambiguous status tag:
   - `[not-started]`
   - `[in-progress]`
   - `[completed]`
   - `[archived]`

4. **Documentation Sweeps:**
   - Periodically archive obsolete planning documents into an `archive/` folder.
   - Do not index or search the `archive/` directory during standard coding turns.
