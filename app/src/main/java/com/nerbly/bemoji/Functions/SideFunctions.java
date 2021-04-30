package com.nerbly.bemoji.Functions;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.nerbly.bemoji.R;

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
                .priority(Priority.HIGH);

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

}
