package cn.edu.cqust.easymenu.utils;

import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class InsetUtils {

    private InsetUtils() {}

    public static void applySystemBarInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int typeMask = WindowInsetsCompat.Type.systemBars();

            int left = insets.getInsets(typeMask).left;
            int top = insets.getInsets(typeMask).top;
            int right = insets.getInsets(typeMask).right;
            int bottom = insets.getInsets(typeMask).bottom;

            v.setPadding(left, top, right, bottom);
            return insets;
        });
    }
}
