package com.nerbly.bemoji.Adapters;

import static com.nerbly.bemoji.Functions.SideFunctions.setTutorialImages;

import android.annotation.SuppressLint;
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


public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.ViewHolder> {
    ArrayList<HashMap<String, Object>> data;

    public TutorialAdapter(ArrayList<HashMap<String, Object>> _arr) {
        data = _arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.tutorialview, null);
        RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _v.setLayoutParams(_lp);
        return new ViewHolder(_v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View view = holder.itemView;

        TextView tutorialTitle = view.findViewById(R.id.tutorialTitle);
        TextView tutorialSubtitle = view.findViewById(R.id.tutorialSubtitle);
        ImageView tutorialImage = view.findViewById(R.id.tutorialImage);

        if (Objects.requireNonNull(data.get(position).get("isTitled")).toString().equals("true")) {
            tutorialTitle.setVisibility(View.VISIBLE);
            tutorialTitle.setText(Objects.requireNonNull(data.get(position).get("title")).toString());
        } else {
            tutorialTitle.setVisibility(View.GONE);
        }
        if (Objects.requireNonNull(data.get(position).get("isSubtitled")).toString().equals("true")) {
            tutorialSubtitle.setVisibility(View.VISIBLE);
            tutorialSubtitle.setText(Objects.requireNonNull(data.get(position).get("subtitle")).toString());
        } else {
            tutorialSubtitle.setVisibility(View.GONE);
        }
        if (Objects.requireNonNull(data.get(position).get("isImaged")).toString().equals("true")) {
            tutorialImage.setVisibility(View.VISIBLE);
            setTutorialImages(tutorialImage, Objects.requireNonNull(data.get(position).get("image")).toString());
        } else {
            tutorialImage.setVisibility(View.GONE);
        }
        RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(_lp);
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
