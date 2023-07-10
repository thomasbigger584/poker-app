package com.twb.pokergame.di.application.network;

import com.twb.pokergame.data.websocket.WebSocketClient;
import com.twb.pokergame.di.application.network.qualifiers.Unauthenticated;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module(includes = {NetworkModule.class})
@InstallIn(SingletonComponent.class)
public class WebSocketModule {

    @Provides
    @Singleton
    public WebSocketClient webSocketClient(@Unauthenticated Retrofit retrofit) {
        String baseUrl = retrofit.baseUrl().toString();
        return new WebSocketClient(baseUrl);
    }
}
