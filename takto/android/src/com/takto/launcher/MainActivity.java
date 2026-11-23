package com.takto.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.takto.launcher.db.TaktoDbHelper;
import com.takto.launcher.ui.AppDrawerView;
import com.takto.launcher.ui.BottomBarView;
import com.takto.launcher.ui.InternalThreadView;
import com.takto.launcher.ui.MetroDashboardView;
import com.takto.launcher.ui.TaktoPager;
import com.takto.launcher.util.AppExecutors;
import com.takto.launcher.util.LogseqSync;
import com.takto.launcher.util.UiUtils;
import java.io.File;

/**
 * MainActivity: Takto Launcher Root Activity
 * Connects the custom 3-page TaktoPager with persistent universal capture,
 * SAF directory integration, and stable block UUID dual-write persistence.
 */
public class MainActivity extends Activity {

    private static final int REQUEST_CODE_SAF = 4201;

    private TaktoDbHelper dbHelper;
    private TaktoPager pager;
    private MetroDashboardView metroView;
    private InternalThreadView threadView;
    private AppDrawerView drawerView;
    private BottomBarView bottomBar;
    private TextView storageWarningBanner;

    private TextView tabMetro;
    private TextView tabThread;
    private TextView tabApps;

    private int currentPage = 1; // Default: Center Thread

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
            getWindow().setNavigationBarColor(Color.BLACK);
        }

        dbHelper = TaktoDbHelper.getInstance(this);

        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setBackgroundColor(Color.BLACK);

        LinearLayout contentVertical = new LinearLayout(this);
        contentVertical.setOrientation(LinearLayout.VERTICAL);
        contentVertical.setBackgroundColor(Color.BLACK);

        // 1. Top Tabs
        LinearLayout tabRow = createTopTabIndicator();
        contentVertical.addView(tabRow, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // 2. Storage Status / SAF Configuration Banner
        storageWarningBanner = new TextView(this);
        storageWarningBanner.setTextSize(10);
        storageWarningBanner.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        storageWarningBanner.setGravity(Gravity.CENTER);
        storageWarningBanner.setPadding(UiUtils.dpToPx(this, 12), UiUtils.dpToPx(this, 8), UiUtils.dpToPx(this, 12), UiUtils.dpToPx(this, 8));
        storageWarningBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSafDirectoryAccess();
            }
        });
        contentVertical.addView(storageWarningBanner, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // 3. Custom TaktoPager (Pure ViewGroup touch interception)
        pager = new TaktoPager(this);
        metroView = new MetroDashboardView(this);
        threadView = new InternalThreadView(this);
        drawerView = new AppDrawerView(this);

        pager.addView(metroView);
        pager.addView(threadView);
        pager.addView(drawerView);

        contentVertical.addView(pager, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

        rootLayout.addView(contentVertical, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // 4. Bottom Universal Capture Bar
        bottomBar = new BottomBarView(this);
        FrameLayout.LayoutParams bottomParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomParams.gravity = Gravity.BOTTOM;
        rootLayout.addView(bottomBar, bottomParams);

        setContentView(rootLayout);

        wireEventListeners();

        pager.post(new Runnable() {
            @Override
            public void run() {
                pager.snapToPage(1, false);
                highlightTab(1);
            }
        });

        refreshAllViews();
        checkStorageAccess();
    }

    private void checkStorageAccess() {
        LogseqSync.StorageStatus status = LogseqSync.getStorageStatus(this);
        if (status == LogseqSync.StorageStatus.APP_PRIVATE_FALLBACK) {
            storageWarningBanner.setVisibility(View.VISIBLE);
            storageWarningBanner.setBackgroundColor(Color.parseColor("#3D1E06"));
            storageWarningBanner.setTextColor(Color.parseColor("#FFC86B"));
            storageWarningBanner.setText("[!] LOGSEQ UNLINKED // TAP TO CONNECT FOLDER VIA SAF");
        } else if (status == LogseqSync.StorageStatus.SAF_CONFIGURED) {
            storageWarningBanner.setVisibility(View.GONE);
        } else {
            storageWarningBanner.setVisibility(View.GONE);
        }
    }

    private void requestSafDirectoryAccess() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try {
            startActivityForResult(intent, REQUEST_CODE_SAF);
        } catch (Exception e) {
            Toast.makeText(this, "SAF file picker not available: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SAF && resultCode == RESULT_OK && data != null) {
            Uri treeUri = data.getData();
            if (treeUri != null) {
                try {
                    int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                    getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                    LogseqSync.setPersistedSafUri(this, treeUri);
                    Toast.makeText(this, "Logseq folder connected via SAF", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed taking persistable permission: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                checkStorageAccess();
            }
        }
    }

    private LinearLayout createTopTabIndicator() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.setPadding(UiUtils.dpToPx(this, 16), UiUtils.dpToPx(this, 28), UiUtils.dpToPx(this, 16), UiUtils.dpToPx(this, 4));
        row.setBackgroundColor(Color.BLACK);
        row.setWeightSum(3.0f);

        tabMetro = createTabButton("// METRO", 0);
        tabThread = createTabButton("// THREAD", 1);
        tabApps = createTabButton("// APPS", 2);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        row.addView(tabMetro, p);
        row.addView(tabThread, p);
        row.addView(tabApps, p);

        return row;
    }

    private TextView createTabButton(final String title, final int targetPage) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextSize(11);
        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(UiUtils.dpToPx(this, 8), UiUtils.dpToPx(this, 8), UiUtils.dpToPx(this, 8), UiUtils.dpToPx(this, 8));
        tv.setTextColor(Color.parseColor("#8892B0"));

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smoothScrollToPage(targetPage);
            }
        });
        return tv;
    }

    private void smoothScrollToPage(int page) {
        currentPage = page;
        pager.snapToPage(page, true);
        highlightTab(page);
    }

    private void highlightTab(int page) {
        currentPage = page;
        tabMetro.setTextColor(page == 0 ? Color.parseColor("#FFC86B") : Color.parseColor("#444F60"));
        tabThread.setTextColor(page == 1 ? Color.parseColor("#FFC86B") : Color.parseColor("#444F60"));
        tabApps.setTextColor(page == 2 ? Color.parseColor("#FFC86B") : Color.parseColor("#444F60"));
    }

    private void wireEventListeners() {
        pager.setOnPageChangeListener(new TaktoPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pageIndex) {
                highlightTab(pageIndex);
            }
        });

        metroView.setOnTaskActionListener(new MetroDashboardView.OnTaskActionListener() {
            @Override
            public void onTaskToggled(final long taskId, final String content, final boolean isDone) {
                handleTaskToggle(taskId, content, isDone);
            }

            @Override
            public void onTaskDeleted(final long taskId, final String content) {
                handleEntryDelete(taskId, "task", content);
            }
        });

        threadView.setOnThreadActionListener(new InternalThreadView.OnThreadActionListener() {
            @Override
            public void onTaskToggled(final long taskId, final String content, final boolean isDone) {
                handleTaskToggle(taskId, content, isDone);
            }

            @Override
            public void onEntryDeleted(final long entryId, final String type, final String content) {
                handleEntryDelete(entryId, type, content);
            }
        });

        bottomBar.setOnBottomActionListener(new BottomBarView.OnBottomActionListener() {
            @Override
            public void onCaptureEntry(final String type, final String content) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        String status = "task".equalsIgnoreCase(type) ? "todo" : "logged";
                        TaktoDbHelper.Entry entry = dbHelper.insertEntry(type, content, status);

                        File journalsDir = LogseqSync.getJournalsDirectory(MainActivity.this);
                        File targetFile = new File(journalsDir, entry.logseqFile);
                        final boolean fileOk = LogseqSync.appendEntryToFile(
                                targetFile, type, content, status, entry.timestamp, entry.logseqId);

                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                triggerHaptic();
                                refreshAllViews();
                                if (!fileOk) {
                                    Toast.makeText(MainActivity.this, "Warning: SQLite saved, Logseq write failed", Toast.LENGTH_SHORT).show();
                                }
                                if (currentPage == 2) {
                                    smoothScrollToPage(1);
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onFilterApps(String query) {
                drawerView.filter(query);
            }

            @Override
            public void onLaunchTopMatch() {
                AppDrawerView.AppEntry top = drawerView.getTopMatch();
                if (top != null) {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(top.packageName);
                    if (intent != null) {
                        startActivity(intent);
                        bottomBar.clearInput();
                    }
                } else if (currentPage != 2) {
                    smoothScrollToPage(2);
                }
            }
        });
    }

    private void handleTaskToggle(final long taskId, final String content, final boolean isDone) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                TaktoDbHelper.Entry entry = dbHelper.getEntry(taskId);
                dbHelper.toggleTaskStatus(taskId, isDone);

                if (entry != null && entry.logseqFile != null) {
                    File journalsDir = LogseqSync.getJournalsDirectory(MainActivity.this);
                    File targetFile = new File(journalsDir, entry.logseqFile);
                    LogseqSync.updateTaskStatusInFile(targetFile, entry.logseqId, content, isDone);
                }

                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        triggerHaptic();
                        refreshAllViews();
                    }
                });
            }
        });
    }

    private void handleEntryDelete(final long entryId, final String type, final String content) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                TaktoDbHelper.Entry entry = dbHelper.getEntry(entryId);
                dbHelper.deleteEntry(entryId);

                if (entry != null && entry.logseqFile != null) {
                    File journalsDir = LogseqSync.getJournalsDirectory(MainActivity.this);
                    File targetFile = new File(journalsDir, entry.logseqFile);
                    LogseqSync.deleteEntryFromFile(targetFile, entry.logseqId, content);
                }

                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        triggerHaptic();
                        refreshAllViews();
                    }
                });
            }
        });
    }

    private void refreshAllViews() {
        metroView.refreshData(dbHelper);
        threadView.refreshData(dbHelper);
    }

    private void triggerHaptic() {
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(20);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAllViews();
        checkStorageAccess();
    }

    @Override
    public void onBackPressed() {
        if (currentPage != 1) {
            smoothScrollToPage(1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (drawerView != null) {
            drawerView.cleanup();
        }
    }
}
