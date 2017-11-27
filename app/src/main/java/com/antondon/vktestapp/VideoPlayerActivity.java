package com.antondon.vktestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.vk.sdk.api.model.VKApiVideo;

public class VideoPlayerActivity extends Activity {

    private WebView webView;
    private FrameLayout fullscreenContainer;
    private View fullscreenView;
    private WebChromeClient.CustomViewCallback fullscreenViewCallback;
    private static final String TAG = "VideoPlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Intent launchIntent = getIntent();
        VKApiVideo video = launchIntent.getParcelableExtra("video");

        if (video != null && video.player != null && !video.player.isEmpty()) {
            initPlayerWebView();
            webView.loadUrl(video.player);
        }
    }

    void initPlayerWebView() {
        fullscreenContainer = findViewById(R.id.fullscreen_container);
        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (fullscreenViewCallback != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                webView.setVisibility(View.GONE);
                fullscreenContainer.setVisibility(View.VISIBLE);
                fullscreenContainer.addView(view);
                fullscreenView = view;
                fullscreenViewCallback = callback;
            }

            @Override
            public void onHideCustomView() {
                fullscreenContainer.removeView(fullscreenView);
                fullscreenViewCallback.onCustomViewHidden();
                fullscreenView = null;
                fullscreenViewCallback = null;

                fullscreenContainer.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        };
        webView.setWebChromeClient(webChromeClient);
    }
}
