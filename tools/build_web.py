#!/usr/bin/env python3
"""
build_web.py: Generates Takto Neocities Landing Page
Layers a translucent glass card over the vanilla-JS 3D geometric canvas in axiom-system-v3.html
Complies with Neocities free-tier limits: static HTML/CSS/JS, zero tracking, <50KB payload.
"""

import os
import sys

AXIOM_SRC = "/storage/emulated/0/Download/axiom-system-v3.html"
WEB_OUT = "/data/data/com.termux/files/home/workspace/takto/web/index.html"

GLASS_CSS = """
/* TAKTO TRANSLUCENT GLASS CARD OVERLAY */
.takto-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 18px;
  pointer-events: none;
  z-index: 10;
}
.takto-card {
  pointer-events: auto;
  width: 100%;
  max-width: 720px;
  max-height: 92vh;
  overflow-y: auto;
  background: rgba(8, 10, 14, 0.82);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(223, 158, 56, 0.4);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.85), 0 0 32px rgba(223, 158, 56, 0.15);
  border-radius: 12px;
  padding: 28px;
  color: #e6e6e6;
  font-family: "JetBrains Mono", "SF Mono", monospace, sans-serif;
  scrollbar-width: thin;
  scrollbar-color: #df9e38 transparent;
}
.takto-card::-webkit-scrollbar { width: 4px; }
.takto-card::-webkit-scrollbar-thumb { background: #df9e38; border-radius: 2px; }

.header {
  border-bottom: 1px solid rgba(223, 158, 56, 0.25);
  padding-bottom: 14px;
  margin-bottom: 16px;
}
.logo-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 0.18em;
  color: #ffc86b;
  text-shadow: 0 0 18px rgba(255, 175, 50, 0.5);
}
.tagline {
  font-size: 11px;
  letter-spacing: 0.16em;
  color: #df9e38;
  margin-top: 4px;
}
.badge-foss {
  display: inline-block;
  font-size: 9px;
  letter-spacing: 0.12em;
  padding: 4px 10px;
  background: rgba(223, 158, 56, 0.15);
  border: 1px solid rgba(223, 158, 56, 0.6);
  color: #ffc86b;
  border-radius: 4px;
}
.manifesto {
  font-size: 11.5px;
  line-height: 1.6;
  color: #b0b0b0;
  margin-bottom: 20px;
  background: rgba(0, 0, 0, 0.3);
  padding: 12px;
  border-left: 2px solid #df9e38;
  border-radius: 2px;
}
.grid-3 {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 18px;
}
.col-card {
  background: rgba(14, 18, 24, 0.88);
  border: 1px solid rgba(223, 158, 56, 0.25);
  border-radius: 6px;
  padding: 12px 14px;
}
.col-card h4 {
  font-size: 11px;
  color: #ffc86b;
  letter-spacing: 0.12em;
  margin-bottom: 6px;
}
.col-card p {
  font-size: 10px;
  line-height: 1.45;
  color: #9c9c9c;
}
.code-inline {
  background: rgba(223, 158, 56, 0.15);
  color: #ffc86b;
  padding: 1px 4px;
  border-radius: 3px;
  font-size: 9.5px;
}
.btn-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}
.btn-primary {
  background: #df9e38;
  color: #020305;
  font-weight: 700;
  font-size: 11px;
  letter-spacing: 0.14em;
  padding: 10px 18px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;
  display: inline-block;
}
.btn-primary:hover {
  background: #ffc86b;
  box-shadow: 0 0 16px rgba(255, 200, 107, 0.5);
}
.btn-sec {
  background: transparent;
  color: #df9e38;
  border: 1px solid rgba(223, 158, 56, 0.45);
  font-size: 11px;
  letter-spacing: 0.12em;
  padding: 10px 18px;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  transition: all 0.2s;
}
.btn-sec:hover {
  background: rgba(223, 158, 56, 0.15);
  color: #ffc86b;
}
.footer-meta {
  font-size: 9px;
  color: #777;
  margin-top: 18px;
  letter-spacing: 0.1em;
  display: flex;
  justify-content: space-between;
  border-top: 1px solid rgba(223, 158, 56, 0.15);
  padding-top: 12px;
}
"""

GLASS_HTML = """
<div class="takto-overlay">
  <div class="takto-card">
    <div class="header">
      <div class="logo-row">
        <div>
          <div class="logo">TAKTO</div>
          <div class="tagline">MINIMALIST // OFFLINE-FIRST // OS FOR LIFE</div>
        </div>
        <span class="badge-foss">F-DROID COMPLIANT · 0 TELEMETRY</span>
      </div>
    </div>
    
    <div class="manifesto">
      Built on an <strong>"OS for Life"</strong> philosophy where the daily journal serves as the central operating system. Routes every thought, task, and expense directly into today's <strong>Logseq Markdown file</strong> via SAF while simultaneously dual-writing to local <strong>Room SQLite (AppDatabase)</strong> for sub-millisecond queries. Background syncing via Syncthing/rclone over Wi-Fi.
    </div>

    <div class="grid-3">
      <div class="col-card">
        <h4>01 // METRO DASHBOARD</h4>
        <p>Windows Phone-inspired 4-column live grid: Hydration tracker, local clock, offline pass wallet, CameraX/ZXing QR, and 4x2 LogseqTaskTile.</p>
      </div>
      <div class="col-card">
        <h4>02 // INTERNAL THREAD</h4>
        <p>Reverse-scrolling SMS-style self-chat canvas. Solves classification paralysis by capturing thoughts immediately into the day stream.</p>
      </div>
      <div class="col-card">
        <h4>03 // UNIFIED COMM HUB</h4>
        <p>Isolated communication hub using offline NotificationListenerService and normalized identities to separate social stress from capture.</p>
      </div>
    </div>

    <div class="grid-3">
      <div class="col-card">
        <h4>DUAL-PATH INTELLIGENCE</h4>
        <p>Instant single-character Logseq prefixes (<span class="code-inline">j </span> journal, <span class="code-inline">t </span> task, <span class="code-inline">$ </span> expense) paired with optional home-server AI (Whisper.cpp/Ollama/RAG) over Tailscale.</p>
      </div>
      <div class="col-card">
        <h4>QUAKE TERMINAL OVERLAY</h4>
        <p>Two-finger pull-down <span class="code-inline">QuakeConsole</span> wired to <span class="code-inline">takto-cli</span>, 3-tier escalating privilege router (libsu UID 0 / Shizuku UID 2000 / shell fallback), and AVF Microdroid Linux VMs.</p>
      </div>
      <div class="col-card">
        <h4>PERFORMANCE ON 80s HW</h4>
        <p>100% Native Kotlin + Jetpack Compose with Rust core via UniFFI. Zero React Native bloat. &lt;45MB RAM footprint, instant cold-start, 120Hz fluid rendering.</p>
      </div>
    </div>

    <div class="btn-row">
      <a href="https://f-droid.org" class="btn-primary">DOWNLOAD APK / F-DROID</a>
      <a href="https://github.com/takto-launcher/takto" class="btn-sec">SOURCE CODE</a>
      <a href="./docs/SPEC.html" class="btn-sec">ARCHITECTURE SPEC</a>
    </div>

    <div class="footer-meta">
      <span>NEOCITIES STATIC DEPLOYMENT · ZERO TRACKERS · 100% AUDITABLE</span>
      <span>AXIOM GEOMETRIC CANVAS v3.0</span>
    </div>
  </div>
</div>
"""

def main():
    if not os.path.exists(AXIOM_SRC):
        print(f"[!] Error: {AXIOM_SRC} not found")
        sys.exit(1)

    with open(AXIOM_SRC, "r", encoding="utf-8") as f:
        html = f.read()

    # Inject CSS
    html = html.replace("</style>", GLASS_CSS + "\n</style>")
    # Inject HTML overlay
    html = html.replace("<body>", "<body>\n" + GLASS_HTML)

    os.makedirs(os.path.dirname(WEB_OUT), exist_ok=True)
    with open(WEB_OUT, "w", encoding="utf-8") as f:
        f.write(html)

    print(f"Successfully generated Takto landing page: {WEB_OUT} ({os.path.getsize(WEB_OUT)} bytes)")

if __name__ == "__main__":
    main()
