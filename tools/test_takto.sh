#!/usr/bin/env bash
# test_takto.sh: Deterministic Java Unit Test Runner for Takto
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(dirname "$SCRIPT_DIR")"
WORKSPACE="$REPO_ROOT/takto"
ANDROID_JAR="${ANDROID_JAR:-$HOME/.local/opt/android-sdk/platforms/android-34/android.jar}"
TMP_TEST_DIR="$(mktemp -d 2>/dev/null || mktemp -d -t 'takto_test_classes')"

cleanup() {
    rm -rf "$TMP_TEST_DIR"
}
trap cleanup EXIT

echo "================================================================="
echo "COMPILING AND RUNNING TAKTO ENGINE DETERMINISTIC TESTS"
echo "================================================================="

javac -source 8 -target 8 \
    -bootclasspath "$ANDROID_JAR" \
    -cp "$ANDROID_JAR:$WORKSPACE/android/src" \
    -d "$TMP_TEST_DIR" \
    $(find "$WORKSPACE/android/src" "$WORKSPACE/android/tests" -name "*.java")

java -cp "$ANDROID_JAR:$TMP_TEST_DIR" com.takto.launcher.TaktoEngineTest
