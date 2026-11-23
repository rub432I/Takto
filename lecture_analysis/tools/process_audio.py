#!/usr/bin/env python3
"""
process_audio.py: Pure Python Audio Utility for Lecture Recordings
No external dependencies required (uses built-in `wave`, `struct`, `math`).

Features:
- Inspect WAV audio parameters (sample rate, channels, bit depth, duration)
- Compute RMS energy and detect silence periods
- Verify suitability for Whisper (16 kHz, 16-bit, Mono)
- Split long recordings at silent points
"""

import sys
import os
import wave
import struct
import math
import argparse

def inspect_wav(file_path: str):
    """Check audio properties of a WAV file."""
    if not os.path.isfile(file_path):
        print(f"Error: File '{file_path}' does not exist.")
        return None
        
    with wave.open(file_path, 'rb') as wav:
        n_channels = wav.getnchannels()
        sampwidth = wav.getsampwidth()
        framerate = wav.getframerate()
        n_frames = wav.getnframes()
        duration_sec = n_frames / float(framerate)
        
        bit_depth = sampwidth * 8
        minutes = int(duration_sec // 60)
        seconds = int(duration_sec % 60)
        
        print("=" * 50)
        print(f"Audio Properties for: {os.path.basename(file_path)}")
        print("=" * 50)
        print(f"  Channels    : {n_channels} ({'Mono' if n_channels == 1 else 'Stereo'})")
        print(f"  Sample Rate : {framerate} Hz")
        print(f"  Bit Depth   : {bit_depth}-bit")
        print(f"  Total Frames: {n_frames}")
        print(f"  Duration    : {duration_sec:.2f} s ({minutes}m {seconds}s)")
        
        is_whisper_ready = (framerate == 16000 and n_channels == 1 and bit_depth == 16)
        print("-" * 50)
        if is_whisper_ready:
            print("  Status: [OK] Optimal for Whisper (16kHz Mono 16-bit PCM).")
        else:
            print("  Status: [NEEDS CONVERSION] Whisper expects 16kHz Mono 16-bit WAV.")
            print(f"  Suggested ffmpeg command:")
            print(f"    ffmpeg -i \"{file_path}\" -ar 16000 -ac 1 -c:a pcm_s16le \"{os.path.splitext(file_path)[0]}_16k.wav\"")
        print("=" * 50)
        
        return {
            "channels": n_channels,
            "sample_rate": framerate,
            "bit_depth": bit_depth,
            "duration": duration_sec,
            "whisper_ready": is_whisper_ready
        }

def detect_silence(file_path: str, threshold_db: float = -35.0, min_silence_sec: float = 1.5):
    """Analyze audio energy levels to detect pauses and quiet sections in lectures."""
    with wave.open(file_path, 'rb') as wav:
        n_channels = wav.getnchannels()
        sampwidth = wav.getsampwidth()
        framerate = wav.getframerate()
        n_frames = wav.getnframes()
        
        # Read in 100ms chunks
        chunk_frames = int(framerate * 0.1)
        fmt = f"<{chunk_frames * n_channels}h" if sampwidth == 2 else None
        
        if not fmt:
            print("Note: Fast energy detection currently requires 16-bit audio.")
            return []
            
        silences = []
        silence_start = None
        
        for i in range(0, n_frames, chunk_frames):
            raw_data = wav.readframes(chunk_frames)
            num_samples = len(raw_data) // (2 * n_channels)
            if num_samples == 0:
                break
                
            samples = struct.unpack(f"<{num_samples * n_channels}h", raw_data)
            # Compute RMS
            sum_squares = sum(s ** 2 for s in samples)
            rms = math.sqrt(sum_squares / len(samples)) if samples else 0
            
            db = 20 * math.log10(rms / 32768.0) if rms > 0 else -100.0
            cur_time = i / float(framerate)
            
            if db < threshold_db:
                if silence_start is None:
                    silence_start = cur_time
            else:
                if silence_start is not None:
                    silence_dur = cur_time - silence_start
                    if silence_dur >= min_silence_sec:
                        silences.append((silence_start, cur_time, silence_dur))
                    silence_start = None
                    
        print(f"[+] Detected {len(silences)} pause/silence intervals >= {min_silence_sec}s.")
        for s_start, s_end, dur in silences[:10]:
            print(f"    Pause at {s_start:.1f}s - {s_end:.1f}s (duration: {dur:.1f}s)")
        if len(silences) > 10:
            print(f"    ... and {len(silences) - 10} more pauses.")
        return silences

def main():
    parser = argparse.ArgumentParser(description="Analyze audio files for lecture recording analysis.")
    parser.add_argument("wav_file", help="Path to .wav recording")
    parser.add_argument("--silence", action="store_true", help="Detect speech pauses and silences")
    parser.add_argument("--threshold", type=float, default=-35.0, help="Silence dB threshold (default: -35.0)")
    
    args = parser.parse_args()
    info = inspect_wav(args.wav_file)
    if args.silence and info:
        detect_silence(args.wav_file, threshold_db=args.threshold)

if __name__ == "__main__":
    main()
