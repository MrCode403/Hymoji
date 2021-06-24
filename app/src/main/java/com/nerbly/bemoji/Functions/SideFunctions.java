package com.nerbly.bemoji.Functions;

import android.net.Uri;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.nerbly.bemoji.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

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
                .placeholder(R.drawable.loading);

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

    public static void setTutorialImages(final ImageView image, final String url) {
        Glide.with(image.getContext())

                .load(url)
                .fitCenter()
                .into(image);

    }


    public static void initDragNDropRecycler(final RecyclerView recycler, final ArrayList<HashMap<String, Object>> arraylist) {
        //this is an easter egg to play with downloaded emojis a little bit. Have fun!

        androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback simpleCallback = new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(
                androidx.recyclerview.widget.ItemTouchHelper.UP | androidx.recyclerview.widget.ItemTouchHelper.DOWN | androidx.recyclerview.widget.ItemTouchHelper.START | androidx.recyclerview.widget.ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView recyclerView, @androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, @androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(arraylist, fromPosition, toPosition);
                Objects.requireNonNull(recycler.getAdapter()).notifyItemMoved(fromPosition, toPosition);
                return false;
            }

            @Override
            public void onSwiped(@androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int direction) {
            }
        };

        androidx.recyclerview.widget.ItemTouchHelper itemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recycler);
    }

}
