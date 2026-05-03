package com.twb.pokerapp;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;

import androidx.annotation.NonNull;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class PokerApplication extends MultiDexApplication {
    private static boolean isAppInForeground = false;

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onStart(@NonNull LifecycleOwner owner) {
                isAppInForeground = true;
            }

            @Override
            public void onStop(@NonNull LifecycleOwner owner) {
                isAppInForeground = false;
            }
        });
    }

    public static boolean isAppInForeground() {
        return isAppInForeground;
    }
}
