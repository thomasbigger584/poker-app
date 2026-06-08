package com.twb.pokerapp.data.auth;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

/**
 * App-wide one-shot logout signal. Backed by {@link SingleLiveEvent} so a logout is consumed
 * exactly once and is never replayed to a screen that starts observing later (which would cause a
 * spurious sign-out on navigation or configuration change). Triggered from anywhere a session is
 * found to be irrecoverable: a refused token refresh, a STOMP 401, or a REST 401 the authenticator
 * could not retry past.
 */
public class AuthEventBus {
    private static final SingleLiveEvent<Boolean> logoutEvent = new SingleLiveEvent<>();

    public static LiveData<Boolean> getLogoutEvent() {
        return logoutEvent;
    }

    public static void triggerLogout() {
        postValue(true);
    }

    public static void resetLogoutEvent() {
        postValue(false);
    }

    private static void postValue(boolean value) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            logoutEvent.setValue(value);
        } else {
            new Handler(Looper.getMainLooper()).post(() -> logoutEvent.setValue(value));
        }
    }
}
