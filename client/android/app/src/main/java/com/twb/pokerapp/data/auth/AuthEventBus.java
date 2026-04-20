package com.twb.pokerapp.data.auth;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AuthEventBus {
    private static final MutableLiveData<Boolean> logoutEvent = new MutableLiveData<>();

    public static LiveData<Boolean> getLogoutEvent() {
        return logoutEvent;
    }

    public static void triggerLogout() {
        new Handler(Looper.getMainLooper()).post(() -> logoutEvent.setValue(true));
    }

    public static void resetLogoutEvent() {
        new Handler(Looper.getMainLooper()).post(() -> logoutEvent.setValue(false));
    }
}
