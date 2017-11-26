package com.antondon.vktestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (VKSdk.isLoggedIn()) {
            startVideoFeedActivity();
        }

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!VKSdk.isLoggedIn()) {
                    VKSdk.login(MainActivity.this, "wall", "friends", "video");
                } else {
                    startVideoFeedActivity();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(final VKAccessToken res) {
                // User passed Authorization
                startVideoFeedActivity();
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
                Log.d(TAG, "onError: " + error.toString());
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void startVideoFeedActivity() {
        Intent intent = new Intent();
        intent.setClass(this, VideoFeedActivity.class);
        startActivity(intent);
    }
}
