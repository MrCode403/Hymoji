package com.nerbly.bemoji;

import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.changeActivityFont;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private final Intent intent = new Intent();
    com.google.android.material.snackbar.Snackbar snackBarView;
    com.google.android.material.snackbar.Snackbar.SnackbarLayout sblayout;
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

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }

        assert dir != null;
        return dir.delete();
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.settings);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        CoordinatorLayout linear1 = findViewById(R.id.linear1);
        bsheetbehavior = findViewById(R.id.bsheetbehavior);
        background = findViewById(R.id.background);
        slider = findViewById(R.id.slider);
        title = findViewById(R.id.title);
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
                showCustomSnackBar(R.string.emojis_reloaded_success);
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
                if (androidx.core.content.ContextCompat.checkSelfPermission(SettingsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED || androidx.core.content.ContextCompat.checkSelfPermission(SettingsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED) {
                    androidx.core.app.ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    showCustomSnackBar(R.string.ask_for_permission);
                } else {
                    trimCache();
                    initializeCacheScan();
                    showCustomSnackBar(R.string.cache_cleared_success);
                }
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
        initializeCacheScan();
    }

    public void LOGIC_FRONTEND() {
        advancedCorners(background, "#FFFFFF", 40, 40, 0, 0);
        setViewRadius(slider, 90, "#E0E0E0");
        DARK_ICONS(this);
        transparentStatusBar(this);
        changeActivityFont("whitney", this);
        rippleRoundStroke(setting1, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting2, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting3, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting4, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting5, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting6, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting7, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting8, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting10, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
    }

    public void trimCache() {
        try {
            File dir = getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception ignored) {

        }
    }

    public void bottomSheetBehaviorListener() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish();
                } else {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        shadAnim(background, "elevation", 20, 200);
                        shadAnim(slider, "translationY", 0, 200);
                        shadAnim(slider, "alpha", 1, 200);
                        slider.setVisibility(View.VISIBLE);
                    } else {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            shadAnim(background, "elevation", 0, 200);
                            shadAnim(slider, "translationY", -200, 200);
                            shadAnim(slider, "alpha", 0, 200);
                            slider.setVisibility(View.INVISIBLE);
                        } else {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                shadAnim(background, "elevation", 20, 200);
                                shadAnim(slider, "translationY", 0, 200);
                                shadAnim(slider, "alpha", 1, 200);
                                slider.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

    }

    public void showCustomSnackBar(final int _text) {
        ViewGroup parentLayout = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        snackBarView = com.google.android.material.snackbar.Snackbar.make(parentLayout, "", com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
        sblayout = (com.google.android.material.snackbar.Snackbar.SnackbarLayout) snackBarView.getView();

        @SuppressLint("InflateParams") View inflate = getLayoutInflater().inflate(R.layout.snackbar, null);
        sblayout.setPadding(0, 0, 0, 0);
        sblayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
        LinearLayout back = inflate.findViewById(R.id.linear1);

        TextView text = inflate.findViewById(R.id.textview1);
        setViewRadius(back, 20, "#202125");
        text.setText(_text);
        text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
        sblayout.addView(inflate, 0);
        snackBarView.show();
    }

    public void performClick(final View _view) {
        _view.performClick();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performClick(setting2);
            } else {
                showCustomSnackBar(R.string.ask_for_permission);
            }
        }
    }

    public void initializeCacheScan() {
        try {
            long size = 0;
            size += getDirSize(this.getCacheDir());
            size += getDirSize(this.getExternalCacheDir());
            textview8.setText(getString(R.string.settings_option_3_title).concat(" (" + readableFileSize(size) + ")"));
        } catch (Exception ignored) {
        }
    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

}
