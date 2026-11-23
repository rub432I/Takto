# Video 28: Side-Quest Prevention: Codebase as Truth & CLAUDE.md Rules

**Source Document Lines:** `L358 - L372` in `youtube_pl.txt`  
**Index:** Entry 28 of 41 in YouTube Playlist Knowledge Base  

---

## 📝 Transcript Content & Summary

To prevent AI agents from getting distracted by outdated files, irrelevant notes, or "side quests," you can implement several structural changes in your workflow. According to the video, distraction occurs because agents treat old documentation, specs, and code comments as the current source of truth (0:45-2:05).

Key strategies to stop AI distraction:

Set Clear Rules in `CLAUDE.md`:
Force the agent to treat your actual codebase as the primary source of truth (9:39-9:58).
Include a directive to ignore anything you haven't explicitly asked for to prevent unnecessary feature creep (10:13-10:29).
Require the agent to compare new feature requests against existing plans before beginning any work (10:31-11:08).
Manage Task Context:
Always start a fresh session for every new task to avoid context pollution from failed attempts (11:11-11:21).
Use status tags (e.g., "completed," "in progress," "not started") on all tickets so the agent understands the current state of your project (9:20-9:37).
Clean Up Outdated Information:
Build or use a "sweep" skill to identify and archive old documentation, stale plans, and irrelevant notes (11:28-12:50).
Implement a hook that blocks the agent from reading archived files (12:14-12:24).

---

## 💡 Actionable Engineering Takeaways

- To prevent AI agents from getting distracted by outdated files, irrelevant notes, or "side quests," you can implement several structural changes in your workflow. According to the video, distraction occurs because agents treat old documentation, specs, and code comments as the current source of truth (0:45-2:05).
- Key strategies to stop AI distraction:
- Set Clear Rules in `CLAUDE.md`:
- Force the agent to treat your actual codebase as the primary source of truth (9:39-9:58).
- Include a directive to ignore anything you haven't explicitly asked for to prevent unnecessary feature creep (10:13-10:29).
- Require the agent to compare new feature requests against existing plans before beginning any work (10:31-11:08).

