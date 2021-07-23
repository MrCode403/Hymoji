package com.nerbly.bemoji.Functions;

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
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.nerbly.bemoji.R;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.nerbly.bemoji.Functions.SideFunctions.setBlurImageUrl;
import static com.nerbly.bemoji.Functions.SideFunctions.setImageFromUrlForSheet;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

public class DownloaderSheet extends AppCompatActivity {
    public static void showEmojiSheet(Context context, String url, String name, String publisher) {
        Activity activity = (Activity) context;

        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(context, R.style.materialsheet);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bottomSheetView = inflater.inflate(R.layout.previewemoji, null);


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

        MaterialButton btn_download = bottomSheetView.findViewById(R.id.btn_download);
        MaterialButton btn_share = bottomSheetView.findViewById(R.id.btn_share);
        MaterialTextView emoji_title = bottomSheetView.findViewById(R.id.emoji_title);
        ImageView emoji = bottomSheetView.findViewById(R.id.emoji);
        ImageView emoji_background = bottomSheetView.findViewById(R.id.emoji_background);
        MaterialTextView emoji_publisher = bottomSheetView.findViewById(R.id.emoji_publisher);
        LinearLayout relativeView = bottomSheetView.findViewById(R.id.relativeView);


        emoji_publisher.setText(context.getString(R.string.submitted_by).concat(" " + publisher));
        emoji_title.setText(name);
        setBlurImageUrl(emoji_background, 25, url);
        setImageFromUrlForSheet(emoji, url);
        advancedCorners(relativeView, "#ffffff", 38, 38, 0, 0);
        rippleRoundStroke(btn_download, "#7289DA", "#6275BB", 20, 0, "#007EEF");

        btn_download.setOnClickListener(_view -> {
            if (Build.VERSION.SDK_INT >= 29) {
                downloadFile(context, url, name);
                showCustomSnackBar(context.getString(R.string.downloading_sheet_msg), (Activity) context);
                bottomSheetDialog.dismiss();
            } else {
                if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED) {
                    androidx.core.app.ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 999);
                    showCustomSnackBar(context.getString(R.string.ask_for_permission), (Activity) context);
                } else {
                    downloadFile(context, url, name);
                    showCustomSnackBar(context.getString(R.string.downloading_sheet_msg), (Activity) context);
                    bottomSheetDialog.dismiss();
                }
            }

        });
        btn_share.setOnClickListener(_view -> {
            shareEmojiLink(context, url, context.getString(R.string.share_title));
            bottomSheetDialog.dismiss();
        });
        if (!activity.isFinishing()) {
            bottomSheetDialog.show();
        }
    }

    private static void downloadFile(Context context, String url, String name) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(context.getContentResolver().getType(Uri.parse(url)));
                request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url));
                request.setDescription(context.getString(R.string.app_name));
                request.setTitle("Bemoji_" + URLUtil.guessFileName(url, "", ""));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Bemojis/Bemoji_" + URLUtil.guessFileName(url, "", ""));
                downloadmanager.enqueue(request);

                handler.post(() -> {
                });
            } catch (Exception e) {
                Log.e("download error", e.toString());
            }
        });
    }

    public static void shareEmojiLink(Context context, String emoji_link, String intent_text) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", context.getString(R.string.share_main_text));
        intent.putExtra("android.intent.extra.TEXT", emoji_link);
        context.startActivity(Intent.createChooser(intent, intent_text));
    }
}
