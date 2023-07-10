package com.twb.stomplib.stomp;

import com.twb.stomplib.connection.ConnectionProvider;
import com.twb.stomplib.connection.impl.OkHttpConnectionProvider;

import java.util.Map;

import okhttp3.OkHttpClient;

public class StompClientFactory {

    public static StompClient createClient(String url) {
        OkHttpClient httpClient = buildHttpClient();

        ConnectionProvider provider = new OkHttpConnectionProvider(url, httpClient);
        return new StompClient(provider);
    }

    private static OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
    }
}