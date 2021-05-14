package com.nerbly.bemoji.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.nerbly.bemoji.HomeActivity;
import com.nerbly.bemoji.PackPreviewActivity;
import com.nerbly.bemoji.PacksActivity;
import com.nerbly.bemoji.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.nerbly.bemoji.Functions.MainFunctions.capitalizedFirstWord;
import static com.nerbly.bemoji.Functions.SideFunctions.setImageFromUrl;

public class HomePacksAdapter {

    public static class Packs_recyclerAdapter extends RecyclerView.Adapter<Packs_recyclerAdapter.ViewHolder> {
        private final Intent toPreview = new Intent();
        private final Intent toPacks = new Intent();
        private final ArrayList<String> packsArrayList = new ArrayList<>();
        ArrayList<HashMap<String, Object>> _data;
        private String packsTempArrayString = "";
        private String currentPositionPackArray = "";

        public Packs_recyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.packsview, parent, false);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(_lp);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, @SuppressLint("RecyclerView") final int position) {
            View view = _holder.itemView;

            final com.google.android.material.card.MaterialCardView cardview2 = view.findViewById(R.id.cardview2);
            final com.google.android.material.card.MaterialCardView cardview1 = view.findViewById(R.id.cardview1);
            final TextView textview1 = view.findViewById(R.id.emptyTitle);
            final TextView textview2 = view.findViewById(R.id.tutorialSubtitle);
            final ImageView imageview1 = view.findViewById(R.id.emoji);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(_lp);
            textview1.setText(capitalizedFirstWord(Objects.requireNonNull(_data.get(position).get("name")).toString().replace("_", " ")));
            textview2.setText(Objects.requireNonNull(_data.get(position).get("description")).toString());
            setImageFromUrl(imageview1, Objects.requireNonNull(_data.get(position).get("image")).toString());
            cardview1.setOnClickListener(_view -> {
                try {
                    packsTempArrayString = HomeActivity.PacksArray();
                    JSONArray backPacksArray = new JSONArray(packsTempArrayString);
                    JSONObject packsObject = backPacksArray.getJSONObject(position);

                    JSONArray frontPacksArray = packsObject.getJSONArray("emojis");
                    for (int frontPacksInt = 0; frontPacksInt < frontPacksArray.length(); frontPacksInt++) {
                        packsArrayList.add(frontPacksArray.getString(frontPacksInt));
                    }
                    currentPositionPackArray = new Gson().toJson(packsArrayList);
                    packsArrayList.clear();
                } catch (Exception e) {
                    Log.e("Pack Array Crashed", e.toString());
                }
                toPreview.putExtra("switchType", "pack");
                toPreview.putExtra("title", "BemojiPack_" + (long) (Double.parseDouble(Objects.requireNonNull(_data.get(position).get("id")).toString())));
                toPreview.putExtra("subtitle", Objects.requireNonNull(_data.get(position).get("description")).toString());
                toPreview.putExtra("imageUrl", Objects.requireNonNull(_data.get(position).get("image")).toString());
                toPreview.putExtra("fileName", Objects.requireNonNull(_data.get(position).get("slug")).toString());
                toPreview.putExtra("packEmojisArray", currentPositionPackArray);
                toPreview.putExtra("packEmojisAmount", Objects.requireNonNull(_data.get(position).get("amount")).toString());
                toPreview.putExtra("packName", capitalizedFirstWord(Objects.requireNonNull(_data.get(position).get("name")).toString().replace("_", " ")));
                toPreview.putExtra("packId", Objects.requireNonNull(_data.get(position).get("id")).toString());
                toPreview.setClass(imageview1.getContext(), PackPreviewActivity.class);
                imageview1.getContext().startActivity(toPreview);
            });
            if (position == 0) {
                cardview2.setVisibility(View.VISIBLE);
            } else {
                cardview2.setVisibility(View.GONE);
            }
            cardview2.setOnClickListener(_view -> {
                toPacks.setClass(imageview1.getContext(), PacksActivity.class);
                imageview1.getContext().startActivity(toPacks);
            });
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
