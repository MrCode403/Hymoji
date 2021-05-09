package com.nerbly.bemoji.Adapters;

import static com.nerbly.bemoji.EmojisActivity.whenChipItemClicked;

import android.annotation.SuppressLint;
import android.content.Context;
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
            @SuppressLint("InflateParams") View view = _inflater.inflate(R.layout.chipview, null);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
            View view = viewHolder.itemView;

            final com.google.android.material.card.MaterialCardView cardview3 = view.findViewById(R.id.cardView);
            final TextView textview1 = view.findViewById(R.id.emptyTitle);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(_lp);
            cardview3.setOnClickListener(_view -> whenChipItemClicked(Objects.requireNonNull(_data.get(position).get("title")).toString()));
            textview1.setText(Objects.requireNonNull(_data.get(position).get("title")).toString());
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
