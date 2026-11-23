# Takto Launcher

Takto is an offline-first, zero-telemetry Android launcher. It provides a daily capture interface that simultaneously persists notes, tasks, and expenses into local SQLite storage and Logseq-compatible Markdown journals.

---

## Architecture Overview

Takto is implemented in pure Android Java using platform SDK APIs (Android API 26-34). It compiles directly inside Termux on device using `aapt2`, `javac`, `d8`, and `apksigner`.

```
takto/
├── android/
│   ├── AndroidManifest.xml             # Declares HOME launcher intent, minSdk 26, targetSdk 34
│   ├── res/                            # XML layouts, colors, drawables, and themes
│   ├── src/com/takto/launcher/
│   │   ├── MainActivity.java           # 3-page horizontal matrix coordinator
│   │   ├── db/
│   │   │   └── TaktoDbHelper.java      # SQLite persistence with non-destructive migrations
│   │   ├── ui/
│   │   │   ├── MetroDashboardView.java # Left page: clock, metrics, task checklist, quick launch
│   │   │   ├── InternalThreadView.java # Center page: chronological feed of daily captures
│   │   │   ├── AppDrawerView.java      # Right page: asynchronous installed app catalog
│   │   │   └── BottomBarView.java      # Persistent capture bar with prefix routing
│   │   └── util/
│   │       ├── AppExecutors.java       # Background disk I/O and main thread dispatchers
│   │       ├── LogseqSync.java         # Dual-write Logseq journal file appender and task updater
│   │       ├── MathEvaluator.java      # Recursive descent arithmetic parser (+, -, *, /, %, ^)
│   │       └── UiUtils.java            # DPI-to-pixel density scaling
│   └── tests/
│       └── com/takto/launcher/
│           └── TaktoEngineTest.java    # Automated tests for math, prefixes, expenses, and Logseq
└── experimental_scaffolding/           # Uncompiled Kotlin, Compose, and Rust prototypes
```

---

## Universal Capture Bar

The persistent bottom bar inspects trimmed input to route commands:

| Input Pattern | Routed Mode | Destination / Action |
| :--- | :--- | :--- |
| `j <text>` | Journal Stream | Appends `- HH:mm <text>` to today's Logseq journal and SQLite |
| `t <text>` | Task Stream | Appends `- TODO <text>` to today's Logseq journal and SQLite |
| `$ <amount> [memo]` | Expense Stream | Parses amount, records to SQLite, appends `- #expense ...` |
| `25 * 40 + 12` | Math Evaluator | Real-time offline calculation preview; press `=` to commit |
| `<app name>` | App Filter | Filters installed applications in real time; press launch |

---

## Storage & Dual-Write Consistency

1. **SQLite Database (`takto.db`)**:
   - Stores all entries with timestamp, type, status, and parsed numeric values.
   - Schema upgrades run non-destructively through `onUpgrade` without dropping tables.
   - Aggregate stats (`getTodayStats`) execute as SQL queries rather than in-memory scans.

2. **Logseq Journal (`/storage/emulated/0/Documents/Logseq/journals/yyyy_MM_dd.md`)**:
   - Appends entries formatted as Logseq bullets.
   - When a task is marked done or undone in the launcher, `LogseqSync.updateTaskStatus` updates the corresponding bullet (`- TODO` <-> `- DONE`) in the journal file.
   - All disk and file I/O operations execute on a background thread (`AppExecutors.diskIO`).

---

## Build & Test Instructions

### Running Unit Tests
```bash
bash tools/test_takto.sh
```
Compiles and executes the 31-assertion deterministic test suite verifying math evaluation, expense amount extraction, prefix parsing, and Logseq formatting.

### Building Signed APK in Termux
```bash
bash tools/build_takto_apk.sh
```
- Compiles resources via `aapt2`.
- Compiles Java source via `javac`.
- Converts JVM bytecode to DEX via `d8`.
- Signs APK using the persistent keystore (`$HOME/.android/takto-debug.keystore`).
- Copies the signed package to `/storage/emulated/0/Download/Takto-debug.apk`.
