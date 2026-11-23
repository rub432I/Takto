# Experimental Architecture Scaffolding

This directory contains experimental prototypes and theoretical scaffolding explored during architecture design:
- `core/`: Kotlin LogseqFileManager, Room database, and WorkManager SyncWorker prototypes
- `ui/`: Jetpack Compose UI prototypes
- `console/`: QuakeConsole, TerminalEngine (libsu/Shizuku router), and AVFEngine (Microdroid VM)
- `drawer/`: Kotlin AppDrawer prototype
- `rust_core/`: Standalone Rust crate (not linked via JNI)

> [!NOTE]
> The active, production in-termux launcher engine is implemented in pure zero-dependency Android Java under `takto/android/src/com/takto/launcher/`.
