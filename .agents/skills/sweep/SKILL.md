---
name: sweep
description: Identifies, sweeps, and archives outdated documentation, obsolete plans, and stale notes to prevent AI agent side quests and context pollution.
---

# Documentation Sweep & Archiving Protocol

AI agents get distracted by reading old notes, deprecated plans, and stale comments—mistaking them for active project directives.

## The Sweep Protocol

```
Active Workspace Files ──▶ [Sweep Check] ──▶ Stale / Obsolete Notes ──▶ archive/
                                                                           │
                                  [BLOCKED] ◀── Agent search & tools ──────┘
```

### Directives
1. **Detect Stale Context:** Scan project markdown notes, tickets marked `[completed]` or `[archived]`, and old specification drafts.
2. **Move to Archive:** Relocate these files into the `archive/` directory.
3. **Guardrail Hook:** Agents must **never** read or search files inside `archive/`. The codebase remains the sole source of truth.

To perform an automated sweep:
```bash
python3 ~/workspace/tools/sweep.py
```
