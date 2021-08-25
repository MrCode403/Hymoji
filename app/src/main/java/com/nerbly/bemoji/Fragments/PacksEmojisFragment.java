package com.nerbly.bemoji.Fragments;

import static com.nerbly.bemoji.Activities.EmojisActivity.searchBoxField;
import static com.nerbly.bemoji.Configurations.ASSETS_SOURCE_LINK;
import static com.nerbly.bemoji.Functions.MainFunctions.capitalizedFirstWord;
import static com.nerbly.bemoji.Functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.Functions.SideFunctions.getListItemsCount;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Adapters.MainEmojisAdapter;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class PacksEmojisFragment extends Fragment {

    private final Timer timer = new Timer();
    public boolean isSortingNew = true;
    public boolean isSortingOld = false;
    public boolean isSortingAlphabet = false;
    private GridView emojisRecycler;
    private int searchPosition = 0;
    private TextView emptyTitle;
    private LinearLayout loadView;
    private int emojisCount = 0;
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private LottieAnimationView emptyAnimation;
    private HashMap<String, Object> emojisMap = new HashMap<>();
    private SharedPreferences sharedPref;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {
        View _view = _inflater.inflate(R.layout.packs_emojis_fragment, _container, false);
        initialize(_view);
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
        getEmojis();
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

    public void setLoadingScreenData() {
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
                emptyTitle.setText(getString(R.string.emojis_loading));
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
                        emptyAnimation.setAnimation("animations/loading.json");
                        emptyAnimation.playAnimation();
                    });
                }
            };
            timer.schedule(loadingTmr, 200);
        }
    }

    public void getEmojis() {
        if (!sharedPref.getString("packsData", "").isEmpty()) {
            try {
                if (!emojisList.isEmpty()) {
                    emojisList.clear();
                }
            } catch (Exception e) {
                Log.e("getting emojis fail", e.toString());
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                int scanPosition = 0;
                if (!sharedPref.getString("packsData", "").isEmpty()) {
                    try {
                        JSONArray backPacksArray = new JSONArray(sharedPref.getString("packsData", ""));
                        for (int backPacksArrayInt = 0; backPacksArrayInt < backPacksArray.length(); backPacksArrayInt++) {
                            JSONObject packsObject = backPacksArray.getJSONObject(backPacksArrayInt);
                            JSONArray frontPacksArray = packsObject.getJSONArray("emojis");

                            for (int frontPacksInt = 0; frontPacksInt < frontPacksArray.length(); frontPacksInt++) {

                                String emojiName = frontPacksArray.getString(frontPacksInt).replaceAll("[_\\\\-]", " ");
                                emojiName = emojiName.replaceAll("[0-9]", "");
                                emojiName = emojiName.substring(0, emojiName.length() - 4);

                                emojisMap = new HashMap<>();
                                emojisMap.put("image", ASSETS_SOURCE_LINK + frontPacksArray.getString(frontPacksInt));
                                emojisMap.put("name", capitalizedFirstWord(emojiName).trim());
                                emojisMap.put("title", frontPacksArray.getString(frontPacksInt));
                                emojisMap.put("submitted_by", "Emoji lovers");
                                emojisMap.put("id", scanPosition);
                                emojisList.add(emojisMap);
                                scanPosition++;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("error", e.toString());
                    }
                }

                handler.post(() -> {
                    if (isSortingNew) {
                        Utils.sortListMap(emojisList, "id", true, true);
                    } else if (isSortingOld) {
                        Utils.sortListMap(emojisList, "id", true, false);
                    } else if (isSortingAlphabet) {
                        Utils.sortListMap(emojisList, "name", false, true);
                    }
                    emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));
                    sharedPref.edit().putString("packsOneByOne", new Gson().toJson(emojisList)).apply();
                    whenEmojisAreReady();
                });
            });
        } else {
            noEmojisFound(true);
        }
    }

    public void initEmojisRecycler() {
        float scaleFactor = getResources().getDisplayMetrics().density * 70;
        int number = getScreenWidth(requireActivity());
        int columns = (int) ((float) number / scaleFactor);
        emojisRecycler.setNumColumns(columns);
        emojisRecycler.setVerticalSpacing(0);
        emojisRecycler.setHorizontalSpacing(0);
    }

    private void whenEmojisAreReady() {
        new Handler().postDelayed(() -> {
            shadAnim(loadView, "translationY", -1000, 300);
            shadAnim(loadView, "alpha", 0, 300);
            searchBoxField.setEnabled(true);
        }, 1000);
    }

    public void searchTask(String query) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            if (query.trim().length() > 0) {
                emojisList = new Gson().fromJson(sharedPref.getString("packsOneByOne", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
                if (getListItemsCount(emojisList) != 0) {
                    emojisCount = getListItemsCount(emojisList);
                    searchPosition = emojisCount - 1;
                    for (int i = 0; i < emojisCount; i++) {
                        if (!Objects.requireNonNull(emojisList.get(searchPosition).get("name")).toString().toLowerCase().contains(query.trim().toLowerCase())) {
                            emojisList.remove(searchPosition);
                        }
                        searchPosition--;
                    }
                }
            } else {
                emojisList = new Gson().fromJson(sharedPref.getString("packsOneByOne", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
            }

            handler.post(() -> {
                if (getListItemsCount(emojisList) == 0) {
                    noEmojisFound(false);
                } else {
                    loadView.setVisibility(View.GONE);
                    emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));

                }

            });
        });
    }

    public void sort_by_newest() {
        if (!isSortingNew) {
            isSortingNew = true;
            isSortingOld = false;
            isSortingAlphabet = false;
            getEmojis();
        }
    }

    public void sort_by_oldest() {
        if (!isSortingOld) {
            isSortingOld = true;
            isSortingNew = false;
            isSortingAlphabet = false;
            getEmojis();
        }
    }

    public void sort_by_alphabetically() {
        if (!isSortingAlphabet) {
            isSortingAlphabet = true;
            isSortingNew = false;
            isSortingOld = false;
            Utils.sortListMap(emojisList, "name", false, true);
            getEmojis();
        }
    }

}
