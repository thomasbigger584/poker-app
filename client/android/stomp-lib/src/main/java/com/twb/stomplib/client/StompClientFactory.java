package com.twb.stomplib.client;

import com.twb.stomplib.client.StompClient;
import com.twb.stomplib.connection.ConnectionProvider;
import com.twb.stomplib.connection.impl.OkHttpConnectionProvider;

import java.util.Map;

import okhttp3.OkHttpClient;

public class StompClientFactory {

    public static StompClient createClient(String uri, Map<String, String> headers) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();

        ConnectionProvider provider = new OkHttpConnectionProvider(uri, headers, okHttpClient);
        return new StompClient(provider);
    }
}