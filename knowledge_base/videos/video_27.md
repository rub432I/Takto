# Video 27: Lauren Tan: High-Trust Environments & 70% Verification Rule

**Source Document Lines:** `L341 - L357` in `youtube_pl.txt`  
**Index:** Entry 27 of 41 in YouTube Playlist Knowledge Base  

---

## 📝 Transcript Content & Summary

Building trust in AI agents is not about blind confidence, but about investing in a high-trust environment where your agents are constrained to do the right thing by default. According to Lauren Tan (poteto), this is achieved through a multi-layered approach:

Deterministic Foundations (2:03 - 9:40): Move away from prose-based rules (which agents often ignore) toward enforceable code constraints. This includes:

Lint rules and type systems that catch errors automatically.
Enforceable architecture (e.g., using frameworks like Dune) to prevent performance-degrading patterns like mixing main and renderer threads.
Banning ambiguous code comments in favor of deterministic checks that prevent mistakes at the root.

Robust Verification Layers (9:40 - 13:21): Shift your code review process from manual checks to verifying automated artifacts.

Runtime verification: Give agents tools like the Chrome DevTools protocol to test their own code and use feature maps to ensure they share your vocabulary for UI elements.
Formal verification: Use model checkers like TLA+ or proving tools like Lean to verify critical algorithms and state machines.
Token Allocation: Dedicate a significant portion (up to 70%) of your token budget to these verification loops to ensure code quality before it is merged.

The Software Factory (15:11 - 17:12): Once your codebase is 'pristine,' you can scale by automating the outer loop—using autonomous fuzzing to detect bugs and having agents automatically open and merge verified fix PRs.

---

## 💡 Actionable Engineering Takeaways

- Building trust in AI agents is not about blind confidence, but about investing in a high-trust environment where your agents are constrained to do the right thing by default. According to Lauren Tan (poteto), this is achieved through a multi-layered approach:
- Deterministic Foundations (2:03 - 9:40): Move away from prose-based rules (which agents often ignore) toward enforceable code constraints. This includes:
- Robust Verification Layers (9:40 - 13:21): Shift your code review process from manual checks to verifying automated artifacts.
- Runtime verification: Give agents tools like the Chrome DevTools protocol to test their own code and use feature maps to ensure they share your vocabulary for UI elements.
- Formal verification: Use model checkers like TLA+ or proving tools like Lean to verify critical algorithms and state machines.
- Token Allocation: Dedicate a significant portion (up to 70%) of your token budget to these verification loops to ensure code quality before it is merged.

