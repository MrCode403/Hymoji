package com.nerbly.bemoji;

import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.changeActivityFont;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusNavBar;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final Timer _timer = new Timer();
    private final ArrayList<HashMap<String, Object>> viewPagerList = new ArrayList<>();
    private final Intent intent = new Intent();
    FirebaseAnalytics mFirebaseAnalytics;
    private ImageView imageview2;
    private LinearLayout dataView;
    private LinearLayout splashView;
    private ViewPager viewpager1;
    private TextView textview1;
    private TextView textview3;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.main);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        imageview2 = findViewById(R.id.imageview2);
        dataView = findViewById(R.id.dataview);
        splashView = findViewById(R.id.splashview);
        viewpager1 = findViewById(R.id.viewpager1);
        textview1 = findViewById(R.id.textview1);
        textview3 = findViewById(R.id.textview3);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        splashView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        viewpager1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    textview1.setText(R.string.welcome_start);
                } else {
                    textview1.setText(R.string.welcome_continue);
                }
            }

            @Override
            public void onPageScrollStateChanged(int scrollState) {

            }
        });

        textview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (viewpager1.getCurrentItem() == 2) {
                    sharedPref.edit().putString("firstUse", "true").apply();
                    intent.setClass(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                } else {
                    viewpager1.setCurrentItem(viewpager1.getCurrentItem() + 1);
                }
            }
        });
    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }

    @Override
    public void onBackPressed() {
        if (!(viewpager1.getCurrentItem() == 0)) {
            viewpager1.setCurrentItem(viewpager1.getCurrentItem() - 1);
        }
    }


    public void LOGIC_FRONTEND() {
        try {
            InputStream ims = getAssets().open("images/splash.png");
            Drawable d = Drawable.createFromStream(ims, null);
            imageview2.setImageDrawable(d);
        } catch (Exception e) {
            return;
        }
        transparentStatusNavBar(this);

        LIGHT_ICONS(this);

        changeActivityFont("whitney", this);

        rippleRoundStroke(textview1, "#FFFFFF", "#EEEEEE", 200, 0, "#FFFFFF");
        textview1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
        textview3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
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
        viewpager1.setAdapter(new ViewPager1Adapter(viewPagerList));
        dataView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        if (sharedPref.getString("firstUse", "").equals("")) {
            splashView.setVisibility(View.GONE);
            dataView.setVisibility(View.VISIBLE);
        } else {
            splashView.setVisibility(View.VISIBLE);
            dataView.setVisibility(View.GONE);
            TimerTask splashTmr = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            intent.setClass(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            };
            _timer.schedule(splashTmr, 2000);
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
            // use the activity event (onTabLayoutNewTabAdded) in order to use this method
            return "page " + pos;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.welcomepager, container, false);
            final TextView textview2 = view.findViewById(R.id.textview2);
            final TextView textview3 = view.findViewById(R.id.textview3);

            if (position == 0) {
                textview2.setText(R.string.welcome_title_1);
                textview3.setText(R.string.welcome_subtitle_1);
            } else {
                if (position == 1) {
                    textview2.setText(R.string.welcome_title_2);
                    textview3.setText(R.string.welcome_subtitle_2);
                } else {
                    if (position == 2) {
                        textview2.setText(R.string.welcome_title_3);
                        textview3.setText(R.string.welcome_subtitle_3);
                    }
                }
            }
            textview2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
            textview3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);

            container.addView(view);
            return view;
        }
    }
}
