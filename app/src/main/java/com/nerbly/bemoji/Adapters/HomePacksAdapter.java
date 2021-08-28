package com.nerbly.bemoji.Adapters;

import static com.nerbly.bemoji.Functions.MainFunctions.capitalizedFirstWord;
import static com.nerbly.bemoji.Functions.SideFunctions.setHighPriorityImageFromUrl;

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

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.nerbly.bemoji.Activities.HomeActivity;
import com.nerbly.bemoji.Activities.PackPreviewActivity;
import com.nerbly.bemoji.Activities.PacksActivity;
import com.nerbly.bemoji.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HomePacksAdapter {

    public static class Packs_recyclerAdapter extends RecyclerView.Adapter<Packs_recyclerAdapter.ViewHolder> {
        private final ArrayList<String> packsArrayList = new ArrayList<>();
        ArrayList<HashMap<String, Object>> data;
        private String packsTempArrayString = "";
        private String currentPositionPackArray = "";
        public static boolean isEmojiSheetShown = false;

        public Packs_recyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            data = _arr;
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
        public void onBindViewHolder(ViewHolder _holder, @SuppressLint("RecyclerView") int position) {
            View view = _holder.itemView;

            MaterialCardView cardview2 = view.findViewById(R.id.cardview2);
            MaterialCardView cardview1 = view.findViewById(R.id.cardview1);
            TextView pack_title = view.findViewById(R.id.pack_title);
            TextView pack_desc = view.findViewById(R.id.pack_desc);
            ImageView imageview1 = view.findViewById(R.id.emoji);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(_lp);
            pack_title.setText(capitalizedFirstWord(Objects.requireNonNull(data.get(position).get("name")).toString().replace("_", " ")));
            pack_desc.setText(Objects.requireNonNull(data.get(position).get("description")).toString());
            setHighPriorityImageFromUrl(imageview1, Objects.requireNonNull(data.get(position).get("image")).toString());
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
                if (!isEmojiSheetShown) {
                    isEmojiSheetShown = true;
                    Intent toPreview = new Intent();
                    toPreview.putExtra("title", imageview1.getContext().getString(R.string.app_name) + "Pack_" + (long) (Double.parseDouble(Objects.requireNonNull(data.get(position).get("id")).toString())));
                    toPreview.putExtra("subtitle", Objects.requireNonNull(data.get(position).get("description")).toString());
                    toPreview.putExtra("imageUrl", Objects.requireNonNull(data.get(position).get("image")).toString());
                    toPreview.putExtra("fileName", Objects.requireNonNull(data.get(position).get("slug")).toString());
                    toPreview.putExtra("packEmojisArray", currentPositionPackArray);
                    toPreview.putExtra("packEmojisAmount", Objects.requireNonNull(data.get(position).get("amount")).toString());
                    toPreview.putExtra("packName", capitalizedFirstWord(Objects.requireNonNull(data.get(position).get("name")).toString().replace("_", " ")));
                    toPreview.putExtra("packId", Objects.requireNonNull(data.get(position).get("id")).toString());
                    toPreview.setClass(imageview1.getContext(), PackPreviewActivity.class);
                    imageview1.getContext().startActivity(toPreview);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isEmojiSheetShown = false;
                        }
                    }, 1000);
                }
            });
            if (position == 0) {
                cardview2.setVisibility(View.VISIBLE);
            } else {
                cardview2.setVisibility(View.GONE);
            }
            cardview2.setOnClickListener(_view -> {
                try {
                    if (!isEmojiSheetShown) {
                        isEmojiSheetShown = true;
                        Intent toPacks = new Intent();
                        toPacks.setClass(imageview1.getContext(), PacksActivity.class);
                        imageview1.getContext().startActivity(toPacks);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                isEmojiSheetShown = false;
                            }
                        }, 1000);
                    }
                } catch (Exception ignored) {
                }
            });
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
