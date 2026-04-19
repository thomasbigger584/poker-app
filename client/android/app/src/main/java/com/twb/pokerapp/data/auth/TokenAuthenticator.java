package com.twb.pokerapp.data.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    private final AuthService authService;

    public TokenAuthenticator(AuthService authService) {
        this.authService = authService;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        if (responseCount(response) >= 2) {
            return null;
        }

        String newToken = authService.getAccessTokenWithRefresh();

        if (newToken != null) {
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + newToken)
                    .build();
        }
        return null;
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
