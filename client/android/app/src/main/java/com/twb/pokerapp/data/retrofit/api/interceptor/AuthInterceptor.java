package com.twb.pokerapp.data.retrofit.api.interceptor;

import com.twb.pokerapp.data.auth.AuthService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        var builder = chain.request().newBuilder();
        var accessToken = authService.getAccessTokenWithRefresh();
        builder.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken);
        return chain.proceed(builder.build());
    }
}
