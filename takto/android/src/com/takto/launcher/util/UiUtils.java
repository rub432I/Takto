package com.takto.launcher.util;

import android.content.Context;

/**
 * UiUtils: Screen density conversion utilities.
 * Ensures consistent layout sizing across varying device DPI scales.
 */
public class UiUtils {

    public static int dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
