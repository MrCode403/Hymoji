package com.nerbly.bemoji.Fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.nerbly.bemoji.Activities.EmojisActivity.searchBoxField;
import static com.nerbly.bemoji.Configurations.ASSETS_SOURCE_LINK;
import static com.nerbly.bemoji.Functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;


public class PacksEmojisFragment extends Fragment {

    private GridView emojisRecycler;
    private int searchPosition = 0;
    private TextView emptyTitle;
    private LinearLayout loadView;
    private final Timer timer = new Timer();
    private int emojisCount = 0;
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private LottieAnimationView emptyAnimation;
    private HashMap<String, Object> emojisMap = new HashMap<>();
    private SharedPreferences sharedPref;
    public boolean isSortingNew = true;
    public boolean isSortingOld = false;
    public boolean isGettingDataFirstTime = true;
    public boolean isSortingAlphabet = false;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {
        View _view = _inflater.inflate(R.layout.packs_emojis_fragment, _container, false);
        initialize(_savedInstanceState, _view);
        com.google.firebase.FirebaseApp.initializeApp(getContext());
        initializeLogic();
        return _view;
    }

    private void initialize(Bundle _savedInstanceState, View view) {
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
        if (!sharedPref.getString("packsData", "").isEmpty()) {
            getEmojis();
        }
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

    public void getEmojis() {
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
                    for (int backPacksArrayInt = 0; backPacksArrayInt < (int) (backPacksArray.length()); backPacksArrayInt++) {
                        JSONObject packsObject = backPacksArray.getJSONObject((int) backPacksArrayInt);
                        JSONArray frontPacksArray = packsObject.getJSONArray("emojis");
                        for (int frontPacksInt = 0; frontPacksInt < (int) (frontPacksArray.length()); frontPacksInt++) {
                            emojisMap = new HashMap<>();
                            emojisMap.put("image", ASSETS_SOURCE_LINK + frontPacksArray.getString(frontPacksInt));
                            emojisMap.put("title", frontPacksArray.getString(frontPacksInt));
                            emojisMap.put("submitted_by", "Emoji lovers");
                            emojisMap.put("id", (int) scanPosition);
                            emojisList.add(emojisMap);
                            scanPosition++;
                        }
                    }
                } catch (Exception e) {
                }
            }

            handler.post(() -> {
                if (isSortingNew) {
                    Utils.sortListMap2(emojisList, "id", true, true);
                } else  if (isSortingOld) {
                    Utils.sortListMap2(emojisList, "id", true, false);
                } else  if (isSortingAlphabet) {
                    Utils.sortListMap(emojisList, "title", false, true);
                }
                emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));
                sharedPref.edit().putString("packsOneByOne", new Gson().toJson(emojisList)).apply();
                whenEmojisAreReady();
            });
        });

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
                emojisCount = emojisList.size();
                searchPosition = emojisCount - 1;
                for (int i = 0; i < (int) (emojisCount); i++) {

                    if (!Objects.requireNonNull(emojisList.get((int) searchPosition).get("title")).toString().toLowerCase().contains(query.trim().toLowerCase())) {
                        emojisList.remove((int) (searchPosition));
                    }
                    searchPosition--;
                }
            } else {
                emojisList = new Gson().fromJson(sharedPref.getString("packsOneByOne", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
            }

            handler.post(() -> {
                if (emojisList.size() == 0) {
                    noEmojisFound();
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
            Utils.sortListMap2(emojisList, "id", true, true);
            emojisRecycler.setAdapter(new MainEmojisAdapter.Gridview1Adapter(emojisList));
        }
    }

    public void sort_by_oldest() {
        if (!isSortingOld) {
            isSortingOld = true;
            isSortingNew = false;
            isSortingAlphabet = false;
            Utils.sortListMap2(emojisList, "id", true, false);
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

}
