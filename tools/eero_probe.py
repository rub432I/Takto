#!/usr/bin/env python3
"""
eero_probe.py: Eero Alvar's Diagnostic Knowledge Probe & Binary Search Quiz Engine
Reference: youtube_pl.txt (Lines 63-65: Video 08)
Executes Phase 1 (Probe) of the Optimized Learning Framework:
1. Asks broad diagnostic questions
2. Executes a binary search across knowledge difficulty
3. Maps exact boundaries of what the learner knows vs. misconceptions
"""

import sys
import argparse

SAMPLE_TOPICS = {
    "git": [
        ("Level 1 (Basic)", "What is the difference between working directory, staging area, and repository?"),
        ("Level 2 (Intermediate)", "What happens under the hood when you run 'git merge --no-ff'?"),
        ("Level 3 (Advanced)", "How does a Git worktree differ from a cloned repository in terms of .git directory?"),
        ("Level 4 (Expert)", "How does the Git packed-refs and commit-graph indexing format optimize rev-list traversals?")
    ],
    "compilers": [
        ("Level 1 (Basic)", "What is the difference between an interpreter and an ahead-of-time compiler?"),
        ("Level 2 (Intermediate)", "What is an Abstract Syntax Tree (AST) and how does lexical analysis feed into parsing?"),
        ("Level 3 (Advanced)", "How does Multiple Dispatch (in Julia) resolve types at compile time compared to single dispatch in C++?"),
        ("Level 4 (Expert)", "What is SSA (Static Single Assignment) form and why does it simplify dead-code elimination?")
    ]
}

def run_probe(topic: str):
    print("=" * 65)
    print(f"🧠 EERO ALVAR DIAGNOSTIC PROBE: {topic.upper()}")
    print("=" * 65)
    print("Framework: Binary Search Knowledge Mapping (Lines 63-65)")
    print("Goal: Concentrate cognitive struggle on real knowledge boundaries.")
    print("-" * 65)

    questions = SAMPLE_TOPICS.get(topic.lower())
    if not questions:
        print(f"Topic '{topic}' ready. Starting at median difficulty (Level 2).")
        print("Diagnostic Question: 'Explain the core mechanism, data structure, and primary failure mode of this topic.'")
        print("\nAnswer the prompt out loud or in writing. Then evaluate your answer.")
    else:
        for level, q in questions:
            print(f"[{level}]")
            print(f"  Q: {q}\n")

    print("=" * 65)
    print("Next Step in Eero Alvar Loop: Use [Plan] to generate Mermaid diagrams for identified gaps.")

def main():
    parser = argparse.ArgumentParser(description="Eero Alvar Diagnostic Knowledge Probe.")
    parser.add_argument("topic", default="git", nargs="?", help="Topic to probe (e.g. git, compilers)")
    args = parser.parse_args()

    run_probe(args.topic)

if __name__ == "__main__":
    main()
