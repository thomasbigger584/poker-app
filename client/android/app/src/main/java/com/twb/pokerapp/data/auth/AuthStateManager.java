/*
 * Copyright 2017 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twb.pokerapp.data.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.RegistrationResponse;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An persistence mechanism for an {@link AuthState} instance.
 * This stores the instance in a shared preferences file, and provides thread-safe access and
 * mutation.
 */
public class AuthStateManager {
    private static final String TAG = AuthStateManager.class.getSimpleName();
    private static final String STORE_NAME = "com.twb.pokerapp.auth.AuthState.store";
    private static final String KEY_STATE = "com.twb.pokerapp.auth.AuthState.keyState";

    private final SharedPreferences prefs;
    private final ReentrantLock prefsLock;
    private final AtomicReference<AuthState> currentAuthState;

    public AuthStateManager(Context context) {
        prefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
        prefsLock = new ReentrantLock();
        currentAuthState = new AtomicReference<>();
    }

    @NonNull
    @AnyThread
    public AuthState getCurrent() {
        if (currentAuthState.get() != null) {
            return currentAuthState.get();
        }
        AuthState state = readState();
        if (currentAuthState.compareAndSet(null, state)) {
            return state;
        } else {
            return currentAuthState.get();
        }
    }

    @AnyThread
    @NonNull
    public AuthState replace(@NonNull AuthState state) {
        writeState(state);
        currentAuthState.set(state);
        return state;
    }

    @AnyThread
    @NonNull
    public AuthState updateAfterAuthorization(@Nullable AuthorizationResponse response,
                                              @Nullable AuthorizationException ex) {
        AuthState current = getCurrent();
        current.update(response, ex);
        return replace(current);
    }

    @AnyThread
    @NonNull
    public AuthState updateAfterTokenResponse(@Nullable TokenResponse response,
                                              @Nullable AuthorizationException ex) {
        AuthState current = getCurrent();
        current.update(response, ex);
        return replace(current);
    }

    @AnyThread
    @NonNull
    public AuthState updateAfterRegistration(RegistrationResponse response,
                                             AuthorizationException ex) {
        AuthState current = getCurrent();
        if (ex != null) {
            return current;
        }
        current.update(response);
        return replace(current);
    }

    @AnyThread
    @NonNull
    private AuthState readState() {
        prefsLock.lock();
        try {
            String currentState = prefs.getString(KEY_STATE, null);
            if (currentState == null) {
                return new AuthState();
            }

            try {
                return AuthState.jsonDeserialize(currentState);
            } catch (JSONException ex) {
                Log.w(TAG, "Failed to deserialize stored auth state - discarding");
                return new AuthState();
            }
        } finally {
            prefsLock.unlock();
        }
    }

    @AnyThread
    private void writeState(@Nullable AuthState state) {
        prefsLock.lock();
        try {
            SharedPreferences.Editor editor = prefs.edit();
            if (state == null) {
                editor.remove(KEY_STATE);
            } else {
                editor.putString(KEY_STATE, state.jsonSerializeString());
            }

            if (!editor.commit()) {
                throw new IllegalStateException("Failed to write state to shared prefs");
            }
        } finally {
            prefsLock.unlock();
        }
    }
}
