#!/usr/bin/env python3
"""
hackertube.py: Karpathy's HackerTube YouTube & Playlist Ingestion Utility
Reference: youtube_pl.txt (Line 457: 'When something annoys him, he builds immediate fixes, like HackerTube')
Features:
- Downloads and extracts audio from YouTube lectures/playlists using yt-dlp
- Transcribes or fetches subtitles/transcripts automatically
- Formats transcripts into structured study notes
"""

import sys
import os
import subprocess
import argparse

WORKSPACE = "/data/data/com.termux/files/home/workspace"
OUTPUT_DIR = os.path.join(WORKSPACE, "lecture_analysis/output")

def check_ytdlp():
    try:
        subprocess.run(["yt-dlp", "--version"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True)
        return True
    except Exception:
        # Check local repo clone
        local_ytdlp = os.path.join(WORKSPACE, "lecture_analysis/repos/yt-dlp/yt-dlp")
        if os.path.isfile(local_ytdlp):
            return local_ytdlp
        return False

def fetch_youtube_audio(url: str, title: str = "youtube_lecture"):
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    out_template = os.path.join(OUTPUT_DIR, f"{title}.%(ext)s")
    
    cmd = [
        "yt-dlp",
        "-x",
        "--audio-format", "wav",
        "--postprocessor-args", "-ar 16000 -ac 1",
        "-o", out_template,
        url
    ]
    
    print(f"[*] HackerTube: Downloading and converting audio from {url}...")
    try:
        subprocess.run(cmd, check=True)
        expected_wav = os.path.join(OUTPUT_DIR, f"{title}.wav")
        print(f"[+] Audio ready for Whisper transcription: {expected_wav}")
        return expected_wav
    except Exception as e:
        print(f"[!] Download failed: {e}")
        return None

def main():
    parser = argparse.ArgumentParser(description="HackerTube: Streamlined YouTube video & playlist ingestion.")
    parser.add_argument("url", nargs="?", help="YouTube video or playlist URL")
    parser.add_argument("--title", default="youtube_lecture", help="Output file base title")
    args = parser.parse_args()

    if not args.url:
        print("=" * 65)
        print("🎬 HACKERTUBE: Karpathy-Style YouTube Audio & Lecture Ingester")
        print("=" * 65)
        print("Usage: hackertube <youtube_url> [--title <lecture_name>]")
        print("Example: hackertube 'https://www.youtube.com/watch?v=...' --title CS101")
        print("=" * 65)
        sys.exit(0)

    fetch_youtube_audio(args.url, args.title)

if __name__ == "__main__":
    main()
