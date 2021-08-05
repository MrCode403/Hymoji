package com.nerbly.bemoji.UI;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

public class SideUIMethods {
    public static void shadBackground(View view) {
        android.graphics.drawable.GradientDrawable JJACCAI = new android.graphics.drawable.GradientDrawable();
        int[] JJACCAIADD = new int[]{Color.parseColor("#00000000"), Color.parseColor("#80000000")};
        JJACCAI.setColors(JJACCAIADD);
        JJACCAI.setOrientation(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM);
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


}
