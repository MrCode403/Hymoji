package com.nerbly.bemoji;

import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.nerbly.bemoji.Adapters.LoadingPacksAdapter;
import com.nerbly.bemoji.Functions.RequestNetwork;
import com.nerbly.bemoji.Functions.RequestNetworkController;
import com.nerbly.bemoji.Functions.Utils;

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
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private HashMap<String, Object> categoriesMap = new HashMap<>();
    private ArrayList<HashMap<String, Object>> categoriesList = new ArrayList<>();
    private LinearLayout bsheetbehavior;
    private LinearLayout background;
    private LinearLayout slider;
    private RecyclerView categoriesRecycler;
    private RecyclerView loadingRecycler;
    private RequestNetwork RequestCategories;
    private RequestNetwork.RequestListener CategoriesRequestListener;
    private SharedPreferences sharedPref;
    private TimerTask loadingTmr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.categories);
        initialize();
        initializeLogic();
    }

    private void initialize() {
        CoordinatorLayout linear1 = findViewById(R.id.tutorialBg);
        bsheetbehavior = findViewById(R.id.sheetBehavior);
        background = findViewById(R.id.background);
        slider = findViewById(R.id.slider);
        categoriesRecycler = findViewById(R.id.categoriesRecycler);
        loadingRecycler = findViewById(R.id.loadingRecycler);
        RequestCategories = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        linear1.setOnClickListener(_view -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));

        CategoriesRequestListener = new RequestNetwork.RequestListener() {
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
                        runOnUiThread(() -> {
                            loadingRecycler.setVisibility(View.GONE);
                            categoriesRecycler.setVisibility(View.VISIBLE);
                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
        LOGIC_BACKEND();
        LOGIC_FRONTEND();
    }

    @Override
    public void onBackPressed() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void LOGIC_BACKEND() {
        sheetBehavior = BottomSheetBehavior.from(bsheetbehavior);
        loadingRecycler.setLayoutManager(new LinearLayoutManager(this));
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(this));
        for (int i = 0; i < 30; i++) {
            HashMap<String, Object> shimmerMap = new HashMap<>();
            shimmerMap.put("key", "value");
            shimmerList.add(shimmerMap);
        }
        loadingRecycler.setAdapter(new LoadingPacksAdapter.LoadingRecyclerAdapter(shimmerList));
        if (sharedPref.getString("categoriesData", "").isEmpty()) {
            RequestCategories.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/?request=categories", "", CategoriesRequestListener);
        } else {
            categoriesList = new Gson().fromJson(sharedPref.getString("categoriesData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            Utils.sortListMap(categoriesList, "category_name", false, true);
            categoriesRecycler.setAdapter(new CategoriesRecyclerAdapter(categoriesList));
            loadingTmr = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        loadingRecycler.setVisibility(View.GONE);
                        categoriesRecycler.setVisibility(View.VISIBLE);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    });
                }
            };
            _timer.schedule(loadingTmr, 1000);
        }
        bottomSheetBehaviorListener();
        background.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        categoriesRecycler.setHasFixedSize(true);
        loadingRecycler.setHasFixedSize(true);
    }


    public void LOGIC_FRONTEND() {
        advancedCorners(background, "#FFFFFF", 40, 40, 0, 0);
        setViewRadius(slider, 90, "#E0E0E0");
        DARK_ICONS(this);
        transparentStatusBar(this);
    }


    public void bottomSheetBehaviorListener() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_DRAGGING:
                        shadAnim(background, "elevation", 20, 200);
                        shadAnim(slider, "translationY", 0, 200);
                        shadAnim(slider, "alpha", 1, 200);
                        slider.setVisibility(View.VISIBLE);
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        shadAnim(background, "elevation", 0, 200);
                        shadAnim(slider, "translationY", -200, 200);
                        shadAnim(slider, "alpha", 0, 200);
                        slider.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        finish();
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;

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
        public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            View view = holder.itemView;

            final TextView textview1 = view.findViewById(R.id.emptyTitle);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(_lp);
            textview1.setText(Objects.requireNonNull(_data.get(position).get("category_name")).toString());
            textview1.setOnClickListener(_view -> {
                if (Objects.requireNonNull(_data.get(position).get("category_name")).toString().equals("Animated")) {
                    final com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(CategoriesActivity.this, R.style.materialsheet);

                    View bottomSheetView;
                    bottomSheetView = getLayoutInflater().inflate(R.layout.infosheet, null);
                    bottomSheetDialog.setContentView(bottomSheetView);

                    bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

                    final TextView infook = bottomSheetView.findViewById(R.id.infosheet_ok);
                    final TextView infocancel = bottomSheetView.findViewById(R.id.infosheet_cancel);
                    final LinearLayout infoback = bottomSheetView.findViewById(R.id.infosheet_back);
                    final LinearLayout slider = bottomSheetView.findViewById(R.id.slider);

                    advancedCorners(infoback, "#ffffff", 38, 38, 0, 0);

                    rippleRoundStroke(infook, "#7289DA", "#6275BB", 20, 0, "#007EEF");

                    rippleRoundStroke(infocancel, "#424242", "#181818", 20, 0, "#007EEF");

                    setViewRadius(slider, 180, "#BDBDBD");
                    infook.setOnClickListener(v -> {
                        toEmojis.putExtra("switchFrom", "categories");
                        toEmojis.putExtra("category_id", Objects.requireNonNull(_data.get(position).get("category_id")).toString());
                        toEmojis.setClass(getApplicationContext(), EmojisActivity.class);
                        startActivity(toEmojis);
                        bottomSheetDialog.dismiss();
                    });
                    infocancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
                    if (!isFinishing()) {
                        bottomSheetDialog.show();
                    }
                } else {
                    toEmojis.putExtra("switchFrom", "categories");
                    toEmojis.putExtra("category_id", Objects.requireNonNull(_data.get(position).get("category_id")).toString());
                    toEmojis.setClass(getApplicationContext(), EmojisActivity.class);
                    startActivity(toEmojis);
                }
            });
            rippleRoundStroke(textview1, "#F5F5F5", "#EEEEEE", 25, 1, "#EEEEEE");
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
