# Video 29: Understanding Side Quests: Context Confusion & Over-Eagerness

**Source Document Lines:** `L373 - L387` in `youtube_pl.txt`  
**Index:** Entry 29 of 41 in YouTube Playlist Knowledge Base  

---

## 📝 Transcript Content & Summary

In the context of AI agents, side quests occur when an AI gets distracted by outdated information and starts working on tasks that were not requested (1:48).

Think of it as the AI finding an old note in your project files—like a plan for a feature you already dropped or a bug fix that was finished years ago—and deciding it needs to "fix" or "address" it on its own (2:41-3:11).

Why this happens:

Context Confusion: Agents read all your project files, including outdated `CLAUDE.md` documents, old specs, and stale comments in the code (0:52-1:28).
Over-eagerness: Models often try to be helpful by solving problems they see, even if those problems are no longer relevant or were intentionally left alone (3:04-3:20).

The impact:

Efficiency Loss: Sessions with side quests take significantly longer, use more tokens, and require more actions to complete because the agent is doing work you didn't ask for (3:45-3:56).
Project Drift: The agent might alter code or configurations based on obsolete information, leading to discrepancies between your desired plan and the actual codebase (3:30-3:43).

---

## 💡 Actionable Engineering Takeaways

- In the context of AI agents, side quests occur when an AI gets distracted by outdated information and starts working on tasks that were not requested (1:48).
- Think of it as the AI finding an old note in your project files—like a plan for a feature you already dropped or a bug fix that was finished years ago—and deciding it needs to "fix" or "address" it on its own (2:41-3:11).
- Why this happens:
- Context Confusion: Agents read all your project files, including outdated `CLAUDE.md` documents, old specs, and stale comments in the code (0:52-1:28).
- Over-eagerness: Models often try to be helpful by solving problems they see, even if those problems are no longer relevant or were intentionally left alone (3:04-3:20).
- The impact:

