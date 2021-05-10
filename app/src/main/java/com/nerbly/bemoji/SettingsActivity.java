package com.nerbly.bemoji;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static com.nerbly.bemoji.Functions.MainFunctions.initializeCacheScan;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.Functions.MainFunctions.setLocale;
import static com.nerbly.bemoji.Functions.MainFunctions.trimCache;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

public class SettingsActivity extends AppCompatActivity {

    private final Intent intent = new Intent();
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private LinearLayout bsheetbehavior;
    private LinearLayout background;
    private LinearLayout slider;
    private RelativeLayout setting1;
    private RelativeLayout setting3;
    private RelativeLayout setting2;
    private RelativeLayout setting4;
    private RelativeLayout setting5;
    private RelativeLayout setting8;
    private RelativeLayout setting6;
    private RelativeLayout setting7;
    private RelativeLayout setting10;
    private RelativeLayout setting11;
    private RelativeLayout setting12;
    private TextView textview8;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
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
        setting1 = findViewById(R.id.setting1);
        setting3 = findViewById(R.id.setting3);
        setting2 = findViewById(R.id.setting2);
        setting4 = findViewById(R.id.setting4);
        setting5 = findViewById(R.id.setting5);
        setting8 = findViewById(R.id.setting8);
        setting6 = findViewById(R.id.setting6);
        setting7 = findViewById(R.id.setting7);
        setting10 = findViewById(R.id.setting10);
        setting11 = findViewById(R.id.setting11);
        setting12 = findViewById(R.id.setting12);
        textview8 = findViewById(R.id.textview8);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        linear1.setOnClickListener(_view -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));

        setting1.setOnClickListener(_view -> {
            sharedPref.edit().putString("emojisData", "").apply();

            sharedPref.edit().putString("categoriesData", "").apply();

            sharedPref.edit().putString("packsData", "").apply();

            sharedPref.edit().putString("isAskingForReload", "true").apply();
            showCustomSnackBar(getString(R.string.emojis_reloaded_success), SettingsActivity.this);
        });

        setting3.setOnClickListener(_view -> {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://emoji.gg/submit"));
            startActivity(intent);
        });

        setting2.setOnClickListener(_view -> {
            trimCache(SettingsActivity.this);
            textview8.setText(getString(R.string.settings_option_3_title).concat(" (" + initializeCacheScan(SettingsActivity.this) + ")"));
            showCustomSnackBar(getString(R.string.cache_cleared_success), SettingsActivity.this);
        });

        setting4.setOnClickListener(_view -> {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("mailto:nerblyteam@gmail.com"));
            startActivity(intent);
        });

        setting5.setOnClickListener(_view -> {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.nerbly.bemoji"));
            startActivity(intent);
        });

        setting6.setOnClickListener(_view -> {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://emoji.gg/copyright"));
            startActivity(intent);
        });

        setting7.setOnClickListener(_view -> {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://emoji.gg/"));
            startActivity(intent);
        });
        setting10.setOnClickListener(_view -> {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/ilyassesalama/bemoji"));
            startActivity(intent);
        });
        setting11.setOnClickListener(_view -> showLanguagesSheet());

        setting12.setOnClickListener(view -> {
            intent.setAction(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nerblyteam@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Bemoji Translation Contribution");
            if (intent.resolveActivity(getPackageManager()) != null) {
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
        rippleRoundStroke(setting11, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting12, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
    }

    private void showLanguagesSheet() {
        final String[] languages = {"English", "Deutsche", "Français", "Português"};
        MaterialAlertDialogBuilder languagesDialog = new MaterialAlertDialogBuilder(this, R.style.RoundShapeTheme);
        int languagePosition = -1;
        if (sharedPref.getString("language_position", "") != null) {
            if (!sharedPref.getString("language_position", "").equals("")) {
                languagePosition = Integer.parseInt(sharedPref.getString("language_position", ""));
            }
        }
        languagesDialog.setTitle("Choose your language")
                .setSingleChoiceItems(languages, languagePosition, (dialog, i) -> {
                    if (i == 0) {
                        setLocale("en", Integer.toString(i), SettingsActivity.this);
                    } else if (i == 1) {
                        setLocale("de", Integer.toString(i), SettingsActivity.this);
                    } else if (i == 2) {
                        setLocale("fr", Integer.toString(i), SettingsActivity.this);
                    } else if (i == 3) {
                        setLocale("pt", Integer.toString(i), SettingsActivity.this);
                    }
                    dialog.dismiss();
                    sharedPref.edit().putString("isAskingForReload", "true").apply();
                    recreate();
                })
                .show();
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