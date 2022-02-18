package com.nerbly.bemoji.Activities;

import static com.nerbly.bemoji.Configurations.ASSETS_SOURCE_LINK;
import static com.nerbly.bemoji.Functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.Functions.SideFunctions.setImgURL;
import static com.nerbly.bemoji.Functions.Utils.ZIP;
import static com.nerbly.bemoji.Functions.Utils.isStoragePermissionGranted;
import static com.nerbly.bemoji.Functions.Utils.requestStoragePermission;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.marqueeTextView;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Functions.FileUtil;
import com.nerbly.bemoji.R;
import com.nerbly.bemoji.UI.DownloaderSheet;
import com.nerbly.bemoji.UI.UserInteractions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackPreviewActivity extends AppCompatActivity {
    private final ArrayList<HashMap<String, Object>> emojisListMap = new ArrayList<>();
    private final ObjectAnimator downAnim = new ObjectAnimator();
    private GridLayoutManager layoutManager1 = new GridLayoutManager(this, 3);
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private boolean isDownloading = false;
    private boolean isPackDownloaded = false;
    private String tempPackName = "";
    private boolean isGoingToZipPack = false;
    private String packEmojisArrayString = "";
    private RelativeLayout relativeView;
    private LinearLayout download;
    private LinearLayout bsheetbehavior;
    private LinearLayout background;
    private LinearLayout slider;
    private TextView activityTitle;
    private TextView activityDescription;
    private RecyclerView packsRecycler;
    private ImageView download_ic;
    private TextView download_tv;
    private SharedPreferences sharedPref;
    private String currentDownloadURL = "";
    private String currentDownloadPath = "";
    private String currentDownloadName = "";
    private int currentDownloadPosition = 0;
    private String packDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.packpreview);
        initialize();
        FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        relativeView = findViewById(R.id.relativeView);
        download = findViewById(R.id.download);
        bsheetbehavior = findViewById(R.id.sheetBehavior);
        background = findViewById(R.id.background);
        slider = findViewById(R.id.slider);
        activityTitle = findViewById(R.id.activityTitle);
        activityDescription = findViewById(R.id.activityDescription);
        packsRecycler = findViewById(R.id.packEmojisRecycler);
        download_ic = findViewById(R.id.download_ic);
        download_tv = findViewById(R.id.download_tv);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        relativeView.setOnClickListener(_view -> {
            shadAnim(download, "translationY", 200, 200);
            shadAnim(download, "alpha", 0, 200);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        download.setOnClickListener(_view -> askForZippingSheet());
    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }

    public void LOGIC_BACKEND() {
        overridePendingTransition(R.anim.fade_in, 0);
        sheetBehavior = BottomSheetBehavior.from(bsheetbehavior);
        setGridColumns();
        bottomSheetBehaviorListener();
        try {
            tempPackName = getIntent().getStringExtra("packName");
            activityTitle.setText(tempPackName);
            activityDescription.setText(getIntent().getStringExtra("subtitle"));
            packEmojisArrayString = getIntent().getStringExtra("packEmojisArray");

            ArrayList<String> emojisStringArray = new Gson().fromJson(packEmojisArrayString, new TypeToken<ArrayList<String>>() {
            }.getType());

            for (int i = 0; i < emojisStringArray.size(); i++) {
                HashMap<String, Object> emojisMap = new HashMap<>();
                emojisMap.put("emoji_link", ASSETS_SOURCE_LINK + emojisStringArray.get(i));
                emojisMap.put("slug", emojisStringArray.get(i));
                emojisListMap.add(emojisMap);
            }
            packsRecycler.setAdapter(new Recycler1Adapter(emojisListMap));

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            UserInteractions.showCustomSnackBar(getString(R.string.failed_to_load_emojis), this);
            new Handler().postDelayed(() -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN), 3000);
        }
    }

    public void LOGIC_FRONTEND() {
        advancedCorners(background, "#FFFFFF", 40, 40, 0, 0);
        marqueeTextView(activityTitle);
        setViewRadius(slider, 90, "#E0E0E0");
        rippleRoundStroke(download, "#7289DA", "#687DC8", getResources().getDimension(R.dimen.buttons_corners_radius), 0, "#7289DA");
        DARK_ICONS(this);
        transparentStatusBar(this);
    }


    private void setGridColumns() {
        float scaleFactor = getResources().getDisplayMetrics().density * 60;
        int screenWidth = getScreenWidth(this);
        int columns = (int) ((float) screenWidth / scaleFactor);
        layoutManager1 = new GridLayoutManager(this, columns);
        packsRecycler.setLayoutManager(layoutManager1);
    }

    private void bottomSheetBehaviorListener() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        shadAnim(background, "elevation", 20, 200);
                        shadAnim(slider, "translationY", 0, 200);
                        shadAnim(slider, "alpha", 1, 200);
                        shadAnim(download, "translationY", 0, 200);
                        shadAnim(download, "alpha", 1, 200);
                        slider.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        shadAnim(background, "elevation", 20, 200);
                        shadAnim(slider, "translationY", 0, 200);
                        shadAnim(slider, "alpha", 1, 200);
                        slider.setVisibility(View.VISIBLE);
                        if (!isDownloading) {
                            shadAnim(download, "translationY", 200, 200);
                            shadAnim(download, "alpha", 0, 200);
                        }
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        shadAnim(background, "elevation", 0, 200);
                        shadAnim(slider, "translationY", -200, 200);
                        shadAnim(slider, "alpha", 0, 200);
                        shadAnim(download, "translationY", 0, 200);
                        shadAnim(download, "alpha", 1, 200);
                        slider.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if (isDownloading) {
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        } else {
                            shadAnim(relativeView, "alpha", 0, 200);
                            new Handler().postDelayed(() -> finish(), 150);
                        }
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

    private void downloadPack() {
        if (!isDownloading) {
            download_tv.setText(R.string.downloading);
            download_ic.setImageResource(R.drawable.loadingimg);
            downAnim.setTarget(download_ic);
            downAnim.setPropertyName("rotation");
            downAnim.setFloatValues((float) (1000));
            downAnim.setRepeatCount(999);
            downAnim.setDuration(1000);
            downAnim.setRepeatMode(ValueAnimator.REVERSE);
            downAnim.start();
        }
        isDownloading = true;

        if (isGoingToZipPack) {
            currentDownloadPath = FileUtil.getPackageDataDir(getApplicationContext()) + "/Zipper/" + tempPackName;
            Log.d("HYMOJI_PACK_DOWNLOAD", "Downloading as ZIP, download path: " + currentDownloadPath);
        } else {
            currentDownloadPath = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS) + "/" + getString(R.string.app_name) + "/" + tempPackName;
            Log.d("HYMOJI_PACK_DOWNLOAD", "Downloading as folder, download path: " + currentDownloadPath);
        }

        currentDownloadName = getString(R.string.app_name) + "_" + Objects.requireNonNull(emojisListMap.get(currentDownloadPosition).get("slug"));
        currentDownloadURL = Objects.requireNonNull(emojisListMap.get(currentDownloadPosition).get("emoji_link")).toString();


        PRDownloader.download(currentDownloadURL, currentDownloadPath, currentDownloadName)
                .build()
                .setOnStartOrResumeListener(() -> {

                })
                .setOnPauseListener(() -> {

                })
                .setOnCancelListener(() -> {

                })
                .setOnProgressListener(progress -> {
                })
                .start(new OnDownloadListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDownloadComplete() {
                        if (currentDownloadPosition < emojisListMap.size() - 1) {
                            currentDownloadPosition++;
                            downloadPack();
                            download_tv.setText(getString(R.string.pack_downloading_progress) + " " + currentDownloadPosition + "/" + emojisListMap.size());
                        } else {
                            if (isGoingToZipPack) {
                                zippingTask();
                            } else {
                                downloadFinished(true);
                            }
                        }

                    }

                    @Override
                    public void onError(Error error) {
                        downloadFinished(false);
                        showCustomSnackBar(getString(R.string.error_msg_2) + error.getServerErrorMessage(), PackPreviewActivity.this);
                    }
                });
    }

    private void downloadFinished(boolean isSuccess) {
        isDownloading = false;
        download_ic.setRotation(0);
        downAnim.cancel();
        if (isSuccess) {
            showCustomSnackBar(getString(R.string.full_download_path) + " " + packDestination, this);
            download_ic.setImageResource(R.drawable.round_done_white_48dp);
            download_tv.setText(R.string.download_success);
            isPackDownloaded = true;
        } else {
            download_tv.setText(R.string.download_btn_txt);
            download_ic.setImageResource(R.drawable.round_get_app_white_48dp);
        }
    }


    public void askForZippingSheet() {
        if (!isDownloading && !isPackDownloaded) {
            if (isStoragePermissionGranted(this) || Build.VERSION.SDK_INT >= 30) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.materialsheet);
                View bottomSheetView;
                bottomSheetView = getLayoutInflater().inflate(R.layout.infosheet, (ViewGroup) null);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

                final ImageView image = bottomSheetView.findViewById(R.id.image);
                final TextView infook = bottomSheetView.findViewById(R.id.infosheet_ok);
                final TextView infocancel = bottomSheetView.findViewById(R.id.infosheet_cancel);
                final TextView infotitle = bottomSheetView.findViewById(R.id.infosheet_title);
                final TextView infosub = bottomSheetView.findViewById(R.id.infosheet_description);
                final LinearLayout infoback = bottomSheetView.findViewById(R.id.infosheet_back);
                final LinearLayout slider = bottomSheetView.findViewById(R.id.slider);

                advancedCorners(infoback, "#ffffff", 38, 38, 0, 0);
                setViewRadius(slider, 180, "#BDBDBD");
                infotitle.setText(R.string.pack_confirmation_sheet_title);
                infosub.setText(R.string.pack_confirmation_sheet_subtitle);
                infook.setText(R.string.pack_confirmation_sheet_btn1);
                infocancel.setText(R.string.pack_confirmation_sheet_btn2);
                image.setImageResource(R.drawable.ic_files_and_folder_flatline);
                infook.setOnClickListener(v -> {
                    isGoingToZipPack = true;
                    downloadPack();
                    bottomSheetDialog.dismiss();
                });
                infocancel.setOnClickListener(v -> {
                    isGoingToZipPack = false;
                    downloadPack();
                    bottomSheetDialog.dismiss();
                });
                if (!isFinishing()) {
                    try {
                        bottomSheetDialog.show();
                    } catch (Exception e) {
                        showCustomSnackBar(getString(R.string.error_msg), this);
                    }
                } else {
                    showCustomSnackBar(getString(R.string.error_msg), this);
                }
            } else {
                requestStoragePermission(1, this);
                showCustomSnackBar(getString(R.string.ask_for_permission), this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askForZippingSheet();
            } else {
                showCustomSnackBar(getString(R.string.permission_denied_packs), this);
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void zippingTask() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {

            packDestination = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS) + "/" + getString(R.string.app_name) + "/" + tempPackName + ".zip";

            Log.d("HYMOJI_PACK_DOWNLOAD", "Zipping at destination: " + packDestination);

            ZIP(currentDownloadPath, packDestination);

            handler.post(() -> {
                downloadFinished(true);
                FileUtil.deleteFile(currentDownloadPath);
            });
        });
    }

    @Override
    public void onBackPressed() {
        shadAnim(download, "translationY", 200, 200);
        shadAnim(download, "alpha", 0, 200);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public class Recycler1Adapter extends RecyclerView.Adapter<Recycler1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> data;

        public Recycler1Adapter(ArrayList<HashMap<String, Object>> arr) {
            data = arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.emojisview, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            View view = holder.itemView;

            final LinearLayout space = view.findViewById(R.id.space);
            LinearLayout emojisBackground = view.findViewById(R.id.emojiBackground);
            ImageView emoji = view.findViewById(R.id.emoji);

            setImgURL(emoji, Objects.requireNonNull(data.get(position).get("emoji_link")).toString());
            emojisBackground.setOnClickListener(_view -> {
                try {
                    DownloaderSheet downloaderSheet = new DownloaderSheet();
                    downloaderSheet.showEmojiSheet(PackPreviewActivity.this, Objects.requireNonNull(data.get(position).get("emoji_link")).toString(), Objects.requireNonNull(data.get(position).get("slug")).toString(), "Emoji lovers");
                } catch (Exception e) {
                    Log.e("HYMOJI_EMOJI_PREVIEWER", e.toString());
                    e.printStackTrace();
                }
            });

            if (position == getItemCount() - 1) {
                space.setVisibility(View.VISIBLE);
            } else {
                space.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }
    }
}
