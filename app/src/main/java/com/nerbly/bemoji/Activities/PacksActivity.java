package com.nerbly.bemoji.Activities;

import static com.nerbly.bemoji.Functions.MainFunctions.capitalizedFirstWord;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.Functions.SideFunctions.hideShowKeyboard;
import static com.nerbly.bemoji.Functions.SideFunctions.setHighPriorityImageFromUrl;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleEffect;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.setImageViewRipple;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Adapters.LoadingPacksAdapter;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
    private LinearLayout searchBox;
    private AdView adview;
    private RecyclerView packsRecycler;
    private RecyclerView loadingRecycler;
    private SharedPreferences sharedPref;
    private EditText searchInput;
    private boolean isPacksOpened = false;
    private String packsJson = "";
    private String searchQuery = "";
    private TextView emptyTitle;
    private LinearLayout loadView;
    private LottieAnimationView emptyAnimation;
    private ImageView searchFilter;
    private boolean isSearching = false;
    private boolean isSortingNew = true;
    private boolean isSortingOld = false;
    private boolean isSortingAlphabet = false;


    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.packs);
        initialize();
        FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        emptyTitle = findViewById(R.id.emptyTitle);
        emptyAnimation = findViewById(R.id.emptyAnimation);
        loadView = findViewById(R.id.emptyview);
        background = findViewById(R.id.background);
        adview = findViewById(R.id.adview);
        searchBox = findViewById(R.id.searchBox);
        searchInput = findViewById(R.id.searchInput);
        packsRecycler = findViewById(R.id.packsRecycler);
        loadingRecycler = findViewById(R.id.loadingRecycler);
        searchFilter = findViewById(R.id.searchFilter);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTask();
                return true;
            }
            return false;
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSeq, int start, int count, int after) {
                searchQuery = charSeq.toString().trim().toUpperCase();

                if (searchQuery.length() == 0 && isSearching) {
                    searchFilter.setImageResource(R.drawable.outline_filter_alt_black_48dp);
                    getPacks();
                } else {
                    searchFilter.setImageResource(R.drawable.round_clear_black_48dp);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {

            }

            @Override
            public void afterTextChanged(Editable _param1) {

            }
        });

        searchFilter.setOnClickListener(v -> {
            if (searchQuery.length() > 0) {
                searchInput.setText("");
            } else {
                searchInput.setEnabled(false);
                searchInput.setEnabled(true);
                showFilterMenu(searchFilter);
            }
        });

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
        if (Build.VERSION.SDK_INT <= 30) {
            OverScrollDecoratorHelper.setUpOverScroll(packsRecycler, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        }
        background.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        loadAds();

        for (int i = 0; i < 30; i++) {
            HashMap<String, Object> shimmerMap = new HashMap<>();
            shimmerMap.put("key", "value");
            shimmerList.add(shimmerMap);
        }
        loadingRecycler.setAdapter(new LoadingPacksAdapter(shimmerList));

        getPacks();
    }

    public void LOGIC_FRONTEND() {
        rippleRoundStroke(searchBox, "#FFFFFF", "#FFFFFF", 200, 1, "#C4C4C4");
        rippleEffect("#E0E0E0", searchFilter);
        if (Build.VERSION.SDK_INT <= 27) {
            statusBarColor("#7289DA", this);
            LIGHT_ICONS(this);
        } else {
            statusBarColor("#FFFFFF", this);
            DARK_ICONS(this);
        }
        emptyAnimation.setAnimation("animations/not_found.json");
        emptyAnimation.playAnimation();
        emptyTitle.setText(getString(R.string.emojis_not_found));
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
        isSearching = false;
        packsJson = sharedPref.getString("packsData", "");
        loadView.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                isSortingNew = true;
                isSortingOld = false;
                isSortingAlphabet = false;
                packsList = new Gson().fromJson(packsJson, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());

            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Utils.showToast(getApplicationContext(), e.toString());
            }
            handler.post(() -> {
                if (!packsJson.isEmpty()) {
                    packsRecycler.setAdapter(new PacksRecyclerAdapter(packsList));
                    new Handler().postDelayed(() -> {
                        packsRecycler.setVisibility(View.VISIBLE);
                        loadingRecycler.setVisibility(View.GONE);
                    }, 1000);
                }
            });
        });
    }

    private void searchTask() {
        isSearching = true;
        hideShowKeyboard(false, searchInput, this);

        packsList = new Gson().fromJson(packsJson, new TypeToken<ArrayList<HashMap<String, Object>>>() {
        }.getType());

        Log.d("HYMOJI_PACKS_SEARCH", "Total packs: " + packsList.size());

        for (Iterator<HashMap<String, Object>> iterator = packsList.iterator(); iterator.hasNext(); ) {
            HashMap<String, Object> emojiName = iterator.next();
            if (!Objects.requireNonNull(emojiName.get("name")).toString().toUpperCase().contains(searchQuery)) {
                iterator.remove();
            }
        }

        if (packsList.isEmpty()) {
            loadView.setVisibility(View.VISIBLE);
            Log.d("HYMOJI_PACKS_SEARCH", "Nothing found.");
        } else {
            Log.d("HYMOJI_PACKS_SEARCH", "Found: " + packsList.size() + " emojis.");
            loadView.setVisibility(View.GONE);
            packsRecycler.setAdapter(new PacksRecyclerAdapter(packsList));
        }

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


    public void showFilterMenu(final View view) {
        View popupView = getLayoutInflater().inflate(R.layout.sortby_view, (ViewGroup) null);
        final PopupWindow popup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        LinearLayout bg = popupView.findViewById(R.id.bg);
        ImageView i1 = popupView.findViewById(R.id.i1);
        ImageView i2 = popupView.findViewById(R.id.i2);
        ImageView i3 = popupView.findViewById(R.id.i3);
        LinearLayout b1 = popupView.findViewById(R.id.b1);
        LinearLayout b2 = popupView.findViewById(R.id.b2);
        LinearLayout b3 = popupView.findViewById(R.id.b3);
        setImageViewRipple(i1, "#414141", "#7289DA");
        setImageViewRipple(i2, "#414141", "#7289DA");
        setImageViewRipple(i3, "#414141", "#7289DA");

        setClippedView(bg, "#FFFFFF", 25, 7);
        if (isSortingNew) {
            rippleRoundStroke(b1, "#EEEEEE", "#BDBDBD", 0, 0, "#EEEEEE");
        } else {
            rippleRoundStroke(b1, "#FFFFFF", "#EEEEEE", 0, 0, "#EEEEEE");
        }
        if (isSortingOld) {
            rippleRoundStroke(b2, "#EEEEEE", "#BDBDBD", 0, 0, "#EEEEEE");
        } else {
            rippleRoundStroke(b2, "#FFFFFF", "#EEEEEE", 0, 0, "#EEEEEE");
        }
        if (isSortingAlphabet) {
            rippleRoundStroke(b3, "#EEEEEE", "#BDBDBD", 0, 0, "#EEEEEE");
        } else {
            rippleRoundStroke(b3, "#FFFFFF", "#EEEEEE", 0, 0, "#EEEEEE");
        }
        b1.setOnClickListener(view1 -> {
            sort_by_newest();
            popup.dismiss();
        });
        b2.setOnClickListener(view12 -> {
            sort_by_oldest();
            popup.dismiss();

        });
        b3.setOnClickListener(view13 -> {
            sort_by_alphabetically();
            popup.dismiss();
        });
        popup.setAnimationStyle(android.R.style.Animation_Dialog);
        popup.setFocusable(false);
        popup.setOutsideTouchable(true);
        popup.showAsDropDown(view, 0, 0);
        popup.setBackgroundDrawable(null);
    }


    public void sort_by_newest() {
        if (!isSortingNew) {
            isSortingNew = true;
            isSortingOld = false;
            isSortingAlphabet = false;
            packsList = new Gson().fromJson(packsJson, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            packsRecycler.setAdapter(new PacksRecyclerAdapter(packsList));
        }
    }

    public void sort_by_oldest() {
        if (!isSortingOld) {
            isSortingOld = true;
            isSortingNew = false;
            isSortingAlphabet = false;
            packsList = new Gson().fromJson(packsJson, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            Collections.reverse(packsList);
            packsRecycler.setAdapter(new PacksRecyclerAdapter(packsList));
        }
    }

    public void sort_by_alphabetically() {
        if (!isSortingAlphabet) {
            isSortingAlphabet = true;
            isSortingNew = false;
            isSortingOld = false;
            Utils.sortListMap(packsList, "name", false, true);
            packsRecycler.setAdapter(new PacksRecyclerAdapter(packsList));
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
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fullpacksview, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            View view = holder.itemView;
            final MaterialCardView cardView = view.findViewById(R.id.cardView);
            final ImageView emoji = view.findViewById(R.id.emoji);
            final TextView title = view.findViewById(R.id.title);
            final TextView description = view.findViewById(R.id.description);
            final TextView amount = view.findViewById(R.id.amount);
            final LinearLayout warningView = view.findViewById(R.id.warningView);

            if (Objects.requireNonNull(data.get(position).get("name")).toString().toLowerCase().contains("nsfw")) {
                warningView.setVisibility(View.VISIBLE);
            } else {
                warningView.setVisibility(View.GONE);
            }
            title.setText(capitalizedFirstWord(Objects.requireNonNull(data.get(position).get("name")).toString().replace("_", " ")));
            description.setText(Objects.requireNonNull(data.get(position).get("description")).toString());
            amount.setText(String.valueOf((long) (Double.parseDouble(Objects.requireNonNull(data.get(position).get("amount")).toString()))));
            setHighPriorityImageFromUrl(emoji, Objects.requireNonNull(data.get(position).get("image")).toString());

            warningView.setOnClickListener(v -> {
                shadAnim(warningView, "scaleX", 4, 400);
                shadAnim(warningView, "scaleY", 4, 400);
                shadAnim(warningView, "alpha", 0, 300);
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> warningView.setVisibility(View.GONE), 500);
            });

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
                        Log.d("HYMOJI_PACKS_RECYCLER", e.toString());
                    }
                    toPreview.putExtra("switchType", "pack");
                    toPreview.putExtra("title", getString(R.string.app_name) + "Pack_" + ((long) (Double.parseDouble(Objects.requireNonNull(data.get(position).get("id")).toString()))));
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