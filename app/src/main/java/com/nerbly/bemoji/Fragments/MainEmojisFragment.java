package com.nerbly.bemoji.Fragments;

import static com.nerbly.bemoji.Activities.EmojisActivity.searchBoxField;
import static com.nerbly.bemoji.Functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.nerbly.bemoji.Adapters.MainEmojisAdapter;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class MainEmojisFragment extends Fragment {

    private final Timer timer = new Timer();
    public boolean isSortingNew = true;
    public boolean isSortingOld = false;
    public boolean isSortingAlphabet = false;
    private TextView emptyTitle;
    private LinearLayout loadView;
    private GridView emojisRecycler;
    private final ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private LottieAnimationView emptyAnimation;
    private SharedPreferences sharedPref;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {
        View _view = _inflater.inflate(R.layout.main_emojis_fragment, _container, false);
        initialize(_view);
        com.google.firebase.FirebaseApp.initializeApp(requireContext());
        initializeLogic();
        return _view;
    }

    private void initialize(View view) {
        emptyTitle = view.findViewById(R.id.emptyTitle);
        emptyAnimation = view.findViewById(R.id.emptyAnimation);
        loadView = view.findViewById(R.id.emptyview);
        emojisRecycler = view.findViewById(R.id.emojisRecycler);
        sharedPref = requireContext().getSharedPreferences("AppData", Activity.MODE_PRIVATE);
    }

    private void initializeLogic() {
        LOGIC_BACKEND();
    }

    public void LOGIC_BACKEND() {
        initEmojisRecycler();
        if (!sharedPref.getString("emojisData", "").isEmpty()) {
            getEmojis();
        } else {
            noEmojisFound(true);
        }
        OverScrollDecoratorHelper.setUpOverScroll(emojisRecycler);
    }

    private void noEmojisFound(boolean isError) {
        loadView.setTranslationY(0);
        loadView.setAlpha(1);
        loadView.setVisibility(View.VISIBLE);
        shadAnim(emptyAnimation, "alpha", 0, 200);

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        emptyTitle.startAnimation(fadeOut);
        fadeOut.setDuration(350);
        fadeOut.setFillAfter(true);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isError) {
                    emptyTitle.setText(getString(R.string.error_msg_2));
                } else {
                    emptyTitle.setText(getString(R.string.emojis_not_found));
                }
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                emptyTitle.startAnimation(fadeIn);
                fadeIn.setDuration(350);
                fadeIn.setFillAfter(true);
                shadAnim(emptyAnimation, "alpha", 1, 200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        if (getActivity() != null) {
            TimerTask loadingTmr = new TimerTask() {
                @Override
                public void run() {
                    requireActivity().runOnUiThread(() -> {
                        emptyAnimation.setAnimation("animations/not_found.json");
                        emptyAnimation.playAnimation();
                    });
                }
            };
            timer.schedule(loadingTmr, 200);
        }
    }

    public void getEmojis() {
        if (!sharedPref.getString("emojisData", "").isEmpty()) {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                if (!emojisList.isEmpty()) {
                    try {
                        emojisList.clear();
                    } catch (Exception e) {
                        Log.e("Emojis Response", "couldn't clear the list for new emojis");
                    }
                }
                try {
                    JSONArray emojisArray = new JSONArray(sharedPref.getString("emojisData", ""));
                    Log.d("Emojis Response", "found " + emojisArray.length() + " emojis");

                    for (int i = 0; i < emojisArray.length(); i++) {
                        JSONObject emojisObject = emojisArray.getJSONObject(i);
                        HashMap<String, Object> emojisMap = new HashMap<>();
                        emojisMap.put("image", emojisObject.getString("image"));
                        emojisMap.put("name", emojisObject.getString("name"));
                        emojisMap.put("title", emojisObject.getString("title"));
                        emojisMap.put("submitted_by", emojisObject.getString("submitted_by"));
                        emojisMap.put("id", emojisObject.getInt("id"));
                        emojisList.add(emojisMap);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (isSortingNew) {
                    Utils.sortListMap(emojisList, "id", false, false);
                } else if (isSortingOld) {
                    Collections.reverse(emojisList);
                } else if (isSortingAlphabet) {
                    Utils.sortListMap(emojisList, "title", false, true);
                }

                handler.post(() -> {
                    emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));
                    whenEmojisAreReady();

                });
            });
        }
    }

    private void whenEmojisAreReady() {
        new Handler().postDelayed(() -> {
            shadAnim(loadView, "translationY", -1000, 300);
            shadAnim(loadView, "alpha", 0, 300);
            searchBoxField.setEnabled(true);
        }, 1000);
        new Handler().postDelayed(() -> {
            emptyAnimation.cancelAnimation();
        }, 2000);
    }

    public void initEmojisRecycler() {
        float scaleFactor = getResources().getDisplayMetrics().density * 70;
        int number = getScreenWidth(requireActivity());
        int columns = (int) ((float) number / scaleFactor);
        emojisRecycler.setNumColumns(columns);
        emojisRecycler.setVerticalSpacing(0);
        emojisRecycler.setHorizontalSpacing(0);
    }

    public void sort_by_newest() {
        if (!isSortingNew) {
            isSortingNew = true;
            isSortingOld = false;
            isSortingAlphabet = false;
            Utils.sortListMap(emojisList, "id", false, false);
            emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));
        }
    }

    public void sort_by_oldest() {
        if (!isSortingOld) {
            isSortingOld = true;
            isSortingNew = false;
            isSortingAlphabet = false;
            Utils.sortListMap(emojisList, "id", false, true);
            emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));
        }
    }

    public void sort_by_alphabetically() {
        if (!isSortingAlphabet) {
            isSortingAlphabet = true;
            isSortingNew = false;
            isSortingOld = false;
            Utils.sortListMap(emojisList, "title", false, true);
            emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));
        }
    }

    public void searchTask(String query) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String search_query = query.trim().toLowerCase();

            if (search_query.length() > 0) {
                if (!emojisList.isEmpty()) {
                    try {
                        emojisList.clear();
                    } catch (Exception e) {
                        Log.e("Emojis Response", "couldn't clear the list for new emojis");
                    }
                }
                try {
                    JSONArray emojisArray = new JSONArray(sharedPref.getString("emojisData", ""));
                    Log.d("Emojis Response", "found " + emojisArray.length() + " emojis");

                    for (int i = 0; i < emojisArray.length(); i++) {
                        JSONObject emojisObject = emojisArray.getJSONObject(i);
                        HashMap<String, Object> emojisMap = new HashMap<>();
                        emojisMap.put("image", emojisObject.getString("image"));
                        emojisMap.put("name", emojisObject.getString("name"));
                        emojisMap.put("title", emojisObject.getString("title"));
                        emojisMap.put("submitted_by", emojisObject.getString("submitted_by"));
                        emojisMap.put("id", emojisObject.getInt("id"));
                        if (emojisObject.getString("submitted_by").toLowerCase().contains(search_query) || emojisObject.getString("title").toLowerCase().contains(search_query)) {
                            emojisList.add(emojisMap);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                getEmojis();
            }

            handler.post(() -> {
                if (emojisList.size() == 0) {
                    noEmojisFound(false);
                } else {
                    emojisRecycler.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));
                }
            });
        });
    }
}
