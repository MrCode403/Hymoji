package com.nerbly.bemoji;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.nerbly.bemoji.Functions.FileUtil;
import com.nerbly.bemoji.Functions.Utils;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setClippedView;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

public class PreviewActivity extends AppCompatActivity {

    private final ObjectAnimator downloadAnimation = new ObjectAnimator();
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private String downloadPath = "";
    private String downloadUrl = "";
    private boolean isDownloading = false;
    private boolean isEmojiDownloaded = false;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout bsheetbehavior;
    private LinearLayout relativeview;
    private TextView activityTitle;
    private TextView activitySubtitle;
    private TextView information;
    private LinearLayout download;
    private ImageView emoji;
    private ImageView imageview7;
    private ImageView download_ic;
    private TextView download_tv;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.preview);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        coordinatorLayout = findViewById(R.id.coordinator);
        bsheetbehavior = findViewById(R.id.sheetBehavior);
        relativeview = findViewById(R.id.relativeView);
        activityTitle = findViewById(R.id.activityTitle);
        activitySubtitle = findViewById(R.id.activitySubtitle);
        information = findViewById(R.id.information);
        download = findViewById(R.id.download);
        emoji = findViewById(R.id.emoji);
        imageview7 = findViewById(R.id.imageview7);
        download_ic = findViewById(R.id.download_ic);
        download_tv = findViewById(R.id.download_tv);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        coordinatorLayout.setOnClickListener(_view -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));

        download.setOnClickListener(_view -> {
            if (!isDownloading && !isEmojiDownloaded) {
                if (sharedPref.getString("downloadPath", "").isEmpty()) {
                    downloadPath = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS) + "/Bemojis";
                } else {
                    downloadPath = sharedPref.getString("downloadPath", "");
                }
                downloadUrl = getIntent().getStringExtra("imageUrl");
                startDownload("Bemoji_".concat(downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1)), downloadUrl, downloadPath);
            }
        });
    }

    private void initializeLogic() {
        LOGIC_BACKEND();
        LOGIC_FRONTEND();
    }


    @Override
    public void onBackPressed() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void LOGIC_BACKEND() {
        sheetBehavior = BottomSheetBehavior.from(bsheetbehavior);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        new Handler().postDelayed(() -> sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 200);
        setBlurImageUrl(emoji, 25, getIntent().getStringExtra("imageUrl"));
        setImageFromUrl(imageview7, getIntent().getStringExtra("imageUrl"));
        activityTitle.setText(getIntent().getStringExtra("title"));
        activitySubtitle.setText(getString(R.string.submitted_by) + " " + getIntent().getStringExtra("submitted_by"));
        bottomBehaviourListener();
        shadAnim(coordinatorLayout, "alpha", 1, 200);
    }


    public void LOGIC_FRONTEND() {
        DARK_ICONS(this);

        transparentStatusBar(this);

        setClippedView(relativeview, "#FFFFFF", 25, 7);

        rippleRoundStroke(download, "#7289DA", "#687DC8", 25, 0, "#7289DA");
        android.graphics.drawable.GradientDrawable JJACCAI = new android.graphics.drawable.GradientDrawable();
        int[] JJACCAIADD = new int[]{Color.parseColor("#00000000"), Color.parseColor("#80000000")};
        JJACCAI.setColors(JJACCAIADD);
        JJACCAI.setOrientation(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM);
        JJACCAI.setCornerRadius(0);
        coordinatorLayout.setBackground(JJACCAI);
    }


    public void bottomBehaviourListener() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (isDownloading) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        shadAnim(coordinatorLayout, "alpha", 0, 200);
                        new Handler().postDelayed(() -> finish(), 150);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

    }


    public void startDownload(final String name, String url, String path) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED || androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            showCustomSnackBar(getString(R.string.ask_for_permission), this);
        } else {
            isDownloading = true;
            download_tv.setText(R.string.downloading);
            download_ic.setImageResource(R.drawable.loadingimg);
            downloadAnimation.setTarget(download_ic);
            downloadAnimation.setPropertyName("rotation");
            downloadAnimation.setFloatValues((float) (1000));
            downloadAnimation.setRepeatCount(999);
            downloadAnimation.setDuration(1000);
            downloadAnimation.setRepeatMode(ValueAnimator.REVERSE);
            downloadAnimation.start();
            PRDownloader.download(url, path, name)
                    .build()
                    .setOnStartOrResumeListener(() -> {

                    })
                    .setOnPauseListener(() -> {

                    })
                    .setOnCancelListener(() -> {

                    })
                    .setOnProgressListener(progress -> {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        download_tv.setText(getString(R.string.downloading).concat(progressPercent + "%"));
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {

                            isDownloading = false;
                            isEmojiDownloaded = true;
                            download_tv.setText(R.string.download_success);
                            download_ic.setImageResource(R.drawable.round_done_white_48dp);
                            download_ic.setRotation((float) (0));
                            information.setText(getString(R.string.full_download_path).concat(downloadPath + "/" + name));
                            information.setVisibility(View.VISIBLE);
                            downloadAnimation.cancel();

                            MediaScannerConnection.scanFile(PreviewActivity.this,
                                    new String[]{path}, null,
                                    (path1, uri) -> {

                                    });

                        }

                        @Override
                        public void onError(Error error) {


                            isDownloading = false;
                            download_tv.setText(R.string.download_btn_txt);
                            download_ic.setImageResource(R.drawable.round_get_app_white_48dp);
                            showCustomSnackBar(getString(R.string.error_msg), PreviewActivity.this);
                            download_ic.setRotation((float) (0));
                            downloadAnimation.cancel();
                        }
                    });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performClick(download);
            } else {
                showCustomSnackBar(getString(R.string.ask_for_permission), this);
            }
        }
    }

    public void performClick(View view) {
        view.performClick();
    }


    public void setBlurImageUrl(ImageView image, double blur, String url) {
        try {
            RequestOptions options1 = new RequestOptions()
                    .priority(Priority.HIGH);

            Glide.with(this)

                    .load(url)
                    .apply(options1)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(bitmapTransform(new BlurTransformation((int) blur, 4)))
                    .into(image);
        } catch (Exception e) {
            Utils.showToast(getApplicationContext(), (e.toString()));
        }

    }


    public void setImageFromUrl(ImageView image, String url) {
        RequestOptions options = new RequestOptions()
                .priority(Priority.IMMEDIATE);

        Glide.with(this)
                .load(url)
                .apply(options)
                .into(image);

    }

}
