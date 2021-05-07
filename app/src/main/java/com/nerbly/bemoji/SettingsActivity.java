package com.nerbly.bemoji;

import static com.nerbly.bemoji.Functions.MainFunctions.initializeCacheScan;
import static com.nerbly.bemoji.Functions.MainFunctions.trimCache;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class SettingsActivity extends AppCompatActivity {

    private final Intent intent = new Intent();
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private LinearLayout bsheetbehavior;
    private LinearLayout background;
    private LinearLayout slider;
    private TextView title;
    private RelativeLayout setting1;
    private RelativeLayout setting3;
    private RelativeLayout setting2;
    private RelativeLayout setting4;
    private RelativeLayout setting5;
    private RelativeLayout setting8;
    private RelativeLayout setting6;
    private RelativeLayout setting7;
    private RelativeLayout setting10;
    private TextView textview8;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.settings);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        CoordinatorLayout linear1 = findViewById(R.id.tutorialBg);
        bsheetbehavior = findViewById(R.id.sheetBehavior);
        background = findViewById(R.id.background);
        slider = findViewById(R.id.slider);
        title = findViewById(R.id.activityTitle);
        setting1 = findViewById(R.id.setting1);
        setting3 = findViewById(R.id.setting3);
        setting2 = findViewById(R.id.setting2);
        setting4 = findViewById(R.id.setting4);
        setting5 = findViewById(R.id.setting5);
        setting8 = findViewById(R.id.setting8);
        setting6 = findViewById(R.id.setting6);
        setting7 = findViewById(R.id.setting7);
        setting10 = findViewById(R.id.setting10);
        textview8 = findViewById(R.id.textview8);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        setting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                sharedPref.edit().putString("emojisData", "").apply();

                sharedPref.edit().putString("categoriesData", "").apply();

                sharedPref.edit().putString("packsData", "").apply();

                sharedPref.edit().putString("isAskingForReload", "true").apply();
                showCustomSnackBar(getString(R.string.emojis_reloaded_success), SettingsActivity.this);
            }
        });

        setting3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://emoji.gg/submit"));
                startActivity(intent);
            }
        });

        setting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                trimCache(SettingsActivity.this);
                textview8.setText(getString(R.string.settings_option_3_title).concat(" (" + initializeCacheScan(SettingsActivity.this) + ")"));
                showCustomSnackBar(getString(R.string.cache_cleared_success), SettingsActivity.this);
            }
        });

        setting4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("mailto:nerblyteam@gmail.com"));
                startActivity(intent);
            }
        });

        setting5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.nerbly.bemoji"));
                startActivity(intent);
            }
        });

        setting6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://emoji.gg/copyright"));
                startActivity(intent);
            }
        });

        setting7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://emoji.gg/"));
                startActivity(intent);
            }
        });
        setting10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/ilyassesalama/bemoji"));
                startActivity(intent);
            }
        });
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
        bottomSheetBehaviorListener();
        textview8.setText(getString(R.string.settings_option_3_title).concat(" (" + initializeCacheScan(this) + ")"));
    }

    public void LOGIC_FRONTEND() {
        advancedCorners(background, "#FFFFFF", 40, 40, 0, 0);
        setViewRadius(slider, 90, "#E0E0E0");
        DARK_ICONS(this);
        transparentStatusBar(this);
        rippleRoundStroke(setting1, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting2, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting3, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting4, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting5, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting6, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting7, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting8, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting10, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
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
}