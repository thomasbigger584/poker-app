package com.twb.stomplib.stomp;

import android.util.Log;

import com.twb.stomplib.connection.ConnectionProvider;
import com.twb.stomplib.connection.impl.OkHttpConnectionProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StompClientFactory {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    public static StompClient createClient(String url, String accessToken) {
        OkHttpClient httpClient = buildHttpClient(accessToken);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken);

        ConnectionProvider provider = new OkHttpConnectionProvider(url, headers, httpClient);
        return new StompClient(provider);
    }

    private static OkHttpClient buildHttpClient(String accessToken) {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addInterceptor(new AuthInterceptor(accessToken))
                .build();
    }

    private static class AuthInterceptor implements Interceptor {
        private static final String TAG = AuthInterceptor.class.getSimpleName();
        private final String accessToken;

        private AuthInterceptor(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();

            Log.i(TAG, "websocket intercept: token: " + accessToken);
            builder.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken);

            return chain.proceed(builder.build());
        }
    }
}