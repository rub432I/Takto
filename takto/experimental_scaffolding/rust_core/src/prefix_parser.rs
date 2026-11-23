//! prefix_parser: Deterministic Single-Character Logseq Prefix Parser
//! Reference: Takto Specification (j for journal, t for task, $ for expense)

#[derive(Debug, PartialEq, Eq, Clone)]
pub enum EntryType {
    Journal,
    Task,
    Expense,
}

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct LogseqEntry {
    pub entry_type: EntryType,
    pub raw_input: String,
    pub payload: String,
    pub markdown_bullet: String,
}

/// Parses a query string with single-character prefix
/// Returns None if no prefix matches
pub fn parse_prefix(input: &str) -> Option<LogseqEntry> {
    let trimmed = input.trim();
    if trimmed.is_empty() {
        return None;
    }

    if let Some(rest) = trimmed.strip_prefix("j ") {
        let content = rest.trim();
        if content.is_empty() { return None; }
        Some(LogseqEntry {
            entry_type: EntryType::Journal,
            raw_input: input.to_string(),
            payload: content.to_string(),
            markdown_bullet: format!("- {}", content),
        })
    } else if let Some(rest) = trimmed.strip_prefix("t ") {
        let content = rest.trim();
        if content.is_empty() { return None; }
        Some(LogseqEntry {
            entry_type: EntryType::Task,
            raw_input: input.to_string(),
            payload: content.to_string(),
            markdown_bullet: format!("- TODO {}", content),
        })
    } else if let Some(rest) = trimmed.strip_prefix("$ ") {
        let content = rest.trim();
        if content.is_empty() { return None; }
        Some(LogseqEntry {
            entry_type: EntryType::Expense,
            raw_input: input.to_string(),
            payload: content.to_string(),
            markdown_bullet: format!("- #expense {}", content),
        })
    } else {
        None
    }
}
