#!/usr/bin/env python3
"""
study_tracker.py: 30-Day Incremental Study Habit Tracker & Eero Alvar Tutor
Implements the 4-week habit progression from youtube_pl.txt (Lines 69-82):
- Week 1: 45-min desk session (phone in another room) + physical paper goal
- Week 2: + 10-min morning review of previous day + 3 Sunday night priorities
- Week 3: + 2-min closed-book Feynman 'explain-it-back' out loud
- Week 4: + 3x/week active retrieval practice questions
"""

import sys
import os
import json
import argparse
from datetime import datetime

DATA_FILE = os.path.expanduser("~/workspace/knowledge_base/study_progress.json")

def load_data():
    if os.path.isfile(DATA_FILE):
        try:
            return json.load(DATA_FILE)
        except Exception:
            pass
    return {"start_date": datetime.now().strftime("%Y-%m-%d"), "sessions": [], "sunday_priorities": []}

def save_data(data):
    os.makedirs(os.path.dirname(DATA_FILE), exist_ok=True)
    with open(DATA_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)

def status():
    data = load_data()
    print("=" * 65)
    print("🎓 30-DAY INCREMENTAL STUDY HABIT PROGRESSION")
    print("=" * 65)
    sessions = data.get("sessions", [])
    total_sessions = len(sessions)
    print(f"Total Completed Sessions: {total_sessions} / 30")

    current_week = min(4, (total_sessions // 7) + 1)
    print(f"Current Phase           : Week {current_week}")
    print("-" * 65)
    if current_week == 1:
        print("📌 Week 1 Rules:")
        print("  1. Study for 45 minutes at a desk (phone in another room).")
        print("  2. Write your study goal on a physical piece of paper before starting.")
    elif current_week == 2:
        print("📌 Week 2 Rules:")
        print("  1. Continue 45-min desk session.")
        print("  2. Start with a 10-minute review of yesterday's material.")
        print("  3. Write down 3 study priorities every Sunday night.")
    elif current_week == 3:
        print("📌 Week 3 Rules:")
        print("  1. Continue Week 1 & 2 habits.")
        print("  2. End session with 2-minute closed-book Feynman explanation out loud.")
    else:
        print("📌 Week 4 Rules (Final Mastery):")
        print("  1. Complete practice questions 3x per week for memory retrieval.")
        print("  2. Maintain all prior habits.")

    print("=" * 65)

def log_session(notes: str = ""):
    data = load_data()
    now_str = datetime.now().strftime("%Y-%m-%d %H:%M")
    data["sessions"].append({"timestamp": now_str, "notes": notes})
    save_data(data)
    count = len(data["sessions"])
    print(f"[+] Session #{count} logged at {now_str}.")
    print("Remember to complete the 2-minute Feynman explain-it-back review!")

def main():
    parser = argparse.ArgumentParser(description="Track the 30-Day study habit progression.")
    parser.add_argument("--status", action="store_true", help="Display current habit phase and rules")
    parser.add_argument("--log", metavar="NOTES", help="Log a completed 45-minute study session")
    args = parser.parse_args()

    if args.log is not None:
        log_session(args.log)
    else:
        status()

if __name__ == "__main__":
    main()
