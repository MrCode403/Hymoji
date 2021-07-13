package com.nerbly.bemoji.Functions;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.nerbly.bemoji.R;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SideFunctions {


    public static void setImageFromPath(final ImageView image, final String path) {
        java.io.File file = new java.io.File(path);
        Uri imageUri = Uri.fromFile(file);
        Glide.with(image.getContext())
                .load(imageUri)
                .into(image);
    }

    public static void setImgURL(final String url, final ImageView image) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.IMMEDIATE);

        Glide.with(image.getContext())
                .load(url)
                .apply(options)
                .into(image);

    }

    public static void setImageFromUrl(final ImageView image, final String url) {
        Glide.with(image.getContext())
                .load(url)
                .centerCrop()
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
}
