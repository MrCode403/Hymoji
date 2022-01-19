package com.nerbly.bemoji.Activities;

import static com.nerbly.bemoji.Configurations.CATEGORIES_API_LINK;
import static com.nerbly.bemoji.Configurations.DISCORD_INVITE_LINK;
import static com.nerbly.bemoji.Configurations.EMOJIS_API_LINK;
import static com.nerbly.bemoji.Configurations.PACKS_API_LINK;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.Functions.Utils.getAdSize;
import static com.nerbly.bemoji.Functions.Utils.getLocalEmojisMediaStore;
import static com.nerbly.bemoji.Functions.Utils.isStoragePermissionGranted;
import static com.nerbly.bemoji.Functions.Utils.requestStoragePermission;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.navStatusBarColor;
import static com.nerbly.bemoji.UI.MainUIMethods.numbersAnimator;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.statusBarColor;
import static com.nerbly.bemoji.UI.SideUIMethods.marqueeTextView;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;
import static com.nerbly.bemoji.UI.UserInteractions.showMessageDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.card.MaterialCardView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Adapters.HomePacksAdapter;
import com.nerbly.bemoji.Adapters.LocalEmojisAdapter;
import com.nerbly.bemoji.Fragments.CategoriesFragment;
import com.nerbly.bemoji.Fragments.SettingsFragment;
import com.nerbly.bemoji.Fragments.TutorialFragment;
import com.nerbly.bemoji.Functions.RequestNetwork;
import com.nerbly.bemoji.Functions.RequestNetworkController;
import com.nerbly.bemoji.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class HomeActivity extends AppCompatActivity {

    public static ArrayList<HashMap<String, Object>> packsList = new ArrayList<>();
    private final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    private final Intent toSearch = new Intent();
    private final Intent toPacks = new Intent();
    private final Timer timer = new Timer();
    private final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    public boolean isFragmentAttached = false;
    public boolean isActivityAttached = false;
    private int emojisCount = 0;
    private boolean isAdLoaded = false;
    private boolean noInternetConnectionDialogShown = false;
    private HashMap<String, Object> categoriesMap = new HashMap<>();
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> categoriesList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> localEmojisList = new ArrayList<>();
    private HashMap<String, Object> emojisMap = new HashMap<>();
    private LinearLayout loadingView;
    private LottieAnimationView animated_logo;
    private LinearLayout splashView;
    private LinearLayout mainView;
    private LinearLayout shimmer1;
    private LinearLayout shimmer2;
    private LinearLayout shimmer7;
    private LinearLayout shimmer3;
    private LinearLayout shimmer4;
    private LinearLayout shimmer6;
    private LinearLayout shimmer5;
    private LinearLayout shimmer9;
    private LinearLayout shimmer10;
    private LinearLayout shimmer11;
    private ImageView discord_img;
    private ImageView premium_img;
    private LinearLayout localEmojisView;
    private RecyclerView packs_recycler;
    private RecyclerView local_recycler;
    private LinearLayout dock1;
    private LinearLayout dock2;
    private TextView emojisCounter;
    private TextView categoriesCounter;
    private LinearLayout dock3;
    private LinearLayout dock4;
    private TextView seeMorePacks;
    private TextView activityDescription;
    private TextView dock_txt_1;
    private TextView dock_txt_2;
    private TextView dock_txt_3;
    private TextView dock_txt_4;
    private TextView app_title;
    private RequestNetwork startGettingEmojis;
    private RequestNetwork.RequestListener EmojisRequestListener;
    private SharedPreferences sharedPref;
    private SwipeRefreshLayout swipe_to_refresh;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;
    private MaterialCardView premium_dock;
    private MaterialCardView pro_tip_view;
    private LinearLayout adContainerView;
    private LinearLayout adBackView;
    private AdView adView;

    public void userIsAskingForActivityToReload(Activity context) {
        context.recreate();
    }

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.home);
        initialize();
        FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        adContainerView = findViewById(R.id.adContainerView);
        adBackView = findViewById(R.id.adBackView);
        animated_logo = findViewById(R.id.animated_logo);
        activityDescription = findViewById(R.id.activityDescription);
        loadingView = findViewById(R.id.loadingView);
        app_title = findViewById(R.id.app_title);
        MaterialCardView discord_dock = findViewById(R.id.discord_dock);
        premium_dock = findViewById(R.id.premium_dock);
        pro_tip_view = findViewById(R.id.pro_tip_view);
        splashView = findViewById(R.id.splashView);
        dock_txt_1 = findViewById(R.id.dock_txt_1);
        dock_txt_2 = findViewById(R.id.dock_txt_2);
        dock_txt_3 = findViewById(R.id.dock_txt_3);
        dock_txt_4 = findViewById(R.id.dock_txt_4);
        mainView = findViewById(R.id.mainView);
        premium_img = findViewById(R.id.premium_img);
        shimmer1 = findViewById(R.id.shimmer1);
        shimmer2 = findViewById(R.id.shimmer2);
        shimmer7 = findViewById(R.id.shimmer7);
        shimmer3 = findViewById(R.id.shimmer3);
        shimmer4 = findViewById(R.id.shimmer4);
        shimmer6 = findViewById(R.id.shimmer6);
        shimmer5 = findViewById(R.id.shimmer5);
        shimmer9 = findViewById(R.id.shimmer9);
        shimmer10 = findViewById(R.id.shimmer10);
        shimmer11 = findViewById(R.id.shimmer11);
        swipe_to_refresh = findViewById(R.id.swipe_to_refresh);
        discord_img = findViewById(R.id.discord_img);
        MaterialCardView searchcard = findViewById(R.id.searchcard);
        localEmojisView = findViewById(R.id.localemojisview);
        LinearLayout goToPacks = findViewById(R.id.gotopacks);
        packs_recycler = findViewById(R.id.packs_recycler);
        local_recycler = findViewById(R.id.local_recycler);
        dock1 = findViewById(R.id.dock1);
        dock2 = findViewById(R.id.dock2);
        emojisCounter = findViewById(R.id.emojisCounter);
        categoriesCounter = findViewById(R.id.categoriesCounter);
        dock3 = findViewById(R.id.dock3);
        dock4 = findViewById(R.id.dock4);
        seeMorePacks = findViewById(R.id.seeMorePacks);
        startGettingEmojis = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        searchcard.setOnClickListener(view -> {
            if (emojisCounter.getText().toString().equals("0")) {
                showCustomSnackBar(getString(R.string.emojis_still_loading_msg), HomeActivity.this);
            } else if (!isActivityAttached) {
                isActivityAttached = true;
                toSearch.putExtra("switchFrom", "search");
                toSearch.setClass(getApplicationContext(), EmojisActivity.class);
                startActivity(toSearch);
                new Handler().postDelayed(() -> isActivityAttached = false, 1000);
            }
        });

        goToPacks.setOnClickListener(view -> {
            if (!isActivityAttached) {
                isActivityAttached = true;
                toPacks.setClass(getApplicationContext(), PacksActivity.class);
                startActivity(toPacks);
                new Handler().postDelayed(() -> isActivityAttached = false, 1000);
            }
        });
        pro_tip_view.setOnClickListener(view -> {
            if (isStoragePermissionGranted(HomeActivity.this)) {
                pro_tip_view.setVisibility(View.GONE);
            } else {
                requestStoragePermission(1, HomeActivity.this);
            }
        });

        discord_dock.setOnClickListener(view -> {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(DISCORD_INVITE_LINK));
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(true, getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), this,
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(getString(R.string.app_name), DISCORD_INVITE_LINK);
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });
        premium_dock.setOnClickListener(view -> {
            Intent intent1 = new Intent();
            intent1.setClass(this, PremiumActivity.class);
            startActivity(intent1);
        });

        packs_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int _scrollState) {
                super.onScrollStateChanged(recyclerView, _scrollState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int offsetX, int offsetY) {
                super.onScrolled(recyclerView, offsetX, offsetY);
                if (layoutManager.findFirstVisibleItemPosition() >= 1) {
                    seeMorePacks.setVisibility(View.VISIBLE);
                } else {
                    seeMorePacks.setVisibility(View.INVISIBLE);
                }
            }
        });

        dock1.setOnClickListener(view -> {
            if (emojisCounter.getText().toString().equals("0")) {
                showCustomSnackBar(getString(R.string.emojis_still_loading_msg), HomeActivity.this);
            } else {
                if (emojisCounter.getText().toString().equals("0")) {
                    showCustomSnackBar(getString(R.string.emojis_still_loading_msg), HomeActivity.this);
                } else {
                    if (!isActivityAttached) {
                        isActivityAttached = true;
                        toSearch.putExtra("switchFrom", "dock");
                        toSearch.setClass(getApplicationContext(), EmojisActivity.class);
                        startActivity(toSearch);
                        new Handler().postDelayed(() -> isActivityAttached = false, 1000);
                    }
                }
            }
        });

        dock2.setOnClickListener(view -> {
            if (categoriesCounter.getText().toString().equals("0")) {
                showCustomSnackBar(getString(R.string.packs_still_loading_msg), HomeActivity.this);
            } else {
                if (emojisCounter.getText().toString().equals("0")) {
                    showCustomSnackBar(getString(R.string.emojis_still_loading_msg), HomeActivity.this);
                } else {
                    CategoriesFragment bottomSheet = new CategoriesFragment();
                    if (!isFragmentAttached) {
                        isFragmentAttached = true;
                        bottomSheet.show(HomeActivity.this.getSupportFragmentManager(), "Categories");
                    }
                }
            }
        });

        dock3.setOnClickListener(view -> {

            SettingsFragment bottomSheet = new SettingsFragment();
            if (!isFragmentAttached) {
                isFragmentAttached = true;
                bottomSheet.show(HomeActivity.this.getSupportFragmentManager(), "Settings");
            }

        });

        dock4.setOnClickListener(view -> {
            TutorialFragment bottomSheet = new TutorialFragment();
            if (!isFragmentAttached) {
                isFragmentAttached = true;
                bottomSheet.show(HomeActivity.this.getSupportFragmentManager(), "Tutorial");
            }
        });

        EmojisRequestListener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {

                if (swipe_to_refresh.isRefreshing()) swipe_to_refresh.setRefreshing(false);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                switch (tag) {
                    case "EMOJIS":

                        if (!emojisList.isEmpty()) {
                            try {
                                emojisList.clear();
                            } catch (Exception e) {
                                Log.e("Emojis Response", "couldn't clear the list for new emojis");
                            }
                        }


                        executor.execute(() -> {
                            try {
                                JSONArray emojisArray = new JSONArray(response);
                                Log.d("Emojis Response", "found " + emojisArray.length() + " emojis");

                                for (int i = 0; i < emojisArray.length(); i++) {
                                    JSONObject emojisObject = emojisArray.getJSONObject(i);
                                    emojisMap = new HashMap<>();
                                    emojisMap.put("image", emojisObject.getString("image"));
                                    emojisMap.put("name", emojisObject.getString("slug"));
                                    emojisMap.put("title", emojisObject.getString("title"));
                                    emojisMap.put("submitted_by", emojisObject.getString("submitted_by"));
                                    emojisMap.put("id", emojisObject.getInt("id"));
                                    emojisMap.put("category", emojisObject.getInt("category"));
                                    if (emojisObject.getInt("category") != 9) {
                                        emojisList.add(emojisMap);
                                        emojisCount++;
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("EmojisRequestListener", e.toString());
                            }
                            handler.post(() -> {
                                sharedPref.edit().putString("emojisData", new Gson().toJson(emojisList)).apply();
                                Log.d("Emojis Response", emojisCount + " main emojis saved to local database");

                                startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, PACKS_API_LINK, "PACKS", EmojisRequestListener);
                            });
                        });
                        break;
                    case "PACKS":
                        try {
                            executor.execute(() -> {
                                try {
                                    packsList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                                    }.getType());
                                    JSONArray backPacksArray = new JSONArray(response);
                                    for (int backPacksArrayInt = 0; backPacksArrayInt < backPacksArray.length(); backPacksArrayInt++) {
                                        JSONObject packsObject = backPacksArray.getJSONObject(backPacksArrayInt);
                                        JSONArray frontPacksArray = packsObject.getJSONArray("emojis");
                                        for (int frontPacksInt = 0; frontPacksInt < frontPacksArray.length(); frontPacksInt++) {
                                            emojisCount++;
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.d("Packs Response", "failed to get emojis count due to:");
                                    e.printStackTrace();
                                }
                                handler.post(() -> {
                                    Log.d("Packs Response", emojisCount + " packs emojis saved to local database");
                                    sharedPref.edit().putString("packsDataOriginal", response).apply();
                                    sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                                    packs_recycler.setAdapter(new HomePacksAdapter(packsList));
                                    loadingView.setVisibility(View.GONE);
                                    mainView.setVisibility(View.VISIBLE);
                                    adBackView.setVisibility(View.VISIBLE);
                                    numbersAnimator(emojisCounter, 0, emojisCount, 1000);
                                    sharedPref.edit().putInt("emojisTotalCount", emojisCount).apply();
                                });
                            });
                        } catch (Exception e) {
                            getOnlineEmojis();
                        }
                        break;
                    case "CATEGORIES":
                        if (!categoriesList.isEmpty()) {
                            categoriesList.clear();
                        }
                        try {
                            JSONObject object = new JSONObject(response);
                            Iterator<String> keys = object.keys();

                            while (keys.hasNext()) {
                                String key = keys.next();
                                String value = String.valueOf(object.get(key));
                                if (!value.equals("NSFW")) {
                                    categoriesMap = new HashMap<>();
                                    categoriesMap.put("category_id", key);
                                    categoriesMap.put("category_name", value);
                                    categoriesList.add(categoriesMap);
                                }
                            }

                        } catch (JSONException e) {
                            getOnlineEmojis();
                        }

                        sharedPref.edit().putString("categoriesData", new Gson().toJson(categoriesList)).apply();
                        numbersAnimator(categoriesCounter, 0, categoriesList.size(), 1000);

                        break;
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {
                swipe_to_refresh.setRefreshing(false);
                noInternetAccessAction();
            }
        };

        swipe_to_refresh.setOnRefreshListener(() -> {
            TimerTask loadingTmr = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        generateActivityDescription(false);
                        emojisCount = 0;
                        startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, CATEGORIES_API_LINK, "CATEGORIES", EmojisRequestListener);
                        startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, EMOJIS_API_LINK, "EMOJIS", EmojisRequestListener);
                        loadingView.setVisibility(View.VISIBLE);
                        mainView.setVisibility(View.GONE);
                        getLocalEmojis();
                        loadAds();
                    });
                }
            };
            timer.schedule(loadingTmr, 500);
        });
    }

    private void noInternetAccessAction() {
        if (!noInternetConnectionDialogShown) {
            noInternetConnectionDialogShown = true;
            showMessageDialog(false, getString(R.string.no_internet_connection_title), getString(R.string.no_internet_connection_desc), getString(R.string.settings_option_1_title), getString(R.string.exit_app), HomeActivity.this,
                    (dialog, which) -> {
                        startManualRefresh();
                        loadAds();
                        noInternetConnectionDialogShown = false;
                    },
                    (dialog, which) -> finishAffinity());
        }
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
        getLocalEmojis();
        if (sharedPref.getBoolean("isPremium", false)) {
            if (isAdLoaded) {
                adView.destroy();
                adView.setVisibility(View.GONE);
                premium_dock.setVisibility(View.GONE);
            }
        }
        super.onResume();
    }

    public void LOGIC_BACKEND() {
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener();
            }
        };

        checkUpdate();

        local_recycler.setItemAnimator(null);
        packs_recycler.setItemAnimator(null);

        SnapHelper snapHelper = new LinearSnapHelper();
        packs_recycler.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(packs_recycler);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        local_recycler.setLayoutManager(layoutManager2);

        if (sharedPref.getInt("opened_so_far", 0) >= 2) {
            sharedPref.edit().putInt("opened_so_far", 0).apply();
            sharedPref.edit().putString("emojisData", "").apply();
            sharedPref.edit().putString("categoriesData", "").apply();
            sharedPref.edit().putString("packsData", "").apply();
        } else {
            int opened_so_far = sharedPref.getInt("opened_so_far", 0) + 1;
            sharedPref.edit().putInt("opened_so_far", opened_so_far).apply();
        }

        if ((sharedPref.getString("categoriesData", "").isEmpty() || (sharedPref.getString("packsData", "").isEmpty() || sharedPref.getString("emojisData", "").isEmpty()))) {
            loadingView.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.GONE);
        } else {
            loadingView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
            adBackView.setVisibility(View.VISIBLE);
        }

        if (Build.VERSION.SDK_INT <= 30) {
            OverScrollDecoratorHelper.setUpOverScroll(packs_recycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
            OverScrollDecoratorHelper.setUpOverScroll(local_recycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        }

        getOnlineEmojis();

        getLocalEmojis();

        adContainerView.post(this::loadAds);

    }


    public void LOGIC_FRONTEND() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> animated_logo.playAnimation(), 1000);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            app_title.setVisibility(View.GONE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                shadAnim(animated_logo, "scaleX", 0, 200);
                shadAnim(animated_logo, "scaleY", 0, 200);
                shadAnim(animated_logo, "alpha", 0, 200);

                shadAnim(splashView, "scaleX", 4, 400);
                shadAnim(splashView, "scaleY", 4, 400);
                shadAnim(splashView, "alpha", 0, 400);
                afterSplashAnimationAction();
            }, 2000);
        }, 3500);

        if (Build.VERSION.SDK_INT < 23) {
            statusBarColor("#7289DA", this);
            LIGHT_ICONS(this);
        } else {
            navStatusBarColor("#FFFFFF", "#FFFFFF", this);
            DARK_ICONS(this);
        }

        if (sharedPref.getBoolean("isPremium", false)) {
            premium_dock.setVisibility(View.GONE);
        }


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
        setClippedView(shimmer9, "#FFFFFF", 30, 0);
        setClippedView(shimmer10, "#FFFFFF", 200, 0);
        setClippedView(shimmer11, "#FFFFFF", 200, 0);
        setViewRadius(discord_img, 30, "#FAFAFA");
        setViewRadius(premium_img, 30, "#FAFAFA");

        marqueeTextView(dock_txt_1);
        marqueeTextView(dock_txt_2);
        marqueeTextView(dock_txt_3);
        marqueeTextView(dock_txt_4);

        generateActivityDescription(true);

        swipe_to_refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));

    }

    private void afterSplashAnimationAction() {
        if (!isStoragePermissionGranted(this)) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> pro_tip_view.setVisibility(View.VISIBLE), 1000);
        }
    }

    @SuppressLint("SetTextI18n")
    private void getOnlineEmojis() {
        if (sharedPref.getString("emojisData", "").isEmpty() || sharedPref.getString("packsData", "").isEmpty()) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, EMOJIS_API_LINK, "EMOJIS", EmojisRequestListener);
        } else {
            emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            emojisCounter.setText("" + sharedPref.getInt("emojisTotalCount", emojisList.size()));
            loadingView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }

        if (sharedPref.getString("categoriesData", "").isEmpty()) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, CATEGORIES_API_LINK, "CATEGORIES", EmojisRequestListener);
        } else {
            categoriesList = new Gson().fromJson(sharedPref.getString("categoriesData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            categoriesCounter.setText(String.valueOf((long) (categoriesList.size())));
        }

        if (!sharedPref.getString("packsData", "").isEmpty()) {
            try {
                packsList = new Gson().fromJson(sharedPref.getString("packsData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
                sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                packs_recycler.setAdapter(new HomePacksAdapter(packsList));
            } catch (Exception ignored) {
            }
        }
    }

    public void getLocalEmojis() {
        if (isStoragePermissionGranted(this)) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                try {
                    localEmojisList = getLocalEmojisMediaStore(this);
                } catch (Exception ignored) {
                }
                handler.post(() -> {
                    if (localEmojisList.size() == 0) {
                        localEmojisView.setVisibility(View.GONE);
                    } else {
                        local_recycler.setAdapter(new LocalEmojisAdapter(localEmojisList));
                        new Handler().postDelayed(() -> localEmojisView.setVisibility(View.VISIBLE), 1000);
                    }
                });
            });
        }
    }

    private void generateActivityDescription(boolean isFirstTime) {
        try {
            String[] tips = new String[]{getString(R.string.home_subtitle), getString(R.string.pro_tip_1), getString(R.string.pro_tip_2), getString(R.string.welcome_subtitle_1)};
            int min = 0;
            int max = tips.length;
            Random random1 = new Random();
            int random = random1.nextInt(max - min) + min;

            if (isFirstTime) {
                activityDescription.setText(tips[random]);
            } else {
                activityDescription.setVisibility(View.GONE);
                new Handler().postDelayed(() -> activityDescription.setText(tips[random]), 400);
                new Handler().postDelayed(() -> activityDescription.setVisibility(View.VISIBLE), 500);
            }

        } catch (Exception ignored) {
        }
    }

    private void loadAds() {
        if (!sharedPref.getBoolean("isPremium", false)) {
            if (!isAdLoaded) {
                MobileAds.initialize(this, initializationStatus -> {
                });
                adView = new AdView(this);
                adView.setAdUnitId(getString(R.string.home_admob_banner_id));
                adContainerView.removeAllViews();
                adContainerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                adContainerView.addView(adView);

                AdSize adSize = getAdSize(adContainerView, this);
                adView.setAdSize(adSize);

                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);

                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        isAdLoaded = true;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        isAdLoaded = false;
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

    public void startManualRefresh() {
        emojisCount = 0;
        startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, CATEGORIES_API_LINK, "CATEGORIES", EmojisRequestListener);
        startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, EMOJIS_API_LINK, "EMOJIS", EmojisRequestListener);
        loadingView.setVisibility(View.VISIBLE);
        mainView.setVisibility(View.GONE);
        swipe_to_refresh.setRefreshing(true);
    }

    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
            } else if (resultCode == RESULT_OK) {
            } else {
                checkUpdate();
            }
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        try {
            showMessageDialog(true, getString(R.string.update_now_dialog_title), getString(R.string.new_update_downloaded), getString(R.string.update_now_button), getString(R.string.dialog_negative_text), this,
                    (dialog, which) -> {
                        if (appUpdateManager != null) {
                            appUpdateManager.completeUpdate();
                        }
                        dialog.dismiss();
                    },
                    (dialog, which) -> dialog.dismiss());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeInstallStateUpdateListener();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pro_tip_view.setVisibility(View.GONE);
                getLocalEmojis();
            } else {
                showCustomSnackBar("Like just why, this is a cool feature.", this);
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public String PacksArray() {
        return new Gson().toJson(packsList);
    }
}