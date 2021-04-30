package com.nerbly.bemoji.Adapters;

import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;

import android.annotation.SuppressLint;
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

public class LoadingPacksAdapter {

    public static class LoadingRecyclerAdapter extends RecyclerView.Adapter<LoadingRecyclerAdapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;

        public LoadingRecyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.loadingview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final LinearLayout categoriesShimmer = _view.findViewById(R.id.categoriesShimmer);
            final LinearLayout shimmer2 = _view.findViewById(R.id.shimmer2);
            final LinearLayout shimmer3 = _view.findViewById(R.id.shimmer3);
            final LinearLayout shimmer4 = _view.findViewById(R.id.shimmer4);

            categoriesShimmer.setVisibility(View.GONE);
            setClippedView(shimmer2, "#FFFFFF", 30, 0);
            setClippedView(shimmer3, "#FFFFFF", 200, 0);
            setClippedView(shimmer4, "#FFFFFF", 200, 0);
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
