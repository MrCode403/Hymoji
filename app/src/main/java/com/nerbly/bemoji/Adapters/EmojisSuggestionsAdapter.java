package com.nerbly.bemoji.Adapters;

import static com.nerbly.bemoji.EmojisActivity.whenChipItemClicked;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nerbly.bemoji.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EmojisSuggestionsAdapter {

    public static class ChipRecyclerAdapter extends RecyclerView.Adapter<ChipRecyclerAdapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;

        public ChipRecyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.chipview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, @SuppressLint("RecyclerView") final int _position) {
            View _view = _holder.itemView;

            final com.google.android.material.card.MaterialCardView cardview3 = _view.findViewById(R.id.cardview3);
            final TextView textview1 = _view.findViewById(R.id.textview1);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _view.setLayoutParams(_lp);
            cardview3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    whenChipItemClicked(Objects.requireNonNull(_data.get(_position).get("title")).toString());
                }
            });
            textview1.setText(Objects.requireNonNull(_data.get(_position).get("title")).toString());
            textview1.setTypeface(Typeface.createFromAsset(textview1.getContext().getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
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
