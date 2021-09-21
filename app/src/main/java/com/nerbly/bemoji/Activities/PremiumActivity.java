package com.nerbly.bemoji.Activities;


import static com.nerbly.bemoji.Configurations.PAYMENT_SOURCE;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.LIGHT_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.R;

public class PremiumActivity extends AppCompatActivity {
    private final boolean isTryingToPurchase = false;
    private MaterialButton premium_go;
    private TextView payment_holder_text;
    private WebView webview;
    private boolean isPurchased = false;
    private boolean isUserAbleToBuy = false;
    private boolean isFirstView = false;
    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.payment_main);
        initialize();
        com.google.firebase.FirebaseApp.initializeApp(this);
        initializeLogic();
    }

    private void initialize() {
        premium_go = findViewById(R.id.premium_go);
        payment_holder_text = findViewById(R.id.payment_holder_text);
        webview = findViewById(R.id.webview);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);


        premium_go.setOnClickListener(v -> {
            webview.setVisibility(View.VISIBLE);
        });

        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.equals("https://nerbly.com/bemoji/payment/payment_success.json")) {
                    if (isFirstView) {
                        isPurchased = true;
                        isFirstView = false;
                        sharedPref.edit().putBoolean("isPremium", true).apply();
                        showThanksBottomSheet();
                    } else {
                        isPurchased = true;
                    }
                } else {
                    if (url.equals("https://nerbly.com/updatify/payment/payment_fail.json")) {
                        showCustomSnackBar(getString(R.string.payment_failed), PremiumActivity.this);
                    }
                }
            }

            public void onPageFinished(WebView view, String url) {

                if (Utils.isConnected(PremiumActivity.this)) {
                    isUserAbleToBuy = true;
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                isUserAbleToBuy = false;
                showCustomSnackBar(getString(R.string.no_internet_connection), PremiumActivity.this);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }
        });
    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }

    public void LOGIC_BACKEND() {
        isPurchased = false;
        isFirstView = false;
        webViewSettings(webview);
        webview.loadUrl(PAYMENT_SOURCE);
    }

    public void LOGIC_FRONTEND() {
        LIGHT_ICONS(this);
        transparentStatusBar(this);
    }

    @Override
    public void onBackPressed() {
        if (webview.getVisibility() == View.VISIBLE) {
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                webview.setVisibility(View.GONE);
            }
        } else {
            finish();
        }

    }


    @SuppressLint("SetJavaScriptEnabled")
    public void webViewSettings(final WebView webview) {
        try {
            webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webview.getSettings().setUseWideViewPort(true);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setLoadWithOverviewMode(true);
            webview.getSettings().setBuiltInZoomControls(true);
            webview.getSettings().setSupportZoom(true);
            webview.getSettings().setDisplayZoomControls(false);
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webview.getSettings().setDomStorageEnabled(true);
            webview.getSettings().setSaveFormData(true);
            webview.getSettings().setAllowContentAccess(true);
            webview.getSettings().setAllowFileAccess(true);
            webview.getSettings().setAllowFileAccessFromFileURLs(true);
            webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        } catch (Exception e) {
        }
    }

    private void showThanksBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this, R.style.materialsheet);

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

        infotitle.setText(R.string.payment_success_title);
        infosub.setText(R.string.payment_success_desc);
        infook.setText(R.string.payment_success_positive_btn);
        infocancel.setVisibility(View.GONE);
        image.setImageResource(R.drawable.thanks_premium);

        advancedCorners(infoback, "#ffffff", 38, 38, 0, 0);

        setViewRadius(slider, 180, "#BDBDBD");
        infook.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            finish();
        });
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
    }

    public void marqueeTextView(final TextView view) {
        view.setSingleLine(true);
        view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        view.setSelected(true);
        view.setMarqueeRepeatLimit(-1);
        view.setHorizontalFadingEdgeEnabled(true);
        view.setFadingEdgeLength(20);
    }
}