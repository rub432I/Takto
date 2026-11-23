package com.takto.launcher;

import com.takto.launcher.db.TaktoDbHelper;
import com.takto.launcher.util.LogseqSync;
import com.takto.launcher.util.MathEvaluator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * TaktoEngineTest: Deterministic test suite for production launcher logic.
 * Tests offline math parsing, expense extraction, prefix routing,
 * and calls the REAL production LogseqSync file synchronization methods.
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
        testProductionLogseqSyncDirectly();
        testMultiDayAndSubstringDisambiguation();

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
        String block = LogseqSync.formatLogseqBlock("task", "buy milk", "todo", 0, "uuid-123");
        assertTrue(block.contains("- TODO buy milk"), "Logseq: Header TODO");
        assertTrue(block.contains("id:: uuid-123"), "Logseq: Block id property");

        String expBlock = LogseqSync.formatLogseqBlock("expense", "50 coffee", "logged", 0, null);
        assertTrue(expBlock.startsWith("- #expense 50 coffee"), "Logseq: Expense format");
    }

    private static void testProductionLogseqSyncDirectly() {
        try {
            File tempFile = File.createTempFile("takto_prod_test", ".md");
            tempFile.deleteOnExit();

            // 1. Call REAL production appendEntryToFile
            boolean app1 = LogseqSync.appendEntryToFile(tempFile, "journal", "Morning reflection", "logged", System.currentTimeMillis(), "id-j1");
            boolean app2 = LogseqSync.appendEntryToFile(tempFile, "task", "Submit tax documents", "todo", System.currentTimeMillis(), "id-t1");
            boolean app3 = LogseqSync.appendEntryToFile(tempFile, "expense", "45 dinner", "logged", System.currentTimeMillis(), "id-e1");

            assertTrue(app1, "ProdSync: append journal");
            assertTrue(app2, "ProdSync: append task");
            assertTrue(app3, "ProdSync: append expense");

            List<String> lines = readLines(tempFile);
            assertTrue(lines.size() >= 5, "ProdSync: Lines written including id properties");

            // 2. Call REAL production updateTaskStatusInFile
            boolean updated = LogseqSync.updateTaskStatusInFile(tempFile, "id-t1", "Submit tax documents", true);
            assertTrue(updated, "ProdSync: Update task to DONE via logseqId");

            lines = readLines(tempFile);
            boolean foundDone = false;
            boolean foundOldTodo = false;
            for (String l : lines) {
                if (l.contains("- DONE Submit tax documents")) foundDone = true;
                if (l.contains("- TODO Submit tax documents")) foundOldTodo = true;
            }
            assertTrue(foundDone, "ProdSync: - DONE present in file");
            assertFalse(foundOldTodo, "ProdSync: - TODO replaced");

            // 3. Call REAL production deleteEntryFromFile
            boolean deleted = LogseqSync.deleteEntryFromFile(tempFile, "id-e1", "45 dinner");
            assertTrue(deleted, "ProdSync: Delete expense entry via logseqId");

            lines = readLines(tempFile);
            for (String l : lines) {
                assertFalse(l.contains("45 dinner"), "ProdSync: Expense line removed from file");
                assertFalse(l.contains("id-e1"), "ProdSync: Expense block id property removed");
            }

            tempFile.delete();
        } catch (Exception e) {
            throw new RuntimeException("Production LogseqSync test failed", e);
        }
    }

    private static void testMultiDayAndSubstringDisambiguation() {
        try {
            File fileDay1 = File.createTempFile("takto_day1", ".md");
            File fileDay2 = File.createTempFile("takto_day2", ".md");
            fileDay1.deleteOnExit();
            fileDay2.deleteOnExit();

            // Yesterday's task
            LogseqSync.appendEntryToFile(fileDay1, "task", "Prepare slides", "todo", 1000, "uuid-yesterday");

            // Today's similar tasks: "buy milk" and "buy milk and eggs"
            LogseqSync.appendEntryToFile(fileDay2, "task", "buy milk", "todo", 2000, "uuid-milk");
            LogseqSync.appendEntryToFile(fileDay2, "task", "buy milk and eggs", "todo", 3000, "uuid-milk-eggs");

            // Toggle yesterday's task using its file and id
            boolean updatedPast = LogseqSync.updateTaskStatusInFile(fileDay1, "uuid-yesterday", "Prepare slides", true);
            assertTrue(updatedPast, "MultiDay: Update past day task");

            List<String> day1Lines = readLines(fileDay1);
            boolean day1Done = false;
            for (String l : day1Lines) {
                if (l.contains("- DONE Prepare slides")) day1Done = true;
            }
            assertTrue(day1Done, "MultiDay: Yesterday file updated to - DONE");

            // Toggle "buy milk" in today's file: MUST NOT flip "buy milk and eggs"
            boolean milkUpdated = LogseqSync.updateTaskStatusInFile(fileDay2, "uuid-milk", "buy milk", true);
            assertTrue(milkUpdated, "Disambig: Update buy milk");

            List<String> day2Lines = readLines(fileDay2);
            boolean foundMilkDone = false;
            boolean foundEggsStillTodo = false;
            for (String l : day2Lines) {
                if (l.contains("- DONE buy milk") && !l.contains("eggs")) foundMilkDone = true;
                if (l.contains("- TODO buy milk and eggs")) foundEggsStillTodo = true;
            }
            assertTrue(foundMilkDone, "Disambig: 'buy milk' is DONE");
            assertTrue(foundEggsStillTodo, "Disambig: 'buy milk and eggs' remains TODO");

            fileDay1.delete();
            fileDay2.delete();
        } catch (Exception e) {
            throw new RuntimeException("Multi-day and disambiguation test failed", e);
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
}
