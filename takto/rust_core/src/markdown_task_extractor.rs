//! markdown_task_extractor: High-speed Logseq Markdown task parser
//! Powers the 4x2 LogseqTaskTile on the Metro Widget Dashboard.

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct TaskItem {
    pub line_number: usize,
    pub title: String,
    pub is_completed: bool,
    pub raw_line: String,
}

pub fn extract_todo_tasks(markdown: &str) -> Vec<TaskItem> {
    let mut tasks = Vec::new();

    for (idx, line) in markdown.lines().enumerate() {
        let trimmed = line.trim();
        if trimmed.starts_with("- TODO ") {
            let title = trimmed.trim_start_matches("- TODO ").trim();
            tasks.push(TaskItem {
                line_number: idx + 1,
                title: title.to_string(),
                is_completed: false,
                raw_line: line.to_string(),
            });
        } else if trimmed.starts_with("- DONE ") {
            let title = trimmed.trim_start_matches("- DONE ").trim();
            tasks.push(TaskItem {
                line_number: idx + 1,
                title: title.to_string(),
                is_completed: true,
                raw_line: line.to_string(),
            });
        }
    }

    tasks
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_task_extraction() {
        let md = r#"
- Personal notes for today
- TODO Implement QuakeConsole terminal overlay
- Some other note
- DONE Set up Rust toolchain
- TODO Review Merge Danger Matrix
"#;
        let tasks = extract_todo_tasks(md);
        assert_eq!(tasks.len(), 3);
        assert_eq!(tasks[0].title, "Implement QuakeConsole terminal overlay");
        assert_eq!(tasks[0].is_completed, false);
        assert_eq!(tasks[1].title, "Set up Rust toolchain");
        assert_eq!(tasks[1].is_completed, true);
        assert_eq!(tasks[2].title, "Review Merge Danger Matrix");
        assert_eq!(tasks[2].is_completed, false);
    }
}
