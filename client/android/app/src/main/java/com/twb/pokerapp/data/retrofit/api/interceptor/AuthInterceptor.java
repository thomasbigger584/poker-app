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
        var request = chain.request();
        var accessToken = authService.getAccessTokenWithRefresh();
        if (accessToken != null) {
            request = request.newBuilder()
                    .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                    .build();
        }
        return chain.proceed(request);
    }
}
