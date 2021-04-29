package com.nerbly.bemoji.Functions;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SideFunctions {


    public static void setImageFromPath(final ImageView image, final String path) {
        java.io.File file = new java.io.File(path);
        Uri imageUri = Uri.fromFile(file);

        Glide.with(image.getContext())
                .load(imageUri)
                .into(image);
    }

}
