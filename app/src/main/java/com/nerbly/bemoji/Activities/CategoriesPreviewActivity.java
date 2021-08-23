package com.nerbly.bemoji.Activities;


import static com.nerbly.bemoji.Adapters.MainEmojisAdapter.Gridview1Adapter;
import static com.nerbly.bemoji.Adapters.MainEmojisAdapter.isEmojiSheetShown;
import static com.nerbly.bemoji.Functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.Functions.SideFunctions.hideShowKeyboard;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.RippleEffects;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.setImageViewRipple;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.statusBarColor;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Functions.Utils;
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

public class CategoriesPreviewActivity extends AppCompatActivity {
    private static EditText searchBoxField;
    private final Timer timer = new Timer();
    private double searchPosition = 0;
    private double emojisCount = 0;
    private boolean isSortingNew = true;
    private boolean isSortingOld = false;
    private boolean isSortingAlphabet = false;
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private AdView adview;
    private LottieAnimationView emptyAnimation;
    private LinearLayout searchBox;
    private ImageView sortByBtn;
    private ImageView searchBtn;
    private TextView emptyTitle;
    private GridView emojisRecycler;
    private HashMap<String, Object> emojisMap = new HashMap<>();
    private LinearLayout loadView;
    private SharedPreferences sharedPref;
    private boolean isSearching = false;
    private boolean isGettingDataFirstTime = true;
    private String lastSearchedEmoji = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.categories_emojis);
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
        emojisRecycler = findViewById(R.id.emojisRecycler);
        loadView = findViewById(R.id.emptyview);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        searchBoxField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSeq, int start, int count, int after) {
                if (charSeq.toString().trim().length() == 0 && isSearching) {
                    isSearching = false;
                    getEmojis();
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
                String searchValue = searchBoxField.getText().toString().trim();
                if (!searchValue.isEmpty() && !lastSearchedEmoji.equals(searchValue)) {
                    lastSearchedEmoji = searchValue;
                    hideShowKeyboard(false, searchBoxField, CategoriesPreviewActivity.this);
                    isSearching = true;
                    searchTask();
                    return true;
                }

            }
            return false;
        });

        sortByBtn.setOnClickListener(_view -> {
            if (searchBoxField.getText().toString().trim().length() > 0) {
                lastSearchedEmoji = "";
                searchBoxField.setText("");
            } else {
                searchBoxField.setEnabled(false);
                searchBoxField.setEnabled(true);
                showFilterMenu(sortByBtn);
            }
        });

        searchBtn.setOnClickListener(_view -> {
            String searchValue = searchBoxField.getText().toString().trim();
            if (!searchValue.isEmpty() && !lastSearchedEmoji.equals(searchValue)) {
                lastSearchedEmoji = searchBoxField.getText().toString().trim();
                hideShowKeyboard(false, searchBoxField, CategoriesPreviewActivity.this);
                isSearching = true;
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

    public void initEmojisRecycler() {
        float scaleFactor = getResources().getDisplayMetrics().density * 70;
        int number = getScreenWidth(this);
        int columns = (int) ((float) number / scaleFactor);
        emojisRecycler.setNumColumns(columns);
    }

    public void loadCategorizedEmojis() {
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
                emojisMap = new HashMap<>();
                emojisMap.put("image", emojisObject.getString("image"));
                emojisMap.put("name", emojisObject.getString("name"));
                emojisMap.put("title", emojisObject.getString("title"));
                emojisMap.put("submitted_by", emojisObject.getString("submitted_by"));
                emojisMap.put("id", emojisObject.getInt("id"));
                if (emojisObject.getInt("category") == getIntent().getIntExtra("category_id", 0)) {
                    emojisList.add(emojisMap);
                }
            }

        } catch (Exception e) {
            Log.e("EmojisRequestListener", e.toString());
        }

        if (isSortingNew) {
            Utils.sortListMap2(emojisList, "id", false, false);
        } else if (isSortingOld) {
            Collections.reverse(emojisList);
        } else if (isSortingAlphabet) {
            Utils.sortListMap(emojisList, "title", false, true);
        }
    }

    private void whenEmojisAreReady() {
        new Handler().postDelayed(() -> {
            shadAnim(loadView, "translationY", -1000, 300);
            shadAnim(loadView, "alpha", 0, 300);
            searchBoxField.setEnabled(true);

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
            getEmojisTask();
        }


    }

    private void noEmojisFound(boolean isError) {
        if (isError) {
            emptyTitle.setText(getString(R.string.error_msg_2));
        } else {
            emptyTitle.setText(getString(R.string.emojis_not_found));
        }
        shadAnim(emptyAnimation, "translationX", -200, 200);
        shadAnim(emptyAnimation, "alpha", 0, 200);
        loadView.setVisibility(View.VISIBLE);
        emptyAnimation.setAnimation("animations/not_found.json");
        emptyAnimation.playAnimation();
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
                    if ((!Objects.requireNonNull(emojisList.get((int) searchPosition).get("submitted_by")).toString().toLowerCase().contains(searchBoxField.getText().toString().trim().toLowerCase())
                            && !Objects.requireNonNull(emojisList.get((int) searchPosition).get("title")).toString().toLowerCase().contains(searchBoxField.getText().toString().trim().toLowerCase()))
                            || !String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(emojisList.get((int) searchPosition).get("category")).toString()))).equals(getIntent().getStringExtra("category_id"))) {
                        emojisList.remove((int) (searchPosition));
                    }
                    searchPosition--;
                }
            } else {
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
            }

            handler.post(() -> {
                if (emojisList.size() == 0) {
                    noEmojisFound(false);
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
            loadCategorizedEmojis();
            if (isSortingNew) {
                Utils.sortListMap2(emojisList, "id", false, false);
            } else if (isSortingOld) {
                Collections.reverse(emojisList);
            } else if (isSortingAlphabet) {
                Utils.sortListMap(emojisList, "title", false, true);
            }

            handler.post(() -> {
                if (emojisList.isEmpty()) {
                    noEmojisFound(false);
                } else {
                    emojisRecycler.setVisibility(View.VISIBLE);
                    try {
                        emojisRecycler.setAdapter(new Gridview1Adapter(emojisList));
                        whenEmojisAreReady();
                    } catch (Exception e) {
                        showCustomSnackBar(getString(R.string.error_msg_2), this);
                        noEmojisFound(true);
                    }

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