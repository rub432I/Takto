package com.takto.launcher.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Takto Local Dual-Write Database Helper
 * High-performance, zero-latency SQLite storage for offline notes, tasks, and expenses.
 */
public class TaktoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "takto.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ENTRIES = "entries";
    public static final String COL_ID = "id";
    public static final String COL_TYPE = "type";         // journal, task, expense
    public static final String COL_CONTENT = "content";
    public static final String COL_TIMESTAMP = "timestamp"; // epoch millis
    public static final String COL_STATUS = "status";       // todo, done, logged

    public static class Entry {
        public long id;
        public String type;
        public String content;
        public long timestamp;
        public String status;

        public Entry(long id, String type, String content, long timestamp, String status) {
            this.id = id;
            this.type = type;
            this.content = content;
            this.timestamp = timestamp;
            this.status = status;
        }

        public boolean isTask() {
            return "task".equalsIgnoreCase(type);
        }

        public boolean isDone() {
            return "done".equalsIgnoreCase(status);
        }
    }

    public static class Stats {
        public int noteCount;
        public int activeTaskCount;
        public int completedTaskCount;
        public double expenseTotal;
    }

    private static TaktoDbHelper instance;

    public static synchronized TaktoDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new TaktoDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    public TaktoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "CREATE TABLE " + TABLE_ENTRIES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TYPE + " TEXT NOT NULL, " +
                COL_CONTENT + " TEXT NOT NULL, " +
                COL_TIMESTAMP + " INTEGER NOT NULL, " +
                COL_STATUS + " TEXT NOT NULL" +
                ");";
        db.execSQL(createSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }

    public long insertEntry(String type, String content, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TYPE, type);
        cv.put(COL_CONTENT, content);
        cv.put(COL_TIMESTAMP, System.currentTimeMillis());
        cv.put(COL_STATUS, status);
        return db.insert(TABLE_ENTRIES, null, cv);
    }

    public void toggleTaskStatus(long id, boolean isDone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_STATUS, isDone ? "done" : "todo");
        db.update(TABLE_ENTRIES, cv, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteEntry(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ENTRIES, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<Entry> getTodayEntries() {
        List<Entry> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfDay = cal.getTimeInMillis();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ENTRIES, null,
                COL_TIMESTAMP + " >= ?",
                new String[]{String.valueOf(startOfDay)},
                null, null, COL_TIMESTAMP + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS));
                list.add(new Entry(id, type, content, timestamp, status));
            }
            cursor.close();
        }
        return list;
    }

    public List<Entry> getActiveTasks() {
        List<Entry> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ENTRIES, null,
                COL_TYPE + " = ? AND " + COL_STATUS + " = ?",
                new String[]{"task", "todo"},
                null, null, COL_TIMESTAMP + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS));
                list.add(new Entry(id, type, content, timestamp, status));
            }
            cursor.close();
        }
        return list;
    }

    public Stats getTodayStats() {
        Stats stats = new Stats();
        List<Entry> today = getTodayEntries();
        for (Entry e : today) {
            if ("journal".equalsIgnoreCase(e.type)) {
                stats.noteCount++;
            } else if ("task".equalsIgnoreCase(e.type)) {
                if ("todo".equalsIgnoreCase(e.status)) {
                    stats.activeTaskCount++;
                } else {
                    stats.completedTaskCount++;
                }
            } else if ("expense".equalsIgnoreCase(e.type)) {
                // Try parse leading number from expense content (e.g. "15 coffee")
                try {
                    String[] parts = e.content.trim().split("\\s+");
                    if (parts.length > 0) {
                        String numStr = parts[0].replaceAll("[^0-9.]", "");
                        if (!numStr.isEmpty()) {
                            stats.expenseTotal += Double.parseDouble(numStr);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return stats;
    }
}
