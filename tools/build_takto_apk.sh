#!/usr/bin/env bash
# build_takto_apk.sh: Deterministic In-Termux Android APK Build Pipeline for Takto
# Uses only local Termux tools: aapt2, javac/kotlinc, d8, apksigner, android.jar
set -e

WORKSPACE="/data/data/com.termux/files/home/workspace/takto"
ANDROID_DIR="$WORKSPACE/android"
BUILD_DIR="$WORKSPACE/build"
ANDROID_JAR="$HOME/.local/opt/android-sdk/platforms/android-34/android.jar"
KEYSTORE="$BUILD_DIR/debug.keystore"

echo "================================================================="
echo "🚀 COMPILING TAKTO LAUNCHER ANDROID APK IN TERMUX"
echo "================================================================="

# 1. Clean & Prepare Build Directories
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/res_compiled" "$BUILD_DIR/gen" "$BUILD_DIR/classes" "$BUILD_DIR/dex"

# 2. Check Android.jar
if [ ! -f "$ANDROID_JAR" ]; then
    echo "[!] Error: android.jar not found at $ANDROID_JAR"
    exit 1
fi

# 3. AAPT2 Compile Resources
echo "[1/6] Compiling resources with aapt2..."
aapt2 compile --dir "$ANDROID_DIR/res" -o "$BUILD_DIR/res_compiled/res.zip"

# 4. AAPT2 Link Resources & Generate R.java
echo "[2/6] Linking resources & generating R.java..."
aapt2 link -I "$ANDROID_JAR" \
    --manifest "$ANDROID_DIR/AndroidManifest.xml" \
    --java "$BUILD_DIR/gen" \
    -o "$BUILD_DIR/unaligned.apk" \
    "$BUILD_DIR/res_compiled/res.zip"

# 5. Compile Java Source Code
echo "[3/6] Compiling source code with javac..."
javac -source 8 -target 8 \
    -bootclasspath "$ANDROID_JAR" \
    -cp "$ANDROID_JAR:$BUILD_DIR/gen" \
    -d "$BUILD_DIR/classes" \
    $(find "$ANDROID_DIR/src" "$BUILD_DIR/gen" -name "*.java")

# 6. D8 Dex Bytecode Compilation
echo "[4/6] Compiling bytecode to DEX with d8..."
d8 --lib "$ANDROID_JAR" \
    --output "$BUILD_DIR/dex" \
    $(find "$BUILD_DIR/classes" -name "*.class")

# 7. Add classes.dex into APK
echo "[5/6] Packaging classes.dex into APK..."
cd "$BUILD_DIR/dex"
zip -u "$BUILD_DIR/unaligned.apk" classes.dex
cd "$WORKSPACE"

# 8. Generate debug keystore and Sign APK
echo "[6/6] Signing APK with apksigner..."
if [ ! -f "$KEYSTORE" ]; then
    keytool -genkey -v -keystore "$KEYSTORE" \
        -alias takto-debug -keyalg RSA -keysize 2048 -validity 10000 \
        -storepass android -keypass android \
        -dname "CN=Takto, OU=FOSS, O=TaktoLauncher, L=Berlin, C=DE" \
        2>/dev/null
fi

apksigner sign --ks "$KEYSTORE" \
    --ks-key-alias takto-debug \
    --ks-pass pass:android \
    --key-pass pass:android \
    --out "$BUILD_DIR/Takto-debug.apk" \
    "$BUILD_DIR/unaligned.apk"

echo "================================================================="
echo "✅ BUILD SUCCESSFUL!"
echo "APK Location: $BUILD_DIR/Takto-debug.apk"
ls -lh "$BUILD_DIR/Takto-debug.apk"
echo "================================================================="
