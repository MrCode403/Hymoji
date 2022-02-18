package com.nerbly.bemoji.Fragments;

import static com.nerbly.bemoji.Functions.Utils.getColumns;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Adapters.MainEmojisAdapterExperimental;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class PacksEmojisFragment extends Fragment {

    public static boolean isPacksEmojisLoaded = false;
    public static String lastSearchedPackEmoji = "";
    private final int PAGINATION_LIMIT = 100;
    private final Timer timer = new Timer();
    public boolean isSortingNew = true;
    public boolean isSortingOld = false;
    public boolean isSortingAlphabet = false;
    private RecyclerView emojisRecycler;
    private GridLayoutManager layoutManager;
    private TextView emptyTitle;
    private LinearLayout loadView;
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private LottieAnimationView emptyAnimation;
    private HashMap<String, Object> backendEmojisMap = new HashMap<>();
    private SharedPreferences sharedPref;
    private int paginationPosition = 0;
    private boolean shouldPaginate = true;
    private boolean isSearching = false;
    private CircularProgressIndicator progress_loading;
    private JSONArray emojisArray;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.packs_emojis_fragment, container, false);
        initialize(view);
        initializeLogic();
        return view;
    }

    private void initialize(View view) {
        emptyTitle = view.findViewById(R.id.emptyTitle);
        emptyAnimation = view.findViewById(R.id.emptyAnimation);
        loadView = view.findViewById(R.id.emptyview);
        emojisRecycler = view.findViewById(R.id.emojisRecycler);
        progress_loading = requireActivity().findViewById(R.id.progress_loading);
        sharedPref = requireContext().getSharedPreferences("AppData", Activity.MODE_PRIVATE);
        layoutManager = new GridLayoutManager(requireContext(), getColumns(requireActivity()));


        emojisRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!emojisRecycler.canScrollVertically(1) && shouldPaginate && !isSearching) {
                    startPagination(false);
                }
            }
        });
    }

    private void initializeLogic() {
        LOGIC_BACKEND();
    }


    public void LOGIC_BACKEND() {
        emojisRecycler.setLayoutManager(layoutManager);
        if (Build.VERSION.SDK_INT <= 30) {
            OverScrollDecoratorHelper.setUpOverScroll(emojisRecycler, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        }
        getEmojis();
    }

    public void getEmojis() {
        setLoadingScreenData(false, true);
        lastSearchedPackEmoji = "";
        isSearching = false;
        Log.d("HYMOJI_EMOJIS", "Getting emojis as JSON from SharedPreferences...");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                JSONArray tempJSONArray = new JSONArray(sharedPref.getString("packsOneByOne", ""));

                if (isSortingNew) {
                    emojisArray = Utils.sortJson(tempJSONArray, "id", true, true);
                } else if (isSortingOld) {
                    emojisArray = Utils.sortJson(tempJSONArray, "id", true, false);
                } else if (isSortingAlphabet) {
                    emojisArray = Utils.sortJson(tempJSONArray, "title", false, true);
                }
            } catch (JSONException e) {
                Log.e("HYMOJI_ERROR", e.toString());
            }

            handler.post(() -> {
                startPagination(true);
                whenEmojisAreReady();
            });
        });
    }

    private void startPagination(boolean isFirstData) {
        if (isFirstData) {
            paginationPosition = 0;
            if (!emojisList.isEmpty()) {
                emojisList.clear();
            }
        }

        Log.d("HYMOJI_EMOJIS", "Pagination started, is first data: " + isFirstData);
        final int[] totalEmojisCount = {0};
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                Log.d("HYMOJI_EMOJIS", "Found " + emojisArray.length() + " emojis (from pagination).");
                totalEmojisCount[0] = emojisArray.length();

                for (int i = 0; i < PAGINATION_LIMIT; i++) {
                    JSONObject emojisObject = emojisArray.getJSONObject(paginationPosition);
                    backendEmojisMap = new HashMap<>();
                    backendEmojisMap.put("image", emojisObject.getString("image"));
                    backendEmojisMap.put("name", emojisObject.getString("name"));
                    backendEmojisMap.put("title", emojisObject.getString("title"));
                    backendEmojisMap.put("submitted_by", "Emoji lovers");
                    backendEmojisMap.put("id", emojisObject.getInt("id"));
                    emojisList.add(backendEmojisMap);
                    paginationPosition++;
                    if (paginationPosition == emojisArray.length()) {
                        shouldPaginate = false;
                        break;
                    }
                }
            } catch (JSONException e) {
                Log.e("HYMOJI_ERROR", e.toString());
            }

            handler.post(() -> {
                progress_loading.setVisibility(View.GONE);

                if (isFirstData) {
                    try {
                        emojisRecycler.setAdapter(new MainEmojisAdapterExperimental(emojisList, requireContext()));
                        whenEmojisAreReady();
                    } catch (Exception e) {
                        if (getActivity() != null) {
                            showCustomSnackBar(getString(R.string.error_msg_2), requireActivity());
                        }
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                } else {
                    Log.d("HYMOJI_PAGINATION", "Pagination started, current total item count: " + emojisList.size() + " out of " + totalEmojisCount[0]);
                    Objects.requireNonNull(emojisRecycler.getAdapter()).notifyItemInserted(emojisList.size() - 1);
                }
            });
        });
    }


    private void noEmojisFound(boolean isError) {
        progress_loading.setVisibility(View.GONE);
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

    public void setLoadingScreenData(boolean isFullScreen, boolean isShown) {
        if (isFullScreen) {
            loadView.setVisibility(View.VISIBLE);
            shadAnim(loadView, "translationY", 0, 300);
            shadAnim(loadView, "alpha", 1, 300);
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
        } else {
            if (isShown) {
                progress_loading.setVisibility(View.VISIBLE);
            } else {
                progress_loading.setVisibility(View.GONE);
            }
        }
    }


    private void whenEmojisAreReady() {
        progress_loading.setVisibility(View.GONE);
        new Handler().postDelayed(() -> {
            shadAnim(loadView, "translationY", -1000, 300);
            shadAnim(loadView, "alpha", 0, 300);
        }, 1000);
        isPacksEmojisLoaded = true;
        new Handler().postDelayed(() -> emptyAnimation.cancelAnimation(), 2000);
    }

    public void searchTask(String query) {
        lastSearchedPackEmoji = query;
        isSearching = true;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            emojisList = new Gson().fromJson(sharedPref.getString("packsOneByOne", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());

            for (Iterator<HashMap<String, Object>> iterator = emojisList.iterator(); iterator.hasNext(); ) {
                HashMap<String, Object> emojiName = iterator.next();
                if (!Objects.requireNonNull(emojiName.get("title")).toString().toUpperCase().contains(query)) {
                    iterator.remove();
                }
            }
            handler.post(() -> {
                if (emojisList.isEmpty()) {
                    noEmojisFound(false);
                } else {
                    loadView.setVisibility(View.GONE);
                    emojisRecycler.setAdapter(new MainEmojisAdapterExperimental(emojisList, requireContext()));
                }
                progress_loading.setVisibility(View.GONE);
                isPacksEmojisLoaded = true;
            });
        });
    }


    public void sort_by_newest() {
        if (!isSortingNew) {
            isSortingNew = true;
            isSortingOld = false;
            isSortingAlphabet = false;
            setLoadingScreenData(false, true);
            getEmojis();
        }
    }

    public void sort_by_oldest() {
        if (!isSortingOld) {
            isSortingOld = true;
            isSortingNew = false;
            isSortingAlphabet = false;
            setLoadingScreenData(false, true);
            getEmojis();
        }
    }

    public void sort_by_alphabetically() {
        if (!isSortingAlphabet) {
            isSortingAlphabet = true;
            isSortingNew = false;
            isSortingOld = false;
            setLoadingScreenData(false, true);
            getEmojis();
        }
    }
}
