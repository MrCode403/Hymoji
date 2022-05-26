package com.nerbly.bemoji.activities;


import static com.nerbly.bemoji.adapters.MainEmojisAdapter.isEmojiSheetShown;
import static com.nerbly.bemoji.fragments.MainEmojisFragment.isMainEmojisLoaded;
import static com.nerbly.bemoji.fragments.MainEmojisFragment.lastSearchedMainEmoji;
import static com.nerbly.bemoji.fragments.PacksEmojisFragment.isPacksEmojisLoaded;
import static com.nerbly.bemoji.fragments.PacksEmojisFragment.lastSearchedPackEmoji;
import static com.nerbly.bemoji.functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.functions.SideFunctions.hideShowKeyboard;
import static com.nerbly.bemoji.functions.Utils.getAdSize;
import static com.nerbly.bemoji.ui.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.ui.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.ui.MainUIMethods.navStatusBarColor;
import static com.nerbly.bemoji.ui.MainUIMethods.rippleEffect;
import static com.nerbly.bemoji.ui.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.ui.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.ui.MainUIMethods.setImageViewRipple;
import static com.nerbly.bemoji.ui.MainUIMethods.statusBarColor;

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
import com.google.android.material.tabs.TabLayout;
import com.nerbly.bemoji.R;
import com.nerbly.bemoji.databinding.EmojisBinding;
import com.nerbly.bemoji.databinding.SortbyViewBinding;
import com.nerbly.bemoji.fragments.MainEmojisFragment;
import com.nerbly.bemoji.fragments.PacksEmojisFragment;

import java.util.Objects;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class EmojisActivity extends AppCompatActivity {

    public boolean isSortingNew = true;
    public boolean isSortingOld = false;
    public boolean isSortingAlphabet = false;
    private SharedPreferences sharedPref;
    private boolean isSearching = false;
    private boolean isSearchingMain = false;
    private boolean isSearchingPacks = false;
    private MainEmojisFragment main_emojis_fragment;
    private PacksEmojisFragment packs_emojis_fragment;
    private String lastSearchedEmoji = "";
    private String lastMainEmojisQuery = "";
    private String lastPacksEmojisQuery = "";
    private String searchQuery = "";
    private InterstitialAd mInterstitialAd;
    private int emojisDownloadedSoFar = 0;
    private EmojisBinding emojisBinding;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        emojisBinding = EmojisBinding.inflate(getLayoutInflater());
        View view = emojisBinding.getRoot();
        setContentView(view);
        initialize();
        initializeLogic();
    }

    private void initialize() {

        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        emojisBinding.searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSeq, int start, int count, int after) {
                searchQuery = charSeq.toString().trim().toUpperCase();

                if (searchQuery.length() == 0 && isSearching) {
                    isSearching = false;
                    lastSearchedEmoji = "";
                    searchQuery = "";
                    if (emojisBinding.viewpager.getCurrentItem() == 0) {
                        if (isSearchingMain) {
                            MainEmojisFragment fragment = (MainEmojisFragment) Objects.requireNonNull(emojisBinding.viewpager.getAdapter()).instantiateItem(emojisBinding.viewpager, emojisBinding.viewpager.getCurrentItem());
                            emojisBinding.progressLoading.setVisibility(View.VISIBLE);
                            fragment.getEmojis();
                            isSearchingMain = false;
                            lastMainEmojisQuery = "";
                        }
                    } else if (emojisBinding.viewpager.getCurrentItem() == 1) {
                        if (isSearchingPacks) {
                            PacksEmojisFragment fragment = (PacksEmojisFragment) Objects.requireNonNull(emojisBinding.viewpager.getAdapter()).instantiateItem(emojisBinding.viewpager, emojisBinding.viewpager.getCurrentItem());
                            fragment.setLoadingScreenData(false, true);
                            fragment.getEmojis();
                            isSearchingPacks = false;
                            lastPacksEmojisQuery = "";
                        }
                    }
                }
                if (searchQuery.length() > 0) {
                    emojisBinding.icFilterClear.setImageResource(R.drawable.round_clear_black_48dp);
                } else {
                    emojisBinding.icFilterClear.setImageResource(R.drawable.outline_filter_alt_black_48dp);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {

            }

            @Override
            public void afterTextChanged(Editable _param1) {

            }
        });

        emojisBinding.searchField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTask();
                return true;
            }
            return false;
        });

        emojisBinding.icFilterClear.setOnClickListener(_view -> {
            if (searchQuery.length() > 0) {
                emojisBinding.searchField.setText("");
            } else {
                emojisBinding.searchField.setEnabled(false);
                emojisBinding.searchField.setEnabled(true);
                showFilterMenu(emojisBinding.icFilterClear);
            }
        });

        emojisBinding.searchBtn.setOnClickListener(_view -> searchTask());

        emojisBinding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        emojisBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                emojisBinding.appbar.setExpanded(true, true);
                if (position == 0) {
                    lastSearchedEmoji = lastMainEmojisQuery;

                    if (!lastSearchedMainEmoji.isEmpty()) {
                        emojisBinding.searchField.setText(lastSearchedMainEmoji);
                        emojisBinding.icFilterClear.setImageResource(R.drawable.round_clear_black_48dp);
                        isSearching = true;
                    }
                } else {
                    lastSearchedEmoji = lastPacksEmojisQuery;

                    if (!lastSearchedPackEmoji.isEmpty()) {
                        emojisBinding.searchField.setText(lastSearchedPackEmoji);
                        emojisBinding.icFilterClear.setImageResource(R.drawable.round_clear_black_48dp);
                        isSearching = true;
                    }
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
        emojisBinding.viewpager.setOffscreenPageLimit(2);
        emojisBinding.viewpager.setAdapter(new MyFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), 2));
        emojisBinding.tablayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary));
        emojisBinding.tablayout.setTabTextColors(Color.parseColor("#616161"), ContextCompat.getColor(this, R.color.colorPrimary));
        emojisBinding.tablayout.setupWithViewPager(emojisBinding.viewpager);

        loadAds();
    }

    public void LOGIC_FRONTEND() {
        rippleRoundStroke(emojisBinding.searchbox, "#FFFFFF", "#FFFFFF", 200, 1, "#C4C4C4");
        if (Build.VERSION.SDK_INT <= 27) {
            statusBarColor("#7289DA", this);
            LIGHT_ICONS(this);
        } else {
            navStatusBarColor("#FFFFFF", "#FFFFFF", this);
            DARK_ICONS(this);
        }
        rippleEffect("#E0E0E0", emojisBinding.icFilterClear);
        rippleEffect("#E0E0E0", emojisBinding.searchBtn);
        if (Build.VERSION.SDK_INT >= 31) {
            OverScrollDecoratorHelper.setUpOverScroll(emojisBinding.viewpager);
        }
    }

    public void showFilterMenu(final View view) {
        if (emojisBinding.viewpager.getCurrentItem() == 0) {
            main_emojis_fragment = (MainEmojisFragment) Objects.requireNonNull(emojisBinding.viewpager.getAdapter()).instantiateItem(emojisBinding.viewpager, emojisBinding.viewpager.getCurrentItem());
            isSortingNew = main_emojis_fragment.isSortingNew;
            isSortingOld = main_emojis_fragment.isSortingOld;
            isSortingAlphabet = main_emojis_fragment.isSortingAlphabet;
        } else {
            packs_emojis_fragment = (PacksEmojisFragment) Objects.requireNonNull(emojisBinding.viewpager.getAdapter()).instantiateItem(emojisBinding.viewpager, emojisBinding.viewpager.getCurrentItem());
            isSortingNew = packs_emojis_fragment.isSortingNew;
            isSortingOld = packs_emojis_fragment.isSortingOld;
            isSortingAlphabet = packs_emojis_fragment.isSortingAlphabet;
        }

        SortbyViewBinding sortbyViewBinding = SortbyViewBinding.inflate(getLayoutInflater());
        View popupView = sortbyViewBinding.getRoot();
        final PopupWindow popup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        setImageViewRipple(sortbyViewBinding.i1, "#414141", "#7289DA");
        setImageViewRipple(sortbyViewBinding.i2, "#414141", "#7289DA");
        setImageViewRipple(sortbyViewBinding.i3, "#414141", "#7289DA");

        setClippedView(sortbyViewBinding.bg, "#FFFFFF", 25, 7);
        if (isSortingNew) {
            rippleRoundStroke(sortbyViewBinding.b1, "#EEEEEE", "#BDBDBD", 0, 0, "#EEEEEE");
        } else {
            rippleRoundStroke(sortbyViewBinding.b1, "#FFFFFF", "#EEEEEE", 0, 0, "#EEEEEE");
        }
        if (isSortingOld) {
            rippleRoundStroke(sortbyViewBinding.b2, "#EEEEEE", "#BDBDBD", 0, 0, "#EEEEEE");
        } else {
            rippleRoundStroke(sortbyViewBinding.b2, "#FFFFFF", "#EEEEEE", 0, 0, "#EEEEEE");
        }
        if (isSortingAlphabet) {
            rippleRoundStroke(sortbyViewBinding.b3, "#EEEEEE", "#BDBDBD", 0, 0, "#EEEEEE");
        } else {
            rippleRoundStroke(sortbyViewBinding.b3, "#FFFFFF", "#EEEEEE", 0, 0, "#EEEEEE");
        }
        sortbyViewBinding.b1.setOnClickListener(view1 -> {
            if (emojisBinding.viewpager.getCurrentItem() == 0) {
                main_emojis_fragment.sort_by_newest();
            } else {
                packs_emojis_fragment.sort_by_newest();
            }

            popup.dismiss();
        });
        sortbyViewBinding.b2.setOnClickListener(view12 -> {
            if (emojisBinding.viewpager.getCurrentItem() == 0) {
                main_emojis_fragment.sort_by_oldest();
            } else {
                packs_emojis_fragment.sort_by_oldest();
            }

            popup.dismiss();

        });
        sortbyViewBinding.b3.setOnClickListener(view13 -> {
            if (emojisBinding.viewpager.getCurrentItem() == 0) {
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
        if (emojisBinding.viewpager.getCurrentItem() == 0) {
            if (isMainEmojisLoaded) {
                shouldAllowSearch = true;
                isSearchingMain = true;
            }
        } else if (emojisBinding.viewpager.getCurrentItem() == 1) {
            if (isPacksEmojisLoaded) {
                shouldAllowSearch = true;
                isSearchingPacks = true;
            }
        }

        if (!searchQuery.isEmpty()) {
            emojisBinding.icFilterClear.setImageResource(R.drawable.round_clear_black_48dp);
        } else {
            emojisBinding.icFilterClear.setImageResource(R.drawable.outline_filter_alt_black_48dp);
        }

        if (searchQuery.length() > 0 && !lastSearchedEmoji.equals(searchQuery) && shouldAllowSearch) {
            Log.d("HYMOJI_SEARCH", "allowed to search from step 2");
            lastSearchedEmoji = searchQuery;
            hideShowKeyboard(false, emojisBinding.searchField, this);
            isSearching = true;
            emojisBinding.progressLoading.setVisibility(View.VISIBLE);
            if (emojisBinding.viewpager.getCurrentItem() == 0) {
                isMainEmojisLoaded = false;
                lastMainEmojisQuery = searchQuery;

                MainEmojisFragment fragment = (MainEmojisFragment) Objects.requireNonNull(emojisBinding.viewpager.getAdapter()).instantiateItem(emojisBinding.viewpager, emojisBinding.viewpager.getCurrentItem());
                fragment.searchTask(searchQuery);
            } else {
                isPacksEmojisLoaded = false;
                lastPacksEmojisQuery = searchQuery;
                PacksEmojisFragment fragment = (PacksEmojisFragment) Objects.requireNonNull(emojisBinding.viewpager.getAdapter()).instantiateItem(emojisBinding.viewpager, emojisBinding.viewpager.getCurrentItem());
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
            if (emojisBinding.viewpager.getCurrentItem() == 1) {
                emojisBinding.viewpager.setCurrentItem(0);
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mInterstitialAd = null;
        if (adView != null) {
            adView.destroy();
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
            adView = new AdView(this);
            adView.setAdUnitId(getString(R.string.mainemojis_admob_banner_id));
            emojisBinding.adContainerView.removeAllViews();
            emojisBinding.adContainerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            emojisBinding.adContainerView.addView(adView);

            AdSize adSize = getAdSize(emojisBinding.adContainerView, this);
            adView.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

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
            switch (position) {
                case 0:
                    return getString(R.string.emojis_tab1_title);
                case 1:
                    return getString(R.string.emojis_tab2_title);
            }
            return null;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MainEmojisFragment();
                case 1:
                    return new PacksEmojisFragment();
            }
            return null;
        }

    }

}
