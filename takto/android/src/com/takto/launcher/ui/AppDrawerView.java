package com.takto.launcher.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.takto.launcher.util.AppExecutors;
import com.takto.launcher.util.UiUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * AppDrawerView: Right page application registry & fast launcher.
 * Loads apps asynchronously off the main thread and reacts to package change events.
 */
public class AppDrawerView extends LinearLayout {

    public static class AppEntry {
        public String label;
        public String packageName;
        public String activityName;
    }

    private final List<AppEntry> allApps = new ArrayList<>();
    private final List<AppEntry> filteredApps = new ArrayList<>();
    private ListView listView;
    private AppListAdapter adapter;
    private BroadcastReceiver packageReceiver;

    public AppDrawerView(Context context) {
        super(context);
        init();
    }

    private GradientDrawable createItemDrawable() {
        Context ctx = getContext();
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#080A0E"));
        gd.setStroke(UiUtils.dpToPx(ctx, 1), Color.parseColor("#151B23"));
        gd.setCornerRadius(UiUtils.dpToPx(ctx, 6));
        return gd;
    }

    private void init() {
        Context ctx = getContext();
        setOrientation(VERTICAL);
        setBackgroundColor(Color.BLACK);
        setPadding(UiUtils.dpToPx(ctx, 20), UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 20), UiUtils.dpToPx(ctx, 90));

        TextView header = new TextView(ctx);
        header.setText("// APPLICATION REGISTRY");
        header.setTextSize(10);
        header.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        header.setTextColor(Color.parseColor("#DF9E38"));
        header.setPadding(0, 0, 0, UiUtils.dpToPx(ctx, 12));
        addView(header);

        listView = new ListView(ctx);
        listView.setDivider(null);
        listView.setDividerHeight(UiUtils.dpToPx(ctx, 8));
        listView.setVerticalScrollBarEnabled(false);

        adapter = new AppListAdapter();
        listView.setAdapter(adapter);
        addView(listView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        reloadApps();
        registerPackageReceiver();
    }

    public void reloadApps() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = getContext().getPackageManager();
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
                final List<AppEntry> loaded = new ArrayList<>();

                for (ResolveInfo info : pkgAppsList) {
                    if (info.activityInfo.packageName.equals(getContext().getPackageName())) {
                        continue;
                    }
                    AppEntry entry = new AppEntry();
                    entry.label = info.loadLabel(pm).toString();
                    entry.packageName = info.activityInfo.packageName;
                    entry.activityName = info.activityInfo.name;
                    loaded.add(entry);
                }

                Collections.sort(loaded, new Comparator<AppEntry>() {
                    @Override
                    public int compare(AppEntry a, AppEntry b) {
                        return a.label.compareToIgnoreCase(b.label);
                    }
                });

                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        allApps.clear();
                        allApps.addAll(loaded);
                        filter(null);
                    }
                });
            }
        });
    }

    private void registerPackageReceiver() {
        packageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                reloadApps();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        try {
            getContext().registerReceiver(packageReceiver, filter);
        } catch (Exception ignored) {
        }
    }

    public void cleanup() {
        if (packageReceiver != null) {
            try {
                getContext().unregisterReceiver(packageReceiver);
            } catch (Exception ignored) {
            }
            packageReceiver = null;
        }
    }

    public void filter(String query) {
        filteredApps.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredApps.addAll(allApps);
        } else {
            String lower = query.trim().toLowerCase(Locale.US);
            for (AppEntry app : allApps) {
                if (app.label.toLowerCase(Locale.US).contains(lower) ||
                    app.packageName.toLowerCase(Locale.US).contains(lower)) {
                    filteredApps.add(app);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public AppEntry getTopMatch() {
        if (!filteredApps.isEmpty()) {
            return filteredApps.get(0);
        }
        return null;
    }

    private class AppListAdapter extends BaseAdapter {
        @Override
        public int getCount() { return filteredApps.size(); }
        @Override
        public Object getItem(int pos) { return filteredApps.get(pos); }
        @Override
        public long getItemId(int pos) { return pos; }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            TextView tv = (TextView) convertView;
            Context ctx = getContext();
            if (tv == null) {
                tv = new TextView(ctx);
                tv.setTextSize(12);
                tv.setTypeface(Typeface.MONOSPACE);
                tv.setTextColor(Color.WHITE);
                tv.setPadding(UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 14));
                tv.setBackground(createItemDrawable());
            }

            final AppEntry app = filteredApps.get(pos);
            tv.setText(app.label);

            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(app.packageName);
                    if (launchIntent != null) {
                        getContext().startActivity(launchIntent);
                    }
                }
            });

            return tv;
        }
    }
}
