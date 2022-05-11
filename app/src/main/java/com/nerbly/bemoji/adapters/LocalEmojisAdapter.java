package com.nerbly.bemoji.adapters;

import static com.nerbly.bemoji.functions.SideFunctions.loadImageFromUri;
import static com.nerbly.bemoji.functions.SideFunctions.setImageFromPath;
import static com.nerbly.bemoji.ui.MainUIMethods.setClippedStrokeView;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nerbly.bemoji.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LocalEmojisAdapter extends RecyclerView.Adapter<LocalEmojisAdapter.ViewHolder> {
    ArrayList<HashMap<String, Object>> data;

    public LocalEmojisAdapter(ArrayList<HashMap<String, Object>> _arr) {
        data = _arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.localemojisview, parent, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View view = holder.itemView;
        ImageView emojis = view.findViewById(R.id.emoji);
        LinearLayout cardView = view.findViewById(R.id.cardView);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        if (Build.VERSION.SDK_INT >= 29) {
            loadImageFromUri(Objects.requireNonNull(data.get(position).get("filePath")).toString(), emojis, emojis.getContext());
        } else {
            setImageFromPath(emojis, Objects.requireNonNull(data.get(position).get("filePath")).toString());
        }
        setClippedStrokeView(cardView, "#00FFFFFF", 200, "#aeaeae", 1);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }
}
