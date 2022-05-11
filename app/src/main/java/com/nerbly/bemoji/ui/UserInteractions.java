package com.nerbly.bemoji.ui;

import static com.nerbly.bemoji.ui.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.ui.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.ui.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.ui.MainUIMethods.shadAnim;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.nerbly.bemoji.R;

public class UserInteractions {

    public static void showCustomSnackBar(String message, Activity context) {
        Snackbar snackBarView;
        Snackbar.SnackbarLayout sblayout;
        ViewGroup parentLayout = (ViewGroup) ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);

        snackBarView = Snackbar.make(parentLayout, "", Snackbar.LENGTH_LONG);
        sblayout = (Snackbar.SnackbarLayout) snackBarView.getView();

        View inflate = context.getLayoutInflater().inflate(R.layout.snackbar, parentLayout, false);
        sblayout.setPadding(0, 0, 0, 0);
        sblayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
        LinearLayout back = inflate.findViewById(R.id.snackbar_bg);

        TextView snackbar_tv = inflate.findViewById(R.id.snackbar_text);
        setViewRadius(back, 20, "#202125");
        snackbar_tv.setText(message);
        sblayout.addView(inflate, 0);
        snackBarView.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
        snackBarView.show();
    }

    public static AlertDialog showMessageDialog(boolean cancelable, String title, String message, String positiveButtonText, String negativeButtonText, Activity context, DialogInterface.OnClickListener positiveAction, DialogInterface.OnClickListener negativeAction) {
        if (!(context).isFinishing()) {
            return new MaterialAlertDialogBuilder(context, R.style.RoundShapeTheme)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton(negativeButtonText, negativeAction)
                    .setPositiveButton(positiveButtonText, positiveAction)
                    .setCancelable(cancelable)
                    .show();
        } else {
            return null;
        }
    }

    public static void showMessageSheet(String title, int drawable, String positiveButtonText, String negativeButtonText, String message, Activity context, View.OnClickListener positiveAction, View.OnClickListener negativeAction) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.materialsheet);
        View bottomSheetView;
        bottomSheetView = context.getLayoutInflater().inflate(R.layout.infosheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

        ImageView image = bottomSheetView.findViewById(R.id.image);
        TextView infook = bottomSheetView.findViewById(R.id.infosheet_ok);
        TextView infocancel = bottomSheetView.findViewById(R.id.infosheet_cancel);
        LinearLayout infoback = bottomSheetView.findViewById(R.id.infosheet_back);
        TextView infotitle = bottomSheetView.findViewById(R.id.infosheet_title);
        TextView infosub = bottomSheetView.findViewById(R.id.infosheet_description);
        LinearLayout slider = bottomSheetView.findViewById(R.id.slider);

        infook.setText(positiveButtonText);
        infocancel.setText(negativeButtonText);
        infotitle.setText(title);
        infosub.setText(message);
        image.setImageResource(drawable);

        advancedCorners(infoback, "#ffffff", 38, 38, 0, 0);
        rippleRoundStroke(infook, "#7289DA", "#6275BB", 20, 0, "#007EEF");
        rippleRoundStroke(infocancel, "#424242", "#181818", 20, 0, "#007EEF");
        setViewRadius(slider, 180, "#BDBDBD");

        infook.setOnClickListener(positiveAction);
        infocancel.setOnClickListener(negativeAction);

        if (!context.isFinishing()) {
            bottomSheetDialog.show();
        }
    }

    public static void noEmojisFound(boolean isError, View loadView, LottieAnimationView emptyAnimation, TextView emptyTitle, Activity context) {
        loadView.setTranslationY(0);
        loadView.setAlpha(1);
        loadView.setVisibility(View.VISIBLE);
        shadAnim(emptyAnimation, "alpha", 0, 200);

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        emptyTitle.startAnimation(fadeOut);
        fadeOut.setDuration(350);
        fadeOut.setFillAfter(true);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isError) {
                    emptyTitle.setText(context.getString(R.string.error_msg_2));
                } else {
                    emptyTitle.setText(context.getString(R.string.emojis_not_found));
                }
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                emptyTitle.startAnimation(fadeIn);
                fadeIn.setDuration(350);
                fadeIn.setFillAfter(true);
                shadAnim(emptyAnimation, "alpha", 1, 200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            emptyAnimation.setAnimation("animations/not_found.json");
            emptyAnimation.playAnimation();
        }, 200);
    }

    public void setLoadingScreenData(View loadView, LottieAnimationView emptyAnimation, TextView emptyTitle, Activity context) {
        loadView.setVisibility(View.VISIBLE);
        shadAnim(loadView, "translationY", 0, 300);
        shadAnim(loadView, "alpha", 1, 300);
        shadAnim(emptyAnimation, "alpha", 0, 200);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        emptyTitle.startAnimation(fadeOut);
        fadeOut.setDuration(350);
        fadeOut.setFillAfter(true);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                emptyTitle.setText(context.getString(R.string.emojis_loading));
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                emptyTitle.startAnimation(fadeIn);
                fadeIn.setDuration(350);
                fadeIn.setFillAfter(true);
                shadAnim(emptyAnimation, "alpha", 1, 200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            emptyAnimation.setAnimation("animations/loading.json");
            emptyAnimation.playAnimation();
        }, 200);
    }

}
