package com.takto.launcher.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takto Local Dual-Write Database Helper
 * Offline-first SQLite persistence with non-destructive schema migrations and indexed queries.
 */
public class TaktoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "takto.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_ENTRIES = "entries";
    public static final String COL_ID = "id";
    public static final String COL_TYPE = "type";                 // journal, task, expense
    public static final String COL_CONTENT = "content";
    public static final String COL_TIMESTAMP = "timestamp";         // epoch millis
    public static final String COL_STATUS = "status";               // todo, done, logged
    public static final String COL_NUMERIC_VALUE = "numeric_value"; // parsed expense amount

    private static final Pattern EXPENSE_PATTERN = Pattern.compile("[-+]?\\s*[$€£¥]?\\s*([0-9]+(?:,[0-9]{3})*(?:\\.[0-9]+)?|[0-9]+(?:\\.[0-9]+)?)");

    public static class Entry {
        public long id;
        public String type;
        public String content;
        public long timestamp;
        public String status;
        public double numericValue;

        public Entry(long id, String type, String content, long timestamp, String status, double numericValue) {
            this.id = id;
            this.type = type;
            this.content = content;
            this.timestamp = timestamp;
            this.status = status;
            this.numericValue = numericValue;
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
                COL_STATUS + " TEXT NOT NULL, " +
                COL_NUMERIC_VALUE + " REAL DEFAULT 0" +
                ");";
        db.execSQL(createSql);
        createIndexes(db);
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_entries_timestamp ON " + TABLE_ENTRIES + " (" + COL_TIMESTAMP + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_entries_type_status ON " + TABLE_ENTRIES + " (" + COL_TYPE + ", " + COL_STATUS + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Non-destructive schema migrations: Never drop tables containing user data
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_ENTRIES + " ADD COLUMN " + COL_NUMERIC_VALUE + " REAL DEFAULT 0;");
            createIndexes(db);
            populateLegacyNumericValues(db);
        }
    }

    private void populateLegacyNumericValues(SQLiteDatabase db) {
        Cursor c = db.query(TABLE_ENTRIES, new String[]{COL_ID, COL_CONTENT}, COL_TYPE + " = ?", new String[]{"expense"}, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                long id = c.getLong(0);
                String content = c.getString(1);
                double val = parseExpenseAmount(content);
                ContentValues cv = new ContentValues();
                cv.put(COL_NUMERIC_VALUE, val);
                db.update(TABLE_ENTRIES, cv, COL_ID + " = ?", new String[]{String.valueOf(id)});
            }
            c.close();
        }
    }

    public static double parseExpenseAmount(String content) {
        if (content == null || content.trim().isEmpty()) {
            return 0.0;
        }
        try {
            Matcher m = EXPENSE_PATTERN.matcher(content.trim());
            if (m.find()) {
                String match = m.group(0);
                boolean negative = match.contains("-");
                String numStr = match.replaceAll("[-+$€£¥\\s]", "").replace(",", "");
                if (!numStr.isEmpty()) {
                    double val = Double.parseDouble(numStr);
                    return negative ? -val : val;
                }
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }

    public long insertEntry(String type, String content, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TYPE, type);
        cv.put(COL_CONTENT, content);
        cv.put(COL_TIMESTAMP, System.currentTimeMillis());
        cv.put(COL_STATUS, status);

        double numVal = 0.0;
        if ("expense".equalsIgnoreCase(type)) {
            numVal = parseExpenseAmount(content);
        }
        cv.put(COL_NUMERIC_VALUE, numVal);

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

    public Entry getEntry(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ENTRIES, null, COL_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        Entry entry = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                entry = cursorToEntry(cursor);
            }
            cursor.close();
        }
        return entry;
    }

    private Entry cursorToEntry(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE));
        String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
        long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS));
        double numericValue = 0.0;
        int numIdx = cursor.getColumnIndex(COL_NUMERIC_VALUE);
        if (numIdx != -1 && !cursor.isNull(numIdx)) {
            numericValue = cursor.getDouble(numIdx);
        }
        return new Entry(id, type, content, timestamp, status, numericValue);
    }

    private long getStartOfDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public List<Entry> getTodayEntries() {
        List<Entry> list = new ArrayList<>();
        long startOfDay = getStartOfDay();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ENTRIES, null,
                COL_TIMESTAMP + " >= ?",
                new String[]{String.valueOf(startOfDay)},
                null, null, COL_TIMESTAMP + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToEntry(cursor));
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
                list.add(cursorToEntry(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public Stats getTodayStats() {
        Stats stats = new Stats();
        long startOfDay = getStartOfDay();
        String startStr = String.valueOf(startOfDay);
        SQLiteDatabase db = getReadableDatabase();

        // 1. Note count
        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ENTRIES + " WHERE " + COL_TIMESTAMP + " >= ? AND " + COL_TYPE + " = 'journal'", new String[]{startStr});
        if (c1 != null) {
            if (c1.moveToFirst()) stats.noteCount = c1.getInt(0);
            c1.close();
        }

        // 2. Active tasks count
        Cursor c2 = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ENTRIES + " WHERE " + COL_TIMESTAMP + " >= ? AND " + COL_TYPE + " = 'task' AND " + COL_STATUS + " = 'todo'", new String[]{startStr});
        if (c2 != null) {
            if (c2.moveToFirst()) stats.activeTaskCount = c2.getInt(0);
            c2.close();
        }

        // 3. Completed tasks count
        Cursor c3 = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ENTRIES + " WHERE " + COL_TIMESTAMP + " >= ? AND " + COL_TYPE + " = 'task' AND " + COL_STATUS + " = 'done'", new String[]{startStr});
        if (c3 != null) {
            if (c3.moveToFirst()) stats.completedTaskCount = c3.getInt(0);
            c3.close();
        }

        // 4. Expense sum
        Cursor c4 = db.rawQuery("SELECT SUM(" + COL_NUMERIC_VALUE + ") FROM " + TABLE_ENTRIES + " WHERE " + COL_TIMESTAMP + " >= ? AND " + COL_TYPE + " = 'expense'", new String[]{startStr});
        if (c4 != null) {
            if (c4.moveToFirst() && !c4.isNull(0)) stats.expenseTotal = c4.getDouble(0);
            c4.close();
        }

        return stats;
    }
}
