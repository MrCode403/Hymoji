package com.nerbly.bemoji.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.nerbly.bemoji.Functions.DownloaderSheet.showEmojiSheet;
import static com.nerbly.bemoji.Functions.SideFunctions.setImgURL;

public class MainEmojisAdapter {

    public static boolean isEmojiSheetShown = false;

    public static class Gridview1Adapter extends BaseAdapter {
        ArrayList<HashMap<String, Object>> data;

        public Gridview1Adapter(ArrayList<HashMap<String, Object>> arr) {
            data = arr;
        }

        @Override
        public int getCount() {
            return data.size();
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
            LayoutInflater _inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _view = view;
            if (_view == null) {
                _view = _inflater.inflate(R.layout.emojisview, null);
            }
            assert _view != null;
            final LinearLayout emojiBackground = _view.findViewById(R.id.emojiBackground);
            final ImageView emoji = _view.findViewById(R.id.emoji);

            setImgURL(Objects.requireNonNull(data.get(position).get("image")).toString(), emoji);
            emojiBackground.setOnLongClickListener(_view12 -> {
                Utils.showToast(_view12.getContext(), Objects.requireNonNull(data.get(position).get("title")).toString());
                return true;
            });
            View final_view = _view;
            emojiBackground.setOnClickListener(onClick -> {
                try {
                    if (!isEmojiSheetShown) {
                        isEmojiSheetShown = true;
                        showEmojiSheet(final_view.getContext(), Objects.requireNonNull(data.get(position).get("image")).toString(), Objects.requireNonNull(data.get(position).get("title")).toString(), Objects.requireNonNull(data.get(position).get("submitted_by")).toString());
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                isEmojiSheetShown = false;
                            }
                        }, 1000);

                    }
                } catch (Exception e) {

                }
            });
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(200);
            emojiBackground.startAnimation(anim);
            return _view;
        }
    }
}