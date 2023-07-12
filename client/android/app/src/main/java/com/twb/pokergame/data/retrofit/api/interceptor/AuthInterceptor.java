package com.twb.pokergame.data.retrofit.api.interceptor;

import android.util.Log;

import com.twb.pokergame.data.auth.AuthStateManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private static final String TAG = AuthInterceptor.class.getSimpleName();
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final AuthStateManager authStateManager;

    public AuthInterceptor(AuthStateManager authStateManager) {
        this.authStateManager = authStateManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        Log.i(TAG, "intercept: authStateManager: " + authStateManager.toString());

        String token = authStateManager.getCurrent().getIdToken();
        Log.i(TAG, "intercept: token: " + token);
        builder.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        return chain.proceed(builder.build());
    }
}
