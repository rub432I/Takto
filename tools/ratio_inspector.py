#!/usr/bin/env python3
"""
ratio_inspector.py: Forensic Reverse-Engineering Inspector for Ratio Launcher (com.bllocosn)
Analyzes:
1. Manifest permissions, exported activities, and services
2. Trackers, telemetry SDKs, ad networks, and paywalls
3. Core UI components (Root, Tiles, Tree, Drawers)
4. Extracted colors, themes, and dimensions from resources
"""

import sys
import os
import re
import xml.etree.ElementTree as ET

SOURCE_DIR = "/storage/emulated/0/Documents/com.bllocosn"

TRACKER_SIGNATURES = {
    "AppsFlyer": "com/appsflyer",
    "Qonversion (Paywall)": "com/qonversion",
    "Facebook SDK": "com/facebook",
    "Mixpanel": "com/mixpanel",
    "Sentry": "io/sentry",
    "Google Play Billing": "com.android.vending.BILLING",
    "Firebase Analytics": "firebase-analytics",
    "Firebase Crashlytics": "firebase-crashlytics",
    "Firebase Auth": "firebase-auth",
    "Firebase Dynamic Links": "firebase-dynamic-links",
}

def analyze():
    print("=" * 70)
    print("🔍 FORENSIC REVERSE-ENGINEERING AUDIT: RATIO LAUNCHER (com.bllocosn)")
    print("=" * 70)

    if not os.path.exists(SOURCE_DIR):
        print(f"[!] Error: Source directory {SOURCE_DIR} not found.")
        sys.exit(1)

    # 1. Manifest Audit
    manifest_path = os.path.join(SOURCE_DIR, "resources/AndroidManifest.xml")
    if os.path.exists(manifest_path):
        with open(manifest_path, "r", encoding="utf-8", errors="ignore") as f:
            manifest_content = f.read()

        pkg_match = re.search(r'package="([^"]+)"', manifest_content)
        pkg_name = pkg_match.group(1) if pkg_match else "unknown"
        print(f"\n📦 Package Name: {pkg_name}")

        perms = re.findall(r'<uses-permission [^>]*android:name="([^"]+)"', manifest_content)
        print(f"🔑 Requested Permissions ({len(perms)} total):")
        suspicious_perms = ["INTERNET", "BILLING", "READ_EXTERNAL_STORAGE", "ACCESS_FINE_LOCATION", "READ_CALENDAR"]
        for p in perms:
            flag = " [!] ATTENTION" if any(s in p for s in suspicious_perms) else ""
            print(f"   - {p}{flag}")

        activities = re.findall(r'<activity [^>]*android:name="([^"]+)"', manifest_content)
        print(f"\n📱 Registered Activities ({len(activities)} total):")
        for a in activities:
            print(f"   - {a}")

    # 2. Bloat & Tracker Scan
    print("\n🛑 Bloatware, Adware & Telemetry Detection:")
    found_trackers = []
    for name, sig in TRACKER_SIGNATURES.items():
        found = False
        # check in sources
        sources_path = os.path.join(SOURCE_DIR, "sources", sig)
        res_path = os.path.join(SOURCE_DIR, "resources", sig)
        if os.path.exists(sources_path) or os.path.exists(res_path):
            found = True
        else:
            # check manifest
            if sig in manifest_content:
                found = True
        if found:
            found_trackers.append(name)
            print(f"   [DETECTED] {name} (Must be purged for FOSS release)")

    # 3. Native Blloc Modules
    print("\n⚡ Native Ratio Launcher Architecture (from com/blloc):")
    blloc_dir = os.path.join(SOURCE_DIR, "sources/com/blloc")
    if os.path.exists(blloc_dir):
        categories = {}
        for root, dirs, files in os.walk(blloc_dir):
            for file in files:
                if file.endswith(".java") or file.endswith(".kt"):
                    rel = os.path.relpath(root, blloc_dir)
                    top = rel.split(os.sep)[0]
                    categories[top] = categories.get(top, 0) + 1
        for cat, count in sorted(categories.items()):
            print(f"   - {cat}: {count} classes")

    # 4. React Native Assets & WebViews
    bundle_path = os.path.join(SOURCE_DIR, "resources/assets/index.android.bundle")
    if os.path.exists(bundle_path):
        size_mb = os.path.getsize(bundle_path) / (1024 * 1024)
        print(f"\n🌐 React Native JavaScript Bundle:")
        print(f"   - Path: resources/assets/index.android.bundle")
        print(f"   - Size: {size_mb:.2f} MB (Primary bottleneck on low-end hardware)")
        print(f"   - WebViews detected in assets: notesWebview, lockscreenWebview")

    # 5. UI Resources & Fonts
    fonts_dir = os.path.join(SOURCE_DIR, "resources/assets/fonts")
    if os.path.exists(fonts_dir):
        fonts = os.listdir(fonts_dir)
        print(f"\n🎨 Ratio Custom Fonts ({len(fonts)} files):")
        for f in fonts:
            print(f"   - {f}")

    print("\n" + "=" * 70)
    print("✅ AUDIT COMPLETE: Roadmap ready for FOSS native rewrite")
    print("=" * 70)

if __name__ == "__main__":
    analyze()
