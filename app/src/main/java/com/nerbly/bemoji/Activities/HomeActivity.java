package com.nerbly.bemoji.Activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Adapters.HomePacksAdapter;
import com.nerbly.bemoji.Adapters.LocalEmojisAdapter;
import com.nerbly.bemoji.Fragments.CategoriesFragment;
import com.nerbly.bemoji.Fragments.SettingsFragment;
import com.nerbly.bemoji.Fragments.TutorialFragment;
import com.nerbly.bemoji.Functions.FileManager;
import com.nerbly.bemoji.Functions.RequestNetwork;
import com.nerbly.bemoji.Functions.RequestNetworkController;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.Functions.getDarkModeState;
import com.nerbly.bemoji.R;
import com.nerbly.bemoji.UI.MainUIMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.nerbly.bemoji.Configurations.CATEGORIES_API_LINK;
import static com.nerbly.bemoji.Configurations.EMOJIS_API_LINK;
import static com.nerbly.bemoji.Configurations.PACKS_API_LINK;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.numbersAnimator;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.statusBarColor;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;
import static com.nerbly.bemoji.UI.UserInteractions.showMessageDialog;

public class HomeActivity extends AppCompatActivity {

    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    public static ArrayList<HashMap<String, Object>> packsList = new ArrayList<>();
    private final Intent toSearch = new Intent();
    private final Intent toPacks = new Intent();
    private final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    double emojisCount = 0;
    double emojisScanPosition = 0;
    private FileManager fileManager;
    private String localEmojisScanPath = "";
    private HashMap<String, Object> categoriesMap = new HashMap<>();
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> categoriesList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> localEmojisList = new ArrayList<>();
    private AdView adview;
    private LinearLayout loadingView;
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
    private RequestNetwork startGettingEmojis;
    private RequestNetwork.RequestListener EmojisRequestListener;
    private SharedPreferences sharedPref;
    private SwipeRefreshLayout swipe_to_refresh;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;
    public boolean isFragmentAttached = false;
    public boolean isActivityAttached = false;

    public static String PacksArray() {
        return new Gson().toJson(packsList);
    }

    public static void userIsAskingForActivityToReload(Activity context) {
        context.recreate();
    }

    getDarkModeState state;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        state = new getDarkModeState(this);
        if (state.loadNightModeState() == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (state.loadNightModeState() == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (state.loadNightModeState() == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        loadLocale(this);
        setContentView(R.layout.home);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        adview = findViewById(R.id.adview);
        activityDescription = findViewById(R.id.activityDescription);
        loadingView = findViewById(R.id.loadingView);
        MaterialCardView discord_dock = findViewById(R.id.discord_dock);
        mainView = findViewById(R.id.mainView);
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
            } else {
                if (!isActivityAttached) {
                    isActivityAttached = true;
                    toSearch.putExtra("switchFrom", "search");
                    toSearch.setClass(getApplicationContext(), EmojisActivity.class);
                    startActivity(toSearch);
                    new Handler().postDelayed(() -> isActivityAttached = false, 1000);
                }
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

        discord_dock.setOnClickListener(view -> {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://discord.gg/nxy2Qq4YP4"));
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), this,
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Bemoji", "https://discord.gg/nxy2Qq4YP4");
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
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
                if (swipe_to_refresh.isRefreshing()) {
                    swipe_to_refresh.setRefreshing(false);
                }
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

                        numbersAnimator(emojisCounter, 0, emojisList.size(), 1000);

                    } catch (Exception e) {
                        Log.d("EmojisRequestListener", e.toString());
                    }
                } else {
                    if (tag.equals("CATEGORIES")) {
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
                    } else {
                        if (tag.equals("PACKS")) {
                            try {
                                packsList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                                }.getType());
                                sharedPref.edit().putString("packsDataOriginal", response).apply();
                                sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                                packs_recycler.setAdapter(new HomePacksAdapter.Packs_recyclerAdapter(packsList));

                            } catch (Exception e) {
                                getOnlineEmojis();
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

        swipe_to_refresh.setOnRefreshListener(() -> {
            generateActivityDescription();
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, CATEGORIES_API_LINK, "CATEGORIES", EmojisRequestListener);
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, EMOJIS_API_LINK, "EMOJIS", EmojisRequestListener);
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, PACKS_API_LINK, "PACKS", EmojisRequestListener);
            loadingView.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.GONE);
            loadAds();
        });


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
        if (sharedPref.getBoolean("isAskingForReloadEmojis", false)) {
            sharedPref.edit().putBoolean("isAskingForReloadEmojis", false).apply();
            recreate();
        } else if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_DENIED) {
            getLocalEmojis(false);
        }
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

        SnapHelper snapHelper = new PagerSnapHelper();
        packs_recycler.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(packs_recycler);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        local_recycler.setLayoutManager(layoutManager2);

        if (sharedPref.getInt("opened_so_far", 0) >= 2) {
            sharedPref.edit().putBoolean("isAskingForReloadEmojis", true).apply();
            sharedPref.edit().putInt("opened_so_far", 0).apply();
        } else {
            int opened_so_far = sharedPref.getInt("opened_so_far", 0) + 1;
            sharedPref.edit().putInt("opened_so_far", opened_so_far).apply();
        }

        if (sharedPref.getBoolean("isAskingForReloadEmojis", true)) {
            sharedPref.edit().putBoolean("isAskingForReloadEmojis", false).apply();
            sharedPref.edit().putString("emojisData", "").apply();
            sharedPref.edit().putString("categoriesData", "").apply();
            sharedPref.edit().putString("packsData", "").apply();
        }

        if (sharedPref.getBoolean("isAskingForReloadEmojis", false) || (sharedPref.getString("categoriesData", "").isEmpty() || (sharedPref.getString("packsData", "").isEmpty() || sharedPref.getString("emojisData", "").isEmpty()))) {
            loadingView.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.GONE);
        } else {
            loadingView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }

        OverScrollDecoratorHelper.setUpOverScroll(packs_recycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        OverScrollDecoratorHelper.setUpOverScroll(local_recycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

        getOnlineEmojis();
        loadAds();
    }


    public void LOGIC_FRONTEND() {
        if (Build.VERSION.SDK_INT < 23) {
            statusBarColor("#7289DA", this);
            LIGHT_ICONS(this);
        } else {
            statusBarColor("#FFFFFF", this);
            DARK_ICONS(this);
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
        MainUIMethods.setViewRadius(discord_img, 30, "#FAFAFA");
        generateActivityDescription();
    }

    private void getOnlineEmojis() {
        if (sharedPref.getString("emojisData", "").isEmpty()) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, EMOJIS_API_LINK, "EMOJIS", EmojisRequestListener);
        } else {
            emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            emojisCounter.setText(String.valueOf((long) (emojisList.size())));
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
        if (sharedPref.getString("packsData", "").isEmpty()) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, PACKS_API_LINK, "PACKS", EmojisRequestListener);
        } else {
            try {
                packsList = new Gson().fromJson(sharedPref.getString("packsData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
                sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                packs_recycler.setAdapter(new HomePacksAdapter.Packs_recyclerAdapter(packsList));
            } catch (Exception e) {
                Utils.showToast(getApplicationContext(), (e.toString()));
            }
        }
    }

    public void getLocalEmojis(boolean isGettingDataFirstTime) {
        if (!isGettingDataFirstTime) {
            try {
                Objects.requireNonNull(local_recycler.getAdapter()).notifyDataSetChanged();
            } catch (Exception ignored) {
            }
        }
        try {
            fileManager = new FileManager();
            new Thread(() -> {

                localEmojisList = fileManager.getList();
                new Handler(Looper.getMainLooper()).post(() -> {

                    try {
                        for (int i = 0; i < (localEmojisList.size() - 1); i++) {
                            localEmojisScanPath = Objects.requireNonNull(localEmojisList.get(i).get("filePath")).toString();
                            File file1 = new File(localEmojisScanPath);
                            try {
                                long length = file1.length();
                                length = length / 1024;
                                if (length == 0) {
                                    localEmojisList.remove(i);
                                }
                            } catch (Exception e) {
                                localEmojisList.remove(i);
                            }
                        }
                        if (localEmojisList.size() == 0) {
                            localEmojisView.setVisibility(View.GONE);
                        } else {
                            Utils.sortListMap(localEmojisList, "lastModifiedTime", false, false);
                            local_recycler.setAdapter(new LocalEmojisAdapter.Local_recyclerAdapter(localEmojisList));

                            new Handler().postDelayed(() -> localEmojisView.setVisibility(View.VISIBLE), 1000);

                        }
                    } catch (Exception e) {
                        Log.d("recycler error", e.toString());
                    }

                });
            }).start();
        } catch (Exception e) {
            Log.e("local emojis error", e.toString());
        }
    }

    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
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
            showMessageDialog(getString(R.string.update_now_dialog_title), getString(R.string.new_update_downloaded), getString(R.string.update_now_button), getString(R.string.dialog_negative_text), this,
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

    private void generateActivityDescription() {
        try {
            String[] tips = new String[]{getString(R.string.home_subtitle), getString(R.string.pro_tip_1), getString(R.string.pro_tip_2), getString(R.string.welcome_subtitle_1)};
            int min = 0;
            int max = tips.length;
            Random random1 = new Random();
            int random = random1.nextInt(max - min) + min;
            activityDescription.setText(tips[random]);
        } catch (Exception ignored) {
        }
    }

    private void loadAds() {
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

    public void startRefreshFromSettings() {
        startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, CATEGORIES_API_LINK, "CATEGORIES", EmojisRequestListener);
        startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, EMOJIS_API_LINK, "EMOJIS", EmojisRequestListener);
        startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, PACKS_API_LINK, "PACKS", EmojisRequestListener);
        loadingView.setVisibility(View.VISIBLE);
        mainView.setVisibility(View.GONE);
        swipe_to_refresh.setRefreshing(true);
    }

}