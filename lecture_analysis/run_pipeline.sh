#!/usr/bin/env bash
# ==============================================================================
# run_pipeline.sh - End-to-End Class Lecture Voice Analysis Pipeline
# ==============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOLS_DIR="$SCRIPT_DIR/tools"
REPOS_DIR="$SCRIPT_DIR/repos"
WHISPER_DIR="$REPOS_DIR/whisper.cpp"

usage() {
    echo "Usage: $0 [options] <recording_file_or_transcript>"
    echo ""
    echo "Options:"
    echo "  --title <string>       Lecture/Course Title (default: 'Class Lecture')"
    echo "  --model <name>         Whisper model: tiny.en, base.en, small.en (default: 'base.en')"
    echo "  --output-dir <path>    Directory to store generated notes (default: './output')"
    echo "  --help                 Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 lecture_audio.wav"
    echo "  $0 --title 'CS101 Intro to Algorithms' lecture.vtt"
    exit 1
}

TITLE="Class Lecture"
MODEL="base.en"
OUT_DIR="$SCRIPT_DIR/output"
INPUT_FILE=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        --title)
            TITLE="$2"
            shift 2
            ;;
        --model)
            MODEL="$2"
            shift 2
            ;;
        --output-dir)
            OUT_DIR="$2"
            shift 2
            ;;
        --help)
            usage
            ;;
        *)
            if [[ -z "$INPUT_FILE" ]]; then
                INPUT_FILE="$1"
                shift
            else
                echo "Error: Unknown argument '$1'"
                usage
            fi
            ;;
    esac
done

if [[ -z "$INPUT_FILE" ]]; then
    echo "Error: No input file specified."
    usage
fi

if [[ ! -f "$INPUT_FILE" ]]; then
    echo "Error: File '$INPUT_FILE' not found."
    exit 1
fi

mkdir -p "$OUT_DIR"
BASENAME="$(basename "$INPUT_FILE")"
EXT="${BASENAME##*.}"
EXT_LOWER="$(echo "$EXT" | tr '[:upper:]' '[:lower:]')"

echo "=========================================================="
echo " Starting Lecture Voice Recording Analysis Pipeline"
echo " Title: $TITLE"
echo " Input: $INPUT_FILE"
echo "=========================================================="

# Check if input is already a transcript
if [[ "$EXT_LOWER" == "txt" || "$EXT_LOWER" == "vtt" || "$EXT_LOWER" == "json" ]]; then
    echo "[1/2] Input is a transcript file ($EXT_LOWER). Skipping audio transcription."
    OUT_MD="$OUT_DIR/${BASENAME%.*}_Study_Guide.md"
    python3 "$TOOLS_DIR/analyze_lecture.py" "$INPUT_FILE" --title "$TITLE" --output "$OUT_MD"
    echo "=========================================================="
    echo " Pipeline Completed Successfully!"
    echo " Output Study Guide: $OUT_MD"
    echo "=========================================================="
    exit 0
fi

# If input is audio, inspect audio
echo "[1/3] Inspecting audio recording parameters..."
python3 "$TOOLS_DIR/process_audio.py" "$INPUT_FILE" --silence || true

# Prepare audio for Whisper
WAV_16K="$OUT_DIR/${BASENAME%.*}_16k.wav"
if command -v ffmpeg >/dev/null 2>&1; then
    echo "[2/3] Converting audio to 16kHz Mono 16-bit WAV using ffmpeg..."
    ffmpeg -y -i "$INPUT_FILE" -ar 16000 -ac 1 -c:a pcm_s16le "$WAV_16K"
else
    echo "[!] ffmpeg not detected in PATH."
    if [[ "$EXT_LOWER" == "wav" ]]; then
        echo "Using existing WAV file directly: $INPUT_FILE"
        WAV_16K="$INPUT_FILE"
    else
        echo "Error: Non-WAV audio ($EXT_LOWER) requires ffmpeg for conversion."
        echo "Run: pkg install ffmpeg"
        exit 1
    fi
fi

# Run Whisper transcription if compiled binary exists
WHISPER_BIN="$WHISPER_DIR/build/bin/whisper-cli"
MODEL_PATH="$WHISPER_DIR/models/ggml-$MODEL.bin"

if [[ -f "$WHISPER_BIN" && -f "$MODEL_PATH" ]]; then
    echo "[3/3] Running whisper.cpp transcription ($MODEL)..."
    TRANSCRIPT_PREFIX="$OUT_DIR/${BASENAME%.*}_transcript"
    "$WHISPER_BIN" -m "$MODEL_PATH" -f "$WAV_16K" -ovtt -otxt -oj -of "$TRANSCRIPT_PREFIX"
    
    echo "[+] Transcribed! Generating comprehensive lecture notes..."
    OUT_MD="$OUT_DIR/${BASENAME%.*}_Study_Guide.md"
    python3 "$TOOLS_DIR/analyze_lecture.py" "${TRANSCRIPT_PREFIX}.vtt" --title "$TITLE" --output "$OUT_MD"
    echo "=========================================================="
    echo " Analysis Complete!"
    echo " Study Guide: $OUT_MD"
    echo " Transcripts: ${TRANSCRIPT_PREFIX}.txt, .vtt, .json"
    echo "=========================================================="
else
    echo "[!] Whisper binary ($WHISPER_BIN) or model ($MODEL_PATH) not yet built."
    echo "To setup whisper model:"
    echo "  cd $WHISPER_DIR/models && bash download-ggml-model.sh $MODEL"
    echo "Then re-run this script."
fi
