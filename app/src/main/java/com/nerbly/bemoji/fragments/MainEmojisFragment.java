package com.nerbly.bemoji.fragments;

import static com.nerbly.bemoji.functions.Utils.getColumns;
import static com.nerbly.bemoji.ui.MainUIMethods.shadAnim;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.adapters.MainEmojisAdapterExperimental;
import com.nerbly.bemoji.functions.Utils;
import com.nerbly.bemoji.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class MainEmojisFragment extends Fragment {

    public static boolean isMainEmojisLoaded = false;
    public static String lastSearchedMainEmoji = "";
    private final Timer timer = new Timer();
    public boolean isSortingNew = true;
    public boolean isSortingOld = false;
    public boolean isSortingAlphabet = false;
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private TextView emptyTitle;
    private LinearLayout loadView;
    private RecyclerView emojisRecycler;
    private LottieAnimationView emptyAnimation;
    private SharedPreferences sharedPref;
    private GridLayoutManager layoutManager;
    private CircularProgressIndicator progress_loading;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_emojis_fragment, container, false);
        initialize(view);
        initializeLogic();
        return view;
    }

    private void initialize(View view) {
        emptyTitle = view.findViewById(R.id.emptyTitle);
        emptyAnimation = view.findViewById(R.id.emptyAnimation);
        loadView = view.findViewById(R.id.emptyview);
        emojisRecycler = view.findViewById(R.id.emojisRecycler);
        sharedPref = requireContext().getSharedPreferences("AppData", Activity.MODE_PRIVATE);
        layoutManager = new GridLayoutManager(requireContext(), getColumns(requireActivity()));
        progress_loading = requireActivity().findViewById(R.id.progress_loading);
    }

    private void initializeLogic() {
        LOGIC_BACKEND();
    }

    public void LOGIC_BACKEND() {
        emojisRecycler.setLayoutManager(layoutManager);
        if (Build.VERSION.SDK_INT <= 30) {
            OverScrollDecoratorHelper.setUpOverScroll(emojisRecycler, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        }
        if (!sharedPref.getString("emojisData", "").isEmpty()) {
            getEmojis();
        } else {
            noEmojisFound(false);
        }
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
        progress_loading.setVisibility(View.VISIBLE);
        lastSearchedMainEmoji = "";
        if (!sharedPref.getString("emojisData", "").isEmpty()) {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());

                if (isSortingNew) {
                    Utils.sortListMap(emojisList, "id", true, true);
                } else if (isSortingOld) {
                    Utils.sortListMap(emojisList, "id", true, false);
                } else if (isSortingAlphabet) {
                    Utils.sortListMap(emojisList, "title", false, true);
                }

                handler.post(() -> {
                    emojisRecycler.setAdapter(new MainEmojisAdapterExperimental(emojisList, requireContext()));
                    whenEmojisAreReady();
                });
            });
        }
    }

    private void whenEmojisAreReady() {
        new Handler().postDelayed(() -> {
            progress_loading.setVisibility(View.GONE);
            shadAnim(loadView, "translationY", -1000, 300);
            shadAnim(loadView, "alpha", 0, 300);
            isMainEmojisLoaded = true;
        }, 500);
        new Handler().postDelayed(() -> emptyAnimation.cancelAnimation(), 2000);
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
            getEmojis();
        }
    }

    public void searchTask(String query) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            lastSearchedMainEmoji = query;
            emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
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
                isMainEmojisLoaded = true;
            });
        });
    }
}
