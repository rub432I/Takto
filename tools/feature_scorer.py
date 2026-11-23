#!/usr/bin/env python3
"""
feature_scorer.py: Product Validation & Feature Prioritization Rubric
Reference: youtube_pl.txt (Lines 268-276: Video 23)
Evaluates feature requests against:
1. Request Frequency (1-10): How many independent users asked?
2. Personal Utility (1-10): Would the creator genuinely use it daily?
3. Strategic Impact (1-10): Significant value vs minor cosmetic tweak?
4. Implementation Complexity (1-10): Technical debt, dependencies, maintenance?

Formula:
  Score = (Frequency * Utility * Impact) / Complexity
Threshold:
  >= 35.0 : Build Immediately (High Impact MVP)
  15-34.9 : Backlog Candidate
  < 15.0  : Reject (Assumption / Feature Creep)
"""

import sys
import argparse

def evaluate_feature(name: str, freq: float, util: float, impact: float, comp: float):
    comp = max(0.5, comp)
    score = (freq * util * impact) / comp

    print("=" * 65)
    print(f"📊 PRODUCT FEATURE VALIDATION SCORECARD: {name}")
    print("=" * 65)
    print(f"  • Request Frequency     : {freq:.1f} / 10.0")
    print(f"  • Personal Utility      : {util:.1f} / 10.0")
    print(f"  • Strategic Impact      : {impact:.1f} / 10.0")
    print(f"  • Complexity / Cost     : {comp:.1f} / 10.0")
    print("-" * 65)
    print(f"  ★ COMPOSITE SCORE       : {score:.1f}")
    print("-" * 65)

    if score >= 35.0:
        decision = "🟢 BUILD IMMEDIATELY (Validated High-Impact MVP)"
        reason = "Passes all criteria: frequent requests, high personal utility, and low-to-moderate complexity."
    elif score >= 15.0:
        decision = "🟡 BACKLOG CANDIDATE (Needs Further Validation)"
        reason = "Promising, but impact-to-complexity ratio is borderline. Observe user routines first."
    else:
        decision = "🔴 REJECT / AVOID (Feature Creep / Unverified Assumption)"
        reason = "High complexity with insufficient demand or utility. Violates simplicity principle."

    print(f"Decision: {decision}")
    print(f"Guidance: {reason}")
    print("=" * 65)

def main():
    parser = argparse.ArgumentParser(description="Evaluate feature ideas against the product validation rubric.")
    parser.add_argument("name", help="Feature name or description")
    parser.add_argument("--freq", type=float, default=5.0, help="Request frequency (1-10)")
    parser.add_argument("--util", type=float, default=5.0, help="Personal utility (1-10)")
    parser.add_argument("--impact", type=float, default=5.0, help="Expected value/impact (1-10)")
    parser.add_argument("--comp", type=float, default=5.0, help="Implementation complexity (1-10)")

    args = parser.parse_args()
    evaluate_feature(args.name, args.freq, args.util, args.impact, args.comp)

if __name__ == "__main__":
    main()
