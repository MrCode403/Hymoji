package com.nerbly.bemoji.UI;

import static com.nerbly.bemoji.Functions.MainFunctions.capitalizedFirstWord;
import static com.nerbly.bemoji.Functions.SideFunctions.setBlurImageUrl;
import static com.nerbly.bemoji.Functions.SideFunctions.setImageFromUrlForSheet;
import static com.nerbly.bemoji.Functions.Utils.formatEmojiName;
import static com.nerbly.bemoji.Functions.Utils.isStoragePermissionGranted;
import static com.nerbly.bemoji.Functions.Utils.requestStoragePermission;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.marqueeTextView;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.nerbly.bemoji.Activities.EmojisActivity;
import com.nerbly.bemoji.R;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloaderSheet extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    public void showEmojiSheet(Context context, String url, String name, String publisher) {

        Activity activity = (Activity) context;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.materialsheet);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bottomSheetView = inflater.inflate(R.layout.previewemoji, (ViewGroup) null);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

        MaterialButton btn_download = bottomSheetView.findViewById(R.id.btn_download);
        MaterialButton btn_share = bottomSheetView.findViewById(R.id.btn_share);
        MaterialTextView emoji_title = bottomSheetView.findViewById(R.id.emoji_title);
        ImageView emoji = bottomSheetView.findViewById(R.id.emoji);
        ImageView emoji_background = bottomSheetView.findViewById(R.id.emoji_background);
        MaterialTextView emoji_publisher = bottomSheetView.findViewById(R.id.emoji_publisher);
        LinearLayout relativeView = bottomSheetView.findViewById(R.id.relativeView);

        marqueeTextView(emoji_title);

        emoji_publisher.setText(context.getString(R.string.submitted_by) + " " + publisher);

        emoji_title.setText(name);
        setBlurImageUrl(emoji_background, 25, url);
        setImageFromUrlForSheet(emoji, url);
        advancedCorners(relativeView, "#ffffff", 38, 38, 0, 0);

        btn_download.setOnClickListener(_view -> {
            if (Build.VERSION.SDK_INT >= 29) {
                downloadFile(context, url);
                showCustomSnackBar(context.getString(R.string.downloading_sheet_msg), (Activity) context);
                bottomSheetDialog.dismiss();
            } else {
                if (isStoragePermissionGranted(context)) {
                    downloadFile(context, url);
                    showCustomSnackBar(context.getString(R.string.downloading_sheet_msg), (Activity) context);
                    bottomSheetDialog.dismiss();
                } else {
                    requestStoragePermission(10, (Activity) context);
                    showCustomSnackBar(context.getString(R.string.ask_for_permission), (Activity) context);
                }
            }

        });
        btn_share.setOnClickListener(_view -> {
            shareEmojiLink(context, url);
            bottomSheetDialog.dismiss();
        });
        if (!activity.isFinishing()) {
            bottomSheetDialog.show();
        }
    }

    private void downloadFile(Context context, String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(context.getContentResolver().getType(Uri.parse(url)));
                request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url));
                request.setDescription(context.getString(R.string.app_name));
                request.setTitle(context.getString(R.string.app_name) + "_" + URLUtil.guessFileName(url, "", ""));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, context.getString(R.string.app_name) + "/" + context.getString(R.string.app_name) + "_" + URLUtil.guessFileName(url, "", ""));
                downloadmanager.enqueue(request);

            } catch (Exception e) {
                Log.e("HYMOJI_EMOJI_PREVIEWER", e.toString());
                e.printStackTrace();
            }

            handler.post(() -> {
                if (context instanceof EmojisActivity) {
                    ((EmojisActivity) context).showInterstitialAd();
                    Log.d("HYMOJI_EMOJI_PREVIEWER", "We're in emojis activity");
                } else {
                    Log.d("HYMOJI_EMOJI_PREVIEWER", "We're not in emojis activity");
                }
            });
        });
    }

    public void shareEmojiLink(Context context, String emoji_link) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", context.getString(R.string.share_main_text));
        intent.putExtra("android.intent.extra.TEXT", emoji_link);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_title)));
        showCustomSnackBar(context.getString(R.string.getting_ready_to_share), (Activity) context);
    }
}
