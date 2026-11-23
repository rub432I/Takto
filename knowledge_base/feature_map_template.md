# Feature Map System: Shared Vocabulary for UI & AI Agents

**Reference:** `youtube_pl.txt` (Lines 287, 305, 329, 351)  
**Author:** Lauren Tan (`poteto`), Grok Bot Team at SpaceXSI  

---

## Overview
AI agents fail when there is a semantic mismatch between human language and code elements. A **Feature Map** provides a deterministic, shared vocabulary linking user interface elements, backend state, and developer tools.

---

## Feature Map Architecture

```
User / Prompt Term ──────▶ [Feature Map Registry] ──────▶ Deterministic DOM / State Selector
"Chat Composer"           featureMap.chat.composer        `[data-testid="chat-composer"]`
"Settings Drawer"         featureMap.nav.settingsDrawer   `[data-testid="settings-drawer"]`
"Approval Card"           featureMap.agent.approvalCard   `[data-testid="approval-card"]`
```

### Implementation Rules
1. **Data-TestID Taxonomy:** Assign explicit `data-testid` attributes to all interactive elements matching the Feature Map.
2. **Headless Verification:** Equip agents with the Chrome DevTools protocol to query these exact selectors during runtime verification.
3. **No Visual Guessing:** Never let an agent guess what button to click based on CSS class names or fuzzy text matching.
