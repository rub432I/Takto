# 09: UI Component Libraries & Anti-Generic Aesthetics

**Source:** `youtube_pl.txt` (Lines 168-209)  
**Author:** Griffin Wooldridge  

---

## Overview
To stop your application from looking like an AI-generated template, **stop letting AI code every component from scratch**. Instead, provide the AI with specialized component libraries categorized into three distinct tiers.

---

## The Three-Tier UI Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│ 1. FOUNDATIONS (0:30 - 1:24)                                           │
│    - Role: Core behavior, accessibility, keyboard nav, primitives      │
│    - Libraries: shadcn/ui, Base UI, Radix                              │
│    - Action: Customize border radius, color palette, and typography    │
├────────────────────────────────────────────────────────────────────────┤
│ 2. SPECIALISTS (1:25 - 2:26)                                           │
│    - Role: Complex components AI notoriously builds poorly from scratch │
│    - Libraries:                                                        │
│      • Sonner (toasts & notifications)                                 │
│      • Vaul (mobile-optimized drawers)                                 │
│      • cmdk (command menus / search palettes)                          │
│      • NumberFlow (smooth numeric animations)                          │
├────────────────────────────────────────────────────────────────────────┤
│ 3. DESIGN COMPONENTS (2:27 - 3:39)                                     │
│    - Role: Pre-styled moments, flashy accents, and AI-specific UX      │
│    - Libraries: Beautiful UI, RareUI, Magic UI, Aceternity UI, 21st.dev│
└────────────────────────────────────────────────────────────────────────┘
```

---

## The "One-Standout Rule" (3:40 - 3:55)
- Keep only **one** major animated hero or interactive background per page.
- Using too many animated elements causes visual clutter and screams "cheap AI template."
- When prompting Claude, explicitly name these libraries or instruct it to install the `pick-ui-library` skill.
