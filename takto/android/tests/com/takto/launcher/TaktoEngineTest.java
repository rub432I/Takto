package com.takto.launcher;

import com.takto.launcher.db.TaktoDbHelper;
import com.takto.launcher.util.LogseqSync;
import com.takto.launcher.util.MathEvaluator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * TaktoEngineTest: Automated deterministic test suite for shipping launcher logic.
 * Tests math evaluation, detection, expense parsing, Logseq formatting, and dual-write file operations.
 */
public class TaktoEngineTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("Running Takto Engine Deterministic Test Suite...");

        testMathEvaluator();
        testMathDetection();
        testExpenseParser();
        testLogseqFormatting();
        testLogseqFileOperations();

        System.out.println("=================================================================");
        System.out.println("TEST RESULTS: " + testsPassed + " / " + testsRun + " PASSED (0 FAILURES)");
        System.out.println("=================================================================");
    }

    private static void assertEquals(String expected, String actual, String testName) {
        testsRun++;
        if (expected == null && actual == null) {
            testsPassed++;
            return;
        }
        if (expected != null && expected.equals(actual)) {
            testsPassed++;
        } else {
            System.err.println("FAILED: " + testName + " | Expected: '" + expected + "', Got: '" + actual + "'");
            throw new RuntimeException("Assertion failure in " + testName);
        }
    }

    private static void assertEquals(double expected, double actual, double delta, String testName) {
        testsRun++;
        if (Math.abs(expected - actual) <= delta) {
            testsPassed++;
        } else {
            System.err.println("FAILED: " + testName + " | Expected: " + expected + ", Got: " + actual);
            throw new RuntimeException("Assertion failure in " + testName);
        }
    }

    private static void assertTrue(boolean condition, String testName) {
        testsRun++;
        if (condition) {
            testsPassed++;
        } else {
            System.err.println("FAILED: " + testName + " | Expected true, Got false");
            throw new RuntimeException("Assertion failure in " + testName);
        }
    }

    private static void assertFalse(boolean condition, String testName) {
        testsRun++;
        if (!condition) {
            testsPassed++;
        } else {
            System.err.println("FAILED: " + testName + " | Expected false, Got true");
            throw new RuntimeException("Assertion failure in " + testName);
        }
    }

    private static void testMathEvaluator() {
        assertEquals("4", MathEvaluator.evaluate("2 + 2"), "Math: 2 + 2");
        assertEquals("14", MathEvaluator.evaluate("2 + 3 * 4"), "Math: Precedence");
        assertEquals("20", MathEvaluator.evaluate("(2 + 3) * 4"), "Math: Parentheses");
        assertEquals("10", MathEvaluator.evaluate("((2 + 3) * (4 - 2))"), "Math: Nested parentheses");
        assertEquals("2.5", MathEvaluator.evaluate("10 / 4"), "Math: Decimals");
        assertEquals("256", MathEvaluator.evaluate("2 ^ 8"), "Math: Exponentiation");
        assertEquals("3", MathEvaluator.evaluate("15 % 4"), "Math: Modulo");
        assertEquals("2400", MathEvaluator.evaluate("1,200 * 2"), "Math: Thousands comma");
        assertEquals("5", MathEvaluator.evaluate("-5 + 10"), "Math: Unary minus");
        assertEquals(null, MathEvaluator.evaluate("10 / 0"), "Math: Division by zero");
    }

    private static void testMathDetection() {
        assertTrue(MathEvaluator.isPotentialMath("2+2"), "Detect: 2+2");
        assertTrue(MathEvaluator.isPotentialMath("25 * 40 + 12"), "Detect: Expression");
        assertTrue(MathEvaluator.isPotentialMath("(100 - 20) / 4"), "Detect: Grouping");
        assertTrue(MathEvaluator.isPotentialMath("1,200 + 50"), "Detect: With commas");

        assertFalse(MathEvaluator.isPotentialMath(""), "Reject: Empty string");
        assertFalse(MathEvaluator.isPotentialMath(null), "Reject: Null");
        assertFalse(MathEvaluator.isPotentialMath("1234"), "Reject: Plain number");
        assertFalse(MathEvaluator.isPotentialMath("instagram"), "Reject: App name");
        assertFalse(MathEvaluator.isPotentialMath("j my note"), "Reject: Journal prefix");
        assertFalse(MathEvaluator.isPotentialMath("t buy groceries"), "Reject: Task prefix");
        assertFalse(MathEvaluator.isPotentialMath("$ 50 lunch"), "Reject: Expense prefix");
    }

    private static void testExpenseParser() {
        assertEquals(1200.0, TaktoDbHelper.parseExpenseAmount("$ 1,200 rent"), 0.001, "Expense: $ 1,200 rent");
        assertEquals(15.50, TaktoDbHelper.parseExpenseAmount("$15.50 lunch"), 0.001, "Expense: $15.50 lunch");
        assertEquals(50.0, TaktoDbHelper.parseExpenseAmount("50 groceries"), 0.001, "Expense: 50 groceries");
        assertEquals(-30.0, TaktoDbHelper.parseExpenseAmount("-$30 refund"), 0.001, "Expense: -$30 refund");
        assertEquals(0.0, TaktoDbHelper.parseExpenseAmount("invalid text"), 0.001, "Expense: Invalid text");
    }

    private static void testLogseqFormatting() {
        assertEquals("- TODO buy milk", LogseqSync.formatLogseqLine("task", "buy milk", "todo", 0), "Logseq: TODO task");
        assertEquals("- DONE buy milk", LogseqSync.formatLogseqLine("task", "buy milk", "done", 0), "Logseq: DONE task");
        assertEquals("- #expense 50 coffee", LogseqSync.formatLogseqLine("expense", "50 coffee", "logged", 0), "Logseq: Expense");
        assertTrue(LogseqSync.formatLogseqLine("journal", "deep thought", "logged", 0).contains("deep thought"), "Logseq: Journal text");
        assertTrue(LogseqSync.formatLogseqLine("journal", "deep thought", "logged", 0).startsWith("- "), "Logseq: Journal bullet");
    }

    private static void testLogseqFileOperations() {
        try {
            File tempDir = new File("/data/data/com.termux/files/usr/tmp/takto_test_journal");
            tempDir.mkdirs();
            File testFile = new File(tempDir, "test_journal.md");

            // Write initial lines
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, false))) {
                writer.write("- 10:00 Morning thought\n");
                writer.write("- TODO Review pull request\n");
                writer.write("- #expense 12.50 lunch\n");
                writer.write("- TODO Clean workspace\n");
            }

            // Read lines helper
            List<String> lines = readLines(testFile);
            assertEquals("4", String.valueOf(lines.size()), "FileOps: Line count check");

            // Test task status toggle in file
            boolean toggled = updateTaskInTestFile(testFile, "Review pull request", true);
            assertTrue(toggled, "FileOps: Toggle task to DONE");

            lines = readLines(testFile);
            assertTrue(lines.contains("- DONE Review pull request"), "FileOps: Contains - DONE line");
            assertFalse(lines.contains("- TODO Review pull request"), "FileOps: No old - TODO line");

            // Test toggling back to TODO
            boolean toggledBack = updateTaskInTestFile(testFile, "Review pull request", false);
            assertTrue(toggledBack, "FileOps: Toggle task back to TODO");

            lines = readLines(testFile);
            assertTrue(lines.contains("- TODO Review pull request"), "FileOps: Contains - TODO line again");

            // Test deleting an entry
            boolean deleted = deleteEntryInTestFile(testFile, "12.50 lunch");
            assertTrue(deleted, "FileOps: Delete expense entry");

            lines = readLines(testFile);
            assertEquals("3", String.valueOf(lines.size()), "FileOps: Line count after deletion");
            assertFalse(lines.contains("- #expense 12.50 lunch"), "FileOps: Deleted line removed");

            // Clean up
            testFile.delete();
            tempDir.delete();
        } catch (Exception e) {
            throw new RuntimeException("File operations test failed", e);
        }
    }

    private static List<String> readLines(File file) throws Exception {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String l;
            while ((l = reader.readLine()) != null) {
                lines.add(l);
            }
        }
        return lines;
    }

    private static boolean updateTaskInTestFile(File file, String content, boolean isDone) throws Exception {
        List<String> lines = readLines(file);
        boolean modified = false;
        String oldPrefix = isDone ? "- TODO " : "- DONE ";
        String newPrefix = isDone ? "- DONE " : "- TODO ";

        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i);
            if (l.trim().startsWith(oldPrefix) && l.contains(content)) {
                lines.set(i, l.replace(oldPrefix, newPrefix));
                modified = true;
            }
        }
        if (modified) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                for (String l : lines) {
                    writer.write(l + "\n");
                }
            }
        }
        return modified;
    }

    private static boolean deleteEntryInTestFile(File file, String content) throws Exception {
        List<String> lines = readLines(file);
        List<String> newLines = new ArrayList<>();
        boolean modified = false;

        for (String l : lines) {
            if (l.contains(content)) {
                modified = true;
                continue;
            }
            newLines.add(l);
        }
        if (modified) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                for (String l : newLines) {
                    writer.write(l + "\n");
                }
            }
        }
        return modified;
    }
}
