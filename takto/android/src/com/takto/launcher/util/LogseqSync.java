package com.takto.launcher.util;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * LogseqSync: Synchronizer between SQLite and Logseq Markdown journals.
 * Enforces bidirectional consistency:
 * 1. Appends daily captures into today's journal
 * 2. Rewrites task states (- TODO <-> - DONE) when toggled
 * 3. Removes lines when entries are deleted
 * Targets real Logseq document paths with graceful fallback.
 */
public class LogseqSync {

    private static final String TAG = "LogseqSync";
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd", Locale.US);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    public static File getJournalsDirectory(Context context) {
        // Preferred: Real Logseq directories on shared device storage
        File[] preferred = new File[]{
                new File("/storage/emulated/0/Documents/Logseq/journals"),
                new File("/storage/emulated/0/Logseq/journals"),
                new File("/sdcard/Documents/Logseq/journals")
        };

        for (File dir : preferred) {
            if (dir.exists() && dir.canWrite()) {
                return dir;
            }
            // Try creating if parent exists
            File parent = dir.getParentFile();
            if (parent != null && parent.exists() && parent.canWrite()) {
                if (dir.mkdirs()) {
                    return dir;
                }
            }
        }

        // Try creating preferred standard location
        try {
            File std = new File("/storage/emulated/0/Documents/Logseq/journals");
            if (std.mkdirs() || std.exists()) {
                return std;
            }
        } catch (Exception ignored) {
        }

        // Fallback: App external files directory
        File fallback = new File(context.getExternalFilesDir(null), "Logseq/journals");
        if (!fallback.exists()) {
            fallback.mkdirs();
        }
        return fallback;
    }

    public static File getTodayJournalFile(Context context) {
        File dir = getJournalsDirectory(context);
        String fileName = FILE_DATE_FORMAT.format(new Date()) + ".md";
        return new File(dir, fileName);
    }

    public static String formatLogseqLine(String type, String content, String status, long timestamp) {
        Date date = new Date(timestamp > 0 ? timestamp : System.currentTimeMillis());
        String timeStr = TIME_FORMAT.format(date);

        if ("task".equalsIgnoreCase(type)) {
            String prefix = "done".equalsIgnoreCase(status) ? "- DONE " : "- TODO ";
            return prefix + content;
        } else if ("expense".equalsIgnoreCase(type)) {
            return "- #expense " + content;
        } else {
            return "- " + timeStr + " " + content;
        }
    }

    public static boolean appendEntry(Context context, String type, String content, String status, long timestamp) {
        String line = formatLogseqLine(type, content, status, timestamp);
        File journalFile = getTodayJournalFile(context);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(journalFile, true))) {
            writer.write(line);
            writer.newLine();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to append to Logseq journal: " + journalFile.getAbsolutePath(), e);
            return false;
        }
    }

    public static boolean updateTaskStatus(Context context, String taskContent, boolean isDone) {
        File journalFile = getTodayJournalFile(context);
        if (!journalFile.exists()) {
            return false;
        }

        List<String> lines = new ArrayList<>();
        boolean modified = false;
        String oldPrefix = isDone ? "- TODO " : "- DONE ";
        String newPrefix = isDone ? "- DONE " : "- TODO ";

        try (BufferedReader reader = new BufferedReader(new FileReader(journalFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith(oldPrefix) && line.contains(taskContent)) {
                    line = line.replace(oldPrefix, newPrefix);
                    modified = true;
                }
                lines.add(line);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed reading Logseq journal for task update", e);
            return false;
        }

        if (modified) {
            return writeLinesAtomic(journalFile, lines);
        }
        return false;
    }

    public static boolean deleteEntry(Context context, String content) {
        File journalFile = getTodayJournalFile(context);
        if (!journalFile.exists()) {
            return false;
        }

        List<String> lines = new ArrayList<>();
        boolean modified = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(journalFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(content)) {
                    modified = true;
                    continue; // Skip line to delete
                }
                lines.add(line);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed reading Logseq journal for deletion", e);
            return false;
        }

        if (modified) {
            return writeLinesAtomic(journalFile, lines);
        }
        return false;
    }

    private static boolean writeLinesAtomic(File targetFile, List<String> lines) {
        File tempFile = new File(targetFile.getParentFile(), targetFile.getName() + ".tmp");
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, false))) {
                for (String l : lines) {
                    writer.write(l);
                    writer.newLine();
                }
            }
            if (tempFile.renameTo(targetFile)) {
                return true;
            } else {
                // If direct rename fails, copy and delete
                try (BufferedReader in = new BufferedReader(new FileReader(tempFile));
                     BufferedWriter out = new BufferedWriter(new FileWriter(targetFile, false))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        out.write(line);
                        out.newLine();
                    }
                }
                tempFile.delete();
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed writing atomic update to Logseq journal", e);
            if (tempFile.exists()) tempFile.delete();
            return false;
        }
    }
}
