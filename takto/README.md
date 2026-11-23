# Takto Launcher

Takto is an offline-first, zero-telemetry Android launcher. It captures daily notes, tasks, and expenses simultaneously into local SQLite storage and Logseq Markdown journals.

---

## Architecture Overview

Takto is built in pure Android Java using platform SDK APIs (Android API 26-34). It compiles directly inside Termux on device using `aapt2`, `javac`, `d8`, and `apksigner`.

```
takto/
├── android/
│   ├── AndroidManifest.xml             # Declares HOME launcher intent, minSdk 26, targetSdk 34, versionCode 3
│   ├── res/                            # XML layouts, colors, drawables, and themes
│   ├── src/com/takto/launcher/
│   │   ├── MainActivity.java           # Pager coordinator, SAF directory link, and dual-write dispatch
│   │   ├── db/
│   │   │   └── TaktoDbHelper.java      # SQLite persistence with stable Logseq block UUIDs & migrations
│   │   ├── ui/
│   │   │   ├── TaktoPager.java         # Touch-intercepting horizontal matrix pager (Scroller & VelocityTracker)
│   │   │   ├── MetroDashboardView.java # Left page: clock, metrics, task checklist, and quick launch
│   │   │   ├── InternalThreadView.java # Center page: filterable stream ([ALL], [NOTES], [TASKS], [EXPENSES])
│   │   │   ├── AppDrawerView.java      # Right page: async app catalog with LRU icon cache & long-press actions
│   │   │   └── BottomBarView.java      # Persistent capture bar with prefix routing
│   │   └── util/
│   │       ├── AppExecutors.java       # Segregated diskIO (DB/file) and appScanner (package/icon) thread pools
│   │       ├── LogseqSync.java         # File-locking journal synchronizer with stable id:: block properties
│   │       ├── MathEvaluator.java      # Arithmetic parser (+, -, *, /, %, ^, parentheses, decimals)
│   │       └── UiUtils.java            # DPI-to-pixel density scaling
│   └── tests/
│       └── com/takto/launcher/
│           └── TaktoEngineTest.java    # Deterministic test suite exercising real production LogseqSync & math
└── experimental_scaffolding/           # Uncompiled Kotlin, Compose, and Rust prototypes
```

---

## Dual-Write Synchronization

1. **SQLite Database (`takto.db`)**:
   - Stores each entry with timestamp, type, status, parsed numeric value, target journal file name (`logseq_file`), and a stable block UUID (`logseq_id`).
   - Schema upgrades run non-destructively through `onUpgrade` without dropping user tables.
   - Aggregate statistics execute as SQL queries (`COUNT`, `SUM`).

2. **Logseq Journal Files**:
   - Writes entries with standard Logseq block properties:
     ```markdown
     - TODO Buy milk
       id:: 6a8f1b2c-3d4e-5f6a-7b8c-9d0e1f2a3b4c
     ```
   - **Multi-Day Consistency**: Task updates read the specific `logseq_file` recorded at capture time, ensuring tasks created on previous days are updated in their respective journal files.
   - **Exact Matching**: Updates and deletions target the unique `id:: <uuid>` property, preventing false-positive substring edits on similar tasks.
   - **Concurrency Safety**: File operations use `FileChannel.lock()` to prevent read-modify-write collisions.
   - **Storage Access Framework (SAF)**: If shared document storage is restricted, the launcher displays a banner allowing the user to select their Logseq graph folder once via `ACTION_OPEN_DOCUMENT_TREE` with persistable URI permissions.

---

## Universal Capture Syntax

The persistent bottom bar inspects trimmed input to route commands:

| Input Pattern | Routed Mode | Destination / Action |
| :--- | :--- | :--- |
| `j <text>` | Journal Stream | Appends `- HH:mm <text>` with block UUID to Logseq and SQLite |
| `t <text>` | Task Stream | Appends `- TODO <text>` with block UUID to Logseq and SQLite |
| `$ <amount> [memo]` | Expense Stream | Parses amount, saves numeric value, appends `- #expense ...` |
| `25 * 40 + 12` | Math Evaluator | Real-time offline calculation preview; press `=` to commit |
| `<app name>` | App Filter | Filters installed applications in real time; press launch |

---

## Build & Test Instructions

### Running Automated Tests
```bash
bash tools/test_takto.sh
```
Compiles and runs 50 deterministic assertions testing arithmetic evaluation, expense amount parsing, prefix routing, production Logseq file appending, status updates, multi-day file isolation, and substring disambiguation.

### Building Signed APK in Termux
```bash
bash tools/build_takto_apk.sh
```
- Compiles resources via `aapt2`.
- Compiles Java source via `javac`.
- Converts JVM bytecode to DEX via `d8`.
- Signs APK using the persistent keystore (`$HOME/.android/takto-debug.keystore`).
- Copies the signed package to `/storage/emulated/0/Download/Takto-debug.apk`.
