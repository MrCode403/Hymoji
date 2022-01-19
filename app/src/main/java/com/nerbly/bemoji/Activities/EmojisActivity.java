package com.nerbly.bemoji.Activities;


import static com.nerbly.bemoji.Adapters.MainEmojisAdapter.isEmojiSheetShown;
import static com.nerbly.bemoji.Fragments.MainEmojisFragment.isMainEmojisLoaded;
import static com.nerbly.bemoji.Fragments.PacksEmojisFragment.isPacksEmojisLoaded;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.Functions.SideFunctions.hideShowKeyboard;
import static com.nerbly.bemoji.Functions.Utils.getAdSize;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.RippleEffects;
import static com.nerbly.bemoji.UI.MainUIMethods.navStatusBarColor;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.setImageViewRipple;
import static com.nerbly.bemoji.UI.MainUIMethods.statusBarColor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.nerbly.bemoji.Fragments.MainEmojisFragment;
import com.nerbly.bemoji.Fragments.PacksEmojisFragment;
import com.nerbly.bemoji.R;

import java.util.Objects;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class EmojisActivity extends AppCompatActivity {
    public EditText searchBoxField;
    public LinearLayout searchBox;
    public LinearLayout adContainerView;
    public boolean isSortingNew = true;
    public boolean isSortingOld = false;
    public boolean isSortingAlphabet = false;
    private AdView adview;
    private ImageView sortByBtn;
    private ImageView searchBtn;
    private ViewPager viewpager;
    private TabLayout tablayout;
    private SharedPreferences sharedPref;
    private boolean isSearching = false;
    private MainEmojisFragment main_emojis_fragment;
    private PacksEmojisFragment packs_emojis_fragment;
    private String lastSearchedEmoji = "";
    private String lastMainEmojisQuery = "";
    private String lastPacksEmojisQuery = "";
    private String searchQuery = "";
    private AppBarLayout appbar;
    private InterstitialAd mInterstitialAd;
    private int emojisDownloadedSoFar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.emojis);
        initialize();
        initializeLogic();
    }

    private void initialize() {
        adContainerView = findViewById(R.id.adContainerView);
        viewpager = findViewById(R.id.viewpager);
        appbar = findViewById(R.id.appbar);
        searchBox = findViewById(R.id.searchbox);
        tablayout = findViewById(R.id.tablayout);
        searchBoxField = findViewById(R.id.searchField);
        sortByBtn = findViewById(R.id.ic_filter_clear);
        searchBtn = findViewById(R.id.searchBtn);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        searchBoxField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSeq, int start, int count, int after) {
                searchQuery = charSeq.toString().trim().toUpperCase();

                if (searchQuery.length() == 0 && isSearching) {
                    isSearching = false;
                    lastSearchedEmoji = "";
                    if (viewpager.getCurrentItem() == 0) {
                        MainEmojisFragment fragment = (MainEmojisFragment) Objects.requireNonNull(viewpager.getAdapter()).instantiateItem(viewpager, viewpager.getCurrentItem());
                        fragment.getEmojis();
                    } else if (viewpager.getCurrentItem() == 1) {
                        PacksEmojisFragment fragment = (PacksEmojisFragment) Objects.requireNonNull(viewpager.getAdapter()).instantiateItem(viewpager, viewpager.getCurrentItem());
                        fragment.setLoadingScreenData();
                        fragment.getEmojis();
                    }
                }
                if (searchQuery.length() > 0) {
                    sortByBtn.setImageResource(R.drawable.round_clear_black_48dp);
                } else {
                    sortByBtn.setImageResource(R.drawable.outline_filter_alt_black_48dp);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {

            }

            @Override
            public void afterTextChanged(Editable _param1) {

            }
        });

        searchBoxField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTask();
                return true;
            }
            return false;
        });

        sortByBtn.setOnClickListener(_view -> {
            if (searchQuery.length() > 0) {
                lastSearchedEmoji = "";
                searchBoxField.setText("");
            } else {
                searchBoxField.setEnabled(false);
                searchBoxField.setEnabled(true);
                showFilterMenu(sortByBtn);
            }
        });

        searchBtn.setOnClickListener(_view -> searchTask());

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                final int position = tab.getPosition();
                if (position == 1) {
                    Intent toPacks = new Intent();
                    toPacks.setClass(getApplicationContext(), PacksActivity.class);
                    startActivity(toPacks);
                }
            }
        });

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                appbar.setExpanded(true, true);
                if (position == 0) {
                    lastSearchedEmoji = lastMainEmojisQuery;
                } else {
                    lastSearchedEmoji = lastPacksEmojisQuery;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }

    public void LOGIC_BACKEND() {
        viewpager.setOffscreenPageLimit(2);
        viewpager.setAdapter(new MyFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), 2));
        tablayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tablayout.setTabTextColors(Color.parseColor("#616161"), ContextCompat.getColor(this, R.color.colorPrimary));
        tablayout.setupWithViewPager(viewpager);

        loadAds();
    }

    public void LOGIC_FRONTEND() {
        rippleRoundStroke(searchBox, "#FFFFFF", "#FFFFFF", 200, 1, "#C4C4C4");
        if (Build.VERSION.SDK_INT < 23) {
            statusBarColor("#7289DA", this);
            LIGHT_ICONS(this);
        } else {
            navStatusBarColor("#FFFFFF", "#FFFFFF", this);
            DARK_ICONS(this);
        }
        RippleEffects("#E0E0E0", sortByBtn);
        RippleEffects("#E0E0E0", searchBtn);
        if (Build.VERSION.SDK_INT >= 31) {
            OverScrollDecoratorHelper.setUpOverScroll(viewpager);
        }
    }

    public void showFilterMenu(final View view) {
        if (viewpager.getCurrentItem() == 0) {
            main_emojis_fragment = (MainEmojisFragment) Objects.requireNonNull(viewpager.getAdapter()).instantiateItem(viewpager, viewpager.getCurrentItem());
            isSortingNew = main_emojis_fragment.isSortingNew;
            isSortingOld = main_emojis_fragment.isSortingOld;
            isSortingAlphabet = main_emojis_fragment.isSortingAlphabet;
        } else {
            packs_emojis_fragment = (PacksEmojisFragment) Objects.requireNonNull(viewpager.getAdapter()).instantiateItem(viewpager, viewpager.getCurrentItem());
            isSortingNew = packs_emojis_fragment.isSortingNew;
            isSortingOld = packs_emojis_fragment.isSortingOld;
            isSortingAlphabet = packs_emojis_fragment.isSortingAlphabet;
        }

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
            if (viewpager.getCurrentItem() == 0) {
                main_emojis_fragment.sort_by_newest();
            } else {
                packs_emojis_fragment.sort_by_newest();
            }

            popup.dismiss();
        });
        b2.setOnClickListener(view12 -> {
            if (viewpager.getCurrentItem() == 0) {
                main_emojis_fragment.sort_by_oldest();
            } else {
                packs_emojis_fragment.sort_by_oldest();
            }

            popup.dismiss();

        });
        b3.setOnClickListener(view13 -> {
            if (viewpager.getCurrentItem() == 0) {
                main_emojis_fragment.sort_by_alphabetically();
            } else {
                packs_emojis_fragment.sort_by_alphabetically();
            }
            popup.dismiss();
        });
        popup.setAnimationStyle(android.R.style.Animation_Dialog);
        popup.setFocusable(false);
        popup.setOutsideTouchable(true);
        popup.showAsDropDown(view, 0, 0);
        popup.setBackgroundDrawable(null);
    }

    private void searchTask() {
        boolean shouldAllowSearch = false;
        if (viewpager.getCurrentItem() == 0) {
            if (isMainEmojisLoaded) {
                shouldAllowSearch = true;
            }
        } else if (viewpager.getCurrentItem() == 1) {
            if (isPacksEmojisLoaded) {
                shouldAllowSearch = true;
            }
        }

        if (!searchQuery.isEmpty()) {
            sortByBtn.setImageResource(R.drawable.round_clear_black_48dp);
        } else {
            sortByBtn.setImageResource(R.drawable.outline_filter_alt_black_48dp);
        }

        if (searchQuery.length() > 0 && !lastSearchedEmoji.equals(searchQuery) && shouldAllowSearch) {
            Log.d("HYMOJI_SEARCH", "allowed to search from step 2");
            lastSearchedEmoji = searchQuery;
            hideShowKeyboard(false, searchBoxField, this);
            isSearching = true;
            if (viewpager.getCurrentItem() == 0) {
                isMainEmojisLoaded = false;
                lastMainEmojisQuery = searchQuery;
                MainEmojisFragment fragment = (MainEmojisFragment) Objects.requireNonNull(viewpager.getAdapter()).instantiateItem(viewpager, viewpager.getCurrentItem());
                fragment.searchTask(searchQuery);
            } else {
                isPacksEmojisLoaded = false;
                lastPacksEmojisQuery = searchQuery;
                PacksEmojisFragment fragment = (PacksEmojisFragment) Objects.requireNonNull(viewpager.getAdapter()).instantiateItem(viewpager, viewpager.getCurrentItem());
                fragment.searchTask(searchQuery);
            }
        } else {
            Log.d("HYMOJI_SEARCH", "denied to search from step 1.\nknown reasons:\nmain emojis status: " + isMainEmojisLoaded + "\npacks emojis status: " + isPacksEmojisLoaded);
            Log.d("HYMOJI_SEARCH", "denied to search from step 2 \nknown reasons:\nlast searched emoji: " + lastSearchedEmoji + "\nquery: " + searchQuery);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isEmojiSheetShown) {
            if (viewpager.getCurrentItem() == 1) {
                viewpager.setCurrentItem(0);
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mInterstitialAd = null;
        if (adview != null) {
            adview.destroy();
        }
        super.onDestroy();
    }


    public void loadInterstitialAd(Context context) {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, context.getString(R.string.admob_interstitial_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                Log.d("HYMOJI_ADS", "Interstitial AD is loaded and ready");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("HYMOJI_ADS", "Interstitial AD failed to load, reason: " + loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }

    public void showInterstitialAd() {
        if (emojisDownloadedSoFar == 5) {
            emojisDownloadedSoFar = 0;
            loadAds();
        } else if (emojisDownloadedSoFar == 0) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(this);
                Log.d("HYMOJI_ADS", "Interstitial AD showed up successfully");
            } else {
                Log.d("HYMOJI_ADS", "Interstitial AD is null and can't show");
            }
            emojisDownloadedSoFar++;
        } else {
            emojisDownloadedSoFar++;
        }

        Log.d("HYMOJI_ADS", "Downloaded emojis " + emojisDownloadedSoFar + " times after check");

    }

    private void loadAds() {
        if (!sharedPref.getBoolean("isPremium", false)) {
            adview = new AdView(this);
            adview.setAdUnitId(getString(R.string.mainemojis_admob_banner_id));
            adContainerView.removeAllViews();
            adContainerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            adContainerView.addView(adview);

            AdSize adSize = getAdSize(adContainerView, this);
            adview.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();
            adview.loadAd(adRequest);

            MobileAds.initialize(this, initializationStatus -> {
            });
            loadInterstitialAd(this);
        }
    }

    public class MyFragmentAdapter extends FragmentStatePagerAdapter {
        Context context;
        int tabCount;

        public MyFragmentAdapter(Context context, FragmentManager fm, int tabCount) {
            super(fm);
            this.context = context;
            this.tabCount = tabCount;
        }

        @Override
        public int getCount() {
            return tabCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.emojis_tab1_title);
            }
            if (position == 1) {
                return getString(R.string.emojis_tab2_title);
            }
            return null;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new MainEmojisFragment();
            } else if (position == 1) {
                return new PacksEmojisFragment();
            }
            return null;
        }

    }

}