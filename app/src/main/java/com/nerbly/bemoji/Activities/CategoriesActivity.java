package com.nerbly.bemoji.Activities;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.nerbly.bemoji.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import static com.nerbly.bemoji.Configurations.CATEGORIES_API_LINK;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;
import static com.nerbly.bemoji.UI.UserInteractions.showMessageSheet;

public class CategoriesActivity extends AppCompatActivity {
    ArrayList<HashMap<String, Object>> shimmerList = new ArrayList<>();
    Intent toEmojis = new Intent();
    BottomSheetBehavior<LinearLayout> sheetBehavior;
    HashMap<String, Object> categoriesMap = new HashMap<>();
    ArrayList<HashMap<String, Object>> categoriesList = new ArrayList<>();
    LinearLayout bsheetbehavior;
    LinearLayout background;
    LinearLayout slider;
    RecyclerView categoriesRecycler;
    RecyclerView loadingRecycler;
    RequestNetwork RequestCategories;
    RequestNetwork.RequestListener CategoriesRequestListener;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.categories);
        initialize();
        initializeLogic();
    }

    void initialize() {
        bsheetbehavior = findViewById(R.id.sheetBehavior);
        background = findViewById(R.id.background);
        slider = findViewById(R.id.slider);
        categoriesRecycler = findViewById(R.id.categoriesRecycler);
        loadingRecycler = findViewById(R.id.loadingRecycler);
        RequestCategories = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);
        CoordinatorLayout coordinator = findViewById(R.id.coordinator);

        coordinator.setOnClickListener(_view -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));

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
                new Handler().postDelayed(() -> sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 500);

                new Handler().postDelayed(() -> {
                    loadingRecycler.setVisibility(View.GONE);
                    categoriesRecycler.setVisibility(View.VISIBLE);
                }, 1000);
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        };
    }

    void initializeLogic() {
        LOGIC_BACKEND();
        LOGIC_FRONTEND();
    }

    @Override
    public void onBackPressed() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void LOGIC_BACKEND() {
        overridePendingTransition(R.anim.fade_in, 0);
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
            RequestCategories.startRequestNetwork(RequestNetworkController.GET, CATEGORIES_API_LINK, "", CategoriesRequestListener);
        } else {
            categoriesList = new Gson().fromJson(sharedPref.getString("categoriesData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            Utils.sortListMap(categoriesList, "category_name", false, true);
            categoriesRecycler.setAdapter(new CategoriesRecyclerAdapter(categoriesList));

            new Handler().postDelayed(() -> sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 500);

            new Handler().postDelayed(() -> {
                loadingRecycler.setVisibility(View.GONE);
                categoriesRecycler.setVisibility(View.VISIBLE);
            }, 1000);
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
        ArrayList<HashMap<String, Object>> data;

        public CategoriesRecyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            data = _arr;
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
        public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            View view = holder.itemView;

            TextView textview1 = view.findViewById(R.id.emptyTitle);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(_lp);
            textview1.setText(Objects.requireNonNull(data.get(position).get("category_name")).toString());
            textview1.setOnClickListener(_view -> {
                if (Objects.requireNonNull(data.get(position).get("category_name")).toString().equals("Animated")) {


                    showMessageSheet(getString(R.string.animated_emojis_warning_title), R.drawable.smiley_face_flatline,
                            getString(R.string.animated_emojis_warning_btnok), getString(R.string.animated_emojis_warning_btncancel),
                            getString(R.string.animated_emojis_warning_subtitle), CategoriesActivity.this, View -> {

                                toEmojis.putExtra("switchFrom", "categories");
                                toEmojis.putExtra("category_id", Objects.requireNonNull(data.get(position).get("category_id")).toString());
                                toEmojis.setClass(getApplicationContext(), EmojisActivity.class);
                                startActivity(toEmojis);


                    }, View -> {
                    });

/*
                    com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(CategoriesActivity.this, R.style.materialsheet);

                    View bottomSheetView;
                    bottomSheetView = getLayoutInflater().inflate(R.layout.infosheet, null);
                    bottomSheetDialog.setContentView(bottomSheetView);

                    bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

                    TextView infook = bottomSheetView.findViewById(R.id.infosheet_ok);
                    TextView infocancel = bottomSheetView.findViewById(R.id.infosheet_cancel);
                    LinearLayout infoback = bottomSheetView.findViewById(R.id.infosheet_back);
                    LinearLayout slider = bottomSheetView.findViewById(R.id.slider);

                    advancedCorners(infoback, "#ffffff", 38, 38, 0, 0);

                    rippleRoundStroke(infook, "#7289DA", "#6275BB", 20, 0, "#007EEF");

                    rippleRoundStroke(infocancel, "#424242", "#181818", 20, 0, "#007EEF");

                    setViewRadius(slider, 180, "#BDBDBD");
                    infook.setOnClickListener(v -> {
                        toEmojis.putExtra("switchFrom", "categories");
                        toEmojis.putExtra("category_id", Objects.requireNonNull(data.get(position).get("category_id")).toString());
                        toEmojis.setClass(getApplicationContext(), EmojisActivity.class);
                        startActivity(toEmojis);
                        bottomSheetDialog.dismiss();
                    });
                    infocancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
                    if (!isFinishing()) {
                        bottomSheetDialog.show();
                    }

 */
                } else {
                    toEmojis.putExtra("switchFrom", "categories");
                    toEmojis.putExtra("category_id", Objects.requireNonNull(data.get(position).get("category_id")).toString());
                    toEmojis.setClass(getApplicationContext(), EmojisActivity.class);
                    startActivity(toEmojis);
                }
            });
            rippleRoundStroke(textview1, "#F5F5F5", "#EEEEEE", 25, 1, "#EEEEEE");
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

    }

}
