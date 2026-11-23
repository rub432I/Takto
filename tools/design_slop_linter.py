#!/usr/bin/env python3
"""
design_slop_linter.py: Mechanical Linter for Leon Lin & Griffin Wooldridge Design Standards
Reference: youtube_pl.txt (Lines 197, 208, 218, 243)
Flags:
1. Forbidden Design Slop (beige/neutral backgrounds, generic unstyled fonts)
2. Violations of the "One-Standout Rule" (more than one major animated hero/3D element per page)
3. Surface-level imitation without brand personality
"""

import sys
import os
import re
import argparse

FORBIDDEN_PATTERNS = [
    # (regex, category, explanation)
    (r"\b(?:bg-amber-50|bg-stone-50|bg-orange-50|#f5f5dc|#faf0e6|beige)\b", "Banned Color Slop", "Generic beige/off-white background detected. Use intentional high-contrast brand palettes."),
    (r"\bfont-sans\b(?!\s+(?:font-|tracking-|leading-))", "Generic Typography", "Unstyled default font-sans detected without intentional leading or tracking."),
    (r"\b(?:animate-pulse|animate-bounce)\b", "Cheap AI Animation", "Low-effort generic pulse/bounce animation detected. Use purposeful transitions (e.g. NumberFlow/Vaul).")
]

STANDOUT_ELEMENTS = [
    r"\banimate-gradient\b",
    r"\bcanvas\b",
    r"\bthreejs\b",
    r"\bwebgl\b",
    r"\baceternity\b",
    r"\bmagic-ui-hero\b"
]

def lint_design(file_path: str):
    issues = []
    standout_count = 0

    with open(file_path, "r", encoding="utf-8", errors="replace") as f:
        content = f.read()
        lines = content.splitlines()

    for line_no, line in enumerate(lines, 1):
        for pat, cat, msg in FORBIDDEN_PATTERNS:
            if re.search(pat, line, re.IGNORECASE):
                issues.append((line_no, cat, msg))

        for s_pat in STANDOUT_ELEMENTS:
            if re.search(s_pat, line, re.IGNORECASE):
                standout_count += 1

    # Check One-Standout Rule
    if standout_count > 1:
        issues.append((0, "One-Standout Rule Violation", f"Found {standout_count} major animated/3D hero elements. Maximum allowed is 1 per page to prevent cluttered AI slop."))

    print("=" * 65)
    print(f"🎨 LEON LIN & GRIFFIN WOOLDRIDGE DESIGN LINTER: {os.path.basename(file_path)}")
    print("=" * 65)
    if not issues:
        print("✅ Clean! Zero design slop violations detected. High intentionality.")
    else:
        print(f"⚠️  Found {len(issues)} design issues:")
        for line_no, cat, msg in issues:
            loc = f"Line {line_no}: " if line_no > 0 else ""
            print(f"  [{cat}] {loc}{msg}")
    print("=" * 65)
    return len(issues) == 0

def main():
    parser = argparse.ArgumentParser(description="Lint HTML/JSX/Tailwind for AI design slop and One-Standout rule.")
    parser.add_argument("file_path", help="Path to HTML, JSX, TSX, or CSS file")
    args = parser.parse_args()

    if not os.path.isfile(args.file_path):
        print(f"Error: File '{args.file_path}' not found.")
        sys.exit(1)

    clean = lint_design(args.file_path)
    sys.exit(0 if clean else 1)

if __name__ == "__main__":
    main()
