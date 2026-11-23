package com.takto.launcher.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.takto.launcher.db.TaktoDbHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * MetroDashboardView: Left-page live widget dashboard
 * Minimalist, high-contrast, information-dense brutalist cockpit.
 */
public class MetroDashboardView extends ScrollView {

    public interface OnTaskActionListener {
        void onTaskToggled(long taskId, boolean isDone);
    }

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);

    private TextView clockView;
    private TextView dateView;
    private TextView statNotesView;
    private TextView statTasksView;
    private TextView statExpensesView;
    private LinearLayout taskContainer;
    private OnTaskActionListener taskListener;

    public MetroDashboardView(Context context) {
        super(context);
        init();
    }

    public void setOnTaskActionListener(OnTaskActionListener listener) {
        this.taskListener = listener;
    }

    private GradientDrawable createCardDrawable(String bgColor, String borderColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setStroke(2, Color.parseColor(borderColor));
        gd.setCornerRadius(10f);
        return gd;
    }

    private void init() {
        setVerticalScrollBarEnabled(false);
        setBackgroundColor(Color.BLACK);

        LinearLayout contentLayout = new LinearLayout(getContext());
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(32, 24, 32, 120);

        // 1. Clock & Date
        clockView = new TextView(getContext());
        clockView.setText(timeFormat.format(new Date()));
        clockView.setTextSize(44);
        clockView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        clockView.setTextColor(Color.parseColor("#FFC86B")); // Takto Gold
        contentLayout.addView(clockView);

        dateView = new TextView(getContext());
        dateView.setText(dateFormat.format(new Date()).toUpperCase(Locale.US));
        dateView.setTextSize(11);
        dateView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        dateView.setTextColor(Color.parseColor("#8892B0"));
        dateView.setPadding(0, 4, 0, 24);
        contentLayout.addView(dateView);

        // 2. Metrics Row (3-Column Live Metric Tiles)
        LinearLayout statsRow = new LinearLayout(getContext());
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setWeightSum(3.0f);

        statNotesView = createStatCard("NOTES", "0", "#FFC86B");
        statTasksView = createStatCard("TASKS", "0", "#50FA7B");
        statExpensesView = createStatCard("EXPENSE", "$0", "#BD93F9");

        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        p1.setMargins(0, 0, 8, 0);
        statsRow.addView(statNotesView, p1);

        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        p2.setMargins(4, 0, 4, 0);
        statsRow.addView(statTasksView, p2);

        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        p3.setMargins(8, 0, 0, 0);
        statsRow.addView(statExpensesView, p3);

        contentLayout.addView(statsRow);

        // 3. Quick Action Tiles Row (Termux, Logseq)
        TextView quickTitle = new TextView(getContext());
        quickTitle.setText("// SYSTEM QUICK LAUNCH");
        quickTitle.setTextSize(10);
        quickTitle.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        quickTitle.setTextColor(Color.parseColor("#DF9E38"));
        quickTitle.setPadding(0, 24, 0, 8);
        contentLayout.addView(quickTitle);

        LinearLayout quickRow = new LinearLayout(getContext());
        quickRow.setOrientation(LinearLayout.HORIZONTAL);
        quickRow.setWeightSum(2.0f);

        View termuxBtn = createActionTile("TERMUX", "com.termux");
        View logseqBtn = createActionTile("LOGSEQ", "org.logseq");

        LinearLayout.LayoutParams q1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        q1.setMargins(0, 0, 8, 0);
        quickRow.addView(termuxBtn, q1);

        LinearLayout.LayoutParams q2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        q2.setMargins(8, 0, 0, 0);
        quickRow.addView(logseqBtn, q2);

        contentLayout.addView(quickRow);

        // 4. Active Tasks Container
        TextView taskSectionTitle = new TextView(getContext());
        taskSectionTitle.setText("// ACTIVE TASKS (- TODO)");
        taskSectionTitle.setTextSize(10);
        taskSectionTitle.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        taskSectionTitle.setTextColor(Color.parseColor("#50FA7B"));
        taskSectionTitle.setPadding(0, 24, 0, 8);
        contentLayout.addView(taskSectionTitle);

        taskContainer = new LinearLayout(getContext());
        taskContainer.setOrientation(LinearLayout.VERTICAL);
        taskContainer.setBackground(createCardDrawable("#080A0E", "#1E2630"));
        taskContainer.setPadding(20, 20, 20, 20);
        contentLayout.addView(taskContainer);

        addView(contentLayout);
    }

    private TextView createStatCard(String label, String value, String colorHex) {
        TextView tv = new TextView(getContext());
        tv.setText(label + "\n" + value);
        tv.setTextSize(11);
        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv.setTextColor(Color.parseColor(colorHex));
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(16, 20, 16, 20);
        tv.setBackground(createCardDrawable("#0E1218", "#1E2630"));
        return tv;
    }

    private View createActionTile(final String label, final String packageName) {
        TextView tv = new TextView(getContext());
        tv.setText(label);
        tv.setTextSize(12);
        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(20, 22, 20, 22);
        tv.setBackground(createCardDrawable("#0E1218", "#FFC86B"));

        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    getContext().startActivity(intent);
                }
            }
        });
        return tv;
    }

    public void refreshData(TaktoDbHelper db) {
        clockView.setText(timeFormat.format(new Date()));
        dateView.setText(dateFormat.format(new Date()).toUpperCase(Locale.US));

        TaktoDbHelper.Stats stats = db.getTodayStats();
        statNotesView.setText("NOTES\n" + stats.noteCount);
        statTasksView.setText("TASKS\n" + stats.activeTaskCount);
        statExpensesView.setText("EXPENSE\n$" + (long) stats.expenseTotal);

        List<TaktoDbHelper.Entry> tasks = db.getActiveTasks();
        taskContainer.removeAllViews();

        if (tasks.isEmpty()) {
            TextView emptyTv = new TextView(getContext());
            emptyTv.setText("No active tasks.\nType 't <task>' below to capture.");
            emptyTv.setTextSize(11);
            emptyTv.setTypeface(Typeface.MONOSPACE);
            emptyTv.setTextColor(Color.parseColor("#8892B0"));
            emptyTv.setPadding(8, 12, 8, 12);
            taskContainer.addView(emptyTv);
        } else {
            for (final TaktoDbHelper.Entry t : tasks) {
                LinearLayout taskRow = new LinearLayout(getContext());
                taskRow.setOrientation(LinearLayout.HORIZONTAL);
                taskRow.setGravity(Gravity.CENTER_VERTICAL);
                taskRow.setPadding(4, 10, 4, 10);

                CheckBox cb = new CheckBox(getContext());
                cb.setChecked(t.isDone());
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (taskListener != null) {
                            taskListener.onTaskToggled(t.id, isChecked);
                        }
                    }
                });
                taskRow.addView(cb);

                TextView taskText = new TextView(getContext());
                taskText.setText(t.content);
                taskText.setTextSize(12);
                taskText.setTypeface(Typeface.MONOSPACE);
                taskText.setTextColor(Color.WHITE);
                taskText.setPadding(8, 0, 0, 0);
                taskRow.addView(taskText);

                taskContainer.addView(taskRow);
            }
        }
    }
}
