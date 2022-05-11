package com.nerbly.bemoji.activities;


import static com.nerbly.bemoji.adapters.MainEmojisAdapter.isEmojiSheetShown;
import static com.nerbly.bemoji.functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.functions.SideFunctions.hideShowKeyboard;
import static com.nerbly.bemoji.ui.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.ui.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.ui.MainUIMethods.rippleEffect;
import static com.nerbly.bemoji.ui.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.ui.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.ui.MainUIMethods.setImageViewRipple;
import static com.nerbly.bemoji.ui.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.ui.MainUIMethods.statusBarColor;
import static com.nerbly.bemoji.ui.UserInteractions.showCustomSnackBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.adapters.MainEmojisAdapter;
import com.nerbly.bemoji.functions.Utils;
import com.nerbly.bemoji.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class PreviewCategoryActivity extends AppCompatActivity {
    private final Timer timer = new Timer();
    private EditText searchBoxField;
    private double searchPosition = 0;
    private double emojisCount = 0;
    private boolean isSortingNew = true;
    private boolean isSortingOld = false;
    private boolean isSortingAlphabet = false;
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> emojisBackupList = new ArrayList<>();
    private AdView adview;
    private LottieAnimationView emptyAnimation;
    private LinearLayout searchBox;
    private ImageView sortByBtn;
    private ImageView searchBtn;
    private TextView emptyTitle;
    private GridView emojisRecycler;
    private LinearLayout loadView;
    private SharedPreferences sharedPref;
    private boolean isSearching = false;
    private boolean isGettingDataFirstTime = true;
    private String lastSearchedEmoji = "";
    private String searchQuery = "";
    private boolean isSearchEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.categories_emojis);
        initialize();
        FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        adview = findViewById(R.id.adview);
        searchBox = findViewById(R.id.searchbox);
        emptyTitle = findViewById(R.id.emptyTitle);
        emptyAnimation = findViewById(R.id.emptyAnimation);
        searchBoxField = findViewById(R.id.searchField);
        sortByBtn = findViewById(R.id.ic_filter_clear);
        searchBtn = findViewById(R.id.searchBtn);
        emojisRecycler = findViewById(R.id.emojisRecycler);
        loadView = findViewById(R.id.emptyview);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        searchBoxField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSeq, int start, int count, int after) {
                searchQuery = charSeq.toString().trim().toUpperCase();

                if (searchQuery.length() == 0 && isSearching) {
                    isSearching = false;
                    lastSearchedEmoji = "";
                    getEmojis();
                }

                if (searchQuery.length() > 0) {
                    sortByBtn.setImageResource(R.drawable.round_clear_black_48dp);
                } else {
                    sortByBtn.setImageResource(R.drawable.outline_filter_alt_black_48dp);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {

            }

            @Override
            public void afterTextChanged(Editable _param1) {

            }
        });

        searchBoxField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTask();
                return true;
            }
            return false;
        });

        sortByBtn.setOnClickListener(_view -> {
            if (searchQuery.length() > 0) {
                lastSearchedEmoji = "";
                searchBoxField.setText("");
            } else if (isSearchEnabled) {
                searchBoxField.setEnabled(false);
                searchBoxField.setEnabled(true);
                showFilterMenu(sortByBtn);
            }
        });

        searchBtn.setOnClickListener(_view -> {
            if (isSearchEnabled) {
                searchTask();
            }
        });

    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }


    public void LOGIC_BACKEND() {
        initEmojisRecycler();
        getEmojis();
        loadAds();
    }

    public void LOGIC_FRONTEND() {
        rippleRoundStroke(searchBox, "#FFFFFF", "#FFFFFF", 200, 1, "#C4C4C4");
        if (Build.VERSION.SDK_INT <= 27) {
            statusBarColor("#7289DA", this);
            LIGHT_ICONS(this);
        } else {
            statusBarColor("#FFFFFF", this);
            DARK_ICONS(this);
        }
        rippleEffect("#E0E0E0", sortByBtn);
        rippleEffect("#E0E0E0", searchBtn);
    }

    public void initEmojisRecycler() {
        float scaleFactor = getResources().getDisplayMetrics().density * 70;
        int number = getScreenWidth(this);
        int columns = (int) ((float) number / scaleFactor);
        emojisRecycler.setNumColumns(columns);
        if (Build.VERSION.SDK_INT <= 30) {
            OverScrollDecoratorHelper.setUpOverScroll(emojisRecycler);
        }

    }

    public void loadCategorizedEmojis() {
        try {
            if (isGettingDataFirstTime) {
                isGettingDataFirstTime = false;
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
                    if (emojisObject.getInt("category") == getIntent().getIntExtra("category_id", 0)) {
                        emojisList.add(emojisMap);
                        emojisBackupList.add(emojisMap);
                    }
                }
            } else {
                emojisList = new Gson().fromJson(new Gson().toJson(emojisBackupList), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
            }

            if (isSortingNew) {
                Utils.sortListMap(emojisList, "id", false, false);
            } else if (isSortingOld) {
                Collections.reverse(emojisList);
            } else if (isSortingAlphabet) {
                Utils.sortListMap(emojisList, "title", false, true);
            }
        } catch (Exception e) {
            Log.e("EmojisRequestListener", e.toString());
            FirebaseCrashlytics.getInstance().recordException(e);
        }

    }

    private void whenEmojisAreReady() {
        try {
            emojisRecycler.setAdapter(new MainEmojisAdapter(emojisList, this));
            searchBoxField.setFocusable(true);
            searchBoxField.setFocusableInTouchMode(true);
            searchBoxField.setEnabled(true);
            emojisRecycler.setVisibility(View.VISIBLE);
            isSearchEnabled = true;
            new Handler().postDelayed(() -> {
                shadAnim(loadView, "translationY", -1000, 300);
                shadAnim(loadView, "alpha", 0, 300);
            }, 1000);
        } catch (Exception e) {
            showCustomSnackBar(getString(R.string.error_msg_2), this);
            noEmojisFound(true);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void showFilterMenu(final View view) {
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.sortby_view, null);
        final PopupWindow popup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        LinearLayout bg = popupView.findViewById(R.id.bg);
        ImageView i1 = popupView.findViewById(R.id.i1);
        ImageView i2 = popupView.findViewById(R.id.i2);
        ImageView i3 = popupView.findViewById(R.id.i3);
        LinearLayout b1 = popupView.findViewById(R.id.b1);
        LinearLayout b2 = popupView.findViewById(R.id.b2);
        LinearLayout b3 = popupView.findViewById(R.id.b3);
        setImageViewRipple(i1, "#414141", "#7289DA");
        setImageViewRipple(i2, "#414141", "#7289DA");
        setImageViewRipple(i3, "#414141", "#7289DA");

        setClippedView(bg, "#FFFFFF", 25, 7);
        if (isSortingNew) {
            rippleRoundStroke(b1, "#EEEEEE", "#BDBDBD", 0, 0, "#EEEEEE");
        } else {
            rippleRoundStroke(b1, "#FFFFFF", "#EEEEEE", 0, 0, "#EEEEEE");
        }
        if (isSortingOld) {
            rippleRoundStroke(b2, "#EEEEEE", "#BDBDBD", 0, 0, "#EEEEEE");
        } else {
            rippleRoundStroke(b2, "#FFFFFF", "#EEEEEE", 0, 0, "#EEEEEE");
        }
        if (isSortingAlphabet) {
            rippleRoundStroke(b3, "#EEEEEE", "#BDBDBD", 0, 0, "#EEEEEE");
        } else {
            rippleRoundStroke(b3, "#FFFFFF", "#EEEEEE", 0, 0, "#EEEEEE");
        }
        b1.setOnClickListener(view1 -> {
            if (!isSortingNew) {
                isSortingNew = true;
                isSortingOld = false;
                isSortingAlphabet = false;
                Utils.sortListMap(emojisList, "id", false, false);
                emojisRecycler.setAdapter(new MainEmojisAdapter(emojisList, this));
                popup.dismiss();
            }
        });
        b2.setOnClickListener(view12 -> {
            if (!isSortingOld) {
                isSortingOld = true;
                isSortingNew = false;
                isSortingAlphabet = false;
                Utils.sortListMap(emojisList, "id", false, true);
                emojisRecycler.setAdapter(new MainEmojisAdapter(emojisList, this));
                popup.dismiss();
            }
        });
        b3.setOnClickListener(view13 -> {
            if (!isSortingAlphabet) {
                isSortingAlphabet = true;
                isSortingNew = false;
                isSortingOld = false;
                Utils.sortListMap(emojisList, "title", false, true);
                emojisRecycler.setAdapter(new MainEmojisAdapter(emojisList, this));
                popup.dismiss();
            }
        });
        popup.setAnimationStyle(android.R.style.Animation_Dialog);
        popup.setFocusable(false);
        popup.setOutsideTouchable(true);
        popup.showAsDropDown(view, 0, 0);
        popup.setBackgroundDrawable(null);
    }

    private void getEmojis() {
        if (sharedPref.getString("emojisData", "").isEmpty()) {
            sharedPref.edit().putString("emojisData", "").apply();
            sharedPref.edit().putString("categoriesData", "").apply();
            sharedPref.edit().putString("packsData", "").apply();
            noEmojisFound(true);
            disableSearch();
        } else {
            getEmojisTask();
        }
    }

    private void disableSearch() {
        isSearchEnabled = false;
        searchBoxField.setEnabled(false);
    }

    private void noEmojisFound(boolean isError) {
        shadAnim(emptyAnimation, "alpha", 0, 200);
        loadView.setVisibility(View.VISIBLE);
        shadAnim(loadView, "translationY", 0, 300);
        shadAnim(loadView, "alpha", 1, 300);
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

        TimerTask loadingTmr = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    emptyAnimation.setAnimation("animations/not_found.json");
                    emptyAnimation.playAnimation();
                });
            }
        };
        timer.schedule(loadingTmr, 200);
    }


    private void searchTask() {
        if (!searchQuery.isEmpty() && !lastSearchedEmoji.equals(searchQuery)) {
            Log.d("HYMOJI_SEARCH", "Current search query: " + searchQuery);
            lastSearchedEmoji = searchQuery;
            hideShowKeyboard(false, searchBoxField, PreviewCategoryActivity.this);
            isSearching = true;
            sortByBtn.setImageResource(R.drawable.round_clear_black_48dp);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                emojisList = new Gson().fromJson(new Gson().toJson(emojisBackupList), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());

                emojisCount = emojisList.size();
                searchPosition = emojisCount - 1;
                for (int i = 0; i < (int) (emojisCount); i++) {
                    if ((!Objects.requireNonNull(emojisList.get((int) searchPosition).get("submitted_by")).toString().toUpperCase().contains(searchQuery)
                            && !Objects.requireNonNull(emojisList.get((int) searchPosition).get("title")).toString().toUpperCase().contains(searchQuery))) {
                        emojisList.remove((int) (searchPosition));
                    }
                    searchPosition--;
                }

                Log.d("HYMOJI_SEARCH", "Found " + emojisList.size() + " emojis");


                handler.post(() -> {
                    if (emojisList.size() == 0) {
                        noEmojisFound(false);
                    } else {
                        emojisRecycler.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        emojisRecycler.setAdapter(new MainEmojisAdapter(emojisList, this));
                    }

                });
            });
        } else {
            sortByBtn.setImageResource(R.drawable.outline_filter_alt_black_48dp);
        }
    }

    private void getEmojisTask() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            loadCategorizedEmojis();
            if (isSortingNew) {
                Utils.sortListMap(emojisList, "id", false, false);
            } else if (isSortingOld) {
                Utils.sortListMap(emojisList, "id", false, true);
            } else if (isSortingAlphabet) {
                Utils.sortListMap(emojisList, "title", false, true);
            }

            handler.post(() -> {
                if (emojisList.isEmpty()) {
                    noEmojisFound(false);
                    disableSearch();
                } else {
                    whenEmojisAreReady();
                }
            });
        });

    }

    @Override
    public void onBackPressed() {
        if (!isEmojiSheetShown) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (adview != null) {
            adview.destroy();
        }
        super.onDestroy();
    }

    private void loadAds() {
        if (!sharedPref.getBoolean("isPremium", false)) {
            MobileAds.initialize(this, initializationStatus -> {
            });

            AdRequest adRequest = new AdRequest.Builder().build();
            adview.loadAd(adRequest);

            adview.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdClicked() {
                }

                @Override
                public void onAdClosed() {
                }
            });
        }
    }

}