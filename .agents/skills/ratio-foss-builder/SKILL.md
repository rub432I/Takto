---
name: ratio-foss-builder
description: End-to-end framework, reverse-engineering toolkit, and performance architecture for rebuilding the Blloc Ratio Android Launcher into an ultra-fast, zero-bloat, FOSS native Android application.
---

# Ratio FOSS Builder Skill

This skill guides the reconstruction, reverse engineering, and modernization of the Blloc Ratio Launcher (`com.bllocosn`).

## 1. Architectural Overview of Blloc Ratio

Ratio is composed of three primary functional planes:
1. **The Root (Left / Main Page):**
   - Actionable tiles (Calculator, Weather, Notes, Unit Converter, Timer, Flight Tickets).
   - Universal search and quick command bar.
2. **The Tree (Right / Drawer):**
   - Distraction-free, categorizable app drawer.
   - App drawer drawers, custom tags, and monochrome app icons.
3. **Conversations (Central / Messaging Hub):**
   - Unified notification and messaging aggregator for WhatsApp, Telegram, Signal, SMS.
4. **Visual Language:**
   - Monochromatic brutalism: True OLED black (`#000000`), stark white (`#FFFFFF`), and signature vibrant yellow accent.
   - Typography: Greycliff CF geometric sans-serif family.
   - Zero animations or micro-interactions unless functionally purposeful.

## 2. Legacy Bloat & Adware to Purge (Scammer Additions)

The original proprietary app and subsequent scammer forks suffer from severe telemetry bloat:
- **`com.appsflyer`:** Attribution tracking and ad campaign telemetry.
- **`com.qonversion`:** Server-side paywall, subscription verification, and user tracking.
- **`com.facebook`:** Audience network, event tracking, and login SDKs.
- **`com.mixpanel`:** Granular user behavior analytics.
- **`io.sentry`:** Crash reporting with aggressive system breadcrumb collection.
- **`com.google.android.gms.permission.AD_ID`:** Android Advertising ID harvest.
- **`firebase-*`:** Dynamic links, analytics, and remote config.
- **`index.android.bundle` (10.21 MB):** Bulky React Native JavaScript bundle running on top of embedded WebViews (`notesWebview`, `lockscreenWebview`).

## 3. The "Fast on 80s Hardware" Performance Mandate

To make the launcher run instantaneously with zero frame drops:
1. **Eliminate React Native & WebViews:**
   - Migrate entire UI to native **Jetpack Compose** (or lightweight Android Views).
   - Zero JavaScript engine overhead; zero bridge serialization lag.
2. **Deterministic Memory Footprint:**
   - Target RAM usage: <45 MB (legacy Ratio consumed 350MB–500MB).
   - Target Cold Launch Time: <80 ms.
3. **Local-First / Zero Cloud Requirement:**
   - All tiles (Notes, Calculator, Weather cache) operate via local SQLite / Room databases.
   - Zero telemetry, zero network polling unless explicitly triggered by the user (e.g., weather refresh).

## 4. Reverse Engineering Toolchain in Termux

- **JADX:** `jadx -d output/ /path/to/apk` (CLI at `~/.local/bin/jadx`).
- **APKTool:** `apktool d app.apk -o decoded/` (`/data/data/com.termux/files/usr/bin/apktool`).
- **Forensic Inspector:** `ratio-inspect` (Scans manifest, permissions, and trackers).
- **Rust Toolchain:** `rustc` and `cargo` installed for high-performance JNI modules if needed.

## 5. Development Strategy (Matt Pocock Dev Loop)

1. **Spec & Grill:** Clarify user ideas, custom tiles, and gesture configurations.
2. **Asset Extraction:** Extract clean drawables, icons, and Greycliff CF fonts from `/storage/emulated/0/Documents/com.bllocosn`.
3. **Modular Jetpack Compose Scaffold:** Build independent modules:
   - `core-design` (Theme, Greycliff CF fonts, OLED Black canvas).
   - `feature-tree` (High-performance app cataloging and launcher intent handling).
   - `feature-root` (Tile engine with pluggable, modular native tiles).
   - `feature-conversations` (Clean notification listener service without cloud relay).
