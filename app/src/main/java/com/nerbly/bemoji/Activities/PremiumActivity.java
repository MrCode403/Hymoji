package com.nerbly.bemoji.Activities;


import static com.nerbly.bemoji.Configurations.PAYMENT_SOURCE;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.Functions.SideFunctions.getNavigationBarHeight;
import static com.nerbly.bemoji.Functions.SideFunctions.getStatusBarHeight;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.MainUIMethods.shadAnim;
import static com.nerbly.bemoji.UI.MainUIMethods.transparentStatusBar;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.R;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class PremiumActivity extends AppCompatActivity {
    private boolean isTryingToPurchase = false;
    private MaterialButton premium_go;
    private WebView webview;
    private boolean isPurchased = false;
    private boolean isUserAbleToBuy = false;
    private boolean isFirstView = true;
    private SharedPreferences sharedPref;
    private ScrollView payment_features_scrollview;
    private RelativeLayout webview_holder;
    private MaterialCardView loading_progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.payment_main);
        initialize();
        initializeLogic();
    }

    private void initialize() {
        premium_go = findViewById(R.id.premium_go);
        loading_progress = findViewById(R.id.loading_progress);
        webview = findViewById(R.id.webview);
        payment_features_scrollview = findViewById(R.id.payment_features_scrollview);
        webview_holder = findViewById(R.id.webview_holder);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);


        premium_go.setOnClickListener(v -> {
            isTryingToPurchase = true;
            shadAnim(webview_holder, "translationY", 0, 300);
            shadAnim(webview_holder, "alpha", 1, 300);
            webview_holder.setVisibility(View.VISIBLE);
            if (!isUserAbleToBuy) {
                shouldShowProgressBar(true);
                webview.loadUrl(PAYMENT_SOURCE);
            }
        });

        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.equals("https://nerbly.com/hymoji/payment/payment_success.json")) {
                    if (isFirstView) {
                        isPurchased = true;
                        isFirstView = false;
                        sharedPref.edit().putBoolean("isPremium", true).apply();
                        showThanksBottomSheet();
                    }
                } else {
                    if (url.equals("https://nerbly.com/hymoji/payment/payment_fail.json")) {
                        showCustomSnackBar(getString(R.string.payment_failed), PremiumActivity.this);
                        webview.loadUrl(PAYMENT_SOURCE);
                        dismissWebView();
                    }
                }
                shouldShowProgressBar(true);
            }

            public void onPageFinished(WebView view, String url) {
                if (Utils.isConnected(PremiumActivity.this)) {
                    isUserAbleToBuy = true;
                }
                shouldShowProgressBar(false);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                isUserAbleToBuy = false;
                showCustomSnackBar(getString(R.string.no_internet_connection) + " " + description, PremiumActivity.this);
                webview.loadUrl(PAYMENT_SOURCE);
                dismissWebView();
                shouldShowProgressBar(false);
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
        webViewSettings(webview);

        new Handler().postDelayed(() -> {
            webview.loadUrl(PAYMENT_SOURCE);
            shouldShowProgressBar(true);
        } ,1000);

    }

    public void LOGIC_FRONTEND() {
        DARK_ICONS(this);
        transparentStatusBar(this);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) premium_go.getLayoutParams();
        params.bottomMargin = getNavigationBarHeight(this) + 10;

        ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) webview.getLayoutParams();
        params1.topMargin = getStatusBarHeight(this) + 10;

        ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) webview.getLayoutParams();
        params2.bottomMargin = getNavigationBarHeight(this);

        OverScrollDecoratorHelper.setUpOverScroll(payment_features_scrollview);

    }

    @Override
    public void onBackPressed() {
        if (webview_holder.getVisibility() == View.VISIBLE) {
            if (webview.canGoBack()) {
                shouldShowProgressBar(true);
                webview.goBack();
            } else {
                dismissWebView();
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

    private void dismissWebView() {
        isTryingToPurchase = false;
        shadAnim(webview_holder, "translationY", 1000, 300);
        shadAnim(webview_holder, "alpha", 0, 300);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            webview_holder.setVisibility(View.GONE);
        }, 400);
    }

    private void shouldShowProgressBar(boolean bool) {
        if (bool) {
            shadAnim(loading_progress, "scaleX", 1, 400);
            shadAnim(loading_progress, "scaleY", 1, 400);
            shadAnim(loading_progress, "alpha", 1, 400);
        } else {
            shadAnim(loading_progress, "scaleX", 0, 400);
            shadAnim(loading_progress, "scaleY", 0, 400);
            shadAnim(loading_progress, "alpha", 0, 400);
        }
    }

    private void showThanksBottomSheet() {
        dismissWebView();

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
}