#!/usr/bin/env python3
"""
grill_spec.py: Matt Pocock's "Grill with Docs" and Strategic Spec Generator
Generates:
1. Domain Glossary
2. Architecture Decision Record (ADR)
3. Granular Ticket Breakdown with Status Tags ([not-started], [in-progress], [completed])
4. Merge Danger Assessment (One-way door vs. Two-way door)
"""

import sys
import os
import argparse
from datetime import datetime

def generate_adr(feature_name: str, goal: str, decision: str, consequences: str, output_path: str):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M")
    content = f"""# Architecture Decision Record (ADR): {feature_name}
**Date:** {timestamp}  
**Status:** Accepted  
**Driver:** Strategic Planning Loop (Matt Pocock & Andrej Karpathy Framework)

---

## 1. Context & True Goal (Layer 1: The Spec)
- **Goal:** {goal}
- **Underlying Decision:** Why this implementation is being undertaken now.

---

## 2. Decision & Architecture (Deep Module Standard)
{decision}

---

## 3. Merge Danger Assessment
- **Door Classification:** Two-Way Door (Easily reversible) / One-Way Door (Rigorous check required)
- **Risk Invariants:** What conditions must hold true for this change to be safe?

---

## 4. Consequences & Trade-offs
- **Positive:** {consequences}
- **Negative / Costs:** Tokens, maintenance overhead, or dependencies introduced.

---

## 5. Granular Tickets & Status Tracking
- [ ] `[not-started]` Ticket 1: Core domain model & types
- [ ] `[not-started]` Ticket 2: Implementation & unit tests (70% verification rule)
- [ ] `[not-started]` Ticket 3: Integration test & documentation sweep
"""
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"[+] ADR successfully generated: {output_path}")

def main():
    parser = argparse.ArgumentParser(description="Generate ADR and Granular Tickets.")
    parser.add_argument("feature", help="Feature name")
    parser.add_argument("--goal", default="Implement requested capability", help="Project true goal")
    parser.add_argument("--decision", default="Follow deep module architecture with strict typing", help="Architectural choice")
    parser.add_argument("--consequences", default="High maintainability and zero regressions", help="Trade-offs")
    parser.add_argument("--output", "-o", default="ADR.md", help="Output file path")
    args = parser.parse_args()

    generate_adr(args.feature, args.goal, args.decision, args.consequences, args.output)

if __name__ == "__main__":
    main()
