package com.nerbly.bemoji;

import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Adapters.LoadingPacksAdapter;
import com.nerbly.bemoji.Functions.RequestNetwork;
import com.nerbly.bemoji.Functions.RequestNetworkController;
import com.nerbly.bemoji.Functions.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PacksActivity extends AppCompatActivity {
    private final Timer timer = new Timer();
    private final ArrayList<String> packsArrayList = new ArrayList<>();
    private final ArrayList<HashMap<String, Object>> shimmerList = new ArrayList<>();
    private final Intent toPreview = new Intent();
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private String packsTempArrayString = "";
    private String currentPositionPackArray = "";
    private ArrayList<HashMap<String, Object>> packsList = new ArrayList<>();
    private LinearLayout bsheetbehavior;
    private LinearLayout background;
    private LinearLayout adview;
    private LinearLayout slider;
    private TextView textview3;
    private TextView textview4;
    private RecyclerView packsRecycler;
    private RecyclerView loadingRecycler;
    private RequestNetwork startGettingPacks;
    private RequestNetwork.RequestListener PacksRequestListener;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.packs);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        CoordinatorLayout linear1 = findViewById(R.id.tutorialBg);
        bsheetbehavior = findViewById(R.id.sheetBehavior);
        background = findViewById(R.id.background);
        adview = findViewById(R.id.adview);
        slider = findViewById(R.id.slider);
        textview3 = findViewById(R.id.download_tv);
        textview4 = findViewById(R.id.activityDescription);
        packsRecycler = findViewById(R.id.packsRecycler);
        loadingRecycler = findViewById(R.id.loadingRecycler);
        startGettingPacks = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        PacksRequestListener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                try {
                    packsList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                    sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                    packsRecycler.setAdapter(new PacksRecyclerAdapter(packsList));
                } catch (Exception e) {
                    Utils.showToast(getApplicationContext(), (e.toString()));
                }
                loadingRecycler.setVisibility(View.GONE);
                packsRecycler.setVisibility(View.VISIBLE);
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
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void LOGIC_BACKEND() {
        sheetBehavior = BottomSheetBehavior.from(bsheetbehavior);
        loadingRecycler.setLayoutManager(new LinearLayoutManager(this));
        packsRecycler.setLayoutManager(new LinearLayoutManager(this));
        bottomSheetBehaviorListener();
        background.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        AudienceNetworkAds.initialize(this);

        AdView bannerAd = new AdView(this, "3773974092696684_3774022966025130", AdSize.BANNER_HEIGHT_50);

        adview.addView(bannerAd);

        bannerAd.loadAd();
        for (int i = 0; i < 30; i++) {
            HashMap<String, Object> shimmerMap = new HashMap<>();
            shimmerMap.put("key", "value");
            shimmerList.add(shimmerMap);
        }
        loadingRecycler.setAdapter(new LoadingPacksAdapter.LoadingRecyclerAdapter(shimmerList));
        new getPacksTask().execute("");
        packsRecycler.setHasFixedSize(true);
        loadingRecycler.setHasFixedSize(true);
    }

    public void LOGIC_FRONTEND() {
        textview3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
        textview4.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
        advancedCorners(background, "#FFFFFF", 40, 40, 0, 0);
        setViewRadius(slider, 90, "#E0E0E0");
        DARK_ICONS(this);
        transparentStatusBar(this);
    }

    public void bottomSheetBehaviorListener() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish();
                } else {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        shadAnim(background, "elevation", 20, 200);
                        shadAnim(slider, "translationY", 0, 200);
                        shadAnim(slider, "alpha", 1, 200);
                        slider.setVisibility(View.VISIBLE);
                    } else {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            shadAnim(background, "elevation", 0, 200);
                            shadAnim(slider, "translationY", -200, 200);
                            shadAnim(slider, "alpha", 0, 200);
                            slider.setVisibility(View.INVISIBLE);
                        } else {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                shadAnim(background, "elevation", 20, 200);
                                shadAnim(slider, "translationY", 0, 200);
                                shadAnim(slider, "alpha", 1, 200);
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

    public void _setImageFromUrl(final ImageView image, final String url) {
        Glide.with(this)

                .load(url)
                .centerCrop()
                .into(image);

    }

    public String _capitalizedFirstWord(final String _data) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(_data);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, Objects.requireNonNull(capMatcher.group(1)).toUpperCase() + Objects.requireNonNull(capMatcher.group(2)).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();

    }

    @SuppressLint("StaticFieldLeak")
    private class getPacksTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            if (sharedPref.getString("packsData", "").isEmpty()) {
                startGettingPacks.startRequestNetwork(RequestNetworkController.GET, "https://emoji.gg/api/packs", "", PacksRequestListener);
            } else {
                try {
                    packsList = new Gson().fromJson(sharedPref.getString("packsData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                    sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                } catch (Exception e) {
                    Utils.showToast(getApplicationContext(), (e.toString()));
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(String _result) {
            if (!sharedPref.getString("packsData", "").isEmpty()) {
                packsRecycler.setAdapter(new PacksRecyclerAdapter(packsList));
                TimerTask loadingTmr = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                packsRecycler.setVisibility(View.VISIBLE);
                                loadingRecycler.setVisibility(View.GONE);
                                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        });
                    }
                };
                timer.schedule(loadingTmr, 1000);
            }
        }
    }

    public class PacksRecyclerAdapter extends RecyclerView.Adapter<PacksRecyclerAdapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> data;

        public PacksRecyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.fullpacksview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            View view = holder.itemView;

            final com.google.android.material.card.MaterialCardView cardview1 = view.findViewById(R.id.cardview1);
            final ImageView imageview1 = view.findViewById(R.id.emoji);
            final TextView textview1 = view.findViewById(R.id.tutorialTitle);
            final TextView textview2 = view.findViewById(R.id.tutorialSubtitle);
            final TextView textview3 = view.findViewById(R.id.download_tv);
            final TextView textview4 = view.findViewById(R.id.activityDescription);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(_lp);
            textview1.setText(_capitalizedFirstWord(Objects.requireNonNull(data.get(position).get("name")).toString().replace("_", " ")));
            textview2.setText(Objects.requireNonNull(data.get(position).get("description")).toString());
            textview3.setText(String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(data.get(position).get("amount")).toString()))));
            textview1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
            textview2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
            textview3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
            textview4.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
            _setImageFromUrl(imageview1, Objects.requireNonNull(data.get(position).get("image")).toString());
            cardview1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    try {
                        packsTempArrayString = new Gson().toJson(packsList);
                        JSONArray backPacksArray = new JSONArray(packsTempArrayString);
                        JSONObject packsObject = backPacksArray.getJSONObject(position);

                        JSONArray frontPacksArray = packsObject.getJSONArray("emojis");
                        for (int frontPacksInt = 0; frontPacksInt < frontPacksArray.length(); frontPacksInt++) {
                            packsArrayList.add(frontPacksArray.getString(frontPacksInt));
                        }
                        currentPositionPackArray = new Gson().toJson(packsArrayList);
                        packsArrayList.clear();
                    } catch (Exception ignored) {

                    }
                    toPreview.putExtra("switchType", "pack");
                    toPreview.putExtra("title", "BemojiPack_".concat(String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(data.get(position).get("id")).toString())))));
                    toPreview.putExtra("subtitle", Objects.requireNonNull(data.get(position).get("description")).toString());
                    toPreview.putExtra("imageUrl", Objects.requireNonNull(data.get(position).get("image")).toString());
                    toPreview.putExtra("fileName", Objects.requireNonNull(data.get(position).get("slug")).toString());
                    toPreview.putExtra("packEmojisArray", currentPositionPackArray);
                    toPreview.putExtra("packEmojisAmount", Objects.requireNonNull(data.get(position).get("amount")).toString());
                    toPreview.putExtra("packName", _capitalizedFirstWord(Objects.requireNonNull(data.get(position).get("name")).toString().replace("_", " ")));
                    toPreview.putExtra("packId", Objects.requireNonNull(data.get(position).get("id")).toString());
                    toPreview.setClass(getApplicationContext(), PackPreviewActivity.class);
                    startActivity(toPreview);
                }
            });
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