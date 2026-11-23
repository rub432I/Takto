package com.takto.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.takto.launcher.db.TaktoDbHelper;
import com.takto.launcher.ui.AppDrawerView;
import com.takto.launcher.ui.BottomBarView;
import com.takto.launcher.ui.InternalThreadView;
import com.takto.launcher.ui.MetroDashboardView;
import com.takto.launcher.util.LogseqSync;

/**
 * MainActivity: Takto Launcher Root Activity
 * Orchestrates the 3-page horizontal matrix:
 *   [ Page 0: Metro Dashboard ]
 *   [ Page 1: Internal Thread (Self-Chat) ]
 *   [ Page 2: Application Registry ]
 * Anchored by the universal persistent BottomBarView and Dual-Write SQLite/Logseq engine.
 */
public class MainActivity extends Activity {

    private TaktoDbHelper dbHelper;
    private HorizontalScrollView horizontalPager;
    private LinearLayout pagesContainer;
    private MetroDashboardView metroView;
    private InternalThreadView threadView;
    private AppDrawerView drawerView;
    private BottomBarView bottomBar;

    private TextView tabMetro;
    private TextView tabThread;
    private TextView tabApps;

    private int screenWidth = 1080;
    private int currentPage = 1; // Default to Center: Internal Thread

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Edge-to-Edge Pure OLED Black
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        dbHelper = TaktoDbHelper.getInstance(this);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;

        // 2. Root Container
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setBackgroundColor(Color.BLACK);

        LinearLayout contentVertical = new LinearLayout(this);
        contentVertical.setOrientation(LinearLayout.VERTICAL);
        contentVertical.setBackgroundColor(Color.BLACK);

        // 3. Top Brutalist Navigation Tabs
        LinearLayout tabRow = createTopTabIndicator();
        contentVertical.addView(tabRow, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // 4. Horizontal Scroll Pager
        horizontalPager = new HorizontalScrollView(this);
        horizontalPager.setHorizontalScrollBarEnabled(false);
        horizontalPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        pagesContainer = new LinearLayout(this);
        pagesContainer.setOrientation(LinearLayout.HORIZONTAL);

        metroView = new MetroDashboardView(this);
        threadView = new InternalThreadView(this);
        drawerView = new AppDrawerView(this);

        LinearLayout.LayoutParams pageParams = new LinearLayout.LayoutParams(
                screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);

        pagesContainer.addView(metroView, pageParams);
        pagesContainer.addView(threadView, pageParams);
        pagesContainer.addView(drawerView, pageParams);

        horizontalPager.addView(pagesContainer, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        contentVertical.addView(horizontalPager, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

        rootLayout.addView(contentVertical, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // 5. Bottom Universal Capture Bar
        bottomBar = new BottomBarView(this);
        FrameLayout.LayoutParams bottomParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomParams.gravity = Gravity.BOTTOM;
        rootLayout.addView(bottomBar, bottomParams);

        setContentView(rootLayout);

        // 6. Wire Events & Listeners
        wireEventListeners();

        // 7. Scroll to Center Page (Internal Thread) after layout
        horizontalPager.post(new Runnable() {
            @Override
            public void run() {
                horizontalPager.scrollTo(screenWidth, 0);
                highlightTab(1);
            }
        });

        // 8. Initial Data Refresh
        refreshAllViews();
    }

    private LinearLayout createTopTabIndicator() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.setPadding(24, 40, 24, 8);
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
        tv.setPadding(12, 12, 12, 12);
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
        horizontalPager.smoothScrollTo(page * screenWidth, 0);
        highlightTab(page);
    }

    private void highlightTab(int page) {
        currentPage = page;
        tabMetro.setTextColor(page == 0 ? Color.parseColor("#FFC86B") : Color.parseColor("#444F60"));
        tabThread.setTextColor(page == 1 ? Color.parseColor("#FFC86B") : Color.parseColor("#444F60"));
        tabApps.setTextColor(page == 2 ? Color.parseColor("#FFC86B") : Color.parseColor("#444F60"));
    }

    private void wireEventListeners() {
        // Page scroll change listener
        horizontalPager.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int page = (scrollX + screenWidth / 2) / screenWidth;
                if (page != currentPage) {
                    highlightTab(page);
                }
            }
        });

        // Task toggle listener in MetroDashboardView
        metroView.setOnTaskActionListener(new MetroDashboardView.OnTaskActionListener() {
            @Override
            public void onTaskToggled(long taskId, boolean isDone) {
                dbHelper.toggleTaskStatus(taskId, isDone);
                triggerHaptic();
                refreshAllViews();
            }
        });

        // BottomBar capture and routing listeners
        bottomBar.setOnBottomActionListener(new BottomBarView.OnBottomActionListener() {
            @Override
            public void onCaptureEntry(String type, String content) {
                // 1. Insert into SQLite
                String status = "task".equalsIgnoreCase(type) ? "todo" : "logged";
                dbHelper.insertEntry(type, content, status);

                // 2. Dual-Write to Logseq Markdown
                String logseqLine = LogseqSync.formatLogseqLine(type, content, status, System.currentTimeMillis());
                LogseqSync.appendToLocalLogseqFile(MainActivity.this, logseqLine);

                // 3. Feedback & Refresh
                triggerHaptic();
                refreshAllViews();

                // If captured a thought or task, switch to thread view so user sees it instantly
                if (currentPage == 2) {
                    smoothScrollToPage(1);
                }
            }

            @Override
            public void onFilterApps(String query) {
                drawerView.filter(query);
                if (query != null && !query.isEmpty() && !query.startsWith("j ") && !query.startsWith("t ") && !query.startsWith("$ ")) {
                    // Auto switch to App Drawer page when user is searching apps
                    if (currentPage != 2) {
                        smoothScrollToPage(2);
                    }
                }
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
                }
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
                v.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAllViews();
    }

    @Override
    public void onBackPressed() {
        // Pressing Back on Metro or Apps returns to Center Thread
        if (currentPage != 1) {
            smoothScrollToPage(1);
        } else {
            // Already home
        }
    }
}
