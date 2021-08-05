package com.nerbly.bemoji.Activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Functions.FileUtil;
import com.nerbly.bemoji.R;
import com.nerbly.bemoji.UI.UserInteractions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.nerbly.bemoji.Functions.DownloaderSheet.showEmojiSheet;
import static com.nerbly.bemoji.Functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.Functions.SideFunctions.getNavigationBarHeight;
import static com.nerbly.bemoji.Functions.Utils.ZIP;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;
import static com.nerbly.bemoji.UI.SideUIMethods.setMargins;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

public class PackPreviewActivity extends AppCompatActivity {
    private final ArrayList<HashMap<String, Object>> emojisListMap = new ArrayList<>();
    private final Intent toPreview = new Intent();
    private final ObjectAnimator downAnim = new ObjectAnimator();
    private GridLayoutManager layoutManager1 = new GridLayoutManager(this, 3);
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private boolean isDownloading = false;
    private double downloadPackPosition = 0;
    private boolean isPackDownloaded = false;
    private String tempPackName = "";
    private boolean isGoingToZipPack = false;
    private String downloadPackPath = "";
    private String packEmojisArrayString = "";
    private String downloadPath = "";
    private ArrayList<String> downloadPackArrayList = new ArrayList<>();
    private CoordinatorLayout coordinator;
    private LinearLayout download;
    private LinearLayout bsheetbehavior;
    private LinearLayout background;
    private LinearLayout slider;
    private TextView activityTitle;
    private RecyclerView packsRecycler;
    private ImageView download_ic;
    private TextView download_tv;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.packpreview);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        coordinator = findViewById(R.id.coordinator);
        download = findViewById(R.id.download);
        bsheetbehavior = findViewById(R.id.sheetBehavior);
        background = findViewById(R.id.background);
        slider = findViewById(R.id.slider);
        activityTitle = findViewById(R.id.activityTitle);
        packsRecycler = findViewById(R.id.packEmojisRecycler);
        download_ic = findViewById(R.id.download_ic);
        download_tv = findViewById(R.id.download_tv);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        coordinator.setOnClickListener(_view -> {
            shadAnim(download, "translationY", 200, 200);
            shadAnim(download, "alpha", 0, 200);

            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        download.setOnClickListener(_view -> {
            if (!isDownloading && !isPackDownloaded) {
                if (sharedPref.getString("downloadPath", "").isEmpty()) {
                    downloadPath = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS) + "/Bemojis";
                } else {
                    downloadPath = sharedPref.getString("downloadPath", "");
                }
                askForZippingSheet();
            }
        });
    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }

    @Override
    public void onBackPressed() {
        shadAnim(download, "translationY", 200, 200);
        shadAnim(download, "alpha", 0, 200);

        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void LOGIC_BACKEND() {
        overridePendingTransition(R.anim.fade_in, 0);
        sheetBehavior = BottomSheetBehavior.from(bsheetbehavior);
        activityTitle.setText(getIntent().getStringExtra("packName"));
        rotationListener();
        bottomSheetBehaviorListener();
        try {
            tempPackName = getIntent().getStringExtra("packName");
            packEmojisArrayString = getIntent().getStringExtra("packEmojisArray");
            ArrayList<String> emojisStringArray = new Gson().fromJson(getIntent().getStringExtra("packEmojisArray"), new TypeToken<ArrayList<String>>() {
            }.getType());
            for (int i = 0; i < emojisStringArray.size(); i++) {
                HashMap<String, Object> emojisMap = new HashMap<>();
                emojisMap.put("emoji_link", "https://emoji.gg/assets/emoji/" + emojisStringArray.get(i));
                emojisMap.put("slug", emojisStringArray.get(i));
                emojisListMap.add(emojisMap);
            }
            packsRecycler.setAdapter(new Recycler1Adapter(emojisListMap));
        } catch (Exception e) {
            UserInteractions.showCustomSnackBar(getString(R.string.failed_to_load_emojis), this);
            new Handler().postDelayed(() -> {
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }, 3000);
        }
    }

    public void LOGIC_FRONTEND() {
        advancedCorners(background, "#FFFFFF", 40, 40, 0, 0);
        setViewRadius(slider, 90, "#E0E0E0");
        DARK_ICONS(this);
        transparentStatusBar(this);
        rippleRoundStroke(download, "#7289DA", "#687DC8", 25, 0, "#7289DA");
        setMargins(download, 20, 20, 20, getNavigationBarHeight(this) + 20);
    }

    private void setImgURL(final String url, final ImageView image) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading)
                .priority(Priority.HIGH);

        Glide.with(this)
                .load(url)
                .apply(options)
                .into(image);

    }

    private void rotationListener() {
        float scaleFactor = getResources().getDisplayMetrics().density * 60;
        int number = getScreenWidth(this);
        int columns = (int) ((float) number / scaleFactor);
        layoutManager1 = new GridLayoutManager(this, columns);
        packsRecycler.setLayoutManager(layoutManager1);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            float scaleFactor = getResources().getDisplayMetrics().density * 60;
            int screenWidth = getScreenWidth(this);
            int columns = (int) ((float) screenWidth / scaleFactor);
            layoutManager1 = new GridLayoutManager(this, columns);
            packsRecycler.setLayoutManager(layoutManager1);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            float scaleFactor = getResources().getDisplayMetrics().density * 60;
            int number = getScreenWidth(this);
            int columns = (int) ((float) number / scaleFactor);
            layoutManager1 = new GridLayoutManager(this, columns);
            packsRecycler.setLayoutManager(layoutManager1);
        }
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
                            shadAnim(coordinator, "alpha", 0, 200);

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


    private void startPackDownload(String name, String path, String url) {
        if (!isDownloading) {
            isDownloading = true;
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
        PRDownloader.download(url, path, name)
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
                        downloadPackPosition++;
                        download_tv.setText(getString(R.string.pack_downloading_progress).concat(" ") + (int) downloadPackPosition + "/" + downloadPackArrayList.size());
                        downloadPack(new Gson().toJson(downloadPackArrayList), tempPackName);
                        MediaScannerConnection.scanFile(PackPreviewActivity.this,
                                new String[]{path}, null,
                                (path1, uri) -> {

                                });
                    }

                    @Override
                    public void onError(Error error) {
                        isDownloading = false;
                        download_tv.setText(R.string.download_btn_txt);
                        download_ic.setImageResource(R.drawable.round_get_app_white_48dp);
                        showCustomSnackBar(getString(R.string.error_msg_2), PackPreviewActivity.this);
                        download_ic.setRotation((float) (0));
                        downAnim.cancel();
                    }
                });

    }

    public void downloadPack(String array, String packName) {
        downloadPackArrayList = new Gson().fromJson(array, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (downloadPackPosition == downloadPackArrayList.size()) {
            if (isGoingToZipPack) {
                new zippingTask().execute();
            } else {
                isDownloading = false;
                isPackDownloaded = true;
                download_tv.setText(R.string.download_success);
                download_ic.setImageResource(R.drawable.round_done_white_48dp);
                download_ic.setRotation(0);
                showCustomSnackBar(getString(R.string.full_download_path) + " " + downloadPackPath, this);
                downAnim.cancel();
            }
        } else {
            String downloadPackUrl = "https://emoji.gg/assets/emoji/" + downloadPackArrayList.get((int) (downloadPackPosition));
            String downloadPackName = "Bemoji_" + downloadPackArrayList.get((int) downloadPackPosition);
            if (isGoingToZipPack) {
                downloadPackPath = FileUtil.getPackageDataDir(getApplicationContext()) + "/Zipper/" + packName;
            } else {
                downloadPackPath = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS) + ("/Bemojis/" + packName);
            }
            startPackDownload(downloadPackName, downloadPackPath, downloadPackUrl);
        }
    }

    public void askForZippingSheet() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED || androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            showCustomSnackBar(getString(R.string.ask_for_permission), this);
        } else {
            final com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this, R.style.materialsheet);

            View bottomSheetView;
            bottomSheetView = getLayoutInflater().inflate(R.layout.infosheet, null);
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
            rippleRoundStroke(infook, "#7289DA", "#6275BB", 20, 0, "#007EEF");
            rippleRoundStroke(infocancel, "#424242", "#181818", 20, 0, "#007EEF");
            setViewRadius(slider, 180, "#BDBDBD");
            infotitle.setText(R.string.pack_confirmation_sheet_title);
            infosub.setText(R.string.pack_confirmation_sheet_subtitle);
            infook.setText(R.string.pack_confirmation_sheet_btn1);
            infocancel.setText(R.string.pack_confirmation_sheet_btn2);
            image.setImageResource(R.drawable.files_and_folder_flatline);
            infook.setOnClickListener(v -> {
                isGoingToZipPack = true;
                downloadPack(packEmojisArrayString, tempPackName);
                bottomSheetDialog.dismiss();
            });
            infocancel.setOnClickListener(v -> {
                isGoingToZipPack = false;
                downloadPack(packEmojisArrayString, tempPackName);
                bottomSheetDialog.dismiss();
            });
            if (!isFinishing()) {
                bottomSheetDialog.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                download.performClick();
            } else {
                showCustomSnackBar(getString(R.string.permission_denied_packs), this);

            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    private class zippingTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            ZIP(downloadPackPath, FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS) + "/Bemojis/" + tempPackName + ".zip");
            downloadPackPath = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS) + "/Bemojis/" + tempPackName + ".zip";
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(String _result) {
            isDownloading = false;
            download_tv.setText(R.string.download_success);
            download_ic.setImageResource(R.drawable.round_done_white_48dp);
            download_ic.setRotation((float) (0));
            showCustomSnackBar(R.string.full_download_path + downloadPackPath + ".zip", PackPreviewActivity.this);
            downAnim.cancel();
            FileUtil.deleteFile(downloadPackPath);
        }
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
            View v = inflater.inflate(R.layout.emojisview, null);
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

            setImgURL(Objects.requireNonNull(data.get(position).get("emoji_link")).toString(), emoji);
            emojisBackground.setOnClickListener(_view -> {
                try {
                    showEmojiSheet(PackPreviewActivity.this, Objects.requireNonNull(data.get(position).get("emoji_link")).toString(), Objects.requireNonNull(data.get(position).get("slug")).toString(), "Emoji lovers");
                } catch (Exception e) {
                    Log.e("downloader", e.toString());
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
