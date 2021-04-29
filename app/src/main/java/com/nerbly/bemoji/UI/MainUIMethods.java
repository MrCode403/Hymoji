package com.nerbly.bemoji.UI;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class MainUIMethods {

    //views shape and shadows

    public static void RippleEffects(final String color, final View view) {
        android.content.res.ColorStateList clr = new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor(color)});
        android.graphics.drawable.RippleDrawable ripdr = new android.graphics.drawable.RippleDrawable(clr, null, null);
        view.setBackground(ripdr);
    }

    public static void setViewRadius(final View view, final double radius, final String color) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setColor(Color.parseColor("#" + color.replace("#", "")));
        gd.setCornerRadius((int) radius);
        view.setBackground(gd);
    }

    public static void setClippedView(final View view, final String color, final double radius, final double elevation) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setColor(Color.parseColor(color));
        gd.setCornerRadius((int) radius);
        view.setBackground(gd);
        view.setElevation((int) elevation);
        view.setClipToOutline(true);
    }

    public static void rippleRoundStroke(final View view, final String focus, final String pressed, final double round, final double stroke, final String strokeclr) {
        android.graphics.drawable.GradientDrawable GG = new android.graphics.drawable.GradientDrawable();
        GG.setColor(Color.parseColor(focus));
        GG.setCornerRadius((float) round);
        GG.setStroke((int) stroke,
                Color.parseColor("#" + strokeclr.replace("#", "")));
        android.graphics.drawable.RippleDrawable RE = new android.graphics.drawable.RippleDrawable(new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor(pressed)}), GG, null);
        view.setBackground(RE);
    }

    public static void setImageViewRipple(final ImageView imageview, final String color1, final String color2) {
        imageview.setImageTintList(new android.content.res.ColorStateList(new int[][]{{-android.R.attr.state_pressed}, {android.R.attr.state_pressed}}, new int[]{Color.parseColor(color1), Color.parseColor(color2)}));
    }

    public static void advancedCorners(final View view, final String color, final double n1, final double n2, final double n3, final double n4) {
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

    public static void transparentStatusBar(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static void NavStatusBarColor(final String color1, final String color2, Activity activity) {
        Window w = activity.getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(Color.parseColor("#" + color1.replace("#", "")));
        w.setNavigationBarColor(Color.parseColor("#" + color2.replace("#", "")));
    }

    public static void LIGHT_ICONS(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(0);
    }

    public static void DARK_ICONS(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    //animations

    public static void shadAnim(final View view, final String propertyName, final double value, final double duration) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(view);
        anim.setPropertyName(propertyName);
        anim.setFloatValues((float) value);
        anim.setDuration((long) duration);
        anim.start();
    }

    public static void numbersAnimator(final TextView textview, final double from, final double to, final double duration) {
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

    public static void changeActivityFont(final String fontname, Activity activity) {
        String activityFontName = fontname.trim();
        if (activityFontName.contains(".ttf")) {
            activityFontName = activityFontName.replace(".ttf", "");
        }
        overrideFonts(activityFontName, activity, activity.getWindow().getDecorView());
    }

    public static void overrideFonts(String font, final android.content.Context context, final View v) {
        try {
            Typeface activityTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/" + font + ".ttf");
            if ((v instanceof ViewGroup)) {
                ViewGroup activityFontGroup = (ViewGroup) v;
                for (int i = 0;
                     i < activityFontGroup.getChildCount();
                     i++) {
                    View child = activityFontGroup.getChildAt(i);
                    overrideFonts(font, context, child);
                }
            } else {
                if ((v instanceof TextView)) {
                    ((TextView) v).setTypeface(activityTypeFace);
                } else {
                    if ((v instanceof EditText)) {
                        ((EditText) v).setTypeface(activityTypeFace);
                    } else {
                        if ((v instanceof Switch)) {
                            ((Switch) v).setTypeface(activityTypeFace);
                        } else {
                            if ((v instanceof CheckBox)) {
                                ((CheckBox) v).setTypeface(activityTypeFace);
                            } else {
                                if ((v instanceof Button)) {
                                    ((Button) v).setTypeface(activityTypeFace);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}

