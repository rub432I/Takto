#!/usr/bin/env python3
"""
ticket_tracker.py: Task Status Tag Manager & Anti-Side-Quest Protocol
Reference: youtube_pl.txt (Lines 368, 389, 420, 432: Matt Pocock & Agent Focus)
Enforces task taxonomy:
  [not-started] : Queued work
  [in-progress] : Active implementation
  [completed]   : Verified & merged work
  [archived]    : Obsolete plans swept into archive
"""

import sys
import os
import re
import argparse

WORKSPACE = "/data/data/com.termux/files/home/workspace"

def scan_tickets(directory: str):
    tickets = {"not-started": [], "in-progress": [], "completed": [], "archived": []}
    pattern = re.compile(r"\[(not-started|in-progress|completed|archived)\]\s*(.*)", re.IGNORECASE)

    for root, dirs, files in os.walk(directory):
        dirs[:] = [d for d in dirs if not d.startswith(".") and d not in ("archive", "node_modules")]
        for f in files:
            if f.endswith((".md", ".txt")):
                full_path = os.path.join(root, f)
                rel_path = os.path.relpath(full_path, directory)
                try:
                    with open(full_path, "r", encoding="utf-8", errors="replace") as file_obj:
                        for line_no, line in enumerate(file_obj, 1):
                            m = pattern.search(line)
                            if m:
                                tag = m.group(1).lower()
                                text = m.group(2).strip()
                                tickets[tag].append((rel_path, line_no, text))
                except Exception:
                    pass

    print("=" * 65)
    print("📋 PROJECT TASK STATUS BOARD (Anti-Side-Quest Tracking)")
    print("=" * 65)
    for tag, items in tickets.items():
        icon = {"not-started": "⏳", "in-progress": "🔄", "completed": "✅", "archived": "📦"}.get(tag, "•")
        print(f"\n{icon} [{tag.upper()}] ({len(items)} items)")
        if not items:
            print("  (None)")
        else:
            for path, line_no, text in items[:8]:
                print(f"  • [{path}:{line_no}] {text[:65]}")
            if len(items) > 8:
                print(f"  ... and {len(items)-8} more")
    print("=" * 65)

def main():
    parser = argparse.ArgumentParser(description="Scan and report task status tags across project files.")
    parser.add_argument("directory", default=".", nargs="?", help="Directory to scan")
    args = parser.parse_args()

    scan_tickets(os.path.abspath(args.directory))

if __name__ == "__main__":
    main()
