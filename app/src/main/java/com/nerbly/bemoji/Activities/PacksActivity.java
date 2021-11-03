package com.nerbly.bemoji.Activities;

import static com.nerbly.bemoji.Configurations.PACKS_API_LINK;
import static com.nerbly.bemoji.Functions.MainFunctions.capitalizedFirstWord;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.Functions.SideFunctions.setHighPriorityImageFromUrl;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.statusBarColor;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Adapters.LoadingPacksAdapter;
import com.nerbly.bemoji.Functions.RequestNetwork;
import com.nerbly.bemoji.Functions.RequestNetworkController;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class PacksActivity extends AppCompatActivity {
    private final ArrayList<String> packsArrayList = new ArrayList<>();
    private final ArrayList<HashMap<String, Object>> shimmerList = new ArrayList<>();
    private final Intent toPreview = new Intent();
    private String packsTempArrayString = "";
    private String currentPositionPackArray = "";
    private ArrayList<HashMap<String, Object>> packsList = new ArrayList<>();
    private LinearLayout background;
    private AdView adview;
    private RecyclerView packsRecycler;
    private RecyclerView loadingRecycler;
    private RequestNetwork startGettingPacks;
    private RequestNetwork.RequestListener PacksRequestListener;
    private SharedPreferences sharedPref;
    private boolean isPacksOpened = false;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.packs);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        background = findViewById(R.id.background);
        adview = findViewById(R.id.adview);
        packsRecycler = findViewById(R.id.packsRecycler);
        loadingRecycler = findViewById(R.id.loadingRecycler);
        startGettingPacks = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

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


    public void LOGIC_BACKEND() {
        loadingRecycler.setLayoutManager(new LinearLayoutManager(this));
        packsRecycler.setLayoutManager(new LinearLayoutManager(this));
        packsRecycler.setHasFixedSize(true);
        loadingRecycler.setHasFixedSize(true);
        OverScrollDecoratorHelper.setUpOverScroll(packsRecycler, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        background.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        loadAds();

        for (int i = 0; i < 30; i++) {
            HashMap<String, Object> shimmerMap = new HashMap<>();
            shimmerMap.put("key", "value");
            shimmerList.add(shimmerMap);
        }
        loadingRecycler.setAdapter(new LoadingPacksAdapter.LoadingRecyclerAdapter(shimmerList));
        getPacks();
    }

    public void LOGIC_FRONTEND() {
        if (Build.VERSION.SDK_INT < 23) {
            statusBarColor("#7289DA", this);
            LIGHT_ICONS(this);
        } else {
            statusBarColor("#FFFFFF", this);
            DARK_ICONS(this);
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

    private void getPacks() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            if (sharedPref.getString("packsData", "").isEmpty()) {
                startGettingPacks.startRequestNetwork(RequestNetworkController.GET, PACKS_API_LINK, "", PacksRequestListener);
            } else {
                try {
                    packsList = new Gson().fromJson(sharedPref.getString("packsData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                    sharedPref.edit().putString("packsData", new Gson().toJson(packsList)).apply();
                } catch (Exception e) {
                    Utils.showToast(getApplicationContext(), (e.toString()));
                }
            }
            handler.post(() -> {
                if (!sharedPref.getString("packsData", "").isEmpty()) {
                    packsRecycler.setAdapter(new PacksRecyclerAdapter(packsList));
                    new Handler().postDelayed(() -> {
                        packsRecycler.setVisibility(View.VISIBLE);
                        loadingRecycler.setVisibility(View.GONE);
                    }, 1000);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        isPacksOpened = false;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (adview != null) {
            adview.destroy();
        }
        super.onDestroy();
    }

    public class PacksRecyclerAdapter extends RecyclerView.Adapter<PacksRecyclerAdapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> data;

        public PacksRecyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fullpacksview, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            View view = holder.itemView;
            final com.google.android.material.card.MaterialCardView cardView = view.findViewById(R.id.cardView);
            final ImageView emoji = view.findViewById(R.id.emoji);
            final TextView title = view.findViewById(R.id.title);
            final TextView description = view.findViewById(R.id.description);
            final TextView amount = view.findViewById(R.id.amount);

            title.setText(capitalizedFirstWord(Objects.requireNonNull(data.get(position).get("name")).toString().replace("_", " ")));
            description.setText(Objects.requireNonNull(data.get(position).get("description")).toString());
            amount.setText(String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(data.get(position).get("amount")).toString()))));
            setHighPriorityImageFromUrl(emoji, Objects.requireNonNull(data.get(position).get("image")).toString());

            cardView.setOnClickListener(_view -> {
                if (!isPacksOpened) {
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

                    } catch (Exception e) {
                        Log.d("Recycler Error", e.toString());
                    }
                    toPreview.putExtra("switchType", "pack");
                    toPreview.putExtra("title", getString(R.string.app_name) + "Pack_".concat(String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(data.get(position).get("id")).toString())))));
                    toPreview.putExtra("subtitle", Objects.requireNonNull(data.get(position).get("description")).toString());
                    toPreview.putExtra("imageUrl", Objects.requireNonNull(data.get(position).get("image")).toString());
                    toPreview.putExtra("fileName", Objects.requireNonNull(data.get(position).get("slug")).toString());
                    toPreview.putExtra("packEmojisArray", currentPositionPackArray);
                    toPreview.putExtra("packEmojisAmount", Objects.requireNonNull(data.get(position).get("amount")).toString());
                    toPreview.putExtra("packName", capitalizedFirstWord(Objects.requireNonNull(data.get(position).get("name")).toString().replace("_", " ")));
                    toPreview.putExtra("packId", Objects.requireNonNull(data.get(position).get("id")).toString());
                    toPreview.setClass(getApplicationContext(), PackPreviewActivity.class);
                    startActivity(toPreview);
                    isPacksOpened = true;
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