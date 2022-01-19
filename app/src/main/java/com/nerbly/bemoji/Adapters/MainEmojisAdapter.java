package com.nerbly.bemoji.Adapters;

import static com.nerbly.bemoji.Functions.SideFunctions.setImgURL;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nerbly.bemoji.R;
import com.nerbly.bemoji.UI.DownloaderSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainEmojisAdapter extends BaseAdapter {
    public static boolean isEmojiSheetShown = false;
    ArrayList<HashMap<String, Object>> data;
    Context mContext;
    Activity activity;
    String emojiUrl = "";
    String emojiName = "Unknown emoji";
    String emojiAuthor = "Emoji lovers";

    public MainEmojisAdapter(ArrayList<HashMap<String, Object>> arr, Context context) {
        data = arr;
        mContext = context;
        activity = (Activity) context;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public HashMap<String, Object> getItem(int index) {
        return data.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(final int position, View view, ViewGroup container) {
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.emojisview, container, false);
        }
        assert view != null;
        final LinearLayout emojiBackground = view.findViewById(R.id.emojiBackground);
        final LinearLayout space = view.findViewById(R.id.space);
        final ImageView emoji = view.findViewById(R.id.emoji);

        try {
            setImgURL(Objects.requireNonNull(data.get(position).get("image")).toString(), emoji);
        } catch (Exception ignored) {
        }

        emojiBackground.setOnClickListener(onClick -> {
            try {
                if (!isEmojiSheetShown) {
                    isEmojiSheetShown = true;
                    emojiUrl = Objects.requireNonNull(data.get(position).get("image")).toString();
                    emojiName = Objects.requireNonNull(data.get(position).get("title")).toString();
                    emojiAuthor = Objects.requireNonNull(data.get(position).get("submitted_by")).toString();

                    Log.d("HYMOJI_EMOJI_DATA", "DATA:\nEmoji url: " + emojiUrl + "\nEmoji name: " + emojiName + "\nSubmitted by: " + emojiAuthor);

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

        if (position == getCount() - 1) {
            space.setVisibility(View.VISIBLE);
        } else {
            space.setVisibility(View.GONE);
        }

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);
        emojiBackground.startAnimation(anim);
        return view;
    }
}