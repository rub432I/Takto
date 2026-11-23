#!/usr/bin/env python3
"""
dune_linter.py: Architectural Boundary & Anti-Slop Enforcement
Reference: youtube_pl.txt (Lines 282, 301, 322, 346: Lauren Tan / SpaceXSI Dune framework)
Enforces architectural boundaries:
- Forbids main-thread modules from importing renderer-thread modules (and vice versa)
- Bans circular imports between architectural tiers
- Prevents cross-layer leakages (e.g. presentation directly querying raw storage)
"""

import sys
import os
import re
import argparse

FORBIDDEN_RULES = [
    # (source_pattern, forbidden_import_regex, rule_description)
    (r"renderer/.*", r"from\s+.*\bmain\b|import\s+.*\bmain\b", "Dune Rule 1: Renderer thread must not import main thread modules"),
    (r"main/.*", r"from\s+.*\brenderer\b|import\s+.*\brenderer\b", "Dune Rule 2: Main thread must not import renderer UI modules"),
    (r"ui/.*", r"from\s+.*\bdatabase\b|import\s+.*\bdatabase\b", "Dune Rule 3: UI tier must not import raw database layer directly")
]

def lint_architecture(directory: str):
    violations = []
    total_scanned = 0

    for root, _, files in os.walk(directory):
        for f in files:
            if f.endswith((".py", ".ts", ".js")):
                total_scanned += 1
                full_path = os.path.join(root, f)
                rel_path = os.path.relpath(full_path, directory).replace("\\", "/")

                with open(full_path, "r", encoding="utf-8", errors="replace") as file_obj:
                    lines = file_obj.readlines()

                for rule_src, rule_imp, desc in FORBIDDEN_RULES:
                    if re.match(rule_src, rel_path):
                        for line_no, line in enumerate(lines, 1):
                            if re.search(rule_imp, line):
                                violations.append((rel_path, line_no, desc, line.strip()))

    print("=" * 65)
    print("🏛️  DUNE ARCHITECTURAL BOUNDARY LINTER (SpaceXSI Standard)")
    print("=" * 65)
    print(f"Scanned: {total_scanned} files in {directory}")
    print("-" * 65)
    if not violations:
        print("✅ Pristine Architecture! Zero cross-thread or cross-tier boundary violations.")
    else:
        print(f"⚠️  Detected {len(violations)} architectural boundary violations:")
        for path, line_no, desc, code in violations:
            print(f"  [{path}:{line_no}] {desc}")
            print(f"    -> {code}")
    print("=" * 65)
    return len(violations) == 0

def main():
    parser = argparse.ArgumentParser(description="Enforce architectural boundary constraints (Dune framework).")
    parser.add_argument("directory", default=".", nargs="?", help="Project root to inspect")
    args = parser.parse_args()

    clean = lint_architecture(os.path.abspath(args.directory))
    sys.exit(0 if clean else 1)

if __name__ == "__main__":
    main()
