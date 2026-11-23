#!/usr/bin/env python3
"""
cook.py: Leon Lin's Cook Design Workflow Runner
Manages design briefs, reference assets, web mockups, and independent judge logs in 'cook/'.
"""

import sys
import os
import argparse
from datetime import datetime

WORKSPACE = "/data/data/com.termux/files/home/workspace"
COOK_DIR = os.path.join(WORKSPACE, "cook")

def init_cook(project_name: str):
    """Initialize a dedicated cook workspace directory."""
    os.makedirs(os.path.join(COOK_DIR, "references"), exist_ok=True)
    os.makedirs(os.path.join(COOK_DIR, "history"), exist_ok=True)

    brief_path = os.path.join(COOK_DIR, "brief.md")
    if not os.path.isfile(brief_path):
        with open(brief_path, "w", encoding="utf-8") as f:
            f.write(f"""# Cook Design Brief: {project_name}
**Date:** {datetime.now().strftime('%Y-%m-%d')}  
**Target Quality Bar:** 99.0% fidelity to references  
**Status:** In Progress (Round 1)

---

## 1. Project Personality & Opinion
- **Brand Vibe:** High-craft, purposeful, high-contrast typography
- **Forbidden Slop:** Generic beige backgrounds, unstyled Inter font, excessive glow

## 2. 9–12 Visual Reference Sources
- Reference 1: Dribbble/Awwwards curation
- Reference 2: ...

## 3. UI Component Architecture (Griffin Wooldridge Tiers)
- **Foundations:** shadcn/ui or Base UI
- **Specialists:** Sonner (toasts), Vaul (drawer), cmdk (palette)
- **One-Standout Element:** [Single animated hero or 3D element]
""")
        print(f"[+] Initialized {brief_path}")

    log_path = os.path.join(COOK_DIR, "scores.log")
    if not os.path.isfile(log_path):
        with open(log_path, "w", encoding="utf-8") as f:
            f.write(f"# Cook Design Grading Log: {project_name}\n\n")

    print(f"✅ Cook design workshop initialized at: {COOK_DIR}")

def log_round(round_no: int, score: float, feedback: str):
    """Log an evaluation round from the independent judge."""
    log_path = os.path.join(COOK_DIR, "scores.log")
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M")
    entry = f"""### Round {round_no} - {timestamp}
- **Score:** {score:.1f}% (Target: 99.0%)
- **Result:** {'APPROVED' if score >= 99.0 else 'ITERATE NEXT ROUND'}
- **Judge Feedback:**
{feedback}

---
"""
    with open(log_path, "a", encoding="utf-8") as f:
        f.write(entry)
    print(f"[+] Recorded Round {round_no} evaluation (Score: {score:.1f}%)")

def main():
    parser = argparse.ArgumentParser(description="Leon Lin's Cook Design Workflow Runner.")
    parser.add_argument("--init", metavar="PROJECT_NAME", help="Initialize cook directory")
    parser.add_argument("--log-round", nargs=3, metavar=("ROUND", "SCORE", "FEEDBACK"), help="Log judge review")
    args = parser.parse_args()

    if args.init:
        init_cook(args.init)
    elif args.log_round:
        log_round(int(args.log_round[0]), float(args.log_round[1]), args.log_round[2])
    else:
        parser.print_help()

if __name__ == "__main__":
    main()
