package com.nerbly.bemoji;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.Timer;
import java.util.TimerTask;

public class CategoriesActivity extends AppCompatActivity {
    private final Timer _timer = new Timer();
    private final ArrayList<HashMap<String, Object>> shimmerList = new ArrayList<>();
    private final Intent toEmojis = new Intent();
    BottomSheetBehavior sheetBehavior;
    private HashMap<String, Object> categoriesMap = new HashMap<>();
    private ArrayList<HashMap<String, Object>> categoriesList = new ArrayList<>();
    private LinearLayout bsheetbehavior;
    private LinearLayout background;
    private LinearLayout slider;
    private TextView title;
    private TextView subtitle;
    private RecyclerView categoriesRecycler;
    private RecyclerView loadingRecycler;
    private RequestNetwork startGettingCategories;
    private RequestNetwork.RequestListener _startGettingCategories_request_listener;
    private SharedPreferences sharedPref;
    private TimerTask loadingTmr;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.categories);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        CoordinatorLayout linear1 = findViewById(R.id.linear1);
        bsheetbehavior = findViewById(R.id.bsheetbehavior);
        background = findViewById(R.id.background);
        slider = findViewById(R.id.slider);
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        categoriesRecycler = findViewById(R.id.categoriesRecycler);
        loadingRecycler = findViewById(R.id.loadingRecycler);
        startGettingCategories = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        _startGettingCategories_request_listener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
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
                    System.err.println(e.toString());
                }

                sharedPref.edit().putString("categoriesData", new Gson().toJson(categoriesList)).apply();
                Utils.sortListMap(categoriesList, "category_name", false, true);
                categoriesRecycler.setAdapter(new CategoriesRecyclerAdapter(categoriesList));
                loadingTmr = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingRecycler.setVisibility(View.GONE);
                                categoriesRecycler.setVisibility(View.VISIBLE);
                                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        });
                    }
                };
                _timer.schedule(loadingTmr, 800);
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        };
    }

    private void initializeLogic() {
        _LOGIC_BACKEND();
        _LOGIC_FRONTEND();
    }

    @Override
    public void onBackPressed() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void _LOGIC_BACKEND() {
        sheetBehavior = BottomSheetBehavior.from(bsheetbehavior);
        loadingRecycler.setLayoutManager(new LinearLayoutManager(this));
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(this));
        for (int _repeat39 = 0; _repeat39 < 30; _repeat39++) {
            HashMap<String, Object> shimmerMap = new HashMap<>();
            shimmerMap.put("key", "value");
            shimmerList.add(shimmerMap);
        }
        loadingRecycler.setAdapter(new LoadingRecyclerAdapter(shimmerList));
        if (sharedPref.getString("categoriesData", "").equals("")) {
            startGettingCategories.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/?request=categories", "", _startGettingCategories_request_listener);
        } else {
            categoriesList = new Gson().fromJson(sharedPref.getString("categoriesData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            Utils.sortListMap(categoriesList, "category_name", false, true);
            categoriesRecycler.setAdapter(new CategoriesRecyclerAdapter(categoriesList));
            loadingTmr = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingRecycler.setVisibility(View.GONE);
                            categoriesRecycler.setVisibility(View.VISIBLE);
                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });
                }
            };
            _timer.schedule(loadingTmr, 1000);
        }
        _BottomSheetBehaviorListener();
        background.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        categoriesRecycler.setHasFixedSize(true);
        loadingRecycler.setHasFixedSize(true);
    }


    public void _LOGIC_FRONTEND() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
        subtitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
        MainUIMethods.advancedCorners(background, "#FFFFFF", 40, 40, 0, 0);
        MainUIMethods.setViewRadius(slider, 90, "#E0E0E0");
        MainUIMethods.DARK_ICONS(this);
        MainUIMethods.transparentStatusBar(this);
    }


    public void _BottomSheetBehaviorListener() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish();
                } else {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        MainUIMethods.shadAnim(background, "elevation", 20, 200);
                        MainUIMethods.shadAnim(slider, "translationY", 0, 200);
                        MainUIMethods.shadAnim(slider, "alpha", 1, 200);
                        slider.setVisibility(View.VISIBLE);
                    } else {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            MainUIMethods.shadAnim(background, "elevation", 0, 200);
                            MainUIMethods.shadAnim(slider, "translationY", -200, 200);
                            MainUIMethods.shadAnim(slider, "alpha", 0, 200);
                            slider.setVisibility(View.INVISIBLE);
                        } else {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                MainUIMethods.shadAnim(background, "elevation", 20, 200);
                                MainUIMethods.shadAnim(slider, "translationY", 0, 200);
                                MainUIMethods.shadAnim(slider, "alpha", 1, 200);
                                slider.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

    }

    public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;

        public CategoriesRecyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.categoriesview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, @SuppressLint("RecyclerView") final int _position) {
            View _view = _holder.itemView;

            final TextView textview1 = _view.findViewById(R.id.textview1);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _view.setLayoutParams(_lp);
            textview1.setText(Objects.requireNonNull(_data.get(_position).get("category_name")).toString());
            textview1.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("InflateParams")
                @Override
                public void onClick(View _view) {
                    if (Objects.requireNonNull(_data.get(_position).get("category_name")).toString().equals("Animated")) {
                        final com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(CategoriesActivity.this, R.style.materialsheet);

                        View bottomSheetView;
                        bottomSheetView = getLayoutInflater().inflate(R.layout.infosheet, null);
                        bottomSheetDialog.setContentView(bottomSheetView);

                        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

                        final TextView infook = bottomSheetView.findViewById(R.id.infosheet_ok);

                        final TextView infocancel = bottomSheetView.findViewById(R.id.infosheet_cancel);

                        final TextView infotitle = bottomSheetView.findViewById(R.id.infosheet_title);

                        final TextView infosub = bottomSheetView.findViewById(R.id.infosheet_sub);

                        final LinearLayout infoback = bottomSheetView.findViewById(R.id.infosheet_back);

                        final LinearLayout slider = bottomSheetView.findViewById(R.id.slider);

                        MainUIMethods.advancedCorners(infoback, "#ffffff", 38, 38, 0, 0);

                        MainUIMethods.rippleRoundStroke(infook, "#7289DA", "#6275BB", 20, 0, "#007EEF");

                        MainUIMethods.rippleRoundStroke(infocancel, "#424242", "#181818", 20, 0, "#007EEF");

                        MainUIMethods.setViewRadius(slider, 180, "#BDBDBD");
                        infotitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
                        infosub.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
                        infook.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
                        infocancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
                        infook.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                toEmojis.putExtra("switchFrom", "categories");
                                toEmojis.putExtra("category_id", Objects.requireNonNull(_data.get(_position).get("category_id")).toString());
                                toEmojis.setClass(getApplicationContext(), EmojisActivity.class);
                                startActivity(toEmojis);
                                bottomSheetDialog.dismiss();
                            }
                        });
                        infocancel.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                bottomSheetDialog.dismiss();
                            }
                        });
                        if (!isFinishing()) {
                            bottomSheetDialog.show();
                        }
                    } else {
                        toEmojis.putExtra("switchFrom", "categories");
                        toEmojis.putExtra("category_id", Objects.requireNonNull(_data.get(_position).get("category_id")).toString());
                        toEmojis.setClass(getApplicationContext(), EmojisActivity.class);
                        startActivity(toEmojis);
                    }
                }
            });
            textview1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
            MainUIMethods.rippleRoundStroke(textview1, "#F5F5F5", "#EEEEEE", 25, 1, "#EEEEEE");
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
            final LinearLayout packsloading = _view.findViewById(R.id.packsloading);

            MainUIMethods.setClippedView(categoriesShimmer, "#FFFFFF", 30, 0);
            packsloading.setVisibility(View.GONE);
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
