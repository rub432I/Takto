//! takto-core: High-Speed Core Engine for Takto Launcher
//! Enforces sub-millisecond prefix parsing, offline math evaluation,
//! and Logseq Markdown task extraction.

pub mod prefix_parser;
pub mod math_eval;
pub mod markdown_task_extractor;

pub use prefix_parser::{parse_prefix, LogseqEntry, EntryType};
pub use math_eval::evaluate_expression;
pub use markdown_task_extractor::{extract_todo_tasks, TaskItem};

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_prefix_parsing() {
        let journal = parse_prefix("j Reflecting on launcher architecture").unwrap();
        assert_eq!(journal.entry_type, EntryType::Journal);
        assert_eq!(journal.payload, "Reflecting on launcher architecture");

        let task = parse_prefix("t Build 4x2 LogseqTaskTile").unwrap();
        assert_eq!(task.entry_type, EntryType::Task);
        assert_eq!(task.payload, "Build 4x2 LogseqTaskTile");

        let expense = parse_prefix("$ 14.50 Lunch salad").unwrap();
        assert_eq!(expense.entry_type, EntryType::Expense);
        assert_eq!(expense.payload, "14.50 Lunch salad");
    }

    #[test]
    fn test_math_eval() {
        assert_eq!(evaluate_expression("12 + 34"), Some(46.0));
        assert_eq!(evaluate_expression("100 * 2.5"), Some(250.0));
        assert_eq!(evaluate_expression("100 / 4"), Some(25.0));
        assert_eq!(evaluate_expression("invalid input"), None);
    }
}
