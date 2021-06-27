package com.nerbly.bemoji.Activities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.appbar.AppBarLayout;
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

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.nerbly.bemoji.Adapters.MainEmojisAdapter.Recycler1Adapter;
import static com.nerbly.bemoji.Configurations.BANNER_AD_ID;
import static com.nerbly.bemoji.Configurations.EMOJIS_API_LINK;
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
    private static AppBarLayout appBar;
    private static EditText searchBoxField;
    private final Timer timer = new Timer();
    private double searchPosition = 0;
    private double emojisCount = 0;
    private boolean isRequestingServerEmojis = false;
    private boolean isSortingNew = false;
    private boolean isSortingOld = false;
    private boolean isSortingAlphabet = false;
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> suggestionsList = new ArrayList<>();
    private LinearLayout adview;
    private LottieAnimationView emptyAnimation;
    private LinearLayout searchBox;
    private ImageView sortByBtn;
    private ImageView searchBtn;
    private TextView emptyTitle;
    private RecyclerView chipRecycler;
    private RecyclerView emojisRecycler;
    private LinearLayout loadView;
    private RequestNetwork startGettingEmojis;
    private RequestNetwork.RequestListener RequestEmojis;
    private SharedPreferences sharedPref;
    private RequestNetwork getSuggestions;
    private RequestNetwork.RequestListener RequestSuggestions;

    public static void whenChipItemClicked(String suggestion) {
        isChipSelected = true;
        searchBoxField.setText(suggestion);
        appBar.setExpanded(true, true);
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
        appBar = findViewById(R.id._app_bar);
        adview = findViewById(R.id.adview);
        emptyTitle = findViewById(R.id.emptyTitle);
        emptyAnimation = findViewById(R.id.emptyAnimation);
        searchBox = findViewById(R.id.searchbox);
        searchBoxField = findViewById(R.id.searchField);
        sortByBtn = findViewById(R.id.imageview2);
        searchBtn = findViewById(R.id.searchBtn);
        chipRecycler = findViewById(R.id.chiprecycler);
        emojisRecycler = findViewById(R.id.packEmojisRecycler);
        loadView = findViewById(R.id.emptyview);
        startGettingEmojis = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);
        getSuggestions = new RequestNetwork(this);

        searchBoxField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSeq, int start, int count, int after) {
                if (charSeq.toString().trim().length() == 0 || isChipSelected) {
                    new searchTask().execute();
                    isChipSelected = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {

            }

            @Override
            public void afterTextChanged(Editable _param1) {

            }
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

        searchBtn.setOnClickListener(view -> new searchTask().execute());

        RequestEmojis = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                isRequestingServerEmojis = true;
                if (Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {
                    try {
                        emojisList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                        }.getType());
                        new getEmojisTask().execute("");
                    } catch (Exception e) {
                        Utils.showToast(getApplicationContext(), (e.toString()));
                    }
                } else {
                    try {
                        emojisList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                        }.getType());
                        new getEmojisTask().execute("");
                    } catch (Exception e) {
                        Utils.showToast(getApplicationContext(), (e.toString()));
                    }
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        };

        RequestSuggestions = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                if (!Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {
                    try {
                        suggestionsList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                        }.getType());
                        chipRecycler.setAdapter(new EmojisSuggestionsAdapter.ChipRecyclerAdapter(suggestionsList));
                        chipRecycler.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Utils.showToast(getApplicationContext(), (e.toString()));
                    }
                }
                if (response.contains("STOP_ALL")) {
                    emojisRecycler.setVisibility(View.GONE);
                    chipRecycler.setVisibility(View.GONE);
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
        overridePendingTransition(R.anim.fade_in, 0);
        emojisRecycler.setItemAnimator(null);
        chipRecycler.setItemAnimator(null);
        initEmojisRecycler();
        //set up chips
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        chipRecycler.setLayoutManager(layoutManager2);

        //set up search box
        androidx.core.view.ViewCompat.setNestedScrollingEnabled(emojisRecycler, true);

        //start getting emojis
        if (getIntent().getStringExtra("switchFrom").equals("categories")) {

            if (sharedPref.getString("emojisData", "").isEmpty()) {
                isSortingNew = true;
                startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, EMOJIS_API_LINK, "", RequestEmojis);
            } else {
                isRequestingServerEmojis = false;
                isSortingNew = true;
                //start getting emojis task
                new getEmojisTask().execute();
            }
        } else {
            isSortingNew = true;
            //the user is coming from search box
            //start getting emojis
            if (sharedPref.getString("emojisData", "").isEmpty()) {
                startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, EMOJIS_API_LINK, "", RequestEmojis);
                Log.i("task", "Emojis data is empty, we're getting emojis from the API");
            } else {
                isRequestingServerEmojis = false;
                //start getting emojis task
                new getEmojisTask().execute();
                Log.i("task", "Emojis are available, we're filtering emojis using AsyncTask");
            }
        }
        OverScrollDecoratorHelper.setUpOverScroll(emojisRecycler, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        OverScrollDecoratorHelper.setUpOverScroll(chipRecycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

        AudienceNetworkAds.initialize(this);
        AdView bannerAd = new AdView(this, BANNER_AD_ID, AdSize.BANNER_HEIGHT_50);
        adview.addView(bannerAd);
        bannerAd.loadAd();

        searchBoxField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                new searchTask().execute();
                return true;
            }
            return false;
        });
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
    }

    public void hideShowKeyboard(boolean choice, TextView edittext) {
        if (choice) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edittext, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        } else {
            android.view.View view = this.getCurrentFocus();
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void initEmojisRecycler() {

        float scaleFactor = getResources().getDisplayMetrics().density * 70;
        int number = getScreenWidth(this);
        int columns = (int) ((float) number / scaleFactor);
        GridLayoutManager layoutManager1 = new GridLayoutManager(this, columns);
        emojisRecycler.setLayoutManager(layoutManager1);
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
                    Log.i("filter", "We are filtering emojis according to their category. Removed position: " + searchPosition);
                }
                searchPosition--;
            }
            if (isSortingOld) {
                Collections.reverse(emojisList);
            }
        } catch (Exception e) {
            Utils.showToast(getApplicationContext(), (e.toString()));
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
                emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                popup.dismiss();
            }
        });
        b2.setOnClickListener(view12 -> {
            if (!isSortingOld) {
                isSortingOld = true;
                isSortingNew = false;
                isSortingAlphabet = false;
                Utils.sortListMap2(emojisList, "id", false, true);
                emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                popup.dismiss();
            }
        });
        b3.setOnClickListener(view13 -> {
            if (!isSortingAlphabet) {
                isSortingAlphabet = true;
                isSortingNew = false;
                isSortingOld = false;

                Utils.sortListMap(emojisList, "title", false, true);
                emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                popup.dismiss();
            }
        });
        popup.setAnimationStyle(android.R.style.Animation_Dialog);

        popup.showAsDropDown(view, 0, 0);
    }

    @SuppressLint("StaticFieldLeak")
    private class searchTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            if (searchBoxField.getText().toString().trim().length() > 0) {
                sortByBtn.setImageResource(R.drawable.round_clear_black_48dp);
            } else {
                sortByBtn.setImageResource(R.drawable.outline_filter_alt_black_48dp);
            }
        }

        @Override
        protected String doInBackground(String... params) {
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
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(String _result) {
            if (Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {
                if (emojisList.size() == 0) {
                    loadView.setVisibility(View.VISIBLE);
                    emojisRecycler.setVisibility(View.GONE);
                    emptyAnimation.setAnimation("animations/not_found.json");
                    emptyAnimation.playAnimation();
                    emptyTitle.setText(getString(R.string.emojis_not_found));
                } else {
                    emojisRecycler.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                }
            } else {
                if (emojisList.size() == 0) {
                    loadView.setVisibility(View.VISIBLE);
                    emojisRecycler.setVisibility(View.GONE);
                } else {
                    emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                    emojisRecycler.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    emptyAnimation.setAnimation("animations/not_found.json");
                    emptyAnimation.playAnimation();
                    emptyTitle.setText(getString(R.string.emojis_not_found));
                }
            }
            if (searchBoxField.getText().toString().trim().length() > 0) {
                sortByBtn.setImageResource(R.drawable.round_clear_black_48dp);
            } else {
                sortByBtn.setImageResource(R.drawable.outline_filter_alt_black_48dp);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getEmojisTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            double emojisScanPosition;
            if (Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {
                if (isRequestingServerEmojis) {
                    sharedPref.edit().putString("emojisData", new Gson().toJson(emojisList)).apply();
                    emojisCount = emojisList.size();
                    emojisScanPosition = emojisCount - 1;
                    for (int i = 0; i < (int) (emojisCount); i++) {
                        if (!Objects.requireNonNull(emojisList.get((int) emojisScanPosition).get("category")).toString().equals(getIntent().getStringExtra("category_id"))) {
                            emojisList.remove((int) (emojisScanPosition));
                        }
                        emojisScanPosition--;
                    }
                    if (isSortingOld) {
                        Collections.reverse(emojisList);
                    }
                } else {
                    loadCategorizedEmojis();
                }
            } else {
                if (isRequestingServerEmojis) {
                    emojisCount = emojisList.size();
                    emojisScanPosition = emojisCount - 1;
                    for (int i = 0; i < (int) (emojisCount); i++) {
                        if (Objects.requireNonNull(emojisList.get((int) emojisScanPosition).get("category")).toString().equals("9.0")) {
                            emojisList.remove((int) (emojisScanPosition));
                        }
                        emojisScanPosition--;
                    }
                    sharedPref.edit().putString("emojisData", new Gson().toJson(emojisList)).apply();

                } else {
                    emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                }
                if (isSortingNew) {
                    Utils.sortListMap2(emojisList, "id", false, false);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(String result) {
            if (Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {
                if (emojisList.size() == 0) {
                    loadView.setVisibility(View.VISIBLE);
                    emojisRecycler.setVisibility(View.GONE);
                    searchBox.setVisibility(View.GONE);
                    emptyTitle.setText(getString(R.string.emojis_not_found));
                    shadAnim(emptyAnimation, "translationX", -200, 200);
                    shadAnim(emptyAnimation, "alpha", 0, 200);
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
                } else {
                    emojisRecycler.setVisibility(View.VISIBLE);
                    emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                    whenEmojisAreReady();
                }
            } else {
                emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                getSuggestions.startRequestNetwork(RequestNetworkController.GET, EMOJIS_SUGGESTIONS_SOURCE, "", RequestSuggestions);
                whenEmojisAreReady();
            }
        }
    }
}