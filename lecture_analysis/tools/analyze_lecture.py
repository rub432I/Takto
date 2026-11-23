#!/usr/bin/env python3
"""
analyze_lecture.py: Lecture Transcript Analyzer and Study Guide Generator

Parses raw transcript files (.txt, .vtt, .json) produced by Whisper or other speech-to-text engines,
performs text cleaning, generates timestamped outlines, extracts key terms,
and formats comprehensive Markdown study notes with Anki flashcards.
"""

import sys
import os
import re
import json
import argparse
from typing import List, Dict, Tuple

def parse_vtt(vtt_content: str) -> List[Dict[str, str]]:
    """Parse WebVTT file into timestamped segments."""
    segments = []
    # Pattern for timestamp line: 00:00:00.000 --> 00:00:05.000
    time_pattern = re.compile(r'(\d{2}:\d{2}(?::\d{2})?(?:\.\d{3})?)\s*-->\s*(\d{2}:\d{2}(?::\d{2})?(?:\.\d{3})?)')
    
    lines = vtt_content.splitlines()
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        match = time_pattern.search(line)
        if match:
            start_time, end_time = match.group(1), match.group(2)
            i += 1
            text_lines = []
            while i < len(lines) and lines[i].strip() and not time_pattern.search(lines[i]):
                text_lines.append(lines[i].strip())
                i += 1
            text = " ".join(text_lines)
            if text:
                segments.append({
                    "start": start_time[:8],  # HH:MM:SS
                    "end": end_time[:8],
                    "text": text
                })
        else:
            i += 1
    return segments

def parse_json_transcript(json_content: str) -> List[Dict[str, str]]:
    """Parse Whisper JSON output format."""
    data = json.loads(json_content)
    segments = []
    
    # Check for whisper.cpp or openai-whisper format
    raw_segments = []
    if "transcription" in data:
        raw_segments = data["transcription"]
    elif "segments" in data:
        raw_segments = data["segments"]
        
    for seg in raw_segments:
        start_sec = seg.get("timestamps", {}).get("from", seg.get("start", 0))
        end_sec = seg.get("timestamps", {}).get("to", seg.get("end", 0))
        
        # Format seconds to MM:SS or HH:MM:SS
        if isinstance(start_sec, str):
            start_str = start_sec
        else:
            # Assume numeric seconds or ms
            s = int(start_sec / 1000 if start_sec > 10000 else start_sec)
            start_str = f"{s//3600:02d}:{(s%3600)//60:02d}:{s%60:02d}"
            
        if isinstance(end_sec, str):
            end_str = end_sec
        else:
            s = int(end_sec / 1000 if end_sec > 10000 else end_sec)
            end_str = f"{s//3600:02d}:{(s%3600)//60:02d}:{s%60:02d}"
            
        text = seg.get("text", "").strip()
        if text:
            segments.append({
                "start": start_str,
                "end": end_str,
                "text": text
            })
    return segments

def clean_transcript_text(text: str) -> str:
    """Remove common speech filler artifacts and repetitions."""
    # Normalize spaces
    text = re.sub(r'\s+', ' ', text)
    # Remove excessive repeated phrases
    text = re.sub(r'\b(um|uh|erm|like, you know)\b', '', text, flags=re.IGNORECASE)
    # Clean up whitespace around punctuation
    text = re.sub(r'\s+([,.\?!;:])', r'\1', text)
    return text.strip()

def extract_key_terms(text: str) -> List[Tuple[str, int]]:
    """Simple term frequency extraction to surface potential key technical vocabulary."""
    stop_words = {
        "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with",
        "by", "from", "up", "about", "into", "over", "after", "is", "are", "was", "were",
        "be", "been", "being", "have", "has", "had", "do", "does", "did", "this", "that",
        "these", "those", "it", "its", "they", "them", "their", "we", "us", "our", "you",
        "your", "i", "me", "my", "so", "as", "if", "than", "then", "can", "could", "will",
        "would", "should", "what", "which", "who", "when", "where", "why", "how", "all",
        "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "not",
        "only", "own", "same", "very", "just", "also", "now", "here", "there", "going", "know"
    }
    words = re.findall(r'\b[A-Za-z]{4,}\b', text.lower())
    counts = {}
    for w in words:
        if w not in stop_words:
            counts[w] = counts.get(w, 0) + 1
            
    sorted_terms = sorted(counts.items(), key=lambda x: x[1], reverse=True)
    return sorted_terms[:25]

def generate_study_guide(title: str, segments: List[Dict[str, str]], raw_text: str, output_path: str):
    """Generate structured markdown study guide and notes."""
    key_terms = extract_key_terms(raw_text)
    word_count = len(raw_text.split())
    
    # Group segments into ~5-10 minute topic intervals if timestamps exist
    topic_blocks = []
    if segments:
        current_block = {"start": segments[0]["start"], "end": segments[0]["end"], "texts": []}
        for seg in segments:
            current_block["texts"].append(seg["text"])
            current_block["end"] = seg["end"]
            # After ~15 segments, create a new block
            if len(current_block["texts"]) >= 15:
                topic_blocks.append({
                    "time": f"{current_block['start']} - {current_block['end']}",
                    "text": " ".join(current_block["texts"])
                })
                current_block = {"start": seg["end"], "end": seg["end"], "texts": []}
        if current_block["texts"]:
            topic_blocks.append({
                "time": f"{current_block['start']} - {current_block['end']}",
                "text": " ".join(current_block["texts"])
            })

    md_lines = [
        f"# Lecture Study Guide: {title}",
        f"**Generated on:** *(Automated Analysis)*  ",
        f"**Total Transcript Words:** ~{word_count} words  ",
        f"**Estimated Lecture Duration:** {segments[-1]['end'] if segments else 'N/A'}",
        "",
        "---",
        "",
        "## 1. Executive Summary & Overview",
        "> [!NOTE]",
        "> This lecture covers core theoretical concepts, definitions, and applied examples.",
        "",
        "### Key High-Level Takeaways",
        "- **Core Theme:** Primary subject investigated during the session.",
        "- **Primary Methodology:** Analytical techniques, formulas, or frameworks introduced.",
        "- **Key Takeaway:** Foundational knowledge required for subsequent lectures or upcoming exams.",
        "",
        "---",
        "",
        "## 2. Chronological Lecture Outline & Timeline",
        "| Time Range | Topic / Discussion Block | Highlights & Transcript Excerpt |",
        "| :--- | :--- | :--- |"
    ]

    if topic_blocks:
        for idx, block in enumerate(topic_blocks, 1):
            snippet = block["text"][:160].replace("|", "\\|") + ("..." if len(block["text"]) > 160 else "")
            md_lines.append(f"| `{block['time']}` | **Section {idx}** | {snippet} |")
    else:
        md_lines.append("| `00:00:00` | Full Recording | Transcript ingested without timestamps |")

    md_lines.extend([
        "",
        "---",
        "",
        "## 3. High-Frequency Technical Vocabulary & Concepts",
        "| Term / Keyword | Frequency in Lecture | Contextual Significance |",
        "| :--- | :--- | :--- |"
    ])

    for term, count in key_terms[:15]:
        md_lines.append(f"| **{term.capitalize()}** | {count} mentions | Key technical term emphasized in lecture. |")

    md_lines.extend([
        "",
        "---",
        "",
        "## 4. In-Depth Lecture Transcript Notes",
        ""
    ])

    if topic_blocks:
        for idx, block in enumerate(topic_blocks, 1):
            md_lines.extend([
                f"### Section {idx} (`{block['time']}`)",
                f"{block['text']}",
                ""
            ])
    else:
        # Split raw text into manageable paragraphs
        sentences = re.split(r'(?<=[.!?])\s+', raw_text)
        para = []
        for s in sentences:
            para.append(s)
            if len(para) >= 6:
                md_lines.append(" ".join(para) + "\n")
                para = []
        if para:
            md_lines.append(" ".join(para) + "\n")

    md_lines.extend([
        "---",
        "",
        "## 5. Review & Self-Test Questions",
        "1. **Conceptual Question:** What is the primary purpose and theoretical definition of the topics discussed in this session?",
        "   - *Model Answer:* Refer to Section 1 & 2 definitions.",
        "2. **Critical Application:** How do the concepts introduced connect to the broader syllabus or practical implementation?",
        "   - *Model Answer:* Review the worked examples in the later timestamps.",
        "",
        "---",
        "",
        "## 6. Spaced Repetition (Anki Flashcards)",
        "```tsv",
        "Front\tBack"
    ])

    for term, _ in key_terms[:10]:
        md_lines.append(f"Define: {term.capitalize()}\tCore concept discussed in {title} lecture.")

    md_lines.extend([
        "```",
        ""
    ])

    with open(output_path, "w", encoding="utf-8") as f:
        f.write("\n".join(md_lines))
    print(f"[+] Study guide successfully generated: {output_path}")

def main():
    parser = argparse.ArgumentParser(description="Analyze lecture transcripts into structured study guides.")
    parser.add_argument("transcript_file", help="Path to transcript file (.txt, .vtt, or .json)")
    parser.add_argument("--title", default="Class Lecture", help="Title of the lecture or course")
    parser.add_argument("--output", "-o", default="Lecture_Notes.md", help="Output Markdown file")
    
    args = parser.parse_args()
    
    if not os.path.isfile(args.transcript_file):
        print(f"Error: File '{args.transcript_file}' not found.")
        sys.exit(1)
        
    with open(args.transcript_file, "r", encoding="utf-8", errors="replace") as f:
        content = f.read()
        
    segments = []
    raw_text = ""
    
    ext = os.path.splitext(args.transcript_file)[1].lower()
    if ext == ".vtt":
        segments = parse_vtt(content)
        raw_text = " ".join([s["text"] for s in segments])
    elif ext == ".json":
        try:
            segments = parse_json_transcript(content)
            raw_text = " ".join([s["text"] for s in segments])
        except Exception as e:
            print(f"Warning: Failed parsing as JSON ({e}), treating as plain text.")
            raw_text = content
    else:
        raw_text = clean_transcript_text(content)
        
    generate_study_guide(args.title, segments, raw_text, args.output)

if __name__ == "__main__":
    main()
