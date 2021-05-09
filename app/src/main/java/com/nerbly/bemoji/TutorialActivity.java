package com.nerbly.bemoji;

import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TutorialActivity extends AppCompatActivity {
    private final Timer timer = new Timer();
    private final ArrayList<HashMap<String, Object>> shimmerList = new ArrayList<>();
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private ArrayList<HashMap<String, Object>> tutorialList = new ArrayList<>();
    private LinearLayout bsheetbehavior;
    private LinearLayout background;
    private LinearLayout adView;
    private LinearLayout slider;
    private RecyclerView recyclerview1;
    private RecyclerView loadingRecycler;

    private RequestNetwork requestTutorial;
    private RequestNetwork.RequestListener TutorialRequestListener;
    private TimerTask loadTutorialTmr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.tutorial);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        CoordinatorLayout linear1 = findViewById(R.id.tutorialBg);
        bsheetbehavior = findViewById(R.id.sheetBehavior);
        background = findViewById(R.id.background);
        adView = findViewById(R.id.adView);
        slider = findViewById(R.id.slider);
        recyclerview1 = findViewById(R.id.recyclerview1);
        loadingRecycler = findViewById(R.id.loadingRecycler);
        requestTutorial = new RequestNetwork(this);

        linear1.setOnClickListener(_view -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));

        TutorialRequestListener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                try {
                    tutorialList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                    recyclerview1.setAdapter(new Recyclerview1Adapter(tutorialList));
                } catch (Exception e) {
                    Utils.showToast(getApplicationContext(), (e.toString()));
                }
                loadTutorialTmr = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            recyclerview1.setVisibility(View.VISIBLE);
                            loadingRecycler.setVisibility(View.GONE);
                        });
                    }
                };
                timer.schedule(loadTutorialTmr, 1000);
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
        recyclerview1.setLayoutManager(new LinearLayoutManager(this));
        loadingRecycler.setLayoutManager(new LinearLayoutManager(this));
        requestTutorial.startRequestNetwork(RequestNetworkController.GET, "https://nerbly.com/bemoji/tutorial.json", "", TutorialRequestListener);
        for (int i = 0; i < 20; i++) {
            HashMap<String, Object> shimmerMap = new HashMap<>();
            shimmerMap.put("key", "value");
            shimmerList.add(shimmerMap);
        }
        loadingRecycler.setAdapter(new LoadingPacksAdapter.LoadingRecyclerAdapter(shimmerList));
        bottomSheetBehaviorListener();

        recyclerview1.setHasFixedSize(true);
        loadingRecycler.setHasFixedSize(true);

        AudienceNetworkAds.initialize(this);
        AdView bannerAd = new AdView(this, getString(R.string.banner_id), AdSize.BANNER_HEIGHT_50);
        adView.addView(bannerAd);
        bannerAd.loadAd();
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


    public void setImageFromUrl(final ImageView image, final String url) {
        Glide.with(this)

                .load(url)
                .fitCenter()
                .into(image);

    }


    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> data;

        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.tutorialview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            View view = holder.itemView;

            final TextView tutorialTitle = view.findViewById(R.id.tutorialTitle);
            final TextView tutorialSubtitle = view.findViewById(R.id.tutorialSubtitle);
            final ImageView tutorialImage = view.findViewById(R.id.tutorialImage);

            if (Objects.requireNonNull(data.get(position).get("isTitled")).toString().equals("true")) {
                tutorialTitle.setVisibility(View.VISIBLE);
                tutorialTitle.setText(Objects.requireNonNull(data.get(position).get("title")).toString());
            } else {
                tutorialTitle.setVisibility(View.GONE);
            }
            if (Objects.requireNonNull(data.get(position).get("isSubtitled")).toString().equals("true")) {
                tutorialSubtitle.setVisibility(View.VISIBLE);
                tutorialSubtitle.setText(Objects.requireNonNull(data.get(position).get("subtitle")).toString());
            } else {
                tutorialSubtitle.setVisibility(View.GONE);
            }
            if (Objects.requireNonNull(data.get(position).get("isImaged")).toString().equals("true")) {
                tutorialImage.setVisibility(View.VISIBLE);
                setImageFromUrl(tutorialImage, Objects.requireNonNull(data.get(position).get("image")).toString());
            } else {
                tutorialImage.setVisibility(View.GONE);
            }
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(_lp);
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
