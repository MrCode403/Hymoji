package com.nerbly.bemoji.Activities;

import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusNavBar;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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

import com.google.android.material.button.MaterialButton;
import com.nerbly.bemoji.R;
import com.nerbly.bemoji.UI.MainUIMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<HashMap<String, Object>> viewPagerList = new ArrayList<>();
    private final Intent intent = new Intent();
    private final Timer timer = new Timer();
    private ImageView welcomeImage;
    private LinearLayout dataView;
    private LinearLayout splashView;
    private ViewPager viewPager;
    private MaterialButton continueBtn;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                if (position == 0) {
                    MainUIMethods.shadAnim(welcomeImage, "scaleY", 1.0, 200);
                    MainUIMethods.shadAnim(welcomeImage, "scaleX", 1.0, 200);
                    continueBtn.setText(R.string.welcome_start);
                } else if (position == 1) {
                    MainUIMethods.shadAnim(welcomeImage, "scaleY", 1.1, 200);
                    MainUIMethods.shadAnim(welcomeImage, "scaleX", 1.1, 200);
                    continueBtn.setText(R.string.welcome_continue);
                } else if (position == 2) {
                    MainUIMethods.shadAnim(welcomeImage, "scaleY", 1.2, 200);
                    MainUIMethods.shadAnim(welcomeImage, "scaleX", 1.2, 200);
                    continueBtn.setText(R.string.welcome_continue);
                }
            }

            @Override
            public void onPageScrollStateChanged(int scrollState) {

            }
        });

        continueBtn.setOnClickListener(_view -> {
            if (viewPager.getCurrentItem() == 2) {
                sharedPref.edit().putBoolean("isFirstTime", false).apply();
                intent.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, 0);
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
        if (sharedPref.getBoolean("isFirstTime", true)) {
            if (!(viewPager.getCurrentItem() == 0)) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        } else {
            timer.cancel();
            finishAffinity();
        }
    }


    public void LOGIC_FRONTEND() {
        transparentStatusNavBar(this);
        LIGHT_ICONS(this);
    }


    public void LOGIC_BACKEND() {

        try {
            if (Build.VERSION.SDK_INT >= 26) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    welcomeImage.setImageDrawable(WallpaperManager.getInstance(this).getDrawable());
                }
            } else {
                welcomeImage.setImageDrawable(WallpaperManager.getInstance(this).getDrawable());
            }
        } catch (Exception ignored) {
        }

        dataView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        if (sharedPref.getBoolean("isFirstTime", true)) {
            for (int i = 0; i < 3; i++) {
                HashMap<String, Object> viewPagerMap = new HashMap<>();
                viewPagerMap.put("title", "data");
                viewPagerMap.put("subtitle", "data");
                viewPagerList.add(viewPagerMap);
            }
            viewPager.setAdapter(new ViewPager1Adapter(viewPagerList));
            splashView.setVisibility(View.GONE);
            dataView.setVisibility(View.VISIBLE);
        } else {
            splashView.setVisibility(View.VISIBLE);
            dataView.setVisibility(View.GONE);
            MainUIMethods.shadAnim(welcomeImage, "scaleY", 1.1, 4000);
            MainUIMethods.shadAnim(welcomeImage, "scaleX", 1.1, 4000);
            TimerTask splashTmr = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {

                        intent.setClass(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, 0);

                    });
                }
            };
            timer.schedule(splashTmr, 2000);


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
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int _position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
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
                welcomeTitle2.setText(R.string.app_name);
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
