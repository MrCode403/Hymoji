package com.nerbly.bemoji.Adapters;

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

import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;

public class LoadingPacksAdapter {

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
        public void onBindViewHolder(ViewHolder holder, final int position) {
            View view = holder.itemView;

            final LinearLayout categoriesShimmer = view.findViewById(R.id.categoriesShimmer);
            final LinearLayout shimmer2 = view.findViewById(R.id.shimmer2);
            final LinearLayout shimmer3 = view.findViewById(R.id.shimmer3);
            final LinearLayout shimmer4 = view.findViewById(R.id.shimmer4);

            categoriesShimmer.setVisibility(View.GONE);
            setClippedView(shimmer2, "#FFFFFF", 30, 0);
            setClippedView(shimmer3, "#FFFFFF", 200, 0);
            setClippedView(shimmer4, "#FFFFFF", 200, 0);
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
