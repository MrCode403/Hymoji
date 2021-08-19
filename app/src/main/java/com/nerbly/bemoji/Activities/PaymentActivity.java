package com.nerbly.bemoji.Activities;


import static com.nerbly.bemoji.Configurations.PAYMENT_SOURCE;
import static com.nerbly.bemoji.Functions.MainFunctions.loadLocale;
import static com.nerbly.bemoji.UI.MainUIMethods.DARK_ICONS;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.R;

public class PaymentActivity extends AppCompatActivity {
    public BottomSheetBehavior sheetBehavior;
    private MaterialButton premium_go;
    private TextView payment_holder_text;
    private WebView webview;
    private boolean isPurchased = false;
    private boolean isFirstView = false;
    private final boolean isTryingToPurchase = false;
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
        LinearLayout bottomsheet = findViewById(R.id.bottom_sheet);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);
        sheetBehavior = BottomSheetBehavior.from(bottomsheet);


        premium_go.setOnClickListener(v -> {
            if (sheetBehavior.isDraggable()) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                webview.loadUrl(PAYMENT_SOURCE);

            }
        });

        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.equals("https://nerbly.com/bemoji/payment/payment_success.json")) {
                    if (isFirstView) {
                        isPurchased = true;
                        isFirstView = false;
                        sharedPref.edit().putBoolean("isPremium", true).apply();
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        showThanksBottomSheet();
                    } else {
                        isPurchased = true;
                    }
                } else {
                    if (url.equals("https://nerbly.com/updatify/payment/payment_fail.json")) {
                        showCustomSnackBar(getString(R.string.payment_failed), PaymentActivity.this);
                    }
                }
            }

            public void onPageFinished(WebView view, String url) {

                if (Utils.isConnected(PaymentActivity.this)) {
                    sheetBehavior.setDraggable(true);
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                sheetBehavior.setDraggable(false);
                showCustomSnackBar(getString(R.string.no_internet_connection), PaymentActivity.this);
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
        bottomSheetBehaviorListener();
        isPurchased = false;
        isFirstView = false;
        webViewSettings(webview);
        webview.loadUrl(PAYMENT_SOURCE);
    }

    private void bottomSheetBehaviorListener() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        webview.loadUrl(PAYMENT_SOURCE);
                        LIGHT_ICONS(PaymentActivity.this);
                        premium_go.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        DARK_ICONS(PaymentActivity.this);
                        premium_go.setVisibility(View.INVISIBLE);
                        payment_holder_text.setText(R.string.payments_powered_by);
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset < 0.4) {
                    payment_holder_text.setText(R.string.payment_leaving);
                } else if (slideOffset > 0.4) {
                    payment_holder_text.setText(R.string.payments_powered_by);
                }
            }
        });
    }

    public void LOGIC_FRONTEND() {
        LIGHT_ICONS(this);
        transparentStatusBar(this);
        marqueeTextView(payment_holder_text);
    }

    @Override
    public void onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        } else if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
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