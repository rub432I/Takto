# Class Lecture Voice Recording Analysis Toolkit

A complete suite of git repositories, customized agent skills, and automation tools designed to transcribe, segment, and convert classroom and lecture audio recordings into structured academic study notes, summaries, and flashcards within Termux on Android.

---

## 📁 Workspace Directory Structure

```
~/workspace/lecture_analysis/
├── README.md                      # This guide
├── run_pipeline.sh                # Automated end-to-end runner script
├── sample_lecture.vtt             # Sample WebVTT test transcript
├── sample_Study_Guide.md          # Sample generated study guide
├── repos/                         # Downloaded open-source repositories
│   ├── whisper.cpp/               # ARM64 high-speed C/C++ Whisper transcription engine
│   ├── lecture-notes-generator/   # Academic lecture notes pipeline with Whisper & LLM
│   ├── auditok/                   # Lightweight audio activity detection (VAD)
│   ├── pyAudioAnalysis/           # Audio feature extraction and segmentation
│   ├── Notes-Summarizer/          # AI Lecture Summarizer Pro web app
│   ├── LectureSummarizer/         # Shot/segmentation & keyword indexing
│   └── yt-dlp/                    # Downloader for online/recorded lectures
├── tools/                         # Native execution tools
│   ├── analyze_lecture.py         # Parses transcripts, outputs summaries & Anki cards
│   └── process_audio.py           # Zero-dependency WAV property checker & silence detector
└── output/                        # Default folder for transcripts and study guides
```

---

## ⚡ Quick Start

### 1. Analyzing an Existing Transcript (.vtt, .txt, .json)
If you already have a transcript file:
```bash
bash ~/workspace/lecture_analysis/run_pipeline.sh --title "CS101 Intro to Algorithms" /path/to/lecture.vtt
```
This produces a Markdown study guide with:
- Executive summary & key takeaways
- Chronological timeline outline
- Technical vocabulary frequency table
- In-depth section notes
- Review / practice exam questions
- Spaced-repetition (Anki) flashcard deck

### 2. Inspecting an Audio Recording (.wav)
Check sample rate, channels, bit depth, and speech pauses:
```bash
python3 ~/workspace/lecture_analysis/tools/process_audio.py /path/to/lecture.wav --silence
```

### 3. Audio Ingestion & Whisper Preparation
Whisper models perform best on **16 kHz, 16-bit Mono WAV** audio.
If `ffmpeg` is installed:
```bash
ffmpeg -i my_recording.m4a -ar 16000 -ac 1 -c:a pcm_s16le lecture_16k.wav
```

### 4. Running whisper.cpp
To compile `whisper.cpp` (requires `cmake` or `make`):
```bash
cd ~/workspace/lecture_analysis/repos/whisper.cpp
cmake -B build
cmake --build build --config Release -j4
```
Download a lightweight Whisper model (e.g. `base.en`, ~140MB):
```bash
bash ./models/download-ggml-model.sh base.en
```
Transcribe:
```bash
./build/bin/whisper-cli -m models/ggml-base.en.bin -f lecture_16k.wav -ovtt -otxt -oj -of lecture_out
```

---

## 🧠 Antigravity Agent Skill

The custom skill `lecture-voice-analysis` has been installed into:
- Global: `~/.gemini/config/skills/lecture-voice-analysis/SKILL.md`
- Workspace: `~/workspace/.agents/skills/lecture-voice-analysis/SKILL.md`

Whenever you upload or link a lecture recording, you can simply ask the assistant:
> *"Analyze this lecture recording and generate study notes and Anki flashcards."*
The assistant will automatically apply this skill's formatting and analysis pipeline.
