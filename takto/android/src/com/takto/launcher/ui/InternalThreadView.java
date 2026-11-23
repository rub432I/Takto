package com.takto.launcher.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.takto.launcher.db.TaktoDbHelper;
import com.takto.launcher.util.AppExecutors;
import com.takto.launcher.util.UiUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * InternalThreadView: Center page personal stream with filter chips,
 * direct task status toggles, and deletion support.
 */
public class InternalThreadView extends ScrollView {

    public interface OnThreadActionListener {
        void onTaskToggled(long taskId, String content, boolean isDone);
        void onEntryDeleted(long entryId, String type, String content);
    }

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
    private LinearLayout streamContainer;
    private OnThreadActionListener actionListener;
    private String currentFilter = "ALL"; // ALL, NOTES, TASKS, EXPENSES
    private List<TaktoDbHelper.Entry> cachedEntries = new ArrayList<>();

    private TextView chipAll;
    private TextView chipNotes;
    private TextView chipTasks;
    private TextView chipExpenses;

    public InternalThreadView(Context context) {
        super(context);
        init();
    }

    public void setOnThreadActionListener(OnThreadActionListener listener) {
        this.actionListener = listener;
    }

    private GradientDrawable createCardDrawable(String bgColor, String borderColor) {
        Context ctx = getContext();
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setStroke(UiUtils.dpToPx(ctx, 1), Color.parseColor(borderColor));
        gd.setCornerRadius(UiUtils.dpToPx(ctx, 6));
        return gd;
    }

    private GradientDrawable createChipDrawable(boolean active) {
        Context ctx = getContext();
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(active ? "#FFC86B" : "#0E1218"));
        gd.setStroke(UiUtils.dpToPx(ctx, 1), Color.parseColor(active ? "#FFC86B" : "#1E2630"));
        gd.setCornerRadius(UiUtils.dpToPx(ctx, 4));
        return gd;
    }

    private void init() {
        Context ctx = getContext();
        setVerticalScrollBarEnabled(false);
        setBackgroundColor(Color.BLACK);

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 90));

        // Header
        TextView header = new TextView(ctx);
        header.setText("// INTERNAL THREAD // TODAY'S STREAM");
        header.setTextSize(10);
        header.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        header.setTextColor(Color.parseColor("#FFC86B"));
        header.setPadding(0, 0, 0, UiUtils.dpToPx(ctx, 10));
        root.addView(header);

        // Filter Chips Row
        LinearLayout chipsRow = new LinearLayout(ctx);
        chipsRow.setOrientation(LinearLayout.HORIZONTAL);
        chipsRow.setPadding(0, 0, 0, UiUtils.dpToPx(ctx, 12));

        chipAll = createFilterChip("ALL", "ALL");
        chipNotes = createFilterChip("NOTES", "NOTES");
        chipTasks = createFilterChip("TASKS", "TASKS");
        chipExpenses = createFilterChip("EXPENSES", "EXPENSES");

        chipsRow.addView(chipAll);
        chipsRow.addView(chipNotes);
        chipsRow.addView(chipTasks);
        chipsRow.addView(chipExpenses);
        root.addView(chipsRow);

        // Stream container
        streamContainer = new LinearLayout(ctx);
        streamContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(streamContainer);

        addView(root);
        updateChipStyles();
    }

    private TextView createFilterChip(String label, final String filterKey) {
        Context ctx = getContext();
        TextView tv = new TextView(ctx);
        tv.setText(label);
        tv.setTextSize(10);
        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv.setPadding(UiUtils.dpToPx(ctx, 10), UiUtils.dpToPx(ctx, 6), UiUtils.dpToPx(ctx, 10), UiUtils.dpToPx(ctx, 6));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, UiUtils.dpToPx(ctx, 8), 0);
        tv.setLayoutParams(lp);

        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFilter = filterKey;
                updateChipStyles();
                renderFilteredCards();
            }
        });
        return tv;
    }

    private void updateChipStyles() {
        chipAll.setBackground(createChipDrawable("ALL".equals(currentFilter)));
        chipAll.setTextColor(Color.parseColor("ALL".equals(currentFilter) ? "#000000" : "#8892B0"));

        chipNotes.setBackground(createChipDrawable("NOTES".equals(currentFilter)));
        chipNotes.setTextColor(Color.parseColor("NOTES".equals(currentFilter) ? "#000000" : "#8892B0"));

        chipTasks.setBackground(createChipDrawable("TASKS".equals(currentFilter)));
        chipTasks.setTextColor(Color.parseColor("TASKS".equals(currentFilter) ? "#000000" : "#8892B0"));

        chipExpenses.setBackground(createChipDrawable("EXPENSES".equals(currentFilter)));
        chipExpenses.setTextColor(Color.parseColor("EXPENSES".equals(currentFilter) ? "#000000" : "#8892B0"));
    }

    public void refreshData(final TaktoDbHelper db) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<TaktoDbHelper.Entry> entries = db.getTodayEntries();

                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        cachedEntries = entries;
                        renderFilteredCards();
                    }
                });
            }
        });
    }

    private void renderFilteredCards() {
        streamContainer.removeAllViews();
        Context ctx = getContext();

        List<TaktoDbHelper.Entry> displayed = new ArrayList<>();
        for (TaktoDbHelper.Entry e : cachedEntries) {
            if ("NOTES".equals(currentFilter) && !"journal".equalsIgnoreCase(e.type)) continue;
            if ("TASKS".equals(currentFilter) && !"task".equalsIgnoreCase(e.type)) continue;
            if ("EXPENSES".equals(currentFilter) && !"expense".equalsIgnoreCase(e.type)) continue;
            displayed.add(e);
        }

        if (displayed.isEmpty()) {
            TextView emptyTv = new TextView(ctx);
            emptyTv.setText("No entries matching filter: " + currentFilter + "\n\nQuick Prefixes:\n  j <note>    Journal thought\n  t <task>    - TODO item\n  $ <amount>  Expense item");
            emptyTv.setTextSize(11);
            emptyTv.setTypeface(Typeface.MONOSPACE);
            emptyTv.setTextColor(Color.parseColor("#8892B0"));
            emptyTv.setPadding(UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 16));
            emptyTv.setBackground(createCardDrawable("#0E1218", "#1E2630"));
            streamContainer.addView(emptyTv);
            return;
        }

        for (final TaktoDbHelper.Entry entry : displayed) {
            LinearLayout card = new LinearLayout(ctx);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 12), UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 12));
            card.setBackground(createCardDrawable("#0E1218", "#1E2630"));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, UiUtils.dpToPx(ctx, 8));
            card.setLayoutParams(lp);

            // Header row: [TAG] HH:mm:ss
            LinearLayout tagRow = new LinearLayout(ctx);
            tagRow.setOrientation(LinearLayout.HORIZONTAL);

            TextView tagView = new TextView(ctx);
            tagView.setTextSize(9);
            tagView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

            if ("task".equalsIgnoreCase(entry.type)) {
                tagView.setText(entry.isDone() ? "[TASK // DONE]" : "[TASK // TODO]");
                tagView.setTextColor(entry.isDone() ? Color.parseColor("#8892B0") : Color.parseColor("#50FA7B"));
            } else if ("expense".equalsIgnoreCase(entry.type)) {
                tagView.setText("[EXPENSE // #expense]");
                tagView.setTextColor(Color.parseColor("#BD93F9"));
            } else {
                tagView.setText("[JOURNAL // STREAM]");
                tagView.setTextColor(Color.parseColor("#FFC86B"));
            }
            tagRow.addView(tagView);

            TextView timeView = new TextView(ctx);
            timeView.setText("  " + timeFormat.format(new Date(entry.timestamp)));
            timeView.setTextSize(9);
            timeView.setTypeface(Typeface.MONOSPACE);
            timeView.setTextColor(Color.parseColor("#555555"));
            tagRow.addView(timeView);

            card.addView(tagRow);

            // Body text
            TextView bodyView = new TextView(ctx);
            bodyView.setText(entry.content);
            bodyView.setTextSize(12);
            bodyView.setTypeface(Typeface.MONOSPACE);
            bodyView.setTextColor(entry.isDone() ? Color.parseColor("#777777") : Color.WHITE);
            bodyView.setPadding(0, UiUtils.dpToPx(ctx, 6), 0, 0);
            card.addView(bodyView);

            // Tap task to toggle status
            if ("task".equalsIgnoreCase(entry.type)) {
                card.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (actionListener != null) {
                            actionListener.onTaskToggled(entry.id, entry.content, !entry.isDone());
                        }
                    }
                });
            }

            // Long-press to delete entry
            card.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteConfirmDialog(entry);
                    return true;
                }
            });

            streamContainer.addView(card);
        }
    }

    private void showDeleteConfirmDialog(final TaktoDbHelper.Entry entry) {
        new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Delete Entry")
                .setMessage("Delete this " + entry.type + " from SQLite and today's Logseq file?\n\n\"" + entry.content + "\"")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (actionListener != null) {
                            actionListener.onEntryDeleted(entry.id, entry.type, entry.content);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
