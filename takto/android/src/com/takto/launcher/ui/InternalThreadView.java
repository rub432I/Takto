package com.takto.launcher.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
 * InternalThreadView: Center page personal thought & capture stream.
 * Displays today's notes, tasks, and expenses in chronological cards.
 */
public class InternalThreadView extends ScrollView {

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
    private LinearLayout streamContainer;

    public InternalThreadView(Context context) {
        super(context);
        init();
    }

    private GradientDrawable createCardDrawable(String bgColor, String borderColor) {
        Context ctx = getContext();
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setStroke(UiUtils.dpToPx(ctx, 1), Color.parseColor(borderColor));
        gd.setCornerRadius(UiUtils.dpToPx(ctx, 6));
        return gd;
    }

    private void init() {
        Context ctx = getContext();
        setVerticalScrollBarEnabled(false);
        setBackgroundColor(Color.BLACK);

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(UiUtils.dpToPx(ctx, 20), UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 20), UiUtils.dpToPx(ctx, 90));

        TextView header = new TextView(ctx);
        header.setText("// INTERNAL THREAD // TODAY'S STREAM");
        header.setTextSize(10);
        header.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        header.setTextColor(Color.parseColor("#FFC86B"));
        header.setPadding(0, 0, 0, UiUtils.dpToPx(ctx, 12));
        root.addView(header);

        streamContainer = new LinearLayout(ctx);
        streamContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(streamContainer);

        addView(root);
    }

    public void refreshData(final TaktoDbHelper db) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<TaktoDbHelper.Entry> entries = db.getTodayEntries();

                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        streamContainer.removeAllViews();
                        Context ctx = getContext();

                        if (entries.isEmpty()) {
                            TextView emptyTv = new TextView(ctx);
                            emptyTv.setText("Today's stream is empty.\n\nQuick Prefixes:\n  j <note>    Journal thought\n  t <task>    - TODO item\n  $ <amount>  Expense item\n  <math>      Offline calculation");
                            emptyTv.setTextSize(11);
                            emptyTv.setTypeface(Typeface.MONOSPACE);
                            emptyTv.setTextColor(Color.parseColor("#8892B0"));
                            emptyTv.setPadding(UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 18), UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 18));
                            emptyTv.setBackground(createCardDrawable("#0E1218", "#1E2630"));
                            streamContainer.addView(emptyTv);
                            return;
                        }

                        for (TaktoDbHelper.Entry entry : entries) {
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

                            // Body
                            TextView bodyView = new TextView(ctx);
                            bodyView.setText(entry.content);
                            bodyView.setTextSize(12);
                            bodyView.setTypeface(Typeface.MONOSPACE);
                            bodyView.setTextColor(Color.WHITE);
                            bodyView.setPadding(0, UiUtils.dpToPx(ctx, 6), 0, 0);
                            card.addView(bodyView);

                            streamContainer.addView(card);
                        }
                    }
                });
            }
        });
    }
}
