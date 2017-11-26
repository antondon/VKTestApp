package com.antondon.vktestapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

            @Override
            public Bitmap getDefaultVideoPoster() {
                return super.getDefaultVideoPoster();
            }

            @Override
            public View getVideoLoadingProgressView() {
                return super.getVideoLoadingProgressView();
            }


        };
        webView.setWebChromeClient(webChromeClient);
        webView.loadUrl(video.player);
        //webView.loadUrl("https://www.youtube.com/embed/dCFUZ43IL5w?__ref=vk.api");
        }
}
