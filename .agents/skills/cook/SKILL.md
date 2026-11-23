---
name: cook
description: Leon Lin's automated design-to-review loop. Manages design briefs, reference assets, web mockups, and independent judge evaluations in iterative rounds.
---

# Leon Lin's "Cook" Automated Design Loop

Executes a repeatable, multi-round design cycle that prevents generic AI styling and guarantees professional-grade output.

```
Round Start ──▶ [1. Ingest 9-12 References] ──▶ [2. Build Web Mockup] ──▶ [3. Independent Judge]
                      ▲                                                           │
                      └─────────────── Refine based on critique ──────────────────┘
```

## The Dedicated `cook/` Workspace
Every Cook project maintains a `cook/` folder:
- `cook/brief.md`: App specifications, core constraints, brand personality.
- `cook/references/`: 9–12 high-resolution screenshots from Dribbble/Awwwards.
- `cook/history/`: Iterative code drafts and screenshots of each round.
- `cook/scores.log`: Grading transcripts from the independent critic agent.

## Execution Rules
1. Never build the final application directly—build a static mockup first.
2. Ban the slop: No generic beige backgrounds, no unstyled default fonts.
3. Target a 99% numerical quality bar before writing production backend code.
