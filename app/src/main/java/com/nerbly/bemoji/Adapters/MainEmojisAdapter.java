package com.nerbly.bemoji.Adapters;

import static com.nerbly.bemoji.Functions.SideFunctions.setImgURL;

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

public class MainEmojisAdapter {

    public static class Recycler1Adapter extends RecyclerView.Adapter<Recycler1Adapter.ViewHolder> {
        private final Intent toPreview = new Intent();
        ArrayList<HashMap<String, Object>> _data;

        public Recycler1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.emojisview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, @SuppressLint("RecyclerView") final int _position) {
            View _view = _holder.itemView;

            final LinearLayout linear1 = _view.findViewById(R.id.linear1);
            final LinearLayout linear2 = _view.findViewById(R.id.linear2);
            final ImageView imageview1 = _view.findViewById(R.id.imageview1);

            setImgURL(Objects.requireNonNull(_data.get(_position).get("image")).toString(), imageview1);
            linear1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View _view) {
                    Utils.showMessage(_view.getContext(), Objects.requireNonNull(_data.get(_position).get("title")).toString());
                    return true;
                }
            });
            linear1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    toPreview.putExtra("switchType", "emoji");
                    toPreview.putExtra("title", Objects.requireNonNull(_data.get(_position).get("title")).toString());
                    toPreview.putExtra("submitted_by", Objects.requireNonNull(_data.get(_position).get("submitted_by")).toString());
                    toPreview.putExtra("category", Objects.requireNonNull(_data.get(_position).get("category")).toString());
                    toPreview.putExtra("fileName", Objects.requireNonNull(_data.get(_position).get("slug")).toString());
                    toPreview.putExtra("description", Objects.requireNonNull(_data.get(_position).get("description")).toString());
                    toPreview.putExtra("imageUrl", Objects.requireNonNull(_data.get(_position).get("image")).toString());
                    toPreview.setClass(_view.getContext(), PreviewActivity.class);
                    _view.getContext().startActivity(toPreview);
                }
            });
            linear2.setVisibility(View.GONE);
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(200);
            linear1.startAnimation(anim);
        }

        @Override
        public int getItemCount() {
            return _data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

    }

}
