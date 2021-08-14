package com.nerbly.bemoji.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.nerbly.bemoji.Activities.HomeActivity;
import com.nerbly.bemoji.R;

import java.util.Timer;
import java.util.TimerTask;


public class PaymentProcessorFragment extends Fragment {

    private final Timer _timer = new Timer();

    private boolean isPurchased = false;
    private boolean isFirstView = false;

    private LinearProgressIndicator progress;
    private ProgressBar progressbar1;
    private WebView webview;
    private SwipeRefreshLayout swiperefreshlayout;

    private TimerTask waitThenHome;

    private Intent tohome = new Intent();
    private TimerTask timer;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {
        View _view = _inflater.inflate(R.layout.payment_processor, _container, false);
        initialize(_view);
        initializeLogic();
        return _view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initialize(View view) {

        webview = view.findViewById(R.id.webview);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);


        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.equals("https://nerbly.com/updatify/payment/paymentok.html")) {
                    if (isFirstView) {
                        isPurchased = true;
                        isFirstView = false;

                        Toast.makeText(getActivity(), "Payment successful, thank you!", Toast.LENGTH_SHORT).show();
                        waitThenHome = new TimerTask() {
                            @Override
                            public void run() {
                                requireActivity().runOnUiThread((Runnable) () -> {
                                    tohome.setClass(getActivity(), HomeActivity.class);
                                    startActivity(tohome);
                                });
                            }
                        };
                        _timer.schedule(waitThenHome, (int) (1000));
                    } else {
                        isPurchased = false;
                        Toast.makeText(getActivity(), "You already purchased slots, you'll be redirected to home shortly", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (url.equals("https://nerbly.com/updatify/payment/paymentfail.html")) {
                        Toast.makeText(getActivity(), "Payment cancelled by user", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                swiperefreshlayout.setRefreshing(false);
                if (url.equals("https://nerbly.com/payment/updatify/paymentok.html")) {
                    waitThenHome = new TimerTask() {
                        @Override
                        public void run() {
                            requireActivity().runOnUiThread((Runnable) () -> {
                                tohome.setClass(getActivity(), HomeActivity.class);
                                startActivity(tohome);
                            });
                        }
                    };
                    _timer.schedule(waitThenHome, (int) (2000));
                } else {
                    if (url.equals("https://nerbly.com/payment/updatify/paymentfail.html")) {
                        Toast.makeText(getActivity(), "Payment failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webview.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
            }
        });

        swiperefreshlayout.setOnRefreshListener(() -> {
            swiperefreshlayout.setRefreshing(false);
        });
    }


    private void initializeLogic() {
        isPurchased = false;
        isFirstView = false;
        webviewSettings(webview);
        webview.loadUrl("https://nerbly.com/bemoji/payment/pay.html");
    }

    public void webviewSettings(final WebView webview) {
        try {
            webview.getSettings().setUseWideViewPort(true);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setLoadWithOverviewMode(true);
            webview.getSettings().setBuiltInZoomControls(true);
            webview.getSettings().setSupportZoom(true);
            webview.getSettings().setDisplayZoomControls(false);
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webview.getSettings().setDomStorageEnabled(true);
            webview.getSettings().setSaveFormData(true);
            webview.getSettings().setAllowContentAccess(true);
            webview.getSettings().setAllowFileAccess(true);
            webview.getSettings().setAllowFileAccessFromFileURLs(true);
            webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        } catch (Exception e) {
        }
    }
}
