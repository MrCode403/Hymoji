package com.nerbly.bemoji.Fragments;

import static com.nerbly.bemoji.Activities.EmojisActivity.searchBoxField;
import static com.nerbly.bemoji.Functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
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
    private double searchPosition = 0;
    private double emojisCount = 0;
    private GridView emojisRecycler;
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
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
            noEmojisFound();
        }
        OverScrollDecoratorHelper.setUpOverScroll(emojisRecycler);
    }

    private void noEmojisFound() {
        loadView.setTranslationY(0);
        loadView.setAlpha(1);
        shadAnim(emptyAnimation, "translationX", -200, 200);
        shadAnim(emptyAnimation, "alpha", 0, 200);
        loadView.setVisibility(View.VISIBLE);
        emptyAnimation.setAnimation("animations/not_found.json");
        emptyAnimation.playAnimation();
        emptyTitle.setText(getString(R.string.emojis_not_found));
        TimerTask loadingTmr = new TimerTask() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(() -> {
                    emptyAnimation.setAnimation("animations/not_found.json");
                    emptyAnimation.playAnimation();
                    emptyAnimation.setTranslationX(200);
                    shadAnim(emptyAnimation, "translationX", 0, 200);
                    shadAnim(emptyAnimation, "alpha", 1, 200);
                });
            }
        };
        timer.schedule(loadingTmr, 500);
    }

    private void getEmojisTask() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            if (isSortingNew) {
                Utils.sortListMap2(emojisList, "id", false, false);
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

    private void whenEmojisAreReady() {
        new Handler().postDelayed(() -> {
            shadAnim(loadView, "translationY", -1000, 300);
            shadAnim(loadView, "alpha", 0, 300);
            searchBoxField.setEnabled(true);
        }, 1000);
    }

    public void initEmojisRecycler() {
        float scaleFactor = getResources().getDisplayMetrics().density * 70;
        int number = getScreenWidth(requireActivity());
        int columns = (int) ((float) number / scaleFactor);
        emojisRecycler.setNumColumns(columns);
        emojisRecycler.setVerticalSpacing(0);
        emojisRecycler.setHorizontalSpacing(0);
    }


    public void getEmojis() {
        if (!sharedPref.getString("emojisData", "").isEmpty()) {
            getEmojisTask();
        }
    }

    public void sort_by_newest() {
        if (!isSortingNew) {
            isSortingNew = true;
            isSortingOld = false;
            isSortingAlphabet = false;
            Utils.sortListMap2(emojisList, "id", false, false);
            emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));
        }
    }

    public void sort_by_oldest() {
        if (!isSortingOld) {
            isSortingOld = true;
            isSortingNew = false;
            isSortingAlphabet = false;
            Utils.sortListMap2(emojisList, "id", false, true);
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

            if (query.trim().length() > 0) {
                emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
                if (emojisList.size() != 0) {
                    emojisCount = emojisList.size();
                    searchPosition = emojisCount - 1;
                    for (int i = 0; i < (int) (emojisCount); i++) {

                        if (!Objects.requireNonNull(emojisList.get((int) searchPosition).get("submitted_by")).toString().toLowerCase().contains(query.trim().toLowerCase())
                                && !Objects.requireNonNull(emojisList.get((int) searchPosition).get("title")).toString().toLowerCase().contains(query.trim().toLowerCase())) {
                            emojisList.remove((int) (searchPosition));
                        }
                        searchPosition--;
                    }
                } else {
                    noEmojisFound();
                }
            } else {

                emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
            }

            handler.post(() -> {
                if (emojisList.size() == 0) {
                    noEmojisFound();
                } else {
                    emojisRecycler.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));

                }

            });
        });
    }

}
