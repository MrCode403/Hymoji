package com.nerbly.bemoji.UI;

import android.graphics.Color;
import android.view.View;

public class SideUIMethods {
    public static void shadBackground(View view) {
        android.graphics.drawable.GradientDrawable JJACCAI = new android.graphics.drawable.GradientDrawable();
        int[] JJACCAIADD = new int[]{Color.parseColor("#00000000"), Color.parseColor("#80000000")};
        JJACCAI.setColors(JJACCAIADD);
        JJACCAI.setOrientation(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM);
        JJACCAI.setCornerRadius(0);
        view.setBackground(JJACCAI);
    }
}
