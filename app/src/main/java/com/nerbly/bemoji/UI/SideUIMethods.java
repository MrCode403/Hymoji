package com.nerbly.bemoji.UI;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SideUIMethods {
    public static void shadBackground(View view) {
        GradientDrawable JJACCAI = new GradientDrawable();
        int[] JJACCAIADD = new int[]{Color.parseColor("#00000000"), Color.parseColor("#80000000")};
        JJACCAI.setColors(JJACCAIADD);
        JJACCAI.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        JJACCAI.setCornerRadius(0);
        view.setBackground(JJACCAI);
    }


    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static void marqueeTextView(final TextView view) {
        view.setSingleLine(true);
        view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        view.setSelected(true);
        view.setMarqueeRepeatLimit(-1);
        view.setHorizontalFadingEdgeEnabled(true);
        view.setFadingEdgeLength(20);
    }
}
