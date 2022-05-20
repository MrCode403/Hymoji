package com.nerbly.bemoji.activities;

import static com.nerbly.bemoji.Configurations.ASSETS_SOURCE_LINK;
import static com.nerbly.bemoji.Configurations.CATEGORIES_API_LINK;
import static com.nerbly.bemoji.Configurations.DISCORD_INVITE_LINK;
import static com.nerbly.bemoji.Configurations.EMOJIS_API_LINK;
import static com.nerbly.bemoji.Configurations.PACKS_API_LINK;
import static com.nerbly.bemoji.functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.functions.Utils.formatEmojiName;
import static com.nerbly.bemoji.functions.Utils.getAdSize;
import static com.nerbly.bemoji.functions.Utils.getLocalEmojisMediaStore;
import static com.nerbly.bemoji.functions.Utils.isStoragePermissionGranted;
import static com.nerbly.bemoji.functions.Utils.requestStoragePermission;
import static com.nerbly.bemoji.ui.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.ui.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.ui.MainUIMethods.navStatusBarColor;
import static com.nerbly.bemoji.ui.MainUIMethods.numbersAnimator;
import static com.nerbly.bemoji.ui.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.ui.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.ui.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.ui.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.ui.MainUIMethods.statusBarColor;
import static com.nerbly.bemoji.ui.SideUIMethods.marqueeTextView;
import static com.nerbly.bemoji.ui.UserInteractions.showCustomSnackBar;
import static com.nerbly.bemoji.ui.UserInteractions.showMessageDialog;

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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.adapters.HomePacksAdapter;
import com.nerbly.bemoji.adapters.LocalEmojisAdapter;
import com.nerbly.bemoji.databinding.AnimatedSplashViewBinding;
import com.nerbly.bemoji.databinding.HomeBinding;
import com.nerbly.bemoji.databinding.HomeLoadingBinding;
import com.nerbly.bemoji.fragments.CategoriesFragment;
import com.nerbly.bemoji.fragments.SettingsFragment;
import com.nerbly.bemoji.fragments.TutorialFragment;
import com.nerbly.bemoji.functions.RequestNetwork;
import com.nerbly.bemoji.functions.RequestNetworkController;
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

    private RequestNetwork startGettingEmojis;
    private RequestNetwork.RequestListener EmojisRequestListener;
    private SharedPreferences sharedPref;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    private HomeBinding homeBinding;
    private HomeLoadingBinding homeLoadingBinding;
    private AnimatedSplashViewBinding animatedSplashViewBinding;

    private AdView adView;

    public void userIsAskingForActivityToReload(Activity context) {
        context.recreate();
    }

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        loadLocale(this);
        initViewBinding();
        setContentView(homeBinding.getRoot());
        initialize();
        FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initViewBinding(){
        homeBinding = HomeBinding.inflate(getLayoutInflater());
        homeLoadingBinding = HomeLoadingBinding.inflate(getLayoutInflater());
        animatedSplashViewBinding = AnimatedSplashViewBinding.inflate(getLayoutInflater());
    }

    private void initialize() {

        startGettingEmojis = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        homeBinding.searchcard.setOnClickListener(view -> {
            if (homeBinding.emojisCounter.getText().toString().equals("0")) {
                showCustomSnackBar(getString(R.string.emojis_still_loading_msg), HomeActivity.this);
            } else if (!isActivityAttached) {
                isActivityAttached = true;
                toSearch.putExtra("switchFrom", "search");
                toSearch.setClass(getApplicationContext(), EmojisActivity.class);
                startActivity(toSearch);
                new Handler().postDelayed(() -> isActivityAttached = false, 1000);
            }
        });

        homeBinding.gotopacks.setOnClickListener(view -> {
            if (!isActivityAttached) {
                isActivityAttached = true;
                toPacks.setClass(getApplicationContext(), PacksActivity.class);
                startActivity(toPacks);
                new Handler().postDelayed(() -> isActivityAttached = false, 1000);
            }
        });
        homeBinding.proTipView.setOnClickListener(view -> {
            if (isStoragePermissionGranted(HomeActivity.this)) {
                homeBinding.proTipView.setVisibility(View.GONE);
            } else {
                requestStoragePermission(1, HomeActivity.this);
            }
        });

        homeBinding.discordDock.setOnClickListener(view -> {
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
        homeBinding.premiumDock.setOnClickListener(view -> {
            Intent intent1 = new Intent();
            intent1.setClass(this, PremiumActivity.class);
            startActivity(intent1);
        });

        homeBinding.packsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int _scrollState) {
                super.onScrollStateChanged(recyclerView, _scrollState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int offsetX, int offsetY) {
                super.onScrolled(recyclerView, offsetX, offsetY);
                if (layoutManager.findFirstVisibleItemPosition() >= 1) {
                    homeBinding.seeMorePacks.setVisibility(View.VISIBLE);
                } else {
                    homeBinding.seeMorePacks.setVisibility(View.INVISIBLE);
                }
            }
        });

        homeBinding.dock1.setOnClickListener(view -> {
            if (homeBinding.emojisCounter.getText().toString().equals("0")) {
                showCustomSnackBar(getString(R.string.emojis_still_loading_msg), HomeActivity.this);
            } else {
                if (homeBinding.emojisCounter.getText().toString().equals("0")) {
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

        homeBinding.dock2.setOnClickListener(view -> {
            if (homeBinding.categoriesCounter.getText().toString().equals("0")) {
                showCustomSnackBar(getString(R.string.packs_still_loading_msg), HomeActivity.this);
            } else {
                if (homeBinding.emojisCounter.getText().toString().equals("0")) {
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

        homeBinding.dock3.setOnClickListener(view -> {

            SettingsFragment bottomSheet = new SettingsFragment();
            if (!isFragmentAttached) {
                isFragmentAttached = true;
                bottomSheet.show(HomeActivity.this.getSupportFragmentManager(), "Settings");
            }

        });

        homeBinding.dock4.setOnClickListener(view -> {
            TutorialFragment bottomSheet = new TutorialFragment();
            if (!isFragmentAttached) {
                isFragmentAttached = true;
                bottomSheet.show(HomeActivity.this.getSupportFragmentManager(), "Tutorial");
            }
        });

        EmojisRequestListener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {

                if (homeBinding.swipeToRefresh.isRefreshing()) homeBinding.swipeToRefresh.setRefreshing(false);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                switch (tag) {
                    case "EMOJIS":

                        if (!emojisList.isEmpty()) {
                            try {
                                emojisList.clear();
                            } catch (Exception e) {
                                Log.e("HYMOJI_RESPONSE", "Couldn't clear the list for new emojis");
                            }
                        }


                        executor.execute(() -> {
                            try {
                                JSONArray emojisArray = new JSONArray(response);
                                Log.d("HYMOJI_RESPONSE", "found " + emojisArray.length() + " emojis");

                                for (int i = 0; i < emojisArray.length(); i++) {
                                    JSONObject emojisObject = emojisArray.getJSONObject(i);
                                    emojisMap = new HashMap<>();
                                    emojisMap.put("image", emojisObject.getString("image"));
                                    emojisMap.put("name", emojisObject.getString("slug"));
                                    emojisMap.put("title", formatEmojiName(emojisObject.getString("title")));
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
                                Log.d("HYMOJI_RESPONSE", emojisCount + " main emojis saved to local database");

                                startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, PACKS_API_LINK, "PACKS", EmojisRequestListener);
                            });
                        });
                        break;

                    case "PACKS":
                        final boolean[] isSuccess = {false};

                        ExecutorService executor2 = Executors.newSingleThreadExecutor();
                        Handler handler2 = new Handler(Looper.getMainLooper());
                        executor2.execute(() -> {
                            int currentPosition = 0;
                            ArrayList<HashMap<String, Object>> backendEmojisList = new ArrayList<>();

                            try {
                                packsList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                                }.getType());

                                JSONArray backPacksArray = new JSONArray(response);

                                for (int i1 = 0; i1 < backPacksArray.length(); i1++) {
                                    JSONObject packsObject = backPacksArray.getJSONObject(i1);
                                    JSONArray frontPacksArray = packsObject.getJSONArray("emojis");

                                    for (int i2 = 0; i2 < frontPacksArray.length(); i2++) {

                                        emojisMap = new HashMap<>();
                                        emojisMap.put("image", ASSETS_SOURCE_LINK + frontPacksArray.getString(i2));
                                        emojisMap.put("title", formatEmojiName(frontPacksArray.getString(i2)));
                                        emojisMap.put("name", frontPacksArray.getString(i2));
                                        emojisMap.put("submitted_by", "Emoji lovers");
                                        emojisMap.put("id", currentPosition);
                                        currentPosition++;
                                        emojisCount++;
                                        backendEmojisList.add(emojisMap);
                                    }
                                }

                                sharedPref.edit().putString("packsOneByOne", new Gson().toJson(backendEmojisList)).apply();

                                Log.d("HYMOJI_PACKS", "Found: " + backendEmojisList.size());
                                isSuccess[0] = true;
                            } catch (Exception e) {
                                isSuccess[0] = false;
                                FirebaseCrashlytics.getInstance().recordException(e);
                                Log.e("HYMOJI_ERROR", e.toString());
                            }

                            handler2.post(() -> {
                                if (isSuccess[0]) {
                                    Log.d("HYMOJI_PACKS", emojisCount + " packs emojis saved to local database");
                                    sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                                    homeBinding.packsRecycler.setAdapter(new HomePacksAdapter(packsList, HomeActivity.this));
                                    homeLoadingBinding.loadingView.setVisibility(View.GONE);
                                    homeBinding.mainView.setVisibility(View.VISIBLE);
                                    homeBinding.adBackView.setVisibility(View.VISIBLE);
                                    numbersAnimator(homeBinding.emojisCounter, 0, emojisCount, 1000);
                                    sharedPref.edit().putInt("emojisTotalCount", emojisCount).apply();
                                } else {
                                    Log.d("HYMOJI_PACKS", "Failed to get emojis count due to:");
                                }
                            });
                        });


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
                                if (!value.equalsIgnoreCase("NSFW")) {
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
                        numbersAnimator(homeBinding.categoriesCounter, 0, categoriesList.size(), 1000);

                        break;
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {
                homeBinding.swipeToRefresh.setRefreshing(false);
                noInternetAccessAction();
            }
        };

        homeBinding.swipeToRefresh.setOnRefreshListener(() -> {
            TimerTask loadingTmr = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        generateActivityDescription(false);
                        emojisCount = 0;
                        startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, CATEGORIES_API_LINK, "CATEGORIES", EmojisRequestListener);
                        startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, EMOJIS_API_LINK, "EMOJIS", EmojisRequestListener);
                        homeLoadingBinding.loadingView.setVisibility(View.VISIBLE);
                        homeBinding.mainView.setVisibility(View.GONE);
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
                homeBinding.premiumDock.setVisibility(View.GONE);
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

        homeBinding.localRecycler.setItemAnimator(null);
        homeBinding.packsRecycler.setItemAnimator(null);

        SnapHelper snapHelper = new LinearSnapHelper();
        homeBinding.packsRecycler.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(homeBinding.packsRecycler);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        homeBinding.localRecycler.setLayoutManager(layoutManager2);

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
            homeLoadingBinding.loadingView.setVisibility(View.VISIBLE);
            homeBinding.mainView.setVisibility(View.GONE);
        } else {
            homeLoadingBinding.loadingView.setVisibility(View.GONE);
            homeBinding.mainView.setVisibility(View.VISIBLE);
            homeBinding.adBackView.setVisibility(View.VISIBLE);
        }

        if (Build.VERSION.SDK_INT <= 30) {
            OverScrollDecoratorHelper.setUpOverScroll(homeBinding.packsRecycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
            OverScrollDecoratorHelper.setUpOverScroll(homeBinding.localRecycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        }

        getOnlineEmojis();

        getLocalEmojis();

        homeBinding.adContainerView.post(this::loadAds);

    }


    public void LOGIC_FRONTEND() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> animatedSplashViewBinding.animatedLogo.playAnimation(), 1000);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            animatedSplashViewBinding.appTitle.setVisibility(View.GONE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                shadAnim(animatedSplashViewBinding.animatedLogo, "scaleX", 0, 200);
                shadAnim(animatedSplashViewBinding.animatedLogo, "scaleY", 0, 200);
                shadAnim(animatedSplashViewBinding.animatedLogo, "alpha", 0, 200);
                shadAnim(animatedSplashViewBinding.splashView, "scaleX", 4, 400);
                shadAnim(animatedSplashViewBinding.splashView, "scaleY", 4, 400);
                shadAnim(animatedSplashViewBinding.splashView, "alpha", 0, 400);
                afterSplashAnimationAction();
            }, 2000);
        }, 3500);

        if (Build.VERSION.SDK_INT <= 27) {
            statusBarColor("#7289DA", this);
            LIGHT_ICONS(this);
        } else {
            navStatusBarColor("#FFFFFF", "#FFFFFF", this);
            DARK_ICONS(this);
        }

        if (sharedPref.getBoolean("isPremium", false)) {
            homeBinding.premiumDock.setVisibility(View.GONE);
        }


        rippleRoundStroke(homeBinding.dock1, "#FEF3ED", "#FEE0D0", 25, 0, "#FFFFFF");
        rippleRoundStroke(homeBinding.dock2, "#FAECFD", "#F6D6FD", 25, 0, "#FFFFFF");
        rippleRoundStroke(homeBinding.dock3, "#FFF7EC", "#FFEACE", 25, 0, "#FFFFFF");
        rippleRoundStroke(homeBinding.dock4, "#F3EFFE", "#D8CBFE", 25, 0, "#FFFFFF");
        setClippedView(homeLoadingBinding.shimmer6, "#FFFFFF", 200, 0);
        setClippedView(homeLoadingBinding.shimmer2, "#FFFFFF", 200, 0);
        setClippedView(homeLoadingBinding.shimmer3, "#FFFFFF", 30, 0);
        setClippedView(homeLoadingBinding.shimmer4, "#FFFFFF", 30, 0);
        setClippedView(homeLoadingBinding.shimmer5, "#FFFFFF", 30, 0);
        setClippedView(homeLoadingBinding.shimmer6, "#FFFFFF", 30, 0);
        setClippedView(homeLoadingBinding.shimmer7, "#FFFFFF", 200, 0);
        setClippedView(homeLoadingBinding.shimmer9, "#FFFFFF", 30, 0);
        setClippedView(homeLoadingBinding.shimmer10, "#FFFFFF", 200, 0);
        setClippedView(homeLoadingBinding.shimmer11, "#FFFFFF", 200, 0);
        setViewRadius(homeBinding.discordImg, 30, "#FAFAFA");
        setViewRadius(homeBinding.premiumImg, 30, "#FAFAFA");

        marqueeTextView(homeBinding.dockTxt1);
        marqueeTextView(homeBinding.dockTxt2);
        marqueeTextView(homeBinding.dockTxt3);
        marqueeTextView(homeBinding.dockTxt4);

        generateActivityDescription(true);

        homeBinding.swipeToRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));

    }

    private void afterSplashAnimationAction() {
        if (!isStoragePermissionGranted(this)) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> homeBinding.proTipView.setVisibility(View.VISIBLE), 1000);
        }
    }

    @SuppressLint("SetTextI18n")
    private void getOnlineEmojis() {
        if (sharedPref.getString("emojisData", "").isEmpty() || sharedPref.getString("packsData", "").isEmpty()) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, EMOJIS_API_LINK, "EMOJIS", EmojisRequestListener);
        } else {
            emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            homeBinding.emojisCounter.setText("" + sharedPref.getInt("emojisTotalCount", emojisList.size()));
            homeLoadingBinding.loadingView.setVisibility(View.GONE);
            homeBinding.mainView.setVisibility(View.VISIBLE);
        }

        if (sharedPref.getString("categoriesData", "").isEmpty()) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, CATEGORIES_API_LINK, "CATEGORIES", EmojisRequestListener);
        } else {
            categoriesList = new Gson().fromJson(sharedPref.getString("categoriesData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            homeBinding.categoriesCounter.setText("" + categoriesList.size());
        }

        if (!sharedPref.getString("packsData", "").isEmpty()) {
            try {
                packsList = new Gson().fromJson(sharedPref.getString("packsData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
                sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                homeBinding.packsRecycler.setAdapter(new HomePacksAdapter(packsList, HomeActivity.this));
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
                        homeBinding.localemojisview.setVisibility(View.GONE);
                    } else {
                        homeBinding.localRecycler.setAdapter(new LocalEmojisAdapter(localEmojisList));
                        new Handler().postDelayed(() -> homeBinding.localemojisview.setVisibility(View.VISIBLE), 1000);
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
                homeBinding.activityDescription.setText(tips[random]);
            } else {
                homeBinding.activityDescription.setVisibility(View.GONE);
                new Handler().postDelayed(() -> homeBinding.activityDescription.setText(tips[random]), 400);
                new Handler().postDelayed(() -> homeBinding.activityDescription.setVisibility(View.VISIBLE), 500);
            }

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void loadAds() {
        if (!sharedPref.getBoolean("isPremium", false)) {
            if (!isAdLoaded) {
                MobileAds.initialize(this, initializationStatus -> {
                });
                adView = new AdView(this);
                adView.setAdUnitId(getString(R.string.home_admob_banner_id));
                homeBinding.adContainerView.removeAllViews();
                homeBinding.adContainerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                homeBinding.adContainerView.addView(adView);

                AdSize adSize = getAdSize(homeBinding.adContainerView, this);
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
        homeLoadingBinding.loadingView.setVisibility(View.VISIBLE);
        homeBinding.mainView.setVisibility(View.GONE);
        homeBinding.swipeToRefresh.setRefreshing(true);
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
                homeBinding.proTipView.setVisibility(View.GONE);
                getLocalEmojis();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public String PacksArray() {
        return new Gson().toJson(packsList);
    }


}
