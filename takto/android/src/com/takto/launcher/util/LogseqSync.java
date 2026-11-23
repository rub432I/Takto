package com.takto.launcher.util;

import android.content.Context;
import com.takto.launcher.db.TaktoDbHelper;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Logseq Synchronizer & Markdown Formatter
 * Implements the Logseq bullet format:
 * - Tasks:    - TODO <content> / - DONE <content>
 * - Expenses: - #expense <content>
 * - Journal:  - HH:mm <content>
 * Writes to local app storage or device documents directory.
 */
public class LogseqSync {

    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd", Locale.US);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

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

    public static boolean appendToLocalLogseqFile(Context context, String line) {
        try {
            File journalsDir = new File(context.getExternalFilesDir(null), "Logseq/journals");
            if (!journalsDir.exists()) {
                journalsDir.mkdirs();
            }
            String fileName = FILE_DATE_FORMAT.format(new Date()) + ".md";
            File journalFile = new File(journalsDir, fileName);

            FileWriter fw = new FileWriter(journalFile, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(line);
            pw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
