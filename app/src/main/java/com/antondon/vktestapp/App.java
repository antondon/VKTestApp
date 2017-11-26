package com.antondon.vktestapp;

import android.app.Application;

import com.vk.sdk.VKSdk;

/**
 * Created by anton on 11/22/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
