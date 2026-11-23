# Video 12: Graphify & Dev Swarm: Visual Codebase Dependency Knowledge Graphs

**Source Document Lines:** `L092 - L105` in `youtube_pl.txt`  
**Index:** Entry 12 of 41 in YouTube Playlist Knowledge Base  

---

## 📝 Transcript Content & Summary

This video introduces Graphify, a free, open-source tool designed to resolve a primary limitation of Claude Code: its tendency to repeatedly read repository files, which can waste tokens and overlook connections between files (0:12-0:19).

How Graphify Works:

Mapping: It creates a visual knowledge graph of your entire codebase, including all dependencies and file relationships (0:00-0:06, 0:38-0:46).
Integration: By using specific terminal commands like `pip install graphify` and `graphify claude install`, you can integrate it into Claude Code (0:26-0:33).
Querying: Once installed, typing `/graphify` in Claude Code allows the AI to read the map before accessing files, and `/graphify query` lets you trace specific code flows (0:36-0:57).
Automation: Running `graphify hook install` ensures the graph automatically updates with every git commit, providing fresh context (0:58-1:07).
Support: It is compatible with 28 programming languages, including Python, TypeScript, Go, Rust, Java, C++, SQL, Terraform, and Apex (1:11-1:21).

Additional Recommendation:

The video also mentions Dev Swarm (devswarm.ai), a tool for developers who run multiple Claude Code agents on different branches simultaneously. It provides isolated VS Code environments to prevent conflicts during parallel development (1:22-1:32).

---

## 💡 Actionable Engineering Takeaways

- This video introduces Graphify, a free, open-source tool designed to resolve a primary limitation of Claude Code: its tendency to repeatedly read repository files, which can waste tokens and overlook connections between files (0:12-0:19).
- How Graphify Works:
- Mapping: It creates a visual knowledge graph of your entire codebase, including all dependencies and file relationships (0:00-0:06, 0:38-0:46).
- Integration: By using specific terminal commands like `pip install graphify` and `graphify claude install`, you can integrate it into Claude Code (0:26-0:33).
- Querying: Once installed, typing `/graphify` in Claude Code allows the AI to read the map before accessing files, and `/graphify query` lets you trace specific code flows (0:36-0:57).
- Automation: Running `graphify hook install` ensures the graph automatically updates with every git commit, providing fresh context (0:58-1:07).

