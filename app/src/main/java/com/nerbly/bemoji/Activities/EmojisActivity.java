package com.nerbly.bemoji.Activities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Adapters.EmojisSuggestionsAdapter;
import com.nerbly.bemoji.Functions.RequestNetwork;
import com.nerbly.bemoji.Functions.RequestNetworkController;
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

import static com.nerbly.bemoji.Adapters.MainEmojisAdapter.Gridview1Adapter;
import static com.nerbly.bemoji.Adapters.MainEmojisAdapter.isEmojiSheetShown;
import static com.nerbly.bemoji.Configurations.BANNER_AD_ID;
import static com.nerbly.bemoji.Configurations.EMOJIS_SUGGESTIONS_SOURCE;
import static com.nerbly.bemoji.Functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.RippleEffects;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.setImageViewRipple;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.statusBarColor;

public class EmojisActivity extends AppCompatActivity {
    public static boolean isChipSelected = false;
    private static EditText searchBoxField;
    private final Timer timer = new Timer();
    private double searchPosition = 0;
    private double emojisCount = 0;
    private boolean isSortingNew = true;
    private boolean isSortingOld = false;
    private boolean isSortingAlphabet = false;
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> suggestionsList = new ArrayList<>();
    private LinearLayout adview;
    private LottieAnimationView emptyAnimation;
    private LinearLayout searchBox;
    private LinearLayout chipsasshimmerback;
    private ImageView sortByBtn;
    private ImageView searchBtn;
    private TextView emptyTitle;
    private RecyclerView chipRecycler;
    private GridView emojisRecycler;
    private LinearLayout loadView;
    private ScrollView chipsscroller;
    private SharedPreferences sharedPref;
    private LinearLayout shimmer1;
    private LinearLayout shimmer2;
    private LinearLayout shimmer5;
    private LinearLayout shimmer3;
    private LinearLayout shimmer4;
    private RequestNetwork getSuggestions;
    private RequestNetwork.RequestListener RequestSuggestions;
    private boolean isSearching = false;
    private boolean isGettingDataFirstTime = true;

    public static void whenChipItemClicked(String suggestion) {
        isChipSelected = true;
        searchBoxField.setText(suggestion);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.emojis);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
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
        chipRecycler = findViewById(R.id.chiprecycler);
        emojisRecycler = findViewById(R.id.emojisRecycler);
        chipsscroller = findViewById(R.id.chipsscroller);
        loadView = findViewById(R.id.emptyview);
        chipsasshimmerback = findViewById(R.id.chipsasshimmerback);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);
        getSuggestions = new RequestNetwork(this);

        shimmer1 = findViewById(R.id.shimmer1);
        shimmer2 = findViewById(R.id.shimmer2);
        shimmer5 = findViewById(R.id.shimmer5);
        shimmer3 = findViewById(R.id.shimmer3);
        shimmer4 = findViewById(R.id.shimmer4);

        searchBoxField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSeq, int start, int count, int after) {
                if (charSeq.toString().trim().length() == 0 && isSearching) {
                    isSearching = false;
                    getEmojis();
                } else if (isChipSelected) {
                    isChipSelected = false;
                    isSearching = true;
                    searchTask();
                }
                if (searchBoxField.getText().toString().trim().length() > 0) {
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
                if (searchBoxField.getText().toString().trim().length() > 0) {
                    hideShowKeyboard(false, searchBoxField);
                    isSearching = true;
                    searchTask();
                    return true;
                }

            }
            return false;
        });

        sortByBtn.setOnClickListener(_view -> {
            if (searchBoxField.getText().toString().trim().length() > 0) {
                searchBoxField.setText("");
            } else {
                searchBoxField.setEnabled(false);
                searchBoxField.setEnabled(true);
                showFilterMenu(sortByBtn);
            }
        });

        searchBtn.setOnClickListener(_view -> {
            if (searchBoxField.getText().toString().trim().length() > 0) {
                hideShowKeyboard(false, searchBoxField);
                isSearching = true;
                searchTask();
            }
        });

        RequestSuggestions = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                if (!Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {
                    try {
                        suggestionsList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                        }.getType());
                        chipRecycler.setAdapter(new EmojisSuggestionsAdapter.ChipRecyclerAdapter(suggestionsList));
                        chipRecycler.setVisibility(View.VISIBLE);
                        chipsasshimmerback.setVisibility(View.GONE);

                    } catch (Exception e) {
                        Utils.showToast(getApplicationContext(), (e.toString()));
                    }
                }
                if (response.contains("STOP_ALL")) {
                    emojisRecycler.setVisibility(View.GONE);
                    chipRecycler.setVisibility(View.GONE);
                    chipsasshimmerback.setVisibility(View.GONE);
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        };
    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }


    public void LOGIC_BACKEND() {
//        overridePendingTransition(R.anim.fade_in, 0);

        chipRecycler.setItemAnimator(null);
        initEmojisRecycler();

        //set up chips
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        chipRecycler.setLayoutManager(layoutManager2);

        //start getting emojis

        getEmojis();

        OverScrollDecoratorHelper.setUpOverScroll(chipRecycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

        AudienceNetworkAds.initialize(this);
        AdView bannerAd = new AdView(this, BANNER_AD_ID, AdSize.BANNER_HEIGHT_50);
        adview.addView(bannerAd);
        bannerAd.loadAd();
    }

    public void LOGIC_FRONTEND() {
        rippleRoundStroke(searchBox, "#FFFFFF", "#FFFFFF", 200, 1, "#C4C4C4");
        if (Build.VERSION.SDK_INT < 23) {
            statusBarColor("#7289DA", this);
            LIGHT_ICONS(this);
        } else {
            statusBarColor("#FFFFFF", this);
            DARK_ICONS(this);
        }
        RippleEffects("#E0E0E0", sortByBtn);
        RippleEffects("#E0E0E0", searchBtn);

        setClippedView(shimmer1, "#FFFFFF", 200, 0);
        setClippedView(shimmer2, "#FFFFFF", 200, 0);
        setClippedView(shimmer3, "#FFFFFF", 200, 0);
        setClippedView(shimmer4, "#FFFFFF", 200, 0);
        setClippedView(shimmer5, "#FFFFFF", 200, 0);
    }

    public void hideShowKeyboard(boolean bool, TextView edittext) {
        try {
            if (bool) {
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edittext, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            } else {
                android.view.View view = this.getCurrentFocus();
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initEmojisRecycler() {
        float scaleFactor = getResources().getDisplayMetrics().density * 70;
        int number = getScreenWidth(this);
        int columns = (int) ((float) number / scaleFactor);
        emojisRecycler.setNumColumns(columns);
        //emojisRecycler.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        emojisRecycler.setVerticalSpacing(0);
        emojisRecycler.setHorizontalSpacing(0);
    }

    public void loadCategorizedEmojis() {
        try {
            emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            emojisCount = emojisList.size();
            searchPosition = emojisCount - 1;
            for (int i = 0; i < (int) (emojisCount); i++) {
                if (!String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(emojisList.get((int) searchPosition).get("category")).toString()))).equals(getIntent().getStringExtra("category_id"))) {
                    emojisList.remove((int) (searchPosition));
                }
                searchPosition--;
            }
            if (isSortingNew) {
                Utils.sortListMap2(emojisList, "id", false, false);
            } else if (isSortingOld) {
                Collections.reverse(emojisList);
            } else if (isSortingAlphabet) {
                Utils.sortListMap(emojisList, "title", false, true);
            }
        } catch (Exception e) {
            Log.e("Emojis Error", e.toString());
        }
    }

    private void whenEmojisAreReady() {
        new Handler().postDelayed(() -> {
            shadAnim(loadView, "translationY", -1000, 300);
            shadAnim(loadView, "alpha", 0, 300);
            searchBoxField.setEnabled(true);
            if (Objects.equals(getIntent().getStringExtra("switchFrom"), "search")) {
                searchBoxField.requestFocus();
                new Handler().postDelayed(() -> runOnUiThread(() -> hideShowKeyboard(true, searchBoxField)), 1000);
            }

        }, 1000);
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
                Utils.sortListMap2(emojisList, "id", false, false);
                emojisRecycler.setAdapter(new Gridview1Adapter(emojisList));
                popup.dismiss();
            }
        });
        b2.setOnClickListener(view12 -> {
            if (!isSortingOld) {
                isSortingOld = true;
                isSortingNew = false;
                isSortingAlphabet = false;
                Utils.sortListMap2(emojisList, "id", false, true);
                emojisRecycler.setAdapter(new Gridview1Adapter(emojisList));
                popup.dismiss();
            }
        });
        b3.setOnClickListener(view13 -> {
            if (!isSortingAlphabet) {
                isSortingAlphabet = true;
                isSortingNew = false;
                isSortingOld = false;

                Utils.sortListMap(emojisList, "title", false, true);
                emojisRecycler.setAdapter(new Gridview1Adapter(emojisList));
                popup.dismiss();
            }
        });
        popup.setAnimationStyle(android.R.style.Animation_Dialog);

        popup.showAsDropDown(view, 0, 0);
    }

    private void getEmojis() {

        if (getIntent().getStringExtra("switchFrom").equals("categories")) {

            if (isGettingDataFirstTime) {
                isGettingDataFirstTime = false;
                isSortingNew = true;
            }

            if (sharedPref.getString("emojisData", "").isEmpty()) {
                sharedPref.edit().putString("emojisData", "").apply();
                sharedPref.edit().putString("categoriesData", "").apply();
                sharedPref.edit().putString("packsData", "").apply();
                sharedPref.edit().putString("isAskingForReload", "true").apply();
            } else {
                //start getting emojis task
                getEmojisTask();
            }
        } else {
            //the user is coming from search box
            //start getting emojis
            if (sharedPref.getString("emojisData", "").isEmpty()) {
                Log.i("task", "Emojis data is empty, user must restart the app");
            } else {
                //start getting emojis task
                getEmojisTask();
                Log.i("task", "Emojis are available, we're filtering emojis using AsyncTask");
            }
        }

    }

    private void noEmojisFound() {
        shadAnim(emptyAnimation, "translationX", -200, 200);
        shadAnim(emptyAnimation, "alpha", 0, 200);
        loadView.setVisibility(View.VISIBLE);
        emojisRecycler.setVisibility(View.GONE);
        emptyAnimation.setAnimation("animations/not_found.json");
        emptyAnimation.playAnimation();
        emptyTitle.setText(getString(R.string.emojis_not_found));
        TimerTask loadingTmr = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
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


    private void searchTask() {

        if (searchBoxField.getText().toString().trim().length() > 0) {
            sortByBtn.setImageResource(R.drawable.round_clear_black_48dp);
        } else {
            sortByBtn.setImageResource(R.drawable.outline_filter_alt_black_48dp);
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            if (searchBoxField.getText().toString().trim().length() > 0) {

                emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
                emojisCount = emojisList.size();
                searchPosition = emojisCount - 1;
                for (int i = 0; i < (int) (emojisCount); i++) {
                    if (Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {

                        if ((!Objects.requireNonNull(emojisList.get((int) searchPosition).get("submitted_by")).toString().toLowerCase().contains(searchBoxField.getText().toString().trim().toLowerCase())
                                && !Objects.requireNonNull(emojisList.get((int) searchPosition).get("title")).toString().toLowerCase().contains(searchBoxField.getText().toString().trim().toLowerCase()))
                                || !String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(emojisList.get((int) searchPosition).get("category")).toString()))).equals(getIntent().getStringExtra("category_id"))) {
                            emojisList.remove((int) (searchPosition));
                        }
                    } else {
                        if (!Objects.requireNonNull(emojisList.get((int) searchPosition).get("submitted_by")).toString().toLowerCase().contains(searchBoxField.getText().toString().trim().toLowerCase())
                                && !Objects.requireNonNull(emojisList.get((int) searchPosition).get("title")).toString().toLowerCase().contains(searchBoxField.getText().toString().trim().toLowerCase())) {
                            emojisList.remove((int) (searchPosition));
                        }
                    }
                    searchPosition--;
                }
            } else {
                if (Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {
                    try {
                        emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                        }.getType());
                        emojisCount = emojisList.size();
                        searchPosition = emojisCount - 1;
                        for (int i = 0; i < (int) (emojisCount); i++) {
                            if (!String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(emojisList.get((int) searchPosition).get("category")).toString()))).equals(getIntent().getStringExtra("category_id"))) {
                                emojisList.remove((int) (searchPosition));
                            }
                            searchPosition--;
                        }
                    } catch (Exception e) {
                        Utils.showToast(getApplicationContext(), (e.toString()));
                    }
                } else {
                    emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                }
            }

            handler.post(() -> {
                if (emojisList.size() == 0) {
                    noEmojisFound();
                } else {
                    emojisRecycler.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    emojisRecycler.setAdapter(new Gridview1Adapter(emojisList));
                }

            });
        });
    }

    private void getEmojisTask() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            if (Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {
                loadCategorizedEmojis();
            } else {
                emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
            }
            if (isSortingNew) {
                Utils.sortListMap2(emojisList, "id", false, false);
            } else if (isSortingOld) {
                Collections.reverse(emojisList);
            } else if (isSortingAlphabet) {
                Utils.sortListMap(emojisList, "title", false, true);
            }

            handler.post(() -> {

                if (Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {
                    if (emojisList.size() == 0) {
                        noEmojisFound();
                    } else {
                        emojisRecycler.setVisibility(View.VISIBLE);
                        emojisRecycler.setAdapter(new Gridview1Adapter(emojisList));
                        whenEmojisAreReady();
                    }
                    searchBox.setVisibility(View.GONE);
                    chipsscroller.setVisibility(View.GONE);
                } else {
                    emojisRecycler.setAdapter(new Gridview1Adapter(emojisList));
                    getSuggestions.startRequestNetwork(RequestNetworkController.GET, EMOJIS_SUGGESTIONS_SOURCE, "", RequestSuggestions);
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
}