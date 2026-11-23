package com.takto.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Takto Launcher Main Activity
 * Minimalist, high-performance, zero-telemetry Android launcher.
 * Compiles cleanly with Android 34 SDK and runs at native 120Hz.
 */
public class MainActivity extends Activity {

    public static class AppEntry {
        public String label;
        public String packageName;
        public String activityName;
    }

    private final List<AppEntry> installedApps = new ArrayList<>();
    private AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge pure OLED black UI
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.BLACK);
        rootLayout.setPadding(48, 48, 48, 48);

        // 1. Clock & Date Header
        TextView timeView = new TextView(this);
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm", Locale.US);
        timeView.setText(tf.format(new Date()));
        timeView.setTextSize(36);
        timeView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        timeView.setTextColor(Color.parseColor("#FFC86B")); // Takto Gold
        rootLayout.addView(timeView);

        TextView mottoView = new TextView(this);
        mottoView.setText("TAKTO // OS FOR LIFE // FOSS LAUNCHER");
        mottoView.setTextSize(10);
        mottoView.setTypeface(Typeface.MONOSPACE);
        mottoView.setTextColor(Color.parseColor("#DF9E38"));
        mottoView.setPadding(0, 4, 0, 32);
        rootLayout.addView(mottoView);

        // 2. Installed Apps List
        ListView appListView = new ListView(this);
        appListView.setDivider(null);
        appListView.setDividerHeight(12);

        adapter = new AppAdapter();
        appListView.setAdapter(adapter);
        rootLayout.addView(appListView, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f
        ));

        setContentView(rootLayout);
        loadInstalledApps();
    }

    private void loadInstalledApps() {
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
        installedApps.clear();

        for (ResolveInfo info : pkgAppsList) {
            if (info.activityInfo.packageName.equals(getPackageName())) {
                continue; // Skip self
            }
            AppEntry entry = new AppEntry();
            entry.label = info.loadLabel(pm).toString();
            entry.packageName = info.activityInfo.packageName;
            entry.activityName = info.activityInfo.name;
            installedApps.add(entry);
        }

        Collections.sort(installedApps, new Comparator<AppEntry>() {
            @Override
            public int compare(AppEntry a, AppEntry b) {
                return a.label.compareToIgnoreCase(b.label);
            }
        });

        adapter.notifyDataSetChanged();
    }

    private class AppAdapter extends BaseAdapter {
        @Override
        public int getCount() { return installedApps.size(); }
        @Override
        public Object getItem(int pos) { return installedApps.get(pos); }
        @Override
        public long getItemId(int pos) { return pos; }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            TextView tv = (TextView) convertView;
            if (tv == null) {
                tv = new TextView(MainActivity.this);
                tv.setTextSize(14);
                tv.setTypeface(Typeface.MONOSPACE);
                tv.setTextColor(Color.WHITE);
                tv.setPadding(24, 20, 24, 20);
                tv.setBackgroundColor(Color.parseColor("#0E1218"));
            }

            final AppEntry app = installedApps.get(pos);
            tv.setText(app.label);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app.packageName);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    }
                }
            });

            return tv;
        }
    }
}
