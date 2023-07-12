package com.twb.pokergame.di.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.twb.pokergame.data.retrofit.api.interceptor.AuthInterceptor;
import com.twb.pokergame.di.network.qualifiers.Authenticated;
import com.twb.pokergame.di.network.qualifiers.Unauthenticated;

import java.io.File;
import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    private static final String BASE_URL = "http://192.168.0.118:8081";

    private static final String OKHTTP_CACHE_FILE = "okhttp_cache";
    private static final int MAX_CACHE_SIZE = 10 * 1000 * 1000; //10MB Cache

    @Provides
    @Singleton
    @Unauthenticated
    public Retrofit retrofitUnAuthenticated(@Unauthenticated OkHttpClient okHttpClient, Gson gson) {
        return getRetrofit(okHttpClient, gson);
    }

    @Provides
    @Singleton
    @Unauthenticated
    public OkHttpClient okHttpClientUnAuthenticated(HttpLoggingInterceptor loggingInterceptor, Cache cache) {
        return getOkHttpClientBuilder(loggingInterceptor, cache).build();
    }

    @Provides
    @Singleton
    @Authenticated
    public Retrofit retrofitAuthenticated(@Authenticated OkHttpClient okHttpClient, Gson gson) {
        return getRetrofit(okHttpClient, gson);
    }

    @Provides
    @Singleton
    @Authenticated
    public OkHttpClient okHttpClientAuthenticated(HttpLoggingInterceptor loggingInterceptor,
                                                  Cache cache, AuthInterceptor authInterceptor) {
        OkHttpClient.Builder builder = getOkHttpClientBuilder(loggingInterceptor, cache);
        builder.addInterceptor(authInterceptor);
        return builder.build();
    }

    @NonNull
    private Retrofit getRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .build();
    }

    @NonNull
    private OkHttpClient.Builder getOkHttpClientBuilder(HttpLoggingInterceptor loggingInterceptor, Cache cache) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .cache(cache);
    }

    @Provides
    @Singleton
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
            //todo: verify this works with dates which get returned
            return new Date((long) (json.getAsJsonPrimitive().getAsDouble() * 1000));
        });
        gsonBuilder.setPrettyPrinting();
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    public HttpLoggingInterceptor loggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor =
                new HttpLoggingInterceptor(message -> Log.i(NetworkModule.class.getSimpleName(), message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    @Provides
    @Singleton
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }

    @Provides
    @Singleton
    public Cache cache(@ApplicationContext Context context) {
        File file = new File(context.getCacheDir(), OKHTTP_CACHE_FILE);
        return new Cache(file, MAX_CACHE_SIZE);
    }
}
