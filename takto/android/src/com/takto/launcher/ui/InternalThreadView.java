package com.takto.launcher.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.takto.launcher.db.TaktoDbHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * InternalThreadView: Center page reverse SMS canvas & personal journal stream.
 * Displays today's captured thoughts, tasks, and expenses in brutalist cards.
 */
public class InternalThreadView extends ScrollView {

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
    private LinearLayout streamContainer;

    public InternalThreadView(Context context) {
        super(context);
        init();
    }

    private GradientDrawable createCardDrawable(String bgColor, String borderColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setStroke(1, Color.parseColor(borderColor));
        gd.setCornerRadius(8f);
        return gd;
    }

    private void init() {
        setVerticalScrollBarEnabled(false);
        setBackgroundColor(Color.BLACK);

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 24, 32, 120);

        TextView header = new TextView(getContext());
        header.setText("// INTERNAL THREAD // TODAY'S LOG");
        header.setTextSize(11);
        header.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        header.setTextColor(Color.parseColor("#FFC86B"));
        header.setPadding(0, 0, 0, 16);
        root.addView(header);

        streamContainer = new LinearLayout(getContext());
        streamContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(streamContainer);

        addView(root);
    }

    public void refreshData(TaktoDbHelper db) {
        streamContainer.removeAllViews();
        List<TaktoDbHelper.Entry> entries = db.getTodayEntries();

        if (entries.isEmpty()) {
            TextView emptyTv = new TextView(getContext());
            emptyTv.setText("Today's stream is empty.\n\nQuick Commands:\n  j <note>    Log journal thought\n  t <task>    Add - TODO item\n  $ <amount>  Log expense\n  <math>      Calculate instantly");
            emptyTv.setTextSize(11);
            emptyTv.setTypeface(Typeface.MONOSPACE);
            emptyTv.setTextColor(Color.parseColor("#8892B0"));
            emptyTv.setPadding(16, 24, 16, 24);
            emptyTv.setBackground(createCardDrawable("#0E1218", "#1E2630"));
            streamContainer.addView(emptyTv);
            return;
        }

        for (TaktoDbHelper.Entry entry : entries) {
            LinearLayout card = new LinearLayout(getContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(20, 16, 20, 16);
            card.setBackground(createCardDrawable("#0E1218", "#1E2630"));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 12);
            card.setLayoutParams(lp);

            // Header line: [TAG] HH:mm:ss
            LinearLayout tagRow = new LinearLayout(getContext());
            tagRow.setOrientation(LinearLayout.HORIZONTAL);

            TextView tagView = new TextView(getContext());
            tagView.setTextSize(10);
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

            TextView timeView = new TextView(getContext());
            timeView.setText("  " + timeFormat.format(new Date(entry.timestamp)));
            timeView.setTextSize(10);
            timeView.setTypeface(Typeface.MONOSPACE);
            timeView.setTextColor(Color.parseColor("#555555"));
            tagRow.addView(timeView);

            card.addView(tagRow);

            // Content text
            TextView bodyView = new TextView(getContext());
            bodyView.setText(entry.content);
            bodyView.setTextSize(13);
            bodyView.setTypeface(Typeface.MONOSPACE);
            bodyView.setTextColor(Color.WHITE);
            bodyView.setPadding(0, 8, 0, 0);
            card.addView(bodyView);

            streamContainer.addView(card);
        }
    }
}
