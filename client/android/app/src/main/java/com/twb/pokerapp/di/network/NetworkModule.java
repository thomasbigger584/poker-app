package com.twb.pokerapp.di.network;

import static com.twb.pokerapp.BuildConfig.API_BASE_URL;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.twb.pokerapp.BuildConfig;
import com.twb.pokerapp.data.auth.AuthConfiguration;
import com.twb.pokerapp.data.auth.AuthService;
import com.twb.pokerapp.data.retrofit.api.interceptor.AuthInterceptor;
import com.twb.pokerapp.data.retrofit.gson.ServerMessageDeserializer;
import com.twb.pokerapp.data.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.di.network.qualifiers.Authenticated;
import com.twb.pokerapp.di.network.qualifiers.Unauthenticated;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

@Module(includes = AuthModule.class)
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    private static final String TAG = NetworkModule.class.getSimpleName();
    private static final String OKHTTP_CACHE_FILE = "okhttp_cache";
    private static final int MAX_CACHE_SIZE = 10 * 1024 * 1024; //10MB Cache
    private static final int TIMEOUT_SECONDS = 30;

    @Provides
    @Singleton
    @Unauthenticated
    public Retrofit retrofitUnAuthenticated(@Unauthenticated OkHttpClient okHttpClient, Gson gson, AuthConfiguration authConfiguration) {
        return getRetrofit(okHttpClient, gson, authConfiguration);
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
    public Retrofit retrofitAuthenticated(@Authenticated OkHttpClient okHttpClient, Gson gson, AuthConfiguration authConfiguration) {
        return getRetrofit(okHttpClient, gson, authConfiguration);
    }

    @Provides
    @Singleton
    @Authenticated
    public OkHttpClient okHttpClientAuthenticated(HttpLoggingInterceptor loggingInterceptor,
                                                  Cache cache, AuthInterceptor authInterceptor) {
        var builder = getOkHttpClientBuilder(loggingInterceptor, cache);
        builder.addInterceptor(authInterceptor);
        return builder.build();
    }

    @NonNull
    private Retrofit getRetrofit(OkHttpClient okHttpClient, Gson gson, AuthConfiguration authConfiguration) {
        var protocol = authConfiguration.isHttpsRequired() ? "https://" : "http://";
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .baseUrl(protocol + API_BASE_URL)
                .build();
    }

    @NonNull
    private OkHttpClient.Builder getOkHttpClientBuilder(HttpLoggingInterceptor loggingInterceptor, Cache cache) {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .cache(cache);
    }

    @Provides
    @Singleton
    public Gson gson() {
        var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
            var dateAsDouble = json.getAsJsonPrimitive().getAsDouble();
            try {
                return new Date((long) (dateAsDouble * 1000));
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse date: " + dateAsDouble, e);
                return null;
            }
        });
        gsonBuilder.registerTypeAdapter(ServerMessageDTO.class, new ServerMessageDeserializer());
        if (BuildConfig.DEBUG) {
            gsonBuilder.setPrettyPrinting();
        }
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    public HttpLoggingInterceptor loggingInterceptor() {
        var loggingInterceptor = new HttpLoggingInterceptor(message -> Log.i(NetworkModule.class.getSimpleName(), message));
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        return loggingInterceptor;
    }

    @Provides
    @Singleton
    public AuthInterceptor authInterceptor(AuthService authService) {
        return new AuthInterceptor(authService);
    }

    @Provides
    @Singleton
    public Cache cache(@ApplicationContext Context context) {
        var file = new File(context.getCacheDir(), OKHTTP_CACHE_FILE);
        return new Cache(file, MAX_CACHE_SIZE);
    }
}
