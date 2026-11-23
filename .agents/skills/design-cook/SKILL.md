---
name: design-cook
description: Leon Lin's Cook design workflow and Griffin Wooldridge's 3-tier component architecture. Use when creating UI/UX designs, web mockups, landing pages, or selecting frontend libraries.
---

# UI Design System & The Cook Workflow

This skill prevents generic, bland "AI-looking" interfaces by combining curated component libraries with Leon Lin's automated design-review loop.

## 1. Griffin Wooldridge's 3-Tier Component Stack

1. **Foundations (Behavior, Accessibility & Primitives):**
   - `shadcn/ui`, `Base UI`, `Radix`
   - Use for buttons, inputs, dialogs. Customize radius, color palette, and typography to fit brand identity.
2. **Specialists (Complex, Error-Prone Components):**
   - `Sonner` (toasts)
   - `Vaul` (mobile drawers)
   - `cmdk` (command palettes / search)
   - `NumberFlow` (smooth numeric animations)
3. **Design Components (Visual Accents & Moments):**
   - `Beautiful UI`, `RareUI`, `Magic UI`, `Aceternity UI`, `21st.dev`

### The One-Standout Rule
Include **only one** major animated hero, 3D element, or complex interactive visual element per page. Having everything glow, float, and animate makes the UI look cheap and cluttered.

---

## 2. Leon Lin's "Cook" Iteration Loop

```
Gather 9-12 References ──▶ Build Web Mockup ──▶ Set Quality Bar (99%) ──▶ Independent Judge
                               ▲                                                │
                               └───────── Refine based on feedback ─────────────┘
```

1. **Visual Reference First:** Gather 9–12 high-resolution screenshots from Dribbble or Awwwards.
2. **Web Mockup First:** Build a lightweight HTML/Tailwind mockup before touching backend code.
3. **Set Numerical Quality Bar:** Target 99% fidelity to references.
4. **Ban Design Slop:** Strictly prohibit beige/off-white generic backgrounds, default Inter font with no styling, and unstyled rounded boxes.
5. **Separate Judge:** Use an independent prompt or critic agent to evaluate the final mockup.
