package com.takto.launcher.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.takto.launcher.util.MathEvaluator;
import com.takto.launcher.util.UiUtils;

/**
 * BottomBarView: Universal Capture Bar & Prefix Router
 * Supports trimmed prefixes (j , t , $ ), offline math calculation, and app search.
 */
public class BottomBarView extends LinearLayout {

    public interface OnBottomActionListener {
        void onCaptureEntry(String type, String content);
        void onFilterApps(String query);
        void onLaunchTopMatch();
    }

    private TextView modeIndicator;
    private EditText searchInput;
    private TextView actionButton;
    private OnBottomActionListener listener;
    private String currentMathResult = null;

    public BottomBarView(Context context) {
        super(context);
        init();
    }

    public void setOnBottomActionListener(OnBottomActionListener listener) {
        this.listener = listener;
    }

    private GradientDrawable createBarDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#0E1218"));
        gd.setStroke(UiUtils.dpToPx(getContext(), 1), Color.parseColor("#2B3545"));
        gd.setCornerRadius(UiUtils.dpToPx(getContext(), 8));
        return gd;
    }

    private GradientDrawable createButtonDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#FFC86B"));
        gd.setCornerRadius(UiUtils.dpToPx(getContext(), 6));
        return gd;
    }

    private void init() {
        Context ctx = getContext();
        setOrientation(VERTICAL);
        setBackgroundColor(Color.parseColor("#000000"));
        setPadding(UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 6), UiUtils.dpToPx(ctx, 16), UiUtils.dpToPx(ctx, 12));

        // 1. Mode indicator
        modeIndicator = new TextView(ctx);
        modeIndicator.setText("TAKTO // CAPTURE [ j / t / $ / CALC / APPS ]");
        modeIndicator.setTextSize(9);
        modeIndicator.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        modeIndicator.setTextColor(Color.parseColor("#DF9E38"));
        modeIndicator.setPadding(UiUtils.dpToPx(ctx, 4), 0, UiUtils.dpToPx(ctx, 4), UiUtils.dpToPx(ctx, 4));
        addView(modeIndicator);

        // 2. Input container
        LinearLayout inputRow = new LinearLayout(ctx);
        inputRow.setOrientation(HORIZONTAL);
        inputRow.setGravity(Gravity.CENTER_VERTICAL);
        inputRow.setBackground(createBarDrawable());
        inputRow.setPadding(UiUtils.dpToPx(ctx, 12), UiUtils.dpToPx(ctx, 4), UiUtils.dpToPx(ctx, 8), UiUtils.dpToPx(ctx, 4));

        searchInput = new EditText(ctx);
        searchInput.setHint("j note, t task, $ exp, math, or app...");
        searchInput.setHintTextColor(Color.parseColor("#555555"));
        searchInput.setTextColor(Color.WHITE);
        searchInput.setTextSize(13);
        searchInput.setTypeface(Typeface.MONOSPACE);
        searchInput.setBackground(null);
        searchInput.setSingleLine(true);
        searchInput.setImeOptions(EditorInfo.IME_ACTION_GO);

        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        inputRow.addView(searchInput, inputParams);

        // 3. Action button
        actionButton = new TextView(ctx);
        actionButton.setText("EXEC");
        actionButton.setTextSize(10);
        actionButton.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        actionButton.setTextColor(Color.BLACK);
        actionButton.setGravity(Gravity.CENTER);
        actionButton.setPadding(UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 8), UiUtils.dpToPx(ctx, 14), UiUtils.dpToPx(ctx, 8));
        actionButton.setBackground(createButtonDrawable());
        inputRow.addView(actionButton);

        addView(inputRow);
        setupEvents();
    }

    private void setupEvents() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMode(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    triggerAction();
                    return true;
                }
                return false;
            }
        });

        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerAction();
            }
        });
    }

    private void updateMode(String text) {
        String trimmed = text.trim();
        currentMathResult = null;

        if (trimmed.isEmpty()) {
            modeIndicator.setText("TAKTO // CAPTURE [ j / t / $ / CALC / APPS ]");
            modeIndicator.setTextColor(Color.parseColor("#DF9E38"));
            actionButton.setText("EXEC");
            if (listener != null) listener.onFilterApps("");
            return;
        }

        // Check trimmed prefixes
        if (trimmed.startsWith("j ") || trimmed.startsWith("j\t") || trimmed.equals("j")) {
            modeIndicator.setText("LOGSEQ // JOURNAL STREAM [APPEND]");
            modeIndicator.setTextColor(Color.parseColor("#FFC86B"));
            actionButton.setText("LOG");
            if (listener != null) listener.onFilterApps("");
        } else if (trimmed.startsWith("t ") || trimmed.startsWith("t\t") || trimmed.equals("t")) {
            modeIndicator.setText("LOGSEQ // TASK STREAM [- TODO]");
            modeIndicator.setTextColor(Color.parseColor("#50FA7B"));
            actionButton.setText("TASK");
            if (listener != null) listener.onFilterApps("");
        } else if (trimmed.startsWith("$ ") || trimmed.startsWith("$\t") || trimmed.equals("$")) {
            modeIndicator.setText("LOGSEQ // EXPENSE STREAM [- #expense]");
            modeIndicator.setTextColor(Color.parseColor("#BD93F9"));
            actionButton.setText("EXP");
            if (listener != null) listener.onFilterApps("");
        } else if (MathEvaluator.isPotentialMath(trimmed)) {
            String mathRes = MathEvaluator.evaluate(trimmed);
            if (mathRes != null) {
                currentMathResult = mathRes;
                modeIndicator.setText("CALC // RESULT: = " + mathRes);
                modeIndicator.setTextColor(Color.parseColor("#50FA7B"));
                actionButton.setText("=");
            } else {
                modeIndicator.setText("CALC // EVALUATING...");
                modeIndicator.setTextColor(Color.parseColor("#FFC86B"));
                actionButton.setText("CALC");
            }
            if (listener != null) listener.onFilterApps("");
        } else {
            modeIndicator.setText("APPLICATION // FILTER: " + trimmed);
            modeIndicator.setTextColor(Color.parseColor("#8892B0"));
            actionButton.setText("LAUNCH");
            if (listener != null) listener.onFilterApps(trimmed);
        }
    }

    private void triggerAction() {
        String raw = searchInput.getText().toString();
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) return;

        if (trimmed.startsWith("j ") || trimmed.startsWith("j\t")) {
            String content = trimmed.substring(2).trim();
            if (!content.isEmpty() && listener != null) {
                listener.onCaptureEntry("journal", content);
                clearInput();
            }
        } else if (trimmed.startsWith("t ") || trimmed.startsWith("t\t")) {
            String content = trimmed.substring(2).trim();
            if (!content.isEmpty() && listener != null) {
                listener.onCaptureEntry("task", content);
                clearInput();
            }
        } else if (trimmed.startsWith("$ ") || trimmed.startsWith("$\t")) {
            String content = trimmed.substring(2).trim();
            if (!content.isEmpty() && listener != null) {
                listener.onCaptureEntry("expense", content);
                clearInput();
            }
        } else if (currentMathResult != null) {
            searchInput.setText(currentMathResult);
            searchInput.setSelection(currentMathResult.length());
        } else {
            if (listener != null) {
                listener.onLaunchTopMatch();
            }
        }
    }

    public void clearInput() {
        searchInput.setText("");
        currentMathResult = null;
        updateMode("");
    }
}
