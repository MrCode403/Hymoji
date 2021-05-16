package com.nerbly.bemoji.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.PreviewActivity;
import com.nerbly.bemoji.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.nerbly.bemoji.Functions.SideFunctions.setImgURL;

public class MainEmojisAdapter {

    public static class Recycler1Adapter extends RecyclerView.Adapter<Recycler1Adapter.ViewHolder> {
        private final Intent toPreview = new Intent();
        ArrayList<HashMap<String, Object>> data;

        public Recycler1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.emojisview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(_lp);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, @SuppressLint("RecyclerView") final int position) {
            View view = _holder.itemView;

            final LinearLayout linear1 = view.findViewById(R.id.tutorialBg);
            final LinearLayout linear2 = view.findViewById(R.id.space);
            final ImageView imageview1 = view.findViewById(R.id.emoji);

            setImgURL(Objects.requireNonNull(data.get(position).get("image")).toString(), imageview1);
            linear1.setOnLongClickListener(_view12 -> {
                Utils.showToast(_view12.getContext(), Objects.requireNonNull(data.get(position).get("title")).toString());
                return true;
            });
            linear1.setOnClickListener(view1 -> {
                toPreview.putExtra("switchType", "emoji");
                toPreview.putExtra("title", Objects.requireNonNull(data.get(position).get("title")).toString());
                toPreview.putExtra("submitted_by", Objects.requireNonNull(data.get(position).get("submitted_by")).toString());
                toPreview.putExtra("category", Objects.requireNonNull(data.get(position).get("category")).toString());
                toPreview.putExtra("fileName", Objects.requireNonNull(data.get(position).get("slug")).toString());
                toPreview.putExtra("description", Objects.requireNonNull(data.get(position).get("description")).toString());
                toPreview.putExtra("imageUrl", Objects.requireNonNull(data.get(position).get("image")).toString());
                toPreview.setClass(view1.getContext(), PreviewActivity.class);
                view1.getContext().startActivity(toPreview);
            });
            linear2.setVisibility(View.GONE);
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(200);
            linear1.startAnimation(anim);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

    }

}
