# 05: Small Language Models (SLMs) & Local Execution

**Source:** `youtube_pl.txt` (Lines 83-91)  

---

## Overview
Small language models (2–4 GB) excel as specialized tools for clearly defined, narrow operations. They outperform massive cloud models in speed, cost, and privacy when scoped effectively.

---

## Top 6 Use Cases for Local SLMs (8:21 - 8:55)

```
┌────────────────────────────────────────────────────────┐
│ 1. Complete Privacy & Local Control (4:57 - 5:40)      │
│    - Medical, financial, personal data stays on-device │
├────────────────────────────────────────────────────────┤
│ 2. Offline Functionality                               │
│    - Fully operational without active internet         │
├────────────────────────────────────────────────────────┤
│ 3. Low-Power Hardware (1:02 - 1:13, 3:32 - 3:36)       │
│    - Runs on phones, Raspberry Pi, home micro-servers  │
├────────────────────────────────────────────────────────┤
│ 4. High-Volume Simple Automations (0:45 - 0:48)        │
│    - Background text summarization, OCR, translation   │
├────────────────────────────────────────────────────────┤
│ 5. Cost Minimization                                   │
│    - Zero API token costs for repetitive workloads     │
├────────────────────────────────────────────────────────┤
│ 6. Narrow Specialization (7:58 - 8:20)                 │
│    - Strictly structured JSON input/output schemas     │
└────────────────────────────────────────────────────────┘
```

### Engineering Best Practices
- **Do not** use SLMs for high-level multi-step strategic reasoning.
- **Do** use SLMs for classification, regex generation, structured data extraction, and local speech transcription (e.g. `whisper.cpp`).
