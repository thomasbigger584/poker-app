package com.twb.pokergame.rest;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    //    public static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfTUFOQUdFUixST0xFX1NUQUZGLFJPTEVfVVNFUiIsImV4cCI6MTYwMjE4ODM0OH0.X4ZgYEV5fxaQGb6DqKn6GFAwBlwvfQ6yIo7nUWRDX6Bj8bmXboxJe2ajC9wenuusYikHpHxN60ePZRnanvi38Q";
    public static final String TOKEN = null;
    public static final String URL = "192.168.1.70";
    public static final String SERVER_PORT = "8081";
    private static final String TAG = RestClient.class.getSimpleName();
    private static final Object lock = new Object();
    private static RestClient instance;
    private final ServiceRepository mServiceRepository;

    private RestClient() {

        HttpLoggingInterceptor loggingInterceptor =
                new HttpLoggingInterceptor(message -> Log.i(TAG, message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().
                addInterceptor(loggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://" + URL + ":" + SERVER_PORT + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        mServiceRepository = retrofit.create(ServiceRepository.class);
    }

    public static RestClient getInstance() {
        RestClient instance = RestClient.instance;
        if (instance == null) {
            synchronized (lock) {
                instance = RestClient.instance;
                if (instance == null) {
                    RestClient.instance = instance = new RestClient();
                }
            }
        }
        return instance;
    }

    public ServiceRepository getServiceRepository() {
        return mServiceRepository;
    }
}
