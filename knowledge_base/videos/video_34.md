# Video 34: Matt Pocock: Merge Danger Review (One-Way vs. Two-Way Doors)

**Source Document Lines:** `L440 - L450` in `youtube_pl.txt`  
**Index:** Entry 34 of 41 in YouTube Playlist Knowledge Base  

---

## 📝 Transcript Content & Summary

To code effectively with AI, Matt Pocock outlines a strategic workflow that treats AI as a tactical worker while the human maintains strategic control. Follow these steps for a structured development loop:

1.  Strategic Alignment (12:35 - 17:49): Before writing code, use tools like Grill with Docs to define a shared language, glossary, and Architecture Decision Records (ADRs). This ensures the agent understands the project's specific domain and context.
2.  Spec and Tickets (25:18 - 34:35): Once aligned, create a clear technical specification and break the work down into granular, manageable tickets for the agent.
3.  Parallel Execution (34:35 - 40:20): Use sub-agents operating in separate worktrees to implement features in parallel, which are later merged into a single pull request.
4.  Adversarial Review (40:20 - 42:51): Review the AI's output by assessing merge danger rather than checking line-by-line. Focus on whether the change is a one-way or two-way door.
5.  Retro and Feedback (42:34 - 44:52): Perform a retro at the end of each session to analyze the agent's performance and suggest improvements for future iterations.

To build trust in your agents, Matt emphasizes implementing strict typing, fast automated test suites, and feedback loops (44:52), which constrain the agents' actions and make them more reliable.

---

## 💡 Actionable Engineering Takeaways

- To code effectively with AI, Matt Pocock outlines a strategic workflow that treats AI as a tactical worker while the human maintains strategic control. Follow these steps for a structured development loop:
- 1.  Strategic Alignment (12:35 - 17:49): Before writing code, use tools like Grill with Docs to define a shared language, glossary, and Architecture Decision Records (ADRs). This ensures the agent understands the project's specific domain and context.
- 2.  Spec and Tickets (25:18 - 34:35): Once aligned, create a clear technical specification and break the work down into granular, manageable tickets for the agent.
- 3.  Parallel Execution (34:35 - 40:20): Use sub-agents operating in separate worktrees to implement features in parallel, which are later merged into a single pull request.
- 4.  Adversarial Review (40:20 - 42:51): Review the AI's output by assessing merge danger rather than checking line-by-line. Focus on whether the change is a one-way or two-way door.
- 5.  Retro and Feedback (42:34 - 44:52): Perform a retro at the end of each session to analyze the agent's performance and suggest improvements for future iterations.

