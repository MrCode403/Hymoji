package com.nerbly.bemoji.UI;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainUIMethods {

    //views shape and shadows

    public static void RippleEffects(String color, View view) {
        android.content.res.ColorStateList clr = new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor(color)});
        android.graphics.drawable.RippleDrawable ripdr = new android.graphics.drawable.RippleDrawable(clr, null, null);
        view.setBackground(ripdr);
    }

    public static void setViewRadius(View view, double radius, String color) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setColor(Color.parseColor("#" + color.replace("#", "")));
        gd.setCornerRadius((int) radius);
        view.setBackground(gd);
    }

    public static void setClippedView(View view, String color, double radius, double elevation) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setColor(Color.parseColor(color));
        gd.setCornerRadius((int) radius);
        view.setBackground(gd);
        view.setElevation((int) elevation);
        view.setClipToOutline(true);
    }

    public static void rippleRoundStroke(View view, String focus, String pressed, double round, double stroke, String strokeclr) {
        android.graphics.drawable.GradientDrawable GG = new android.graphics.drawable.GradientDrawable();
        GG.setColor(Color.parseColor(focus));
        GG.setCornerRadius((float) round);
        GG.setStroke((int) stroke,
                Color.parseColor("#" + strokeclr.replace("#", "")));
        android.graphics.drawable.RippleDrawable RE = new android.graphics.drawable.RippleDrawable(new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor(pressed)}), GG, null);
        view.setBackground(RE);
    }

    public static void setImageViewRipple(ImageView imageview, String color1, String color2) {
        imageview.setImageTintList(new android.content.res.ColorStateList(new int[][]{{-android.R.attr.state_pressed}, {android.R.attr.state_pressed}}, new int[]{Color.parseColor(color1), Color.parseColor(color2)}));
    }

    public static void advancedCorners(View view, String color, double n1, double n2, double n3, double n4) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setColor(Color.parseColor(color));
        gd.setCornerRadii(new float[]{(int) n1, (int) n1, (int) n2, (int) n2, (int) n4, (int) n4, (int) n3, (int) n3});
        view.setBackground(gd);
    }

    //status bar customizations

    public static void transparentStatusNavBar(Activity activity) {
        Window w = activity.getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(0xFF008375);
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void transparentStatusBar(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void NavStatusBarColor(String color1, String color2, Activity activity) {
        Window w = activity.getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(Color.parseColor("#" + color1.replace("#", "")));
        w.setNavigationBarColor(Color.parseColor("#" + color2.replace("#", "")));
    }

    public static void statusBarColor(String color, Activity activity) {
        Window w = activity.getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(Color.parseColor("#" + color.replace("#", "")));
    }

    public static void LIGHT_ICONS(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(0);
    }

    public static void DARK_ICONS(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    //animations

    public static void shadAnim(View view, String propertyName, double value, double duration) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(view);
        anim.setPropertyName(propertyName);
        anim.setFloatValues((float) value);
        anim.setDuration((long) duration);
        anim.start();
    }

    public static void numbersAnimator(final TextView textview, double from, double to, double duration) {
        ValueAnimator animator = ValueAnimator.ofInt((int) from, (int) to);
        animator.setDuration((int) duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textview.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }

    //mics
}

