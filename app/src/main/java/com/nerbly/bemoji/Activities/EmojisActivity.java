package com.nerbly.bemoji.Activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.tabs.TabLayout;
import com.nerbly.bemoji.Fragments.MainEmojisFragment;
import com.nerbly.bemoji.Fragments.PacksEmojisFragment;
import com.nerbly.bemoji.R;

import java.util.Objects;

import static com.nerbly.bemoji.Adapters.MainEmojisAdapter.isEmojiSheetShown;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.Functions.SideFunctions.hideShowKeyboard;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.RippleEffects;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.setImageViewRipple;
import static com.nerbly.bemoji.UI.MainUIMethods.statusBarColor;

public class EmojisActivity extends AppCompatActivity {
    public static EditText searchBoxField;
    public LinearLayout searchBox;
    public boolean isSortingNew = true;
    public boolean isSortingOld = false;
    public boolean isSortingAlphabet = false;
    private AdView adview;
    private ImageView sortByBtn;
    private ImageView searchBtn;
    private ViewPager viewpager;
    private TabLayout tablayout;
    private boolean isSearching = false;
    private MainEmojisFragment main_emojis_fragment;
    private PacksEmojisFragment packs_emojis_fragment;
    public static InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.emojis);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        adview = findViewById(R.id.adview);
        viewpager = findViewById(R.id.viewpager);
        searchBox = findViewById(R.id.searchbox);
        tablayout = findViewById(R.id.tablayout);
        searchBoxField = findViewById(R.id.searchField);
        sortByBtn = findViewById(R.id.ic_filter_clear);
        searchBtn = findViewById(R.id.searchBtn);

        searchBoxField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSeq, int start, int count, int after) {
                if (charSeq.toString().trim().length() == 0 && isSearching) {
                    isSearching = false;
                    if (viewpager.getCurrentItem() == 0) {
                        MainEmojisFragment fragment = (MainEmojisFragment) Objects.requireNonNull(viewpager.getAdapter()).instantiateItem(viewpager, viewpager.getCurrentItem());
                        fragment.getEmojis();
                    } else {
                        PacksEmojisFragment fragment = (PacksEmojisFragment) Objects.requireNonNull(viewpager.getAdapter()).instantiateItem(viewpager, viewpager.getCurrentItem());
                        fragment.getEmojis();
                    }
                }
                if (searchBoxField.getText().toString().trim().length() > 0) {
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
            if (searchBoxField.getText().toString().trim().length() > 0) {
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
            statusBarColor("#FFFFFF", this);
            DARK_ICONS(this);
        }
        RippleEffects("#E0E0E0", sortByBtn);
        RippleEffects("#E0E0E0", searchBtn);
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

        View popupView = getLayoutInflater().inflate(R.layout.sortby_view, null);
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
        popup.showAsDropDown(view, 0, 0);
    }


    private void searchTask() {
        if (searchBoxField.getText().toString().trim().length() > 0) {
            hideShowKeyboard(false, searchBoxField, this);
            isSearching = true;
            if (searchBoxField.getText().toString().trim().length() > 0) {
                sortByBtn.setImageResource(R.drawable.round_clear_black_48dp);
            } else {
                sortByBtn.setImageResource(R.drawable.outline_filter_alt_black_48dp);
            }
            if (viewpager.getCurrentItem() == 0) {
                MainEmojisFragment fragment = (MainEmojisFragment) Objects.requireNonNull(viewpager.getAdapter()).instantiateItem(viewpager, viewpager.getCurrentItem());
                fragment.searchTask(searchBoxField.getText().toString().trim());
            } else {
                PacksEmojisFragment fragment = (PacksEmojisFragment) Objects.requireNonNull(viewpager.getAdapter()).instantiateItem(viewpager, viewpager.getCurrentItem());
                fragment.searchTask(searchBoxField.getText().toString().trim());
            }
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

    private void loadAds() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        loadInterstitialAd();

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
            }
            if (position == 1) {
                return new PacksEmojisFragment();
            }
            return null;
        }

    }

    public void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getString(R.string.admob_interstitial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

    public static void showInterstitialAd(Activity context) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(context);
        }
    }

}