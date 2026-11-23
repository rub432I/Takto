# 10: Leon Lin's AI Design Process & The Cook Skill

**Source:** `youtube_pl.txt` (Lines 211-248)  
**Author:** Leon Lin  

---

## Overview
Achieving high-end AI design requires shifting from verbose prompting to a **structured design process with constraint loops**.

---

## The 6-Step Design Process

```
[1. 9-12 References] ──▶ [2. Target Mockup] ──▶ [3. 99% Quality Bar]
                                                       │
[6. Independent Critic] ◀── [5. Iterative Build] ◀── [4. Ban Design Slop]
```

1. **Strong Visual References (0:53 - 1:36):** Provide 9–12 high-resolution screenshots from Dribbble or Awwwards. AI understands typography, spacing, and layout better from visual examples than from words.
2. **Build Web Mockup First (4:09 - 4:34):** Never build the full app directly. Create a simple static web mockup to test styles without burning tokens.
3. **Set a Numerical Quality Bar (5:03 - 6:32):** Give the AI a numerical target (e.g. *99.9% accuracy to reference*) so it continues refining beyond the first functional draft.
4. **Ban Design Slop (8:48 - 10:14):** Explicitly prohibit repetitive AI tropes (generic beige/neutral backgrounds, default unstyled system fonts).
5. **Separate Build from Motion (10:15 - 11:11):** Build static design first. Add animations second using reference videos.
6. **Deploy an Independent Critic (11:53 - 12:53):** The model that generated the code is too biased to critique it. Use a separate model or fresh session to judge and grade the output.

---

## The "Cook" Automated Workflow (12:53 - 15:08)
- Automates this design-to-review loop in iterative rounds.
- Maintains a dedicated `cook/` directory storing the design brief, reference screenshots, comparison side-by-sides, and grading logs.
