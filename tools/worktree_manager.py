#!/usr/bin/env python3
"""
worktree_manager.py: Isolated Git Worktree Orchestrator for Parallel AI Agents
Reference: youtube_pl.txt (Lines 104, 403, 421, 433, 444: Dev Swarm & Matt Pocock)
Enables running multiple subagents on separate branches in isolated directories
without Git collisions or context pollution.

Commands:
    python3 worktree_manager.py create <branch_name>
    python3 worktree_manager.py list
    python3 worktree_manager.py remove <branch_name>
"""

import sys
import os
import subprocess
import argparse

WORKSPACE = "/data/data/com.termux/files/home/workspace"
WORKTREE_BASE = os.path.join(WORKSPACE, ".worktrees")

def run_git(cmd):
    return subprocess.run(["git"] + cmd, cwd=WORKSPACE, capture_output=True, text=True)

def create_worktree(branch_name: str):
    os.makedirs(WORKTREE_BASE, exist_ok=True)
    target_dir = os.path.join(WORKTREE_BASE, branch_name)

    if os.path.exists(target_dir):
        print(f"[!] Worktree directory already exists: {target_dir}")
        return

    # Check if branch exists
    res = run_git(["branch", "--list", branch_name])
    if res.stdout.strip():
        # Attach existing branch
        res = run_git(["worktree", "add", target_dir, branch_name])
    else:
        # Create new branch
        res = run_git(["worktree", "add", "-b", branch_name, target_dir])

    if res.returncode == 0:
        print(f"✅ Isolated worktree created successfully!")
        print(f"  Branch   : {branch_name}")
        print(f"  Directory: {target_dir}")
        print(f"  Subagent command: cd {target_dir} && ...")
    else:
        print(f"Error creating worktree: {res.stderr.strip()}")

def list_worktrees():
    res = run_git(["worktree", "list"])
    print("=" * 65)
    print("🌲 ACTIVE GIT WORKTREES FOR PARALLEL AGENTS")
    print("=" * 65)
    print(res.stdout.strip() if res.stdout.strip() else "No active worktrees.")
    print("=" * 65)

def remove_worktree(branch_name: str):
    target_dir = os.path.join(WORKTREE_BASE, branch_name)
    res = run_git(["worktree", "remove", target_dir, "--force"])
    if res.returncode == 0:
        print(f"✅ Removed worktree: {target_dir}")
    else:
        print(f"Error removing worktree: {res.stderr.strip()}")

def main():
    parser = argparse.ArgumentParser(description="Git Worktree Manager for Parallel Subagent Feature Branches.")
    subparsers = parser.add_subparsers(dest="command")

    create_p = subparsers.add_parser("create", help="Create new isolated worktree")
    create_p.add_argument("branch", help="Branch/Feature name")

    list_p = subparsers.add_parser("list", help="List active worktrees")

    remove_p = subparsers.add_parser("remove", help="Remove an isolated worktree")
    remove_p.add_argument("branch", help="Branch/Feature name")

    args = parser.parse_args()

    if args.command == "create":
        create_worktree(args.branch)
    elif args.command == "list":
        list_worktrees()
    elif args.command == "remove":
        remove_worktree(args.branch)
    else:
        parser.print_help()

if __name__ == "__main__":
    main()
