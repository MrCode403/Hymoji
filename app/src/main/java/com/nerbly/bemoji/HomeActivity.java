package com.nerbly.bemoji;

import static com.nerbly.bemoji.Functions.MainFunctions.capitalizedFirstWord;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.NavStatusBarColor;
import static com.nerbly.bemoji.UI.MainUIMethods.changeActivityFont;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Adapters.*;
import com.nerbly.bemoji.Functions.FileManager;
import com.nerbly.bemoji.Functions.RequestNetwork;
import com.nerbly.bemoji.Functions.RequestNetworkController;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.UI.MainUIMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class HomeActivity extends AppCompatActivity {

    private final ArrayList<HashMap<String, Object>> shimmerList = new ArrayList<>();
    private final ArrayList<String> packsArrayList = new ArrayList<>();
    private final Intent toPreview = new Intent();
    private final Intent toSearch = new Intent();
    private final Intent toCategories = new Intent();
    private final Intent toHelp = new Intent();
    private final Intent toSettings = new Intent();
    private final Intent toPacks = new Intent();
    FirebaseAnalytics mFirebaseAnalytics;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    com.google.android.material.snackbar.Snackbar _snackBarView;
    com.google.android.material.snackbar.Snackbar.SnackbarLayout _sblayout;
    private FileManager fileManager;
    private HashMap<String, Object> categoriesMap = new HashMap<>();
    private double emojisCount = 0;
    private double emojisScanPosition = 0;
    private double localEmojisScanPosition = 0;
    private String localEmojisScanPath = "";
    private String currentPositionPackArray = "";
    private String packsTempArrayString = "";
    private ArrayList<HashMap<String, Object>> emojisList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> categoriesList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> packsList = new ArrayList<>();
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
    private LinearLayout linear53;
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
    private TextView textview53;
    private RequestNetwork startGettingEmojis;
    private RequestNetwork.RequestListener _startGettingEmojis_request_listener;
    private SharedPreferences sharedPref;
    private RequestNetwork getSuggestions;
    private RequestNetwork.RequestListener _getSuggestions_request_listener;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.home);
        initialize(_savedInstanceState);
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize(Bundle _savedInstanceState) {
        adview = findViewById(R.id.adview);
        vscroll2 = findViewById(R.id.vscroll2);
        textview1 = findViewById(R.id.textview1);
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
        linear53 = findViewById(R.id.linear53);
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
        textview4 = findViewById(R.id.textview4);
        textview53 = findViewById(R.id.textview53);
        startGettingEmojis = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);
        getSuggestions = new RequestNetwork(this);

        searchcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                toSearch.putExtra("switchFrom", "search");
                toSearch.setClass(getApplicationContext(), EmojisActivity.class);
                _transitionManager(searchcard, "searchbox", toSearch);
            }
        });

        linear53.setOnClickListener(new View.OnClickListener() {
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
            public void onScrolled(@NonNull RecyclerView recyclerView, int _offsetX, int _offsetY) {
                super.onScrolled(recyclerView, _offsetX, _offsetY);
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    textview53.setVisibility(View.INVISIBLE);
                } else {
                    textview53.setVisibility(View.VISIBLE);
                }
            }
        });

        dock1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (textview42.getText().toString().equals("0")) {
                    _showCustomSnackBar("Hold up, wait a minute. We're getting emojis...");
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
                    _showCustomSnackBar("Please wait, we're getting categories...");
                } else {
                    if (textview42.getText().toString().equals("0")) {
                        _showCustomSnackBar("Hold up, wait a minute. We're getting emojis...");
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

        _startGettingEmojis_request_listener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                if (tag.equals("EMOJIS")) {
                    try {
                        emojisList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                        }.getType());
                        emojisCount = emojisList.size();
                        emojisScanPosition = emojisCount - 1;
                        for (int _repeat91 = 0; _repeat91 < (int) (emojisCount); _repeat91++) {
                            if (Objects.requireNonNull(emojisList.get((int) emojisScanPosition).get("category")).toString().equals("9.0")) {
                                emojisList.remove((int) (emojisScanPosition));
                            }
                            emojisScanPosition--;
                        }
                        sharedPref.edit().putString("emojisData", new Gson().toJson(emojisList)).apply();
                        MainUIMethods.numbersAnimator(textview42, 0, emojisList.size(), 1000);
                    } catch (Exception e) {
                        Utils.showMessage(getApplicationContext(), (e.toString()));
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
                                packs_recycler.setAdapter(new Packs_recyclerAdapter(packsList));
                            } catch (Exception e) {
                                Utils.showMessage(getApplicationContext(), (e.toString()));
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

        _getSuggestions_request_listener = new RequestNetwork.RequestListener() {
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
        _LOGIC_FRONTEND();
        _LOGIC_BACKEND();
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
            _getLocalEmojis();
        }
    }

    public void _LOGIC_BACKEND() {
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
        if (sharedPref.getString("isNewEmojisAvailable", "").equals("true") || (sharedPref.getString("categoriesData", "").equals("") || (sharedPref.getString("packsData", "").equals("") || sharedPref.getString("emojisData", "").equals("")))) {
            loadingView.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.GONE);
            for (int _repeat77 = 0; _repeat77 < 20; _repeat77++) {
                HashMap<String, Object> shimmerMap = new HashMap<>();
                shimmerMap.put("key", "value");
                shimmerList.add(shimmerMap);
            }
            loadingRecycler.setAdapter(new LoadingRecyclerAdapter(shimmerList));
        } else {
            loadingView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }
        getSuggestions.startRequestNetwork(RequestNetworkController.GET, "https://nerbly.com/bemoji/suggestions.json", "", _getSuggestions_request_listener);
        OverScrollDecoratorHelper.setUpOverScroll(packs_recycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

        OverScrollDecoratorHelper.setUpOverScroll(local_recycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

        OverScrollDecoratorHelper.setUpOverScroll(vscroll2);
        if (sharedPref.getString("emojisData", "").equals("") || sharedPref.getString("isNewEmojisAvailable", "").equals("true")) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/", "EMOJIS", _startGettingEmojis_request_listener);
        } else {
            emojisList = new Gson().fromJson(sharedPref.getString("emojisData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            textview42.setText(String.valueOf((long) (emojisList.size())));
        }
        if (sharedPref.getString("categoriesData", "").equals("") || sharedPref.getString("isNewEmojisAvailable", "").equals("true")) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/?request=categories", "CATEGORIES", _startGettingEmojis_request_listener);
        } else {
            categoriesList = new Gson().fromJson(sharedPref.getString("categoriesData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            textview44.setText(String.valueOf((long) (categoriesList.size())));
        }
        if (sharedPref.getString("packsData", "").equals("") || sharedPref.getString("isNewEmojisAvailable", "").equals("true")) {
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/packs", "PACKS", _startGettingEmojis_request_listener);
        } else {
            try {
                packsList = new Gson().fromJson(sharedPref.getString("packsData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
                sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                packs_recycler.setAdapter(new Packs_recyclerAdapter(packsList));
            } catch (Exception e) {
                Utils.showMessage(getApplicationContext(), (e.toString()));
            }
            startGettingEmojis.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/packs", "PACKS_1", _startGettingEmojis_request_listener);
        }
        AudienceNetworkAds.initialize(this);

        AdView bannerAd = new AdView(this, "3773974092696684_3774022966025130", AdSize.BANNER_HEIGHT_50);

        adview.addView(bannerAd);

        bannerAd.loadAd();
    }


    public void _LOGIC_FRONTEND() {
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


    public void _transitionManager(final View _view, final String _transitionName, final Intent _intent) {
        _view.setTransitionName(_transitionName);
        android.app.ActivityOptions optionsCompat = android.app.ActivityOptions.makeSceneTransitionAnimation(this, _view, _transitionName);
        startActivity(_intent, optionsCompat.toBundle());
    }


    public void _setImageFromUrl(final ImageView _image, final String _url) {
        Glide.with(this)

                .load(_url)
                .centerCrop()
                .into(_image);

    }


    public void _showCustomSnackBar(final String _text) {
        ViewGroup parentLayout = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        _snackBarView = com.google.android.material.snackbar.Snackbar.make(parentLayout, "", com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
        _sblayout = (com.google.android.material.snackbar.Snackbar.SnackbarLayout) _snackBarView.getView();

        @SuppressLint("InflateParams") View _inflate = getLayoutInflater().inflate(R.layout.snackbar, null);
        _sblayout.setPadding(0, 0, 0, 0);
        _sblayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
        LinearLayout back =
                _inflate.findViewById(R.id.linear1);

        TextView text =
                _inflate.findViewById(R.id.textview1);
        setViewRadius(back, 20, "#202125");
        text.setText(_text);
        text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
        _sblayout.addView(_inflate, 0);
        _snackBarView.show();
    }

    public void _getLocalEmojis() {
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
                            for (int _repeat14 = 0; _repeat14 < (localEmojisList.size() - 1); _repeat14++) {
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
                                local_recycler.setAdapter(new Local_recyclerAdapter(localEmojisList));
                            }
                        } catch (Exception ignored) {
                        }

                    }
                });
            }
        }).start();

    }

    public void setImageFromPath(final ImageView _image, final String _path) {
        java.io.File file = new java.io.File(_path);
        Uri imageUri = Uri.fromFile(file);

        Glide.with(this)
                .load(imageUri)
                .into(_image);
    }




    /*
    public void _newDataAvailableSnackBar(final String _text) {
        ViewGroup parentLayout2 = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        _snackBarView2 = com.google.android.material.snackbar.Snackbar.make(parentLayout2, "", com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
        _sblayout2 = (com.google.android.material.snackbar.Snackbar.SnackbarLayout) _snackBarView2.getView();

        View _inflate2 = getLayoutInflater().inflate(R.layout.snackbarbtn, null);
        _sblayout2.setPadding(0, 0, 0, 0);
        _sblayout2.setBackgroundColor(Color.argb(0, 0, 0, 0));
        LinearLayout back =
                _inflate2.findViewById(R.id.linear1);

        TextView text =
                _inflate2.findViewById(R.id.textview1);

        TextView btn =
                _inflate2.findViewById(R.id.textview3);
        setViewRadius(back, 20, "#202125");
        text.setText("");
        btn.setText("UPDATE NOW");
        text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), 0);
        text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), 1);
        if (_text.contains("new packs")) {

        } else {
            if (_text.contains("new emojis")) {

            } else {
                if (_text.contains("new packs and emojis")) {

                }
            }
        }
        _sblayout2.addView(_inflate, 0);
        _snackBarView2.show();
    }
    com.google.android.material.snackbar.Snackbar _snackBarView2;
    com.google.android.material.snackbar.Snackbar.SnackbarLayout _sblayout2;
    View _inflate2;
    */


    public class LoadingRecyclerAdapter extends RecyclerView.Adapter<LoadingRecyclerAdapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;

        public LoadingRecyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.loadingview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final LinearLayout categoriesShimmer = _view.findViewById(R.id.categoriesShimmer);
            final LinearLayout shimmer2 = _view.findViewById(R.id.shimmer2);
            final LinearLayout shimmer3 = _view.findViewById(R.id.shimmer3);
            final LinearLayout shimmer4 = _view.findViewById(R.id.shimmer4);

            categoriesShimmer.setVisibility(View.GONE);
            setClippedView(shimmer2, "#FFFFFF", 30, 0);
            setClippedView(shimmer3, "#FFFFFF", 200, 0);
            setClippedView(shimmer4, "#FFFFFF", 200, 0);
        }

        @Override
        public int getItemCount() {
            return _data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

    }

    public class Packs_recyclerAdapter extends RecyclerView.Adapter<Packs_recyclerAdapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;

        public Packs_recyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.packsview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, @SuppressLint("RecyclerView") final int _position) {
            View _view = _holder.itemView;

            final com.google.android.material.card.MaterialCardView cardview2 = _view.findViewById(R.id.cardview2);
            final com.google.android.material.card.MaterialCardView cardview1 = _view.findViewById(R.id.cardview1);
            final TextView textview1 = _view.findViewById(R.id.textview1);
            final TextView textview2 = _view.findViewById(R.id.textview2);
            final ImageView imageview1 = _view.findViewById(R.id.imageview1);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _view.setLayoutParams(_lp);
            textview1.setText(capitalizedFirstWord(Objects.requireNonNull(_data.get(_position).get("name")).toString().replace("_", " ")));
            textview2.setText(Objects.requireNonNull(_data.get(_position).get("description")).toString());
            textview1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
            textview2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
            _setImageFromUrl(imageview1, Objects.requireNonNull(_data.get(_position).get("image")).toString());
            cardview1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    try {
                        packsTempArrayString = new Gson().toJson(packsList);
                        JSONArray backPacksArray = new JSONArray(packsTempArrayString);
                        JSONObject packsObject = backPacksArray.getJSONObject(_position);

                        JSONArray frontPacksArray = packsObject.getJSONArray("emojis");
                        for (int frontPacksInt = 0; frontPacksInt < frontPacksArray.length(); frontPacksInt++) {
                            packsArrayList.add(frontPacksArray.getString(frontPacksInt));
                        }
                        currentPositionPackArray = new Gson().toJson(packsArrayList);
                        packsArrayList.clear();
                    } catch (Exception ignored) {

                    }
                    toPreview.putExtra("switchType", "pack");
                    toPreview.putExtra("title", "BemojiPack_" + (long) (Double.parseDouble(Objects.requireNonNull(_data.get(_position).get("id")).toString())));
                    toPreview.putExtra("subtitle", Objects.requireNonNull(_data.get(_position).get("description")).toString());
                    toPreview.putExtra("imageUrl", Objects.requireNonNull(_data.get(_position).get("image")).toString());
                    toPreview.putExtra("fileName", Objects.requireNonNull(_data.get(_position).get("slug")).toString());
                    toPreview.putExtra("packEmojisArray", currentPositionPackArray);
                    toPreview.putExtra("packEmojisAmount", Objects.requireNonNull(_data.get(_position).get("amount")).toString());
                    toPreview.putExtra("packName", capitalizedFirstWord(Objects.requireNonNull(_data.get(_position).get("name")).toString().replace("_", " ")));
                    toPreview.putExtra("packId", Objects.requireNonNull(_data.get(_position).get("id")).toString());
                    toPreview.setClass(getApplicationContext(), PackpreviewActivity.class);
                    startActivity(toPreview);
                }
            });
            if (_position == 0) {
                cardview2.setVisibility(View.VISIBLE);
            } else {
                cardview2.setVisibility(View.GONE);
            }
            cardview2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    toPacks.setClass(getApplicationContext(), PacksActivity.class);
                    startActivity(toPacks);
                }
            });
        }

        @Override
        public int getItemCount() {
            return _data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

    }

    public class Local_recyclerAdapter extends RecyclerView.Adapter<HomeActivity.Local_recyclerAdapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;

        public Local_recyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NonNull
        @Override
        public HomeActivity.Local_recyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.localemojisview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new HomeActivity.Local_recyclerAdapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(HomeActivity.Local_recyclerAdapter.ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final ImageView imageview1 = _view.findViewById(R.id.imageview1);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _view.setLayoutParams(_lp);
            setImageFromPath(imageview1, Objects.requireNonNull(_data.get(_position).get("filePath")).toString());
        }

        @Override
        public int getItemCount() {
            return _data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

    }

}
