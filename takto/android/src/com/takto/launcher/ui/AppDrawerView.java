package com.takto.launcher.ui;

import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * AppDrawerView: Right page application registry & fast launcher.
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

    public AppDrawerView(Context context) {
        super(context);
        init();
    }

    private GradientDrawable createItemDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#080A0E"));
        gd.setStroke(1, Color.parseColor("#151B23"));
        gd.setCornerRadius(6f);
        return gd;
    }

    private void init() {
        setOrientation(VERTICAL);
        setBackgroundColor(Color.BLACK);
        setPadding(32, 24, 32, 120);

        TextView header = new TextView(getContext());
        header.setText("// APPLICATION REGISTRY");
        header.setTextSize(11);
        header.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        header.setTextColor(Color.parseColor("#DF9E38"));
        header.setPadding(0, 0, 0, 16);
        addView(header);

        listView = new ListView(getContext());
        listView.setDivider(null);
        listView.setDividerHeight(10);
        listView.setVerticalScrollBarEnabled(false);

        adapter = new AppListAdapter();
        listView.setAdapter(adapter);
        addView(listView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        reloadApps();
    }

    public void reloadApps() {
        PackageManager pm = getContext().getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
        allApps.clear();

        for (ResolveInfo info : pkgAppsList) {
            if (info.activityInfo.packageName.equals(getContext().getPackageName())) {
                continue;
            }
            AppEntry entry = new AppEntry();
            entry.label = info.loadLabel(pm).toString();
            entry.packageName = info.activityInfo.packageName;
            entry.activityName = info.activityInfo.name;
            allApps.add(entry);
        }

        Collections.sort(allApps, new Comparator<AppEntry>() {
            @Override
            public int compare(AppEntry a, AppEntry b) {
                return a.label.compareToIgnoreCase(b.label);
            }
        });

        filter(null);
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
            if (tv == null) {
                tv = new TextView(getContext());
                tv.setTextSize(13);
                tv.setTypeface(Typeface.MONOSPACE);
                tv.setTextColor(Color.WHITE);
                tv.setPadding(24, 18, 24, 18);
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
