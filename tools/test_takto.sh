#!/usr/bin/env bash
# test_takto.sh: Deterministic Java Unit Test Runner for Takto
set -e

WORKSPACE="/data/data/com.termux/files/home/workspace/takto"
ANDROID_JAR="${ANDROID_JAR:-$HOME/.local/opt/android-sdk/platforms/android-34/android.jar}"
TMP_TEST_DIR="/data/data/com.termux/files/usr/tmp/takto_test_classes"

rm -rf "$TMP_TEST_DIR"
mkdir -p "$TMP_TEST_DIR"

echo "================================================================="
echo "COMPILING AND RUNNING TAKTO ENGINE DETERMINISTIC TESTS"
echo "================================================================="

javac -source 8 -target 8 \
    -bootclasspath "$ANDROID_JAR" \
    -cp "$ANDROID_JAR:$WORKSPACE/android/src" \
    -d "$TMP_TEST_DIR" \
    $(find "$WORKSPACE/android/src" "$WORKSPACE/android/tests" -name "*.java")

java -cp "$ANDROID_JAR:$TMP_TEST_DIR" com.takto.launcher.TaktoEngineTest

rm -rf "$TMP_TEST_DIR"
