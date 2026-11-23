---
name: pick-ui-library
description: Griffin Wooldridge's UI library selection guide and 3-tier component architecture. Use when choosing UI libraries, frontend components, or styling interfaces to avoid generic AI slop.
---

# Griffin Wooldridge's 3-Tier UI Component Architecture

Stop letting AI build every component from scratch. Use specialized component libraries categorized into three functional tiers to give products distinct character, accessibility, and high performance.

```
┌────────────────────────────────────────────────────────────────────────┐
│ TIER 1: FOUNDATIONS (Behavior & Accessibility)                         │
│ - Libraries: shadcn/ui, Base UI, Radix                                 │
│ - Use for: Buttons, inputs, dialogs, form primitives                   │
│ - Rule: Always customize radius, colors, and typography to brand       │
├────────────────────────────────────────────────────────────────────────┤
│ TIER 2: SPECIALISTS (Complex Components AI Fails to Build Correctly)   │
│ - Sonner: Toast notifications and micro-feedback                       │
│ - Vaul: Smooth, touch-friendly mobile drawers & bottom sheets          │
│ - cmdk: Command palettes and keyboard-first search interfaces          │
│ - NumberFlow: Smooth, layout-stable animated number transitions        │
├────────────────────────────────────────────────────────────────────────┤
│ TIER 3: DESIGN COMPONENTS (Flashy Accents & AI Moments)                │
│ - Beautiful UI: Best for streaming text and agent approval cards       │
│ - RareUI: High-craft animated buttons and text effects                 │
│ - Magic UI: Animated components built on top of shadcn                 │
│ - Aceternity UI: 3D interactive hero backgrounds and landing sections   │
│ - 21st.dev: Marketplace of artisanal design engineer components        │
└────────────────────────────────────────────────────────────────────────┘
```

## The "One-Standout Rule"
Include **only one** major animated hero or complex interactive visual element per page.
If every element glows, floats, or rotates, the interface looks like a cluttered, generic AI template.
