package com.nerbly.bemoji;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusNavBar;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<HashMap<String, Object>> viewPagerList = new ArrayList<>();
    private final Intent intent = new Intent();
    FirebaseAnalytics mFirebaseAnalytics;
    private ImageView welcomeImage;
    private LinearLayout dataView;
    private LinearLayout splashView;
    private ViewPager viewPager;
    private TextView continueBtn;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.main);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        welcomeImage = findViewById(R.id.welcomeImage);
        dataView = findViewById(R.id.dataView);
        splashView = findViewById(R.id.splashview);
        viewPager = findViewById(R.id.viewPager);
        continueBtn = findViewById(R.id.continueBtn);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    continueBtn.setText(R.string.welcome_start);
                } else {
                    continueBtn.setText(R.string.welcome_continue);
                }
            }

            @Override
            public void onPageScrollStateChanged(int scrollState) {

            }
        });

        continueBtn.setOnClickListener(_view -> {
            if (viewPager.getCurrentItem() == 2) {
                sharedPref.edit().putString("firstUse", "true").apply();
                intent.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }

    @Override
    public void onBackPressed() {
        if (!(viewPager.getCurrentItem() == 0)) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }


    public void LOGIC_FRONTEND() {
        try {
            InputStream ims = getAssets().open("images/splash.png");
            Drawable d = Drawable.createFromStream(ims, null);
            welcomeImage.setImageDrawable(d);
        } catch (Exception e) {
            return;
        }
        transparentStatusNavBar(this);
        LIGHT_ICONS(this);
        rippleRoundStroke(continueBtn, "#FFFFFF", "#EEEEEE", 200, 0, "#FFFFFF");
    }


    public void LOGIC_BACKEND() {
        try {
            FirebaseApp.initializeApp(this);
        } catch (Exception ignored) {
        }
        try {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        } catch (Exception ignored) {
        }
        for (int i = 0; i < 3; i++) {
            HashMap<String, Object> viewPagerMap = new HashMap<>();
            viewPagerMap.put("title", "data");
            viewPagerMap.put("subtitle", "data");
            viewPagerList.add(viewPagerMap);
        }
        viewPager.setAdapter(new ViewPager1Adapter(viewPagerList));
        dataView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        if (sharedPref.getString("firstUse", "").isEmpty()) {
            splashView.setVisibility(View.GONE);
            dataView.setVisibility(View.VISIBLE);
        } else {
            splashView.setVisibility(View.VISIBLE);
            dataView.setVisibility(View.GONE);

            new Handler().postDelayed(() -> {
                intent.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }, 2000);

        }
    }

    public class ViewPager1Adapter extends PagerAdapter {
        Context context;
        ArrayList<HashMap<String, Object>> data;

        public ViewPager1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            context = getApplicationContext();
            data = _arr;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View _view, @NonNull Object _object) {
            return _view == _object;
        }

        @Override
        public void destroyItem(ViewGroup _container, int _position, @NonNull Object _object) {
            _container.removeView((View) _object);
        }

        @Override
        public int getItemPosition(@NonNull Object _object) {
            return super.getItemPosition(_object);
        }

        @Override
        public CharSequence getPageTitle(int pos) {
            return "page " + pos;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.welcomepager, container, false);
            final TextView welcomeTitle2 = view.findViewById(R.id.welcomeTitle2);
            final TextView welcomeDescription2 = view.findViewById(R.id.welcomeDescription2);

            if (position == 0) {
                welcomeTitle2.setText(R.string.welcome_title_1);
                welcomeDescription2.setText(R.string.welcome_subtitle_1);
            } else {
                if (position == 1) {
                    welcomeTitle2.setText(R.string.welcome_title_2);
                    welcomeDescription2.setText(R.string.welcome_subtitle_2);
                } else {
                    if (position == 2) {
                        welcomeTitle2.setText(R.string.welcome_title_3);
                        welcomeDescription2.setText(R.string.welcome_subtitle_3);
                    }
                }
            }

            container.addView(view);
            return view;
        }
    }
}
