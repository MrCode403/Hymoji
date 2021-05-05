package com.nerbly.bemoji;


import static com.nerbly.bemoji.Adapters.MainEmojisAdapter.Recycler1Adapter;
import static com.nerbly.bemoji.Functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.RippleEffects;
import static com.nerbly.bemoji.UI.MainUIMethods.changeActivityFont;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.setImageViewRipple;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class EmojisActivity extends AppCompatActivity {
    private static AppBarLayout _app_bar;
    private static EditText searchBoxField;
    private final Timer _timer = new Timer();
    private final boolean isCategorized = false;
    private final Intent toPreview = new Intent();
    GridLayoutManager layoutManager1 = new GridLayoutManager(this, 3);
    private double searchPosition = 0;
    private double emojisCount = 0;
    private boolean isSearching = false;
    private boolean isRequestingServerEmojis = false;
    private boolean isSortingNew = false;
    private boolean isSortingOld = false;
    private boolean isSortingAlphabet = false;
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> suggestionsList = new ArrayList<>();
    private LinearLayout adview;
    private LinearLayout searchBox;
    private ImageView imageview2;
    private RecyclerView chiprecycler;
    private RecyclerView emojisRecycler;
    private LinearLayout emptyview;
    private RequestNetwork startGettingEmojis;
    private RequestNetwork.RequestListener RequestEmojis;
    private SharedPreferences sharedPref;
    private RequestNetwork getSuggestions;
    private RequestNetwork.RequestListener RequestSuggestions;

    public static void whenChipItemClicked(String suggestion) {
        searchBoxField.setText(suggestion);
        _app_bar.setExpanded(true, true);
    }

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.emojis);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        _app_bar = findViewById(R.id._app_bar);
        Toolbar toolbar = findViewById(R.id._toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                onBackPressed();
            }
        });
        adview = findViewById(R.id.adview);
        searchBox = findViewById(R.id.searchbox);
        searchBoxField = findViewById(R.id.edittext1);
        imageview2 = findViewById(R.id.imageview2);
        chiprecycler = findViewById(R.id.chiprecycler);
        emojisRecycler = findViewById(R.id.packEmojisRecycler);
        emptyview = findViewById(R.id.emptyview);
        startGettingEmojis = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);
        getSuggestions = new RequestNetwork(this);

        searchBoxField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSeq, int _param2, int _param3, int _param4) {
                new searchTask().execute("");
            }

            @Override
            public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {

            }

            @Override
            public void afterTextChanged(Editable _param1) {

            }
        });

        imageview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (searchBoxField.getText().toString().trim().length() > 0) {
                    searchBoxField.setText("");
                } else {
                    _showFilterMenu(imageview2);
                }
            }
        });

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
                        chiprecycler.setAdapter(new EmojisSuggestionsAdapter.ChipRecyclerAdapter(suggestionsList));
                        chiprecycler.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Utils.showToast(getApplicationContext(), (e.toString()));
                    }
                }
                if (response.contains("STOP_ALL")) {
                    emojisRecycler.setVisibility(View.GONE);
                    chiprecycler.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        if (searchBoxField.getText().toString().trim().isEmpty()) {
            supportFinishAfterTransition();
        } else {
            searchBoxField.setText("");
        }
    }

    public void LOGIC_BACKEND() {
        //set up chips
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        chiprecycler.setLayoutManager(layoutManager2);

        //set up search box
        Objects.requireNonNull(getSupportActionBar()).hide();
        androidx.core.view.ViewCompat.setNestedScrollingEnabled(emojisRecycler, true);

        //start getting emojis
        if (getIntent().getStringExtra("switchFrom").equals("categories")) {

            if (sharedPref.getString("emojisData", "").isEmpty()) {
                isSortingNew = true;
                startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/", "", RequestEmojis);
            } else {
                isRequestingServerEmojis = false;
                isSortingNew = true;
                new getEmojisTask().execute();
            }
        } else {
            isSortingNew = true;
            //the user is coming from search box
            if (Objects.equals(getIntent().getStringExtra("switchFrom"), "search")) {

                transitionComplete(searchBox, "searchbox");
                searchBoxField.requestFocus();
                TimerTask fixUIIssues = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideShowKeyboard(true, searchBoxField);
                            }
                        });
                    }
                };
                _timer.schedule(fixUIIssues, 500);
            }
            //start getting emojis
            if (sharedPref.getString("emojisData", "").isEmpty()) {
                startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/", "", RequestEmojis);
            } else {
                isRequestingServerEmojis = false;
                new getEmojisTask().execute();
            }
        }
        rotationListener();
        OverScrollDecoratorHelper.setUpOverScroll(emojisRecycler, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        OverScrollDecoratorHelper.setUpOverScroll(chiprecycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        AudienceNetworkAds.initialize(this);

        AdView bannerAd = new AdView(this, "3773974092696684_3774022966025130", AdSize.BANNER_HEIGHT_50);

        adview.addView(bannerAd);

        bannerAd.loadAd();
    }

    public void LOGIC_FRONTEND() {
        changeActivityFont("whitney", this);

        rippleRoundStroke(searchBox, "#FFFFFF", "#FFFFFF", 200, 1, "#C4C4C4");

        DARK_ICONS(this);

        RippleEffects("#E0E0E0", imageview2);
    }

    public void transitionComplete(final View _view, final String _transitionName) {
        _view.setTransitionName(_transitionName);
    }

    public void hideShowKeyboard(final boolean _choice, final TextView _edittext) {
        if (_choice) {

            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(_edittext, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        } else {

            android.view.View view = this.getCurrentFocus();
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void rotationListener() {
        float scalefactor = getResources().getDisplayMetrics().density * 60;

        int number = getScreenWidth(this);

        int columns = (int) ((float) number / scalefactor);

        layoutManager1 = new GridLayoutManager(this, columns);

        emojisRecycler.setLayoutManager(layoutManager1);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


            float scalefactor = getResources().getDisplayMetrics().density * 60;

            int number = getScreenWidth(this);

            int columns = (int) ((float) number / scalefactor);

            layoutManager1 = new GridLayoutManager(this, columns);

            emojisRecycler.setLayoutManager(layoutManager1);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            float scalefactor = getResources().getDisplayMetrics().density * 60;

            int number = getScreenWidth(this);

            int columns = (int) ((float) number / scalefactor);

            layoutManager1 = new GridLayoutManager(this, columns);


            emojisRecycler.setLayoutManager(layoutManager1);
        }
    }

    public void loadCategorizedEmojis() {
        try {
            emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            emojisCount = emojisList.size();
            searchPosition = emojisCount - 1;
            for (int _repeat18 = 0; _repeat18 < (int) (emojisCount); _repeat18++) {
                if (!String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(emojisList.get((int) searchPosition).get("category")).toString()))).equals(getIntent().getStringExtra("category_id"))) {
                    emojisList.remove((int) (searchPosition));
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

    public void _showFilterMenu(final View _view) {
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.sortby_view, null);
        final PopupWindow popup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        TextView title = popupView.findViewById(R.id.activityTitle);
        TextView t1 = popupView.findViewById(R.id.t1);
        TextView t2 = popupView.findViewById(R.id.t2);
        TextView t3 = popupView.findViewById(R.id.t3);
        LinearLayout bg = popupView.findViewById(R.id.bg);
        ImageView i1 = popupView.findViewById(R.id.i1);
        ImageView i2 = popupView.findViewById(R.id.i2);
        ImageView i3 = popupView.findViewById(R.id.i3);
        LinearLayout b1 = popupView.findViewById(R.id.b1);
        LinearLayout b2 = popupView.findViewById(R.id.b2);
        LinearLayout b3 = popupView.findViewById(R.id.b3);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
        t1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
        t2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
        t3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
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
        b1.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!isSortingNew) {
                    isSortingNew = true;
                    isSortingOld = false;
                    isSortingAlphabet = false;
                    Utils.sortListMap2(emojisList, "id", false, false);
                    emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                    popup.dismiss();
                }
            }
        });
        b2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!isSortingOld) {
                    isSortingOld = true;
                    isSortingNew = false;
                    isSortingAlphabet = false;
                    Utils.sortListMap2(emojisList, "id", false, true);
                    emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                    popup.dismiss();
                }
            }
        });
        b3.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!isSortingAlphabet) {
                    isSortingAlphabet = true;
                    isSortingNew = false;
                    isSortingOld = false;

                    Utils.sortListMap(emojisList, "title", false, true);
                    emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                    popup.dismiss();
                }
            }
        });
        popup.setAnimationStyle(android.R.style.Animation_Dialog);

        popup.showAsDropDown(_view, 0, 0);

        popup.setBackgroundDrawable(new BitmapDrawable());

    }

    @SuppressLint("StaticFieldLeak")
    private class searchTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            if (searchBoxField.getText().toString().trim().length() > 0) {
                imageview2.setImageResource(R.drawable.round_clear_black_48dp);
            } else {
                imageview2.setImageResource(R.drawable.outline_filter_alt_black_48dp);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            isSearching = true;

            if (searchBoxField.getText().toString().trim().length() > 0) {

                emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
                emojisCount = emojisList.size();
                searchPosition = emojisCount - 1;
                for (int _repeat22 = 0; _repeat22 < (int) (emojisCount); _repeat22++) {
                    if (Objects.equals(getIntent().getStringExtra("switchFrom"), "categories")) {

                        if ((!Objects.requireNonNull(emojisList.get((int) searchPosition).get("submitted_by")).toString().toLowerCase().contains(searchBoxField.getText().toString().trim().toLowerCase()) && !Objects.requireNonNull(emojisList.get((int) searchPosition).get("title")).toString().toLowerCase().contains(searchBoxField.getText().toString().trim().toLowerCase())) || !String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(emojisList.get((int) searchPosition).get("category")).toString()))).equals(getIntent().getStringExtra("category_id"))) {
                            emojisList.remove((int) (searchPosition));
                        }
                    } else {
                        if (!Objects.requireNonNull(emojisList.get((int) searchPosition).get("submitted_by")).toString().toLowerCase().contains(searchBoxField.getText().toString().trim().toLowerCase()) && !Objects.requireNonNull(emojisList.get((int) searchPosition).get("title")).toString().toLowerCase().contains(searchBoxField.getText().toString().trim().toLowerCase())) {
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
                        for (int _repeat103 = 0; _repeat103 < (int) (emojisCount); _repeat103++) {
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
                    emptyview.setVisibility(View.VISIBLE);
                    emojisRecycler.setVisibility(View.GONE);
                } else {
                    emojisRecycler.setVisibility(View.VISIBLE);
                    emptyview.setVisibility(View.GONE);
                    emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                }
            } else {
                if (emojisList.size() == 0) {
                    emptyview.setVisibility(View.VISIBLE);
                    emojisRecycler.setVisibility(View.GONE);
                } else {
                    emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                    emojisRecycler.setVisibility(View.VISIBLE);
                    emptyview.setVisibility(View.GONE);
                }
            }
            isSearching = false;
            if (searchBoxField.getText().toString().trim().length() > 0) {
                imageview2.setImageResource(R.drawable.round_clear_black_48dp);
            } else {
                imageview2.setImageResource(R.drawable.outline_filter_alt_black_48dp);
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
                    emptyview.setVisibility(View.VISIBLE);
                    emojisRecycler.setVisibility(View.GONE);
                    searchBox.setVisibility(View.GONE);
                } else {
                    emojisRecycler.setVisibility(View.VISIBLE);
                    emptyview.setVisibility(View.GONE);
                    emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                }
            } else {

                emojisRecycler.setAdapter(new Recycler1Adapter(emojisList));
                getSuggestions.startRequestNetwork(RequestNetworkController.GET, "https://nerbly.com/bemoji/suggestions.json", "", RequestSuggestions);
            }
        }
    }
}