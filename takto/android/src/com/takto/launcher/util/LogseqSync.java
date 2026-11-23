package com.takto.launcher.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * LogseqSync: Bidirectional synchronization between SQLite and Logseq Markdown journals.
 * Core file operations take java.io.File directly with file-locking and stable block UUIDs (id:: <uuid>).
 */
public class LogseqSync {

    public enum StorageStatus {
        SAF_CONFIGURED,
        DIRECT_SHARED_WRITABLE,
        APP_PRIVATE_FALLBACK
    }

    private static final String PREFS_NAME = "takto_storage";
    private static final String KEY_SAF_URI = "saf_tree_uri";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    public static StorageStatus getStorageStatus(Context context) {
        if (getPersistedSafUri(context) != null) {
            return StorageStatus.SAF_CONFIGURED;
        }
        File shared = new File("/storage/emulated/0/Documents/Logseq/journals");
        if (shared.exists() && shared.canWrite()) {
            return StorageStatus.DIRECT_SHARED_WRITABLE;
        }
        return StorageStatus.APP_PRIVATE_FALLBACK;
    }

    public static Uri getPersistedSafUri(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String uriStr = prefs.getString(KEY_SAF_URI, null);
        return uriStr != null ? Uri.parse(uriStr) : null;
    }

    public static void setPersistedSafUri(Context context, Uri uri) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_SAF_URI, uri != null ? uri.toString() : null).apply();
    }

    public static File getJournalsDirectory(Context context) {
        File[] candidates = new File[]{
                new File("/storage/emulated/0/Documents/Logseq/journals"),
                new File("/storage/emulated/0/Logseq/journals"),
                new File("/sdcard/Documents/Logseq/journals")
        };
        for (File dir : candidates) {
            if (dir.exists() && dir.canWrite()) {
                return dir;
            }
            File parent = dir.getParentFile();
            if (parent != null && parent.exists() && parent.canWrite()) {
                if (dir.mkdirs()) {
                    return dir;
                }
            }
        }
        File fallback = new File(context.getExternalFilesDir(null), "Logseq/journals");
        if (!fallback.exists()) {
            fallback.mkdirs();
        }
        return fallback;
    }

    public static String formatLogseqBlock(String type, String content, String status, long timestamp, String logseqId) {
        Date date = new Date(timestamp > 0 ? timestamp : System.currentTimeMillis());
        String timeStr = TIME_FORMAT.format(date);
        String header;

        if ("task".equalsIgnoreCase(type)) {
            String prefix = "done".equalsIgnoreCase(status) ? "- DONE " : "- TODO ";
            header = prefix + content;
        } else if ("expense".equalsIgnoreCase(type)) {
            header = "- #expense " + content;
        } else {
            header = "- " + timeStr + " " + content;
        }

        if (logseqId != null && !logseqId.trim().isEmpty()) {
            return header + "\n  id:: " + logseqId.trim();
        } else {
            return header;
        }
    }

    public static boolean appendEntryToFile(File journalFile, String type, String content, String status, long timestamp, String logseqId) {
        String block = formatLogseqBlock(type, content, status, timestamp, logseqId);
        File parent = journalFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (RandomAccessFile raf = new RandomAccessFile(journalFile, "rw");
             FileChannel channel = raf.getChannel();
             FileLock lock = channel.lock()) {
            raf.seek(raf.length());
            byte[] bytes = (block + "\n").getBytes("UTF-8");
            raf.write(bytes);
            return true;
        } catch (Exception e) {
            System.err.println("LogseqSync: appendEntryToFile failed for " + journalFile + ": " + e.getMessage());
            return false;
        }
    }

    public static boolean updateTaskStatusInFile(File journalFile, String logseqId, String taskContent, boolean isDone) {
        if (!journalFile.exists()) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(journalFile, "rw");
             FileChannel channel = raf.getChannel();
             FileLock lock = channel.lock()) {

            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(journalFile))) {
                String l;
                while ((l = reader.readLine()) != null) {
                    lines.add(l);
                }
            }

            boolean modified = false;
            String oldPrefix = isDone ? "- TODO " : "- DONE ";
            String newPrefix = isDone ? "- DONE " : "- TODO ";

            if (logseqId != null && !logseqId.trim().isEmpty()) {
                String targetIdLine = "id:: " + logseqId.trim();
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.contains(targetIdLine) && i > 0) {
                        String parentLine = lines.get(i - 1);
                        if (parentLine.trim().startsWith(oldPrefix)) {
                            lines.set(i - 1, parentLine.replaceFirst(PatternQuote(oldPrefix), newPrefix));
                            modified = true;
                            break;
                        }
                    }
                }
            }

            if (!modified && taskContent != null && !taskContent.trim().isEmpty()) {
                String exactTarget = oldPrefix + taskContent.trim();
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    if (line.equals(exactTarget)) {
                        lines.set(i, lines.get(i).replaceFirst(PatternQuote(oldPrefix), newPrefix));
                        modified = true;
                        break;
                    }
                }
            }

            if (modified) {
                raf.setLength(0);
                for (String line : lines) {
                    raf.write((line + "\n").getBytes("UTF-8"));
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("LogseqSync: updateTaskStatusInFile failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteEntryFromFile(File journalFile, String logseqId, String content) {
        if (!journalFile.exists()) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(journalFile, "rw");
             FileChannel channel = raf.getChannel();
             FileLock lock = channel.lock()) {

            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(journalFile))) {
                String l;
                while ((l = reader.readLine()) != null) {
                    lines.add(l);
                }
            }

            List<String> remaining = new ArrayList<>();
            boolean modified = false;

            for (int i = 0; i < lines.size(); i++) {
                String current = lines.get(i);
                boolean matchesId = (logseqId != null && !logseqId.trim().isEmpty() &&
                        i + 1 < lines.size() && lines.get(i + 1).contains("id:: " + logseqId.trim()));
                boolean matchesExactContent = (content != null && !content.trim().isEmpty() &&
                        current.contains(content.trim()));

                if (matchesId) {
                    modified = true;
                    i++; // Skip the id:: child property line as well
                    continue;
                } else if (matchesExactContent) {
                    modified = true;
                    if (i + 1 < lines.size() && lines.get(i + 1).trim().startsWith("id::")) {
                        i++; // Skip id property if attached
                    }
                    continue;
                }
                remaining.add(current);
            }

            if (modified) {
                raf.setLength(0);
                for (String line : remaining) {
                    raf.write((line + "\n").getBytes("UTF-8"));
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("LogseqSync: deleteEntryFromFile failed: " + e.getMessage());
            return false;
        }
    }

    private static String PatternQuote(String s) {
        return java.util.regex.Pattern.quote(s);
    }
}
