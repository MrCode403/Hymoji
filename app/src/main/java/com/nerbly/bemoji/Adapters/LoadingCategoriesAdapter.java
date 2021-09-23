package com.nerbly.bemoji.Adapters;

import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;

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

public class LoadingCategoriesAdapter {

    public static class LoadingRecyclerAdapter extends RecyclerView.Adapter<LoadingRecyclerAdapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> data;

        public LoadingRecyclerAdapter(ArrayList<HashMap<String, Object>> hashMaps) {
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

            LinearLayout packsloading = view.findViewById(R.id.packsloading);
            LinearLayout categoriesShimmer = view.findViewById(R.id.categoriesShimmer);

            packsloading.setVisibility(View.GONE);

            setClippedView(categoriesShimmer, "#FFFFFF", 30, 0);
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

}
