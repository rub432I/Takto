package com.takto.launcher.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.takto.launcher.util.AppExecutors;
import com.takto.launcher.util.UiUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * MetroDashboardView: Left-page live widget dashboard.
 * Displays live clock, date, aggregate daily metrics, quick launch tiles, and active tasks.
 */
public class MetroDashboardView extends ScrollView {

    public interface OnTaskActionListener {
        void onTaskToggled(long taskId, String content, boolean isDone);
        void onTaskDeleted(long taskId, String content);
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
        Context ctx = getContext();
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setStroke(UiUtils.dpToPx(ctx, 1), Color.parseColor(borderColor));
        gd.setCornerRadius(UiUtils.dpToPx(ctx, 8));
        return gd;
    }

    private void init() {
        Context ctx = getContext();
        setVerticalScrollBarEnabled(false);
        setBackgroundColor(Color.BLACK);

        LinearLayout contentLayout = new LinearLayout(ctx);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 90));

        // 1. Clock & Date
        clockView = new TextView(ctx);
        clockView.setText(timeFormat.format(new Date()));
        clockView.setTextSize(40);
        clockView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        clockView.setTextColor(Color.parseColor("#FFC86B"));
        contentLayout.addView(clockView);

        dateView = new TextView(ctx);
        dateView.setText(dateFormat.format(new Date()).toUpperCase(Locale.US));
        dateView.setTextSize(10);
        dateView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        dateView.setTextColor(Color.parseColor("#8892B0"));
        dateView.setPadding(0, UiUtils.dpToPx(ctx, 2), 0, UiUtils.dpToPx(ctx, 14));
        contentLayout.addView(dateView);

        // 2. Metrics Row (3-Column)
        LinearLayout statsRow = new LinearLayout(ctx);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setWeightSum(3.0f);

        statNotesView = createStatCard("NOTES", "0", "#FFC86B");
        statTasksView = createStatCard("TASKS", "0", "#50FA7B");
        statExpensesView = createStatCard("EXPENSE", "$0", "#BD93F9");

        int m = UiUtils.dpToPx(ctx, 4);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        p1.setMargins(0, 0, m, 0);
        statsRow.addView(statNotesView, p1);

        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        p2.setMargins(m, 0, m, 0);
        statsRow.addView(statTasksView, p2);

        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        p3.setMargins(m, 0, 0, 0);
        statsRow.addView(statExpensesView, p3);

        contentLayout.addView(statsRow);

        // 3. Quick Action Tiles Row (Termux, Logseq)
        TextView quickTitle = new TextView(ctx);
        quickTitle.setText("// QUICK LAUNCH");
        quickTitle.setTextSize(10);
        quickTitle.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        quickTitle.setTextColor(Color.parseColor("#DF9E38"));
        quickTitle.setPadding(0, UiUtils.dpToPx(ctx, 16), 0, UiUtils.dpToPx(ctx, 6));
        contentLayout.addView(quickTitle);

        LinearLayout quickRow = new LinearLayout(ctx);
        quickRow.setOrientation(LinearLayout.HORIZONTAL);
        quickRow.setWeightSum(2.0f);

        View termuxBtn = createActionTile("TERMUX", "com.termux");
        View logseqBtn = createActionTile("LOGSEQ", "org.logseq");

        LinearLayout.LayoutParams q1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        q1.setMargins(0, 0, m, 0);
        quickRow.addView(termuxBtn, q1);

        LinearLayout.LayoutParams q2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        q2.setMargins(m, 0, 0, 0);
        quickRow.addView(logseqBtn, q2);

        contentLayout.addView(quickRow);

        // 4. Active Tasks Container
        TextView taskSectionTitle = new TextView(ctx);
        taskSectionTitle.setText("// ACTIVE TASKS (- TODO)");
        taskSectionTitle.setTextSize(10);
        taskSectionTitle.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        taskSectionTitle.setTextColor(Color.parseColor("#50FA7B"));
        taskSectionTitle.setPadding(0, UiUtils.dpToPx(ctx, 16), 0, UiUtils.dpToPx(ctx, 6));
        contentLayout.addView(taskSectionTitle);

        taskContainer = new LinearLayout(ctx);
        taskContainer.setOrientation(LinearLayout.VERTICAL);
        taskContainer.setBackground(createCardDrawable("#080A0E", "#1E2630"));
        taskContainer.setPadding(UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 14));
        contentLayout.addView(taskContainer);

        addView(contentLayout);
    }

    private TextView createStatCard(String label, String value, String colorHex) {
        Context ctx = getContext();
        TextView tv = new TextView(ctx);
        tv.setText(label + "\n" + value);
        tv.setTextSize(10);
        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv.setTextColor(Color.parseColor(colorHex));
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(UiUtils.dpToPx(ctx, 10), UiUtils.dpToPx(ctx, 12), UiUtils.dpToPx(ctx, 10), UiUtils.dpToPx(ctx, 12));
        tv.setBackground(createCardDrawable("#0E1218", "#1E2630"));
        return tv;
    }

    private View createActionTile(final String label, final String packageName) {
        Context ctx = getContext();
        TextView tv = new TextView(ctx);
        tv.setText(label);
        tv.setTextSize(11);
        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(UiUtils.dpToPx(ctx, 12), UiUtils.dpToPx(ctx, 12), UiUtils.dpToPx(ctx, 12), UiUtils.dpToPx(ctx, 12));
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

    public void refreshData(final TaktoDbHelper db) {
        clockView.setText(timeFormat.format(new Date()));
        dateView.setText(dateFormat.format(new Date()).toUpperCase(Locale.US));

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final TaktoDbHelper.Stats stats = db.getTodayStats();
                final List<TaktoDbHelper.Entry> tasks = db.getActiveTasks();

                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        statNotesView.setText("NOTES\n" + stats.noteCount);
                        statTasksView.setText("TASKS\n" + stats.activeTaskCount);
                        statExpensesView.setText("EXPENSE\n$" + (long) stats.expenseTotal);

                        taskContainer.removeAllViews();
                        if (tasks.isEmpty()) {
                            TextView emptyTv = new TextView(getContext());
                            emptyTv.setText("No active tasks.\nType 't <task>' below to capture.");
                            emptyTv.setTextSize(11);
                            emptyTv.setTypeface(Typeface.MONOSPACE);
                            emptyTv.setTextColor(Color.parseColor("#8892B0"));
                            emptyTv.setPadding(UiUtils.dpToPx(getContext(), 4), UiUtils.dpToPx(getContext(), 8), 0, 0);
                            taskContainer.addView(emptyTv);
                        } else {
                            for (final TaktoDbHelper.Entry t : tasks) {
                                LinearLayout taskRow = new LinearLayout(getContext());
                                taskRow.setOrientation(LinearLayout.HORIZONTAL);
                                taskRow.setGravity(Gravity.CENTER_VERTICAL);
                                taskRow.setPadding(0, UiUtils.dpToPx(getContext(), 6), 0, UiUtils.dpToPx(getContext(), 6));

                                CheckBox cb = new CheckBox(getContext());
                                cb.setChecked(t.isDone());
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (taskListener != null) {
                                            taskListener.onTaskToggled(t.id, t.content, isChecked);
                                        }
                                    }
                                });
                                taskRow.addView(cb);

                                TextView taskText = new TextView(getContext());
                                taskText.setText(t.content);
                                taskText.setTextSize(12);
                                taskText.setTypeface(Typeface.MONOSPACE);
                                taskText.setTextColor(Color.WHITE);
                                taskText.setPadding(UiUtils.dpToPx(getContext(), 6), 0, 0, 0);
                                taskRow.addView(taskText);

                                taskRow.setOnLongClickListener(new OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        showDeleteTaskDialog(t);
                                        return true;
                                    }
                                });

                                taskContainer.addView(taskRow);
                            }
                        }
                    }
                });
            }
        });
    }

    private void showDeleteTaskDialog(final TaktoDbHelper.Entry task) {
        new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Delete Task")
                .setMessage("Delete task from SQLite and Logseq?\n\n\"" + task.content + "\"")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (taskListener != null) {
                            taskListener.onTaskDeleted(task.id, task.content);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
