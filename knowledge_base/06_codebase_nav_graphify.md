# 06: Codebase Navigation, Graphify & Dev Swarm

**Source:** `youtube_pl.txt` (Lines 92-105)  

---

## Overview
AI coding agents often suffer from a major limitation: repeatedly reading entire repository files, consuming massive token budgets, and missing subtle dependency connections between distant modules.

---

## Graphify Architecture (0:12 - 1:21)

Graphify is an open-source tool designed to provide a visual and structural knowledge graph of a repository before the AI reads individual source files.

```
Codebase Files ──▶ [Graphify Analyzer] ──▶ Knowledge Graph (Dependencies, Call Flows)
                          │
                          ▼
            `/graphify query <symbol>`
                          │
                          ▼
             Agent reads exact trace only
```

### Key Capabilities
- **Codebase Mapping (0:00 - 0:46):** Builds a dependency graph of all functions, classes, imports, and file relationships.
- **Commands:**
  - `pip install graphify`
  - `graphify claude install`
  - `/graphify`: Instructs the agent to consult the dependency map first.
  - `/graphify query`: Traces specific code execution flows without reading whole files.
  - `graphify hook install`: Installs a Git commit hook to update the knowledge graph on every commit.
- **Language Support:** Compatible with 28 programming languages (Python, TypeScript, Go, Rust, Java, C++, SQL, Terraform, etc.).

---

## Dev Swarm (1:22 - 1:32)
- Used when running multiple agents in parallel across separate Git branches.
- Provides isolated execution environments to prevent conflicts during parallel feature development.
