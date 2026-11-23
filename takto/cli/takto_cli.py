#!/usr/bin/env python3
"""
takto-cli: Bidirectional Command-Line Interface for Takto Android Launcher
Reference: Takto System Specification
Powers:
1. Live Bash/Python UI scripting and QuakeConsole interactive shell
2. Three-tier privilege router (Root UID 0 -> Shizuku ADB UID 2000 -> Shell fallback)
3. Direct Logseq SAF and Room SQLite queries
4. Microdroid / AVF Virtual Machine status checks
"""

import sys
import os
import argparse
import subprocess
import datetime
import json
import sqlite3

LOGSEQ_DEFAULT_DIR = "/storage/emulated/0/Documents/Logseq/journals"
DB_DEFAULT_PATH = "/data/data/com.termux/files/home/workspace/takto/core/takto_local.db"

def get_privilege_tier():
    """Detects active privilege tier: Root (libsu) -> Shizuku (ADB) -> Shell"""
    # Check root
    try:
        res = subprocess.run(["su", "-c", "id"], capture_output=True, text=True, timeout=2)
        if res.returncode == 0 and "uid=0" in res.stdout:
            return "TIER_1_ROOT_LIBSU (UID 0)"
    except Exception:
        pass

    # Check Shizuku / ADB (UID 2000)
    try:
        res = subprocess.run(["shizuku", "id"], capture_output=True, text=True, timeout=2)
        if res.returncode == 0:
            return "TIER_2_SHIZUKU_ADB (UID 2000)"
    except Exception:
        pass

    # Fallback to standard app shell
    uid = os.getuid()
    return f"TIER_3_SHELL_FALLBACK (UID {uid})"

def get_today_logseq_filename():
    now = datetime.datetime.now()
    return now.strftime("%Y_%m_%d.md")

def cmd_status(args):
    print("=" * 65)
    print("⚡ TAKTO SYSTEM STATUS // OS FOR LIFE")
    print("=" * 65)
    print(f"Privilege Tier: {get_privilege_tier()}")
    print(f"Logseq Journal: {os.path.join(LOGSEQ_DEFAULT_DIR, get_today_logseq_filename())}")
    print(f"Local Database: {DB_DEFAULT_PATH}")
    
    # Check AVF / KVM Microdroid support
    kvm_present = os.path.exists("/dev/kvm")
    print(f"AVF Engine (/dev/kvm): {'[AVAILABLE] Android 14+ Microdroid ready' if kvm_present else '[INACTIVE] Microdroid requires kernel KVM support'}")
    print("=" * 65)

def cmd_log(args):
    entry_text = " ".join(args.text).strip()
    if not entry_text:
        print("[!] Error: No text provided to log.")
        sys.exit(1)

    # Prefix routing: j -> journal, t -> task, $ -> expense
    prefix = entry_text[0] if len(entry_text) > 1 and entry_text[1] == ' ' else 'j'
    payload = entry_text[2:].strip() if len(entry_text) > 1 and entry_text[1] == ' ' else entry_text

    now_iso = datetime.datetime.now().isoformat()
    now_time = datetime.datetime.now().strftime("%H:%M")

    if prefix == 't':
        record_type = "TASK"
        bullet = f"- TODO {payload}"
    elif prefix == '$':
        record_type = "EXPENSE"
        bullet = f"- #expense {payload}"
    else:
        record_type = "JOURNAL"
        bullet = f"- {now_time} {payload}"

    # 1. Dual-Write: SQLite Room Database
    os.makedirs(os.path.dirname(DB_DEFAULT_PATH), exist_ok=True)
    conn = sqlite3.connect(DB_DEFAULT_PATH)
    cursor = conn.cursor()
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS records (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            record_type TEXT NOT NULL,
            payload TEXT NOT NULL,
            markdown_bullet TEXT NOT NULL,
            created_at TEXT NOT NULL
        )
    """)
    cursor.execute("INSERT INTO records (record_type, payload, markdown_bullet, created_at) VALUES (?, ?, ?, ?)",
                   (record_type, payload, bullet, now_iso))
    conn.commit()
    conn.close()

    # 2. Dual-Write: Logseq Markdown file
    os.makedirs(LOGSEQ_DEFAULT_DIR, exist_ok=True)
    target_file = os.path.join(LOGSEQ_DEFAULT_DIR, get_today_logseq_filename())
    try:
        with open(target_file, "a", encoding="utf-8") as f:
            f.write(bullet + "\n")
        print(f"[+] Successfully logged {record_type} to:")
        print(f"    - SQLite (id: local) -> {bullet}")
        print(f"    - Logseq Markdown -> {target_file}")
    except Exception as e:
        print(f"[!] Warning: Could not write to Logseq shared storage: {e}")
        print(f"[+] Wrote to local SQLite Room DB: {bullet}")

def cmd_tasks(args):
    target_file = os.path.join(LOGSEQ_DEFAULT_DIR, get_today_logseq_filename())
    print("=" * 65)
    print("📋 ACTIVE TASKS // METRO LOGSEQ TASK TILE")
    print("=" * 65)
    
    tasks = []
    if os.path.exists(target_file):
        with open(target_file, "r", encoding="utf-8") as f:
            for line in f:
                line_str = line.strip()
                if line_str.startswith("- TODO "):
                    tasks.append(("[ ]", line_str[7:].strip()))
                elif line_str.startswith("- DONE "):
                    tasks.append(("[X]", line_str[7:].strip()))

    # Fallback to local SQLite if file not present
    if not tasks and os.path.exists(DB_DEFAULT_PATH):
        conn = sqlite3.connect(DB_DEFAULT_PATH)
        cursor = conn.cursor()
        cursor.execute("SELECT payload FROM records WHERE record_type = 'TASK' ORDER BY id DESC LIMIT 20")
        for row in cursor.fetchall():
            tasks.append(("[ ]", row[0]))
        conn.close()

    if not tasks:
        print("  (No active tasks found for today)")
    else:
        for status, title in tasks:
            print(f"  {status} {title}")
    print("=" * 65)

def main():
    parser = argparse.ArgumentParser(description="Takto Launcher Bidirectional Scripting CLI")
    subparsers = parser.add_subparsers(dest="command")

    subparsers.add_parser("status", help="Show launcher status and privilege tier")
    
    log_parser = subparsers.add_parser("log", help="Log thought, task, or expense (j/t/$)")
    log_parser.add_argument("text", nargs="+", help="Content to log")

    subparsers.add_parser("tasks", help="List active Logseq tasks")

    args = parser.parse_args()
    if args.command == "status" or not args.command:
        cmd_status(args)
    elif args.command == "log":
        cmd_log(args)
    elif args.command == "tasks":
        cmd_tasks(args)

if __name__ == "__main__":
    main()
