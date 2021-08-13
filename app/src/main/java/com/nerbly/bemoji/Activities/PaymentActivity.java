package com.nerbly.bemoji.Activities;


import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusNavBar;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nerbly.bemoji.Fragments.PaymentFeaturesFragment;
import com.nerbly.bemoji.Fragments.PaymentProcessorFragment;
import com.nerbly.bemoji.R;

import java.util.Timer;
import java.util.TimerTask;

public class PaymentActivity extends AppCompatActivity {
    private PaymentProcessorFragment paymentProcessorFragment;
    private PaymentFeaturesFragment paymentFeaturesFragment;
    private ViewPager viewpager;
    private final Timer timer = new Timer();
    private int doubleBackExit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.payment_main);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        viewpager = findViewById(R.id.viewpager);
    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }

    public void LOGIC_BACKEND() {
        viewpager.setOffscreenPageLimit(2);
        viewpager.setAdapter(new MyFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), 2));
    }

    public void LOGIC_FRONTEND() {

        LIGHT_ICONS(this);


        transparentStatusNavBar(this);
    }

    @Override
    public void onBackPressed() {
        if (viewpager.getCurrentItem() == 1) {
            if (doubleBackExit == 0) {
                showCustomSnackBar("Click twice to cancel the payment", this);


                doubleBackExit = 1;
                TimerTask splashTmr = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {

                            doubleBackExit = 0;
                        });
                    }
                };
                timer.schedule(splashTmr, 1200);

            }
            if (doubleBackExit == 1) {

                finish();
            }
        } else {
            finish();
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
                return new PaymentFeaturesFragment();
            }
            if (position == 1) {
                return new PaymentProcessorFragment();
            }
            return null;
        }

    }

}