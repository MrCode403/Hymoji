package com.nerbly.bemoji.Adapters;

import static com.nerbly.bemoji.UI.MainUIMethods.circularImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nerbly.bemoji.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TranslationContributorsAdapter extends RecyclerView.Adapter<TranslationContributorsAdapter.ViewHolder> {
    ArrayList<HashMap<String, Object>> data;

    public TranslationContributorsAdapter(ArrayList<HashMap<String, Object>> _arr) {
        data = _arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View _v = _inflater.inflate(R.layout.contributorsview, null);
        RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        _v.setLayoutParams(_lp);
        return new ViewHolder(_v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View view = holder.itemView;
        TextView name = view.findViewById(R.id.name);
        TextView language = view.findViewById(R.id.language);
        ImageView image = view.findViewById(R.id.image);

        RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(_lp);
        name.setText(Objects.requireNonNull(data.get(position).get("name")).toString());
        language.setText(Objects.requireNonNull(data.get(position).get("language")).toString());
        circularImage(image, Objects.requireNonNull(data.get(position).get("image")).toString(), view.getContext());
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
