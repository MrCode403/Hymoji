package com.nerbly.bemoji;

import static com.nerbly.bemoji.Functions.MainFunctions.getScreenWidth;
import static com.nerbly.bemoji.Functions.Utils.ZIP;
import static com.nerbly.bemoji.UI.MainUIMethods.*;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Functions.FileUtil;
import com.nerbly.bemoji.UI.MainUIMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class PackPreviewActivity extends AppCompatActivity {
    private final Timer _timer = new Timer();
    private final ArrayList<HashMap<String, Object>> emojisListMap = new ArrayList<>();
    private final Intent toPreview = new Intent();
    private final ObjectAnimator downAnim = new ObjectAnimator();
    BottomSheetBehavior sheetBehavior;
    GridLayoutManager layoutManager1 = new GridLayoutManager(this, 3);
    com.google.android.material.snackbar.Snackbar _snackBarView;
    com.google.android.material.snackbar.Snackbar.SnackbarLayout _sblayout;
    private HashMap<String, Object> emojisMap = new HashMap<>();
    private boolean isDownloading = false;
    private double downloadPackPosition = 0;
    private String tempPackName = "";
    private boolean isGoingToZipPack = false;
    private String downloadPackPath = "";
    private String downloadPackUrl = "";
    private String downloadPackName = "";
    private String packEmojisArrayString = "";
    private String downloadPath = "";
    private ArrayList<String> emojisStringArray = new ArrayList<>();
    private ArrayList<String> downloadPackArrayList = new ArrayList<>();
    private CoordinatorLayout linear1;
    private LinearLayout download;
    private LinearLayout bsheetbehavior;
    private LinearLayout background;
    private LinearLayout slider;
    private TextView title;
    private TextView textview4;
    private RecyclerView recycler1;
    private ImageView imageview6;
    private TextView textview3;
    private SharedPreferences sharedPref;
    private TimerTask fixUIIssues;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.packpreview);
        initialize(_savedInstanceState);
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize(Bundle _savedInstanceState) {
        linear1 = findViewById(R.id.linear1);
        download = findViewById(R.id.download);
        bsheetbehavior = findViewById(R.id.bsheetbehavior);
        background = findViewById(R.id.background);
        slider = findViewById(R.id.slider);
        title = findViewById(R.id.title);
        textview4 = findViewById(R.id.textview4);
        recycler1 = findViewById(R.id.recycler1);
        imageview6 = findViewById(R.id.imageview6);
        textview3 = findViewById(R.id.textview3);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                shadAnim(download, "translationY", 200, 200);
                shadAnim(download, "alpha", 0, 200);

                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (!isDownloading && !textview3.getText().toString().contains("Saved")) {
                    if (sharedPref.getString("downloadPath", "").equals("")) {
                        downloadPath = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS) + "/Bemojis";
                    } else {
                        downloadPath = sharedPref.getString("downloadPath", "");
                    }
                    _askForZippingSheet();
                }
            }
        });
    }

    private void initializeLogic() {
        _LOGIC_FRONTEND();
        _LOGIC_BACKEND();
    }

    @Override
    public void onBackPressed() {
        shadAnim(download, "translationY", 200, 200);
        shadAnim(download, "alpha", 0, 200);

        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void _LOGIC_BACKEND() {
        sheetBehavior = BottomSheetBehavior.from(bsheetbehavior);
        title.setText(getIntent().getStringExtra("packName"));
        _rotationListener();
        _BottomSheetBehaviorListener();
        tempPackName = getIntent().getStringExtra("packName");
        packEmojisArrayString = getIntent().getStringExtra("packEmojisArray");
        emojisStringArray = new Gson().fromJson(getIntent().getStringExtra("packEmojisArray"), new TypeToken<ArrayList<String>>() {
        }.getType());
        for (int i = 0; i < emojisStringArray.size(); i++) {
            emojisMap = new HashMap<>();
            emojisMap.put("emoji_link", "https://emoji.gg/assets/emoji/" + emojisStringArray.get(i));
            emojisMap.put("slug", emojisStringArray.get(i));
            emojisListMap.add(emojisMap);
        }
        recycler1.setAdapter(new Recycler1Adapter(emojisListMap));
    }

    public void _LOGIC_FRONTEND() {
       advancedCorners(background, "#FFFFFF", 40, 40, 0, 0);
       setViewRadius(slider, 90, "#E0E0E0");
        DARK_ICONS(this);
        transparentStatusBar(this);
        rippleRoundStroke(download, "#7289DA", "#687DC8", 25, 0, "#7289DA");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
        textview4.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
        textview3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
    }

    public void _setImgURL(final String _url, final ImageView _image) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading)
                .priority(Priority.HIGH);

        Glide.with(this)
                .load(_url)
                .apply(options)
                .into(_image);

    }

    public void _rotationListener() {
        float scalefactor = getResources().getDisplayMetrics().density * 60;

        int number = getScreenWidth(this);

        int columns = (int) ((float) number / scalefactor);

        layoutManager1 = new GridLayoutManager(this, columns);

        recycler1.setLayoutManager(layoutManager1);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


            float scalefactor = getResources().getDisplayMetrics().density * 60;

            int number = getScreenWidth(this);

            int columns = (int) ((float) number / scalefactor);

            layoutManager1 = new GridLayoutManager(this, columns);

            recycler1.setLayoutManager(layoutManager1);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            float scalefactor = getResources().getDisplayMetrics().density * 60;

            int number = getScreenWidth(this);

            int columns = (int) ((float) number / scalefactor);

            layoutManager1 = new GridLayoutManager(this, columns);


            recycler1.setLayoutManager(layoutManager1);
        }
    }

    public void _BottomSheetBehaviorListener() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (isDownloading) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {
                        shadAnim(linear1, "alpha", 0, 200);
                        fixUIIssues = new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }
                        };
                        _timer.schedule(fixUIIssues, 150);
                    }
                } else {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        shadAnim(background, "elevation", 20, 200);
                        shadAnim(slider, "translationY", 0, 200);
                        shadAnim(slider, "alpha", 1, 200);
                        shadAnim(download, "translationY", 0, 200);
                        shadAnim(download, "alpha", 1, 200);
                        slider.setVisibility(View.VISIBLE);
                    } else {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            shadAnim(background, "elevation", 0, 200);
                            shadAnim(slider, "translationY", -200, 200);
                            shadAnim(slider, "alpha", 0, 200);
                            shadAnim(download, "translationY", 0, 200);
                            shadAnim(download, "alpha", 1, 200);
                            slider.setVisibility(View.INVISIBLE);
                        } else {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                shadAnim(background, "elevation", 20, 200);
                                shadAnim(slider, "translationY", 0, 200);
                                shadAnim(slider, "alpha", 1, 200);
                                slider.setVisibility(View.VISIBLE);
                                if (!isDownloading) {
                                    shadAnim(download, "translationY", 200, 200);
                                    shadAnim(download, "alpha", 0, 200);
                                }
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


    public void _startPackDownload(final String _name, final String _path, final String _url) {
        if (!isDownloading) {
            isDownloading = true;
            textview3.setText(R.string.pack_downloading_txt);
            imageview6.setImageResource(R.drawable.loadingimg);
            downAnim.setTarget(imageview6);
            downAnim.setPropertyName("rotation");
            downAnim.setFloatValues((float) (1000));
            downAnim.setRepeatCount(999);
            downAnim.setDuration(1000);
            downAnim.setRepeatMode(ValueAnimator.REVERSE);
            downAnim.start();
        }
        PRDownloader.download(_url, _path, _name)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        downloadPackPosition++;
                        textview3.setText(getString(R.string.pack_downloading_progress) + (long) (downloadPackPosition) + "/" + (long) (downloadPackArrayList.size()));
                        _downloadPack(new Gson().toJson(downloadPackArrayList), tempPackName);

                    }

                    @Override
                    public void onError(Error error) {


                        isDownloading = false;
                        textview3.setText(R.string.download_btn_txt);
                        imageview6.setImageResource(R.drawable.round_get_app_white_48dp);
                        _showCustomSnackBar("Something went wrong, please try again later.");
                        imageview6.setRotation((float) (0));
                        downAnim.cancel();
                    }
                });

    }

    public void _downloadPack(final String _array, final String _packname) {
        downloadPackArrayList = new Gson().fromJson(_array, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (downloadPackPosition == downloadPackArrayList.size()) {
            if (isGoingToZipPack) {
                new zippingTask().execute("");
            } else {
                isDownloading = false;
                textview3.setText(R.string.download_success_txt);
                imageview6.setImageResource(R.drawable.round_done_white_48dp);
                imageview6.setRotation((float) (0));
                _showCustomSnackBar("Full download path: " + downloadPackPath);
                downAnim.cancel();
            }
        } else {
            downloadPackUrl = "https://emoji.gg/assets/emoji/" + downloadPackArrayList.get((int) (downloadPackPosition));
            downloadPackPath = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS) + ("/Bemojis/" + _packname);
            downloadPackName = "Bemoji_" + downloadPackArrayList.get((int) (downloadPackPosition));
            _startPackDownload(downloadPackName, downloadPackPath, downloadPackUrl);
        }
    }

    public void _askForZippingSheet() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED || androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            _showCustomSnackBar("Please give us the permission to access your storage to continue");
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

            final TextView infosub = bottomSheetView.findViewById(R.id.infosheet_sub);

            final LinearLayout infoback = bottomSheetView.findViewById(R.id.infosheet_back);

            final LinearLayout slider = bottomSheetView.findViewById(R.id.slider);

            final LinearLayout btnsback = bottomSheetView.findViewById(R.id.infosheet_btnsholder);
            advancedCorners(infoback, "#ffffff", 38, 38, 0, 0);
            rippleRoundStroke(infook, "#7289DA", "#6275BB", 20, 0, "#007EEF");
            rippleRoundStroke(infocancel, "#424242", "#181818", 20, 0, "#007EEF");
            setViewRadius(slider, 180, "#BDBDBD");
            infotitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.BOLD);
            infosub.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
            infook.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
            infocancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
            infotitle.setText(R.string.pack_confirmation_sheet_title);
            infosub.setText(R.string.pack_confirmation_sheet_subtitle);
            infook.setText(R.string.pack_confirmation_sheet_btn1);
            infocancel.setText(R.string.pack_confirmation_sheet_btn2);
            image.setImageResource(R.drawable.files_and_folder_flatline);
            infook.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    isGoingToZipPack = true;
                    _downloadPack(packEmojisArrayString, tempPackName);
                    bottomSheetDialog.dismiss();
                }
            });
            infocancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    isGoingToZipPack = false;
                    _downloadPack(packEmojisArrayString, tempPackName);
                    bottomSheetDialog.dismiss();
                }
            });
            if (!isFinishing()) {
                bottomSheetDialog.show();
            }
        }
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
        setViewRadius(back, 20, "#202125");
        text.setText(_text);
        text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf"), Typeface.NORMAL);
        _sblayout.addView(_inflate, 0);
        _snackBarView.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                download.performClick();
            } else {
                _showCustomSnackBar("You can't download packs without storage access permission. Please allow it.");
            }
        }
    }

    private class zippingTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            String _param = params[0];
            ZIP(downloadPackPath, FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS) + "/Bemojis/" + tempPackName + ".zip");
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(String _result) {
            isDownloading = false;
            textview3.setText(R.string.pack_download_success);
            imageview6.setImageResource(R.drawable.round_done_white_48dp);
            imageview6.setRotation((float) (0));
            _showCustomSnackBar("Full download path: " + downloadPackPath + ".zip");
            downAnim.cancel();
            FileUtil.deleteFile(downloadPackPath);
        }
    }

    public class Recycler1Adapter extends RecyclerView.Adapter<Recycler1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;

        public Recycler1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.emojisview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, @SuppressLint("RecyclerView") final int _position) {
            View _view = _holder.itemView;

            final LinearLayout linear1 = _view.findViewById(R.id.linear1);
            final LinearLayout linear2 = _view.findViewById(R.id.linear2);
            final ImageView imageview1 = _view.findViewById(R.id.imageview1);

            _setImgURL(Objects.requireNonNull(_data.get(_position).get("emoji_link")).toString(), imageview1);
            linear1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    toPreview.putExtra("switchType", "emoji");
                    toPreview.putExtra("title", Objects.requireNonNull(_data.get(_position).get("slug")).toString());
                    toPreview.putExtra("submitted_by", "Emojis lovers");
                    toPreview.putExtra("category", "null");
                    toPreview.putExtra("fileName", Objects.requireNonNull(_data.get(_position).get("slug")).toString());
                    toPreview.putExtra("description", "null");
                    toPreview.putExtra("imageUrl", Objects.requireNonNull(_data.get(_position).get("emoji_link")).toString());
                    toPreview.setClass(getApplicationContext(), PreviewActivity.class);
                    startActivity(toPreview);
                }
            });
            if (_position == (_data.size() - 1)) {
                linear2.setVisibility(View.VISIBLE);
            } else {
                linear2.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return _data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

    }
}
