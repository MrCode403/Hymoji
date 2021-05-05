package com.nerbly.bemoji;

import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.NavStatusBarColor;
import static com.nerbly.bemoji.UI.MainUIMethods.changeActivityFont;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Adapters.HomePacksAdapter;
import com.nerbly.bemoji.Adapters.LoadingPacksAdapter;
import com.nerbly.bemoji.Adapters.LocalEmojisAdapter;
import com.nerbly.bemoji.Functions.FileManager;
import com.nerbly.bemoji.Functions.RequestNetwork;
import com.nerbly.bemoji.Functions.RequestNetworkController;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.UI.MainUIMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class HomeActivity extends AppCompatActivity {

    public static ArrayList<HashMap<String, Object>> packsList = new ArrayList<>();
    private final ArrayList<HashMap<String, Object>> shimmerList = new ArrayList<>();
    private final Intent toSearch = new Intent();
    private final Intent toCategories = new Intent();
    private final Intent toHelp = new Intent();
    private final Intent toSettings = new Intent();
    private final Intent toPacks = new Intent();
    FirebaseAnalytics mFirebaseAnalytics;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    com.google.android.material.snackbar.Snackbar snackBarView;
    com.google.android.material.snackbar.Snackbar.SnackbarLayout sblayout;
    private FileManager fileManager;
    private HashMap<String, Object> categoriesMap = new HashMap<>();
    private double emojisCount = 0;
    private double emojisScanPosition = 0;
    private double localEmojisScanPosition = 0;
    private String localEmojisScanPath = "";
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> categoriesList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> localEmojisList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> backendPacksList = new ArrayList<>();
    private LinearLayout adview;
    private ScrollView vscroll2;
    private TextView textview1;
    private LinearLayout loadingView;
    private LinearLayout mainView;
    private LinearLayout shimmer1;
    private LinearLayout shimmer2;
    private LinearLayout shimmer7;
    private RecyclerView loadingRecycler;
    private LinearLayout shimmer3;
    private LinearLayout shimmer4;
    private LinearLayout shimmer6;
    private LinearLayout shimmer5;
    private MaterialCardView searchcard;
    private LinearLayout localemojisview;
    private LinearLayout linear30;
    private RecyclerView packs_recycler;
    private RecyclerView local_recycler;
    private LinearLayout dock1;
    private LinearLayout dock2;
    private TextView dock_txt_1;
    private TextView textview42;
    private TextView dock_txt_2;
    private TextView textview44;
    private LinearLayout dock3;
    private LinearLayout dock4;
    private TextView dock_txt_3;
    private TextView dock_txt_4;
    private TextView textview4;
    private TextView seeMorePacks;
    private RequestNetwork startGettingEmojis;
    private RequestNetwork.RequestListener EmojisRequestListener;
    private SharedPreferences sharedPref;
    private RequestNetwork getSuggestions;
    private RequestNetwork.RequestListener SuggestionsRequestListener;

    public static String PacksArray() {
        return new Gson().toJson(packsList);
    }

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.home);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        adview = findViewById(R.id.adview);
        vscroll2 = findViewById(R.id.vscroll2);
        textview1 = findViewById(R.id.tutorialTitle);
        loadingView = findViewById(R.id.loadingView);
        mainView = findViewById(R.id.mainView);
        shimmer1 = findViewById(R.id.shimmer1);
        shimmer2 = findViewById(R.id.shimmer2);
        shimmer7 = findViewById(R.id.shimmer7);
        loadingRecycler = findViewById(R.id.loadingRecycler);
        shimmer3 = findViewById(R.id.shimmer3);
        shimmer4 = findViewById(R.id.shimmer4);
        shimmer6 = findViewById(R.id.shimmer6);
        shimmer5 = findViewById(R.id.shimmer5);
        searchcard = findViewById(R.id.searchcard);
        localemojisview = findViewById(R.id.localemojisview);
        linear30 = findViewById(R.id.linear30);
        LinearLayout goToPacks = findViewById(R.id.gotopacks);
        packs_recycler = findViewById(R.id.packs_recycler);
        local_recycler = findViewById(R.id.local_recycler);
        dock1 = findViewById(R.id.dock1);
        dock2 = findViewById(R.id.dock2);
        dock_txt_1 = findViewById(R.id.dock_txt_1);
        textview42 = findViewById(R.id.textview42);
        dock_txt_2 = findViewById(R.id.dock_txt_2);
        textview44 = findViewById(R.id.textview44);
        dock3 = findViewById(R.id.dock3);
        dock4 = findViewById(R.id.dock4);
        dock_txt_3 = findViewById(R.id.dock_txt_3);
        dock_txt_4 = findViewById(R.id.dock_txt_4);
        textview4 = findViewById(R.id.activityDescription);
        seeMorePacks = findViewById(R.id.textview53);
        startGettingEmojis = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);
        getSuggestions = new RequestNetwork(this);

        searchcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                toSearch.putExtra("switchFrom", "search");
                toSearch.setClass(getApplicationContext(), EmojisActivity.class);
                transitionManager(searchcard, "searchbox", toSearch);
            }
        });

        goToPacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                toPacks.setClass(getApplicationContext(), PacksActivity.class);
                startActivity(toPacks);
            }
        });

        packs_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int _scrollState) {
                super.onScrollStateChanged(recyclerView, _scrollState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int offsetX, int offsetY) {
                super.onScrolled(recyclerView, offsetX, offsetY);
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    seeMorePacks.setVisibility(View.INVISIBLE);
                } else {
                    seeMorePacks.setVisibility(View.VISIBLE);
                }
            }
        });

        dock1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (textview42.getText().toString().equals("0")) {
                    showCustomSnackBar(getString(R.string.emojis_still_loading_msg));
                } else {
                    toSearch.putExtra("switchFrom", "dock");
                    toSearch.setClass(getApplicationContext(), EmojisActivity.class);
                    startActivity(toSearch);
                }
            }
        });

        dock2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (textview44.getText().toString().equals("0")) {
                    showCustomSnackBar(getString(R.string.packs_still_loading_msg));
                } else {
                    if (textview42.getText().toString().equals("0")) {
                        showCustomSnackBar(getString(R.string.emojis_still_loading_msg));
                    } else {
                        toCategories.setClass(getApplicationContext(), CategoriesActivity.class);
                        startActivity(toCategories);
                    }
                }
            }
        });

        dock3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                toSettings.setClass(getApplicationContext(), SettingsActivity.class);
                startActivity(toSettings);
            }
        });

        dock4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                toHelp.setClass(getApplicationContext(), TutorialActivity.class);
                startActivity(toHelp);
            }
        });

        EmojisRequestListener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                if (tag.equals("EMOJIS")) {
                    try {
                        emojisList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                        }.getType());
                        emojisCount = emojisList.size();
                        emojisScanPosition = emojisCount - 1;
                        for (int i = 0; i < (int) (emojisCount); i++) {
                            if (Objects.requireNonNull(emojisList.get((int) emojisScanPosition).get("category")).toString().equals("9.0")) {
                                emojisList.remove((int) (emojisScanPosition));
                            }
                            emojisScanPosition--;
                        }
                        sharedPref.edit().putString("emojisData", new Gson().toJson(emojisList)).apply();
                        MainUIMethods.numbersAnimator(textview42, 0, emojisList.size(), 1000);
                    } catch (Exception e) {
                        Utils.showToast(getApplicationContext(), (e.toString()));
                    }
                } else {
                    if (tag.equals("CATEGORIES")) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Iterator<String> keys = obj.keys();

                            while (keys.hasNext()) {
                                String key = keys.next();
                                String value = String.valueOf(obj.get(key));
                                if (!value.equals("NSFW")) {
                                    categoriesMap = new HashMap<>();
                                    categoriesMap.put("category_id", key);
                                    categoriesMap.put("category_name", value);
                                    categoriesList.add(categoriesMap);
                                }
                            }

                        } catch (JSONException e) {
                            // Something went wrong!
                        }

                        sharedPref.edit().putString("categoriesData", new Gson().toJson(categoriesList)).apply();
                        MainUIMethods.numbersAnimator(textview44, 0, categoriesList.size(), 1000);
                    } else {
                        if (tag.equals("PACKS")) {
                            try {
                                packsList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                                }.getType());
                                sharedPref.edit().putString("packsDataOriginal", response).apply();
                                sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                                packs_recycler.setAdapter(new HomePacksAdapter.Packs_recyclerAdapter(packsList));

                            } catch (Exception e) {
                                Utils.showToast(getApplicationContext(), (e.toString()));
                            }
                        } else {
                            if (tag.equals("PACKS_1")) {
                                backendPacksList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                                }.getType());
                                if (new Gson().toJson(backendPacksList).equals(new Gson().toJson(packsList))) {
                                    sharedPref.edit().putString("isNewEmojisAvailable", "").apply();
                                } else {
                                    sharedPref.edit().putString("isNewEmojisAvailable", "true").apply();
                                }
                            }
                        }
                    }
                }
                loadingView.setVisibility(View.GONE);
                mainView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onErrorResponse(String atg, String message) {

            }
        };

        SuggestionsRequestListener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                if (response.contains("STOP_ALL")) {
                    packs_recycler.setVisibility(View.GONE);
                    linear30.setVisibility(View.GONE);
                    searchcard.setVisibility(View.GONE);
                    textview4.setVisibility(View.GONE);
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
        finishAffinity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedPref.getString("isAskingForReload", "").equals("true")) {
            sharedPref.edit().putString("isAskingForReload", "").apply();
            recreate();
        }
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_DENIED && androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_DENIED) {
            getLocalEmojis();
        }
    }

    public void LOGIC_BACKEND() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        overridePendingTransition(R.anim.fade_in, 0);

        SnapHelper snapHelper = new PagerSnapHelper();
        packs_recycler.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(packs_recycler);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper1 = new PagerSnapHelper();
        loadingRecycler.setLayoutManager(layoutManager1);
        snapHelper1.attachToRecyclerView(loadingRecycler);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        local_recycler.setLayoutManager(layoutManager2);
        if (sharedPref.getString("isNewEmojisAvailable", "").equals("true") || (sharedPref.getString("categoriesData", "").isEmpty() || (sharedPref.getString("packsData", "").isEmpty() || sharedPref.getString("emojisData", "").isEmpty()))) {
            loadingView.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.GONE);
            for (int _repeat77 = 0; _repeat77 < 20; _repeat77++) {
                HashMap<String, Object> shimmerMap = new HashMap<>();
                shimmerMap.put("key", "value");
                shimmerList.add(shimmerMap);
            }
            loadingRecycler.setAdapter(new LoadingPacksAdapter.LoadingRecyclerAdapter(shimmerList));
        } else {
            loadingView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }
        getSuggestions.startRequestNetwork(RequestNetworkController.GET, "https://nerbly.com/bemoji/suggestions.json", "", SuggestionsRequestListener);

        OverScrollDecoratorHelper.setUpOverScroll(packs_recycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        OverScrollDecoratorHelper.setUpOverScroll(local_recycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        OverScrollDecoratorHelper.setUpOverScroll(vscroll2);

        if (sharedPref.getString("emojisData", "").isEmpty() || sharedPref.getString("isNewEmojisAvailable", "").equals("true")) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/", "EMOJIS", EmojisRequestListener);
        } else {
            emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            textview42.setText(String.valueOf((long) (emojisList.size())));
        }
        if (sharedPref.getString("categoriesData", "").isEmpty() || sharedPref.getString("isNewEmojisAvailable", "").equals("true")) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/?request=categories", "CATEGORIES", EmojisRequestListener);
        } else {
            categoriesList = new Gson().fromJson(sharedPref.getString("categoriesData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            textview44.setText(String.valueOf((long) (categoriesList.size())));
        }
        if (sharedPref.getString("packsData", "").isEmpty() || sharedPref.getString("isNewEmojisAvailable", "").equals("true")) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/packs", "PACKS", EmojisRequestListener);
        } else {
            try {
                packsList = new Gson().fromJson(sharedPref.getString("packsData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
                sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                packs_recycler.setAdapter(new HomePacksAdapter.Packs_recyclerAdapter(packsList));
            } catch (Exception e) {
                Utils.showToast(getApplicationContext(), (e.toString()));
            }
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/packs", "PACKS_1", EmojisRequestListener);
        }


        AudienceNetworkAds.initialize(this);
        AdView bannerAd = new AdView(this, "3773974092696684_3774022966025130", AdSize.BANNER_HEIGHT_50);
        adview.addView(bannerAd);
        bannerAd.loadAd();
    }

    public void LOGIC_FRONTEND() {
        NavStatusBarColor("#FFFFFF", "#FFFFFF", this);
        DARK_ICONS(this);
        changeActivityFont("whitney", this);
        rippleRoundStroke(dock1, "#FEF3ED", "#FEE0D0", 25, 0, "#FFFFFF");
        rippleRoundStroke(dock2, "#FAECFD", "#F6D6FD", 25, 0, "#FFFFFF");
        rippleRoundStroke(dock3, "#FFF7EC", "#FFEACE", 25, 0, "#FFFFFF");
        rippleRoundStroke(dock4, "#F3EFFE", "#D8CBFE", 25, 0, "#FFFFFF");
        setClippedView(shimmer1, "#FFFFFF", 200, 0);
        setClippedView(shimmer2, "#FFFFFF", 200, 0);
        setClippedView(shimmer3, "#FFFFFF", 30, 0);
        setClippedView(shimmer4, "#FFFFFF", 30, 0);
        setClippedView(shimmer5, "#FFFFFF", 30, 0);
        setClippedView(shimmer6, "#FFFFFF", 30, 0);
        setClippedView(shimmer7, "#FFFFFF", 200, 0);

        textview1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
        dock_txt_1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
        dock_txt_2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
        dock_txt_3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
        dock_txt_4.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
    }

    public void transitionManager(final View view, final String transitionName, final Intent intent) {
        view.setTransitionName(transitionName);
        android.app.ActivityOptions optionsCompat = android.app.ActivityOptions.makeSceneTransitionAnimation(this, view, transitionName);
        startActivity(intent, optionsCompat.toBundle());
    }

    public void showCustomSnackBar(final String _text) {
        ViewGroup parentLayout = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        snackBarView = com.google.android.material.snackbar.Snackbar.make(parentLayout, "", com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
        sblayout = (com.google.android.material.snackbar.Snackbar.SnackbarLayout) snackBarView.getView();

        @SuppressLint("InflateParams") View inflate = getLayoutInflater().inflate(R.layout.snackbar, null);
        sblayout.setPadding(0, 0, 0, 0);
        sblayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
        LinearLayout back = inflate.findViewById(R.id.tutorialBg);

        TextView text = inflate.findViewById(R.id.tutorialTitle);
        setViewRadius(back, 20, "#202125");
        text.setText(_text);
        text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
        sblayout.addView(inflate, 0);
        snackBarView.show();
    }

    public void getLocalEmojis() {
        fileManager = new FileManager();
        localEmojisList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {

                localEmojisList = fileManager.getList();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            localEmojisScanPosition = 0;
                            for (int i = 0; i < (localEmojisList.size() - 1); i++) {
                                localEmojisScanPath = Objects.requireNonNull(localEmojisList.get((int) localEmojisScanPosition).get("filePath")).toString();
                                final java.io.File file1 = new java.io.File(localEmojisScanPath);
                                try {

                                    long length = file1.length();
                                    length = length / 1024;
                                    if (length == 0) {
                                        localEmojisList.remove((int) (localEmojisScanPosition));
                                    }
                                } catch (Exception e) {
                                    localEmojisList.remove((int) (localEmojisScanPosition));
                                }
                                localEmojisScanPosition++;
                            }
                            if (localEmojisList.size() == 0) {
                                localemojisview.setVisibility(View.GONE);
                            } else {
                                localemojisview.setVisibility(View.VISIBLE);
                                Utils.sortListMap(localEmojisList, "modi_time", false, false);
                                local_recycler.setAdapter(new LocalEmojisAdapter.Local_recyclerAdapter(localEmojisList));
                                //initDragNDropRecycler(local_recycler, localEmojisList);
                            }
                        } catch (Exception ignored) {
                        }

                    }
                });
            }
        }).start();

    }

}