package com.nerbly.bemoji.UI;

import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nerbly.bemoji.R;

public class UserInteractions {

    public static void showCustomSnackBar(String message, Activity context) {
        try {
            com.google.android.material.snackbar.Snackbar snackBarView;
            com.google.android.material.snackbar.Snackbar.SnackbarLayout sblayout;
            ViewGroup parentLayout = (ViewGroup) ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);

            snackBarView = com.google.android.material.snackbar.Snackbar.make(parentLayout, "", com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
            sblayout = (com.google.android.material.snackbar.Snackbar.SnackbarLayout) snackBarView.getView();

            View inflate = context.getLayoutInflater().inflate(R.layout.snackbar, parentLayout, false);
            sblayout.setPadding(0, 0, 0, 0);
            sblayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
            LinearLayout back = inflate.findViewById(R.id.snackbar_bg);

            TextView snackbar_tv = inflate.findViewById(R.id.snackbar_text);
            setViewRadius(back, 20, "#202125");
            snackbar_tv.setText(message);
            sblayout.addView(inflate, 0);
            snackBarView.show();
        } catch (Exception e) {
        }
    }

    public static AlertDialog showMessageDialog(String title, String message, String positiveButtonText, String negativeButtonText, Activity context, DialogInterface.OnClickListener positiveAction, DialogInterface.OnClickListener negativeAction) {
        return new MaterialAlertDialogBuilder(context, R.style.RoundShapeTheme)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButtonText, negativeAction)
                .setPositiveButton(positiveButtonText, positiveAction)
                .show();
    }

    public static void showMessageSheet(String title, int drawable, String positiveButtonText, String negativeButtonText, String message, Activity context, View.OnClickListener positiveAction, View.OnClickListener negativeAction) {

        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(context, R.style.materialsheet);
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

}
