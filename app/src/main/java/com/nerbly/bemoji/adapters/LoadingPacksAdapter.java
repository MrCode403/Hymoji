package com.nerbly.bemoji.adapters;

import static com.nerbly.bemoji.ui.MainUIMethods.setClippedView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nerbly.bemoji.R;

import java.util.ArrayList;
import java.util.HashMap;


public class LoadingPacksAdapter extends RecyclerView.Adapter<LoadingPacksAdapter.ViewHolder> {
    ArrayList<HashMap<String, Object>> data;

    public LoadingPacksAdapter(ArrayList<HashMap<String, Object>> hashMaps) {
        data = hashMaps;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.loadingview, parent, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View view = holder.itemView;

        LinearLayout categoriesShimmer = view.findViewById(R.id.categoriesShimmer);
        LinearLayout shimmer2 = view.findViewById(R.id.shimmer2);
        LinearLayout shimmer3 = view.findViewById(R.id.shimmer3);
        LinearLayout shimmer4 = view.findViewById(R.id.shimmer4);

        categoriesShimmer.setVisibility(View.GONE);

        setClippedView(shimmer2, "#FFFFFF", 30, 0);
        setClippedView(shimmer3, "#FFFFFF", 200, 0);
        setClippedView(shimmer4, "#FFFFFF", 200, 0);
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
