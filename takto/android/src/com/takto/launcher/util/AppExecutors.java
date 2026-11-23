package com.takto.launcher.util;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * AppExecutors: Segregated thread pools ensuring database and file synchronizations
 * are never queued behind heavy application package and icon decoding scans.
 */
public class AppExecutors {

    private static AppExecutors instance;

    private final Executor diskIO;
    private final Executor appScanner;
    private final Executor mainThread;

    private AppExecutors() {
        this.diskIO = Executors.newSingleThreadExecutor();
        this.appScanner = Executors.newFixedThreadPool(2);
        this.mainThread = new MainThreadExecutor();
    }

    public static synchronized AppExecutors getInstance() {
        if (instance == null) {
            instance = new AppExecutors();
        }
        return instance;
    }

    /**
     * Dedicated serialized executor for SQLite queries and Logseq Markdown file persistence.
     */
    public Executor diskIO() {
        return diskIO;
    }

    /**
     * Dedicated executor for PackageManager lookups and asynchronous icon resolution.
     */
    public Executor appScanner() {
        return appScanner;
    }

    /**
     * Main thread dispatcher for posting UI view updates.
     */
    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
