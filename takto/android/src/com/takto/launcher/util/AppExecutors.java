package com.takto.launcher.util;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * AppExecutors: Global thread executors for offloading disk I/O, SQLite operations,
 * and PackageManager queries from the main UI thread.
 */
public class AppExecutors {

    private static AppExecutors instance;

    private final Executor diskIO;
    private final Executor mainThread;

    private AppExecutors() {
        this.diskIO = Executors.newSingleThreadExecutor();
        this.mainThread = new MainThreadExecutor();
    }

    public static synchronized AppExecutors getInstance() {
        if (instance == null) {
            instance = new AppExecutors();
        }
        return instance;
    }

    public Executor diskIO() {
        return diskIO;
    }

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
