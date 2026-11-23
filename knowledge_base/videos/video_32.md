# Video 32: Matt Pocock: The 5-Step Agent Development Loop

**Source Document Lines:** `L412 - L426` in `youtube_pl.txt`  
**Index:** Entry 32 of 41 in YouTube Playlist Knowledge Base  

---

## 📝 Transcript Content & Summary

Coding with AI is less about writing syntax and more about strategic programming. As Matt Pocock explains, AI agents are excellent at tactical tasks (like writing specific functions), but humans must act as the strategist to ensure long-term code quality and architecture (5:03).

Here is the recommended approach to building software with agents:

1.  Strategic Alignment: Don't just prompt blindly. Use tools like Matt's "Grill with Docs" (12:35) to align the agent with your codebase’s specific domain language and glossary before it writes a single line of code. This ensures the AI understands your project requirements (14:04).
2.  The Development Loop:
Grill: Ask the agent questions to define the problem.
Spec: Create a clear technical specification (25:18).
Tickets: Break the work into manageable pieces for the agent.
Sub-agents: Use agents in parallel worktrees to implement features, then merge them (34:35).
Review & Retro: Conduct adversarial code reviews focused on "merge danger" rather than line-by-line inspection, and run a retro to improve the agent's performance for the next session (40:20-42:51).
3.  Build Trust through Constraints: Instead of manual line-by-line review, build a system you can trust by implementing strict typing, fast automated test suites, and feedback loops (44:52). These constraints keep agents within a "straight jacket," making it difficult for them to introduce errors (51:00).
4.  Optimize Architecture: Focus on building deep modules—large, well-defined chunks of code—rather than shallow, scattered modules. This makes the codebase significantly easier for agents to navigate and manage (56:46).

---

## 💡 Actionable Engineering Takeaways

- Coding with AI is less about writing syntax and more about strategic programming. As Matt Pocock explains, AI agents are excellent at tactical tasks (like writing specific functions), but humans must act as the strategist to ensure long-term code quality and architecture (5:03).
- Here is the recommended approach to building software with agents:
- 1.  Strategic Alignment: Don't just prompt blindly. Use tools like Matt's "Grill with Docs" (12:35) to align the agent with your codebase’s specific domain language and glossary before it writes a single line of code. This ensures the AI understands your project requirements (14:04).
- 2.  The Development Loop:
- Grill: Ask the agent questions to define the problem.
- Spec: Create a clear technical specification (25:18).

