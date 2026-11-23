#!/usr/bin/env python3
"""
sweep.py: Documentation Sweeper & Archiving Guardrail
Identifies stale documentation, archives it into 'archive/',
and maintains '.agentignore' to block AI agents from indexing archived files.
"""

import sys
import os
import shutil
import argparse
from datetime import datetime

WORKSPACE = "/data/data/com.termux/files/home/workspace"
ARCHIVE_DIR = os.path.join(WORKSPACE, "archive")
AGENTIGNORE = os.path.join(WORKSPACE, ".agentignore")

def ensure_agentignore():
    """Ensure .agentignore blocks the archive directory from AI context."""
    patterns = ["archive/", "**/.git/", "**/__pycache__/", "**/node_modules/"]
    existing = []
    if os.path.isfile(AGENTIGNORE):
        existing = open(AGENTIGNORE).read().splitlines()

    needed = [p for p in patterns if p not in existing]
    if needed:
        with open(AGENTIGNORE, "a", encoding="utf-8") as f:
            for p in needed:
                f.write(f"{p}\n")
        print(f"[+] Updated .agentignore with guardrail patterns: {needed}")

def archive_file(filepath: str):
    """Safely move a stale file to the archive directory."""
    os.makedirs(ARCHIVE_DIR, exist_ok=True)
    basename = os.path.basename(filepath)
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    dest_name = f"{timestamp}_{basename}"
    dest_path = os.path.join(ARCHIVE_DIR, dest_name)

    shutil.move(filepath, dest_path)
    print(f"[+] Swept and archived: {basename} -> archive/{dest_name}")

def main():
    parser = argparse.ArgumentParser(description="Sweep and archive stale documentation to eliminate AI side quests.")
    parser.add_argument("files", nargs="*", help="Files to sweep into archive")
    parser.add_argument("--init", action="store_true", help="Initialize sweep guardrails and .agentignore")
    args = parser.parse_args()

    ensure_agentignore()

    if args.files:
        for f in args.files:
            if os.path.isfile(f):
                archive_file(f)
            else:
                print(f"[!] File not found: {f}")
    elif args.init:
        print("[+] Sweep guardrails active. Archive directory ready.")
    else:
        print("[*] No files specified to sweep. Use: python3 sweep.py <stale_file1.md> <stale_file2.md>")

if __name__ == "__main__":
    main()
