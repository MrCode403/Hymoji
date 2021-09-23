package com.nerbly.bemoji.Functions;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.nerbly.bemoji.R;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class SideFunctions {

    public static void hideShowKeyboard(boolean bool, TextView edittext, Activity context) {
        try {
            if (bool) {
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edittext, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            } else {
                android.view.View view = context.getCurrentFocus();
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setImageFromPath(final ImageView image, final String path) {
        java.io.File file = new java.io.File(path);
        Uri imageUri = Uri.fromFile(file);
        Glide.with(image.getContext())
                .load(imageUri)
                .into(image);
    }

    public static void loadImageFromUri(String uri, ImageView image, Context context) {
        Glide.with(context)
                .load(uri)
                .into(image);
    }

    public static void setImgURL(final String url, final ImageView image) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading)
                .fitCenter()
                .priority(Priority.IMMEDIATE);

        Glide.with(image.getContext())
                .load(url)
                .apply(options)
                .into(image);

    }

    public static void setBlurImageUrl(ImageView image, double blur, String url) {
        try {
            RequestOptions options1 = new RequestOptions()
                    .priority(Priority.HIGH);

            Glide.with(image.getContext())

                    .load(url)
                    .apply(options1)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(bitmapTransform(new BlurTransformation((int) blur, 4)))
                    .into(image);
        } catch (Exception e) {
        }
    }


    public static void setHighPriorityImageFromUrl(final ImageView image, final String url) {
        Glide.with(image.getContext())
                .load(url)
                .centerCrop()
                .dontAnimate()
                .priority(Priority.IMMEDIATE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image);

    }

    public static void setImageFromUrlForSheet(ImageView image, String url) {
        RequestOptions options = new RequestOptions()
                .priority(Priority.IMMEDIATE);

        Glide.with(image.getContext())
                .load(url)
                .apply(options)
                .into(image);

    }

    public static void setTutorialImages(final ImageView image, final String url) {
        Glide.with(image.getContext())
                .load(url)
                .fitCenter()
                .into(image);

    }

    public static int getNavigationBarHeight(Activity context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int  getStatusBarHeight(Activity context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getListItemsCount(ArrayList<HashMap<String, Object>> data) {
        return data == null ? 0 : data.size();
    }
}
