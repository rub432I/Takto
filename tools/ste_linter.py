#!/usr/bin/env python3
"""
ste_linter.py: ASD-STE100 & Anti-Slop Writing Linter
Flags the Six Habits of AI Slop:
1. Sentence length violations (>20 words instructions, >25 descriptions, semicolons)
2. Marketing adjectives (seamless, robust, cutting-edge, etc.)
3. Hedging / filler constructions (it is important to note, arguably, etc.)
4. Frozen verbs / nominalizations (perform an analysis -> analyze)
5. Chatty phrasal verbs (take off, look over, etc.)
"""

import sys
import os
import re
import argparse

MARKETING_WORDS = [
    r"\bseamless(?:ly)?\b", r"\brobust(?:ly)?\b", r"\bcutting-edge\b",
    r"\bstate-of-the-art\b", r"\brevolutionary\b", r"\bgame-changing\b",
    r"\bunparalleled\b", r"\beffortless(?:ly)?\b", r"\bgroundbreaking\b"
]

HEDGING_PATTERNS = [
    r"\bit is important to note that\b",
    r"\bit should be noted that\b",
    r"\bit could be argued that\b",
    r"\barguably\b",
    r"\bneedless to say\b",
    r"\bneed to ensure that\b"
]

FROZEN_VERBS = [
    (r"\bperform an analysis of\b", "analyze"),
    (r"\bcarry out an inspection of\b", "inspect"),
    (r"\bmake an assumption that\b", "assume"),
    (r"\bprovide a clarification for\b", "clarify"),
    (r"\bconduct a review of\b", "review"),
    (r"\bgive consideration to\b", "consider")
]

PHRASAL_VERBS = [
    (r"\btake off\b", "remove"),
    (r"\blook over\b", "examine"),
    (r"\bclean out\b", "clean/purge"),
    (r"\bfind out\b", "determine"),
    (r"\bpoint out\b", "indicate")
]

def lint_text(text: str):
    issues = []
    
    # Check for semicolons
    lines = text.splitlines()
    for line_no, line in enumerate(lines, 1):
        if ";" in line:
            issues.append((line_no, "Punctuation Violation", "Semicolons are prohibited under ASD-STE100. Split into two active sentences."))

    # Check sentences
    sentences = re.split(r'(?<=[.!?])\s+', text)
    for s in sentences:
        s_clean = s.strip()
        words = re.findall(r'\b[A-Za-z0-9_-]+\b', s_clean)
        word_count = len(words)
        
        # Length check (max 25 words)
        if word_count > 25:
            snippet = s_clean[:60] + "..." if len(s_clean) > 60 else s_clean
            issues.append((0, "Sentence Length Violation", f"Sentence has {word_count} words (cap is 25): '{snippet}'"))
            
        # Marketing adjectives
        for m_pat in MARKETING_WORDS:
            match = re.search(m_pat, s_clean, re.IGNORECASE)
            if match:
                issues.append((0, "Marketing Slop", f"Prohibited marketing term '{match.group(0)}' found."))
                
        # Hedging
        for h_pat in HEDGING_PATTERNS:
            match = re.search(h_pat, s_clean, re.IGNORECASE)
            if match:
                issues.append((0, "Hedging Violation", f"Weak filler phrase '{match.group(0)}' found."))
                
        # Frozen verbs
        for f_pat, replacement in FROZEN_VERBS:
            match = re.search(f_pat, s_clean, re.IGNORECASE)
            if match:
                issues.append((0, "Frozen Verb Violation", f"Nominalized phrase '{match.group(0)}'. Replace with direct verb '{replacement}'."))

        # Phrasal verbs
        for p_pat, replacement in PHRASAL_VERBS:
            match = re.search(p_pat, s_clean, re.IGNORECASE)
            if match:
                issues.append((0, "Phrasal Verb Violation", f"Informal phrasal verb '{match.group(0)}'. Replace with direct verb '{replacement}'."))

    return issues

def main():
    parser = argparse.ArgumentParser(description="Lint text for ASD-STE100 and AI Slop violations.")
    parser.add_argument("file_path", help="Path to markdown or text file")
    args = parser.parse_args()

    if not os.path.isfile(args.file_path):
        print(f"Error: File '{args.file_path}' not found.")
        sys.exit(1)

    with open(args.file_path, "r", encoding="utf-8", errors="replace") as f:
        content = f.read()

    issues = lint_text(content)
    print("=" * 60)
    print(f"🔍 ASD-STE100 Linter Results: {os.path.basename(args.file_path)}")
    print("=" * 60)
    if not issues:
        print("✅ Clean! Zero ASD-STE100 slop violations detected.")
    else:
        print(f"⚠️  Found {len(issues)} potential style issues:")
        for line_no, category, msg in issues:
            loc = f"Line {line_no}: " if line_no > 0 else ""
            print(f"  [{category}] {loc}{msg}")
    print("=" * 60)

if __name__ == "__main__":
    main()
