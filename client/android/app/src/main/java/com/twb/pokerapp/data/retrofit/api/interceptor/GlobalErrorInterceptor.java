package com.twb.pokerapp.data.retrofit.api.interceptor;

import com.twb.pokerapp.data.auth.AuthEventBus;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Response;

public class GlobalErrorInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        // If we get here and it's still a 401, the Authenticator failed to refresh.
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            AuthEventBus.triggerLogout();
        }
        return response;
    }
}
