package com.nerbly.bemoji;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.nerbly.bemoji.UI.MainUIMethods;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private final Intent toHome = new Intent();
    com.google.android.material.snackbar.Snackbar _snackBarView;
    com.google.android.material.snackbar.Snackbar.SnackbarLayout _sblayout;
    BottomSheetBehavior sheetBehavior;
    private LinearLayout bsheetbehavior;
    private LinearLayout background;
    private LinearLayout slider;
    private TextView title;
    private LinearLayout setting1;
    private LinearLayout setting3;
    private LinearLayout setting2;
    private LinearLayout setting4;
    private LinearLayout setting5;
    private LinearLayout setting8;
    private LinearLayout setting6;
    private LinearLayout setting7;
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
        initialize(_savedInstanceState);
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize(Bundle _savedInstanceState) {
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
                _showCustomSnackBar("Done. Home page will be reloaded when you get back");
            }
        });

        setting3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                toHome.setAction(Intent.ACTION_VIEW);
                toHome.setData(Uri.parse("https://emoji.gg/submit"));
                startActivity(toHome);
            }
        });

        setting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(SettingsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED || androidx.core.content.ContextCompat.checkSelfPermission(SettingsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED) {
                    androidx.core.app.ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    _showCustomSnackBar("Please allow Bemoji to have storage permission");
                } else {
                    trimCache();
                    _initializeCacheScan();
                    _showCustomSnackBar("Cache has been cleared successfully");
                }
            }
        });

        setting4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                toHome.setAction(Intent.ACTION_VIEW);
                toHome.setData(Uri.parse("mailto:nerblyteam@gmail.com"));
                startActivity(toHome);
            }
        });

        setting5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                toHome.setAction(Intent.ACTION_VIEW);
                toHome.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.nerbly.bemoji"));
                startActivity(toHome);
            }
        });

        setting6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                toHome.setAction(Intent.ACTION_VIEW);
                toHome.setData(Uri.parse("https://emoji.gg/copyright"));
                startActivity(toHome);
            }
        });

        setting7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                toHome.setAction(Intent.ACTION_VIEW);
                toHome.setData(Uri.parse("https://emoji.gg/"));
                startActivity(toHome);
            }
        });
    }

    private void initializeLogic() {
        _LOGIC_FRONTEND();
        _LOGIC_BACKEND();
    }

    @Override
    public void onBackPressed() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void _LOGIC_BACKEND() {
        sheetBehavior = BottomSheetBehavior.from(bsheetbehavior);
        _BottomSheetBehaviorListener();
        _initializeCacheScan();
    }

    public void _LOGIC_FRONTEND() {
        MainUIMethods.advancedCorners(background, "#FFFFFF", 40, 40, 0, 0);
        MainUIMethods.setViewRadius(slider, 90, "#E0E0E0");
        MainUIMethods.DARK_ICONS(this);
        MainUIMethods.transparentStatusBar(this);
        MainUIMethods.changeActivityFont("whitney", this);
        MainUIMethods.rippleRoundStroke(setting1, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        MainUIMethods.rippleRoundStroke(setting2, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        MainUIMethods.rippleRoundStroke(setting3, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        MainUIMethods.rippleRoundStroke(setting4, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        MainUIMethods.rippleRoundStroke(setting5, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        MainUIMethods.rippleRoundStroke(setting6, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        MainUIMethods.rippleRoundStroke(setting7, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        MainUIMethods.rippleRoundStroke(setting8, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
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

    public void _BottomSheetBehaviorListener() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish();
                } else {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        MainUIMethods.shadAnim(background, "elevation", 20, 200);
                        MainUIMethods.shadAnim(slider, "translationY", 0, 200);
                        MainUIMethods.shadAnim(slider, "alpha", 1, 200);
                        slider.setVisibility(View.VISIBLE);
                    } else {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            MainUIMethods.shadAnim(background, "elevation", 0, 200);
                            MainUIMethods.shadAnim(slider, "translationY", -200, 200);
                            MainUIMethods.shadAnim(slider, "alpha", 0, 200);
                            slider.setVisibility(View.INVISIBLE);
                        } else {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                MainUIMethods.shadAnim(background, "elevation", 20, 200);
                                MainUIMethods.shadAnim(slider, "translationY", 0, 200);
                                MainUIMethods.shadAnim(slider, "alpha", 1, 200);
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

    public void _showCustomSnackBar(final String _text) {
        ViewGroup parentLayout = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        _snackBarView = com.google.android.material.snackbar.Snackbar.make(parentLayout, "", com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
        _sblayout = (com.google.android.material.snackbar.Snackbar.SnackbarLayout) _snackBarView.getView();

        @SuppressLint("InflateParams") View _inflate = getLayoutInflater().inflate(R.layout.snackbar, null);
        _sblayout.setPadding(0, 0, 0, 0);
        _sblayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
        LinearLayout back =
                _inflate.findViewById(R.id.linear1);

        TextView text =
                _inflate.findViewById(R.id.textview1);
        MainUIMethods.setViewRadius(back, 20, "#202125");
        text.setText(_text);
        text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
        _sblayout.addView(_inflate, 0);
        _snackBarView.show();
    }

    public void _performClick(final View _view) {
        _view.performClick();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                _performClick(setting2);
            } else {
                _showCustomSnackBar("You can't clear cache without storage access permission. Please allow it.");
            }
        }
    }

    public void _initializeCacheScan() {
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
