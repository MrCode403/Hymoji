package com.nerbly.bemoji.adapters;

import static com.nerbly.bemoji.functions.SideFunctions.setImgURL;
import static com.nerbly.bemoji.ui.UserInteractions.showCustomSnackBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nerbly.bemoji.R;
import com.nerbly.bemoji.ui.DownloaderSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainEmojisAdapterExperimental extends RecyclerView.Adapter<MainEmojisAdapterExperimental.ViewHolder> {
    public static boolean isEmojiSheetShown = false;
    ArrayList<HashMap<String, Object>> data;
    Context mContext;
    Activity activity;
    String emojiUrl = "";
    String emojiName = "Unknown emoji";
    String emojiAuthor = "Emoji lovers";
    int emojiID = 0;

    public MainEmojisAdapterExperimental(ArrayList<HashMap<String, Object>> arr, Context context) {
        data = arr;
        mContext = context;
        activity = (Activity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.emojisview, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainEmojisAdapterExperimental.ViewHolder holder, int position) {
        View view = holder.itemView;

        ImageView emoji = view.findViewById(R.id.emoji);

        try {
            setImgURL(emoji, Objects.requireNonNull(data.get(position).get("image")).toString());
        } catch (Exception ignored) {
        }

        view.setOnClickListener(onClick -> {
            try {
                if (!isEmojiSheetShown) {
                    isEmojiSheetShown = true;
                    emojiUrl = Objects.requireNonNull(data.get(position).get("image")).toString();
                    emojiName = Objects.requireNonNull(data.get(position).get("title")).toString();
                    emojiAuthor = Objects.requireNonNull(data.get(position).get("submitted_by")).toString();
                    emojiID = (int) Double.parseDouble(Objects.requireNonNull(data.get(position).get("id")).toString());

                    Log.d("HYMOJI_EMOJI_DATA", "DATA:" +
                            "\nemoji url: " + emojiUrl +
                            "\nemoji name: " + emojiName +
                            "\nid: " + emojiID +
                            "\nsubmitted by: " + emojiAuthor);

                    DownloaderSheet downloaderSheet = new DownloaderSheet();
                    downloaderSheet.showEmojiSheet(mContext, emojiUrl, emojiName, emojiAuthor);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isEmojiSheetShown = false;
                        }
                    }, 700);

                }
            } catch (Exception e) {
                showCustomSnackBar(mContext.getString(R.string.preview_emoji_error), activity);
                e.printStackTrace();
                isEmojiSheetShown = false;
            }
        });

//        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(200);
//        view.startAnimation(anim);
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