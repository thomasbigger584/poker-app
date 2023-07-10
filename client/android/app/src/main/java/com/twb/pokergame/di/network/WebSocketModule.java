package com.twb.pokergame.di.network;

import com.twb.pokergame.data.websocket.WebSocketClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module(includes = {NetworkModule.class})
@InstallIn(SingletonComponent.class)
public class WebSocketModule {
    private static final String BASE_URL = "192.168.0.118:8081";

    @Provides
    @Singleton
    public WebSocketClient webSocketClient() {
        return new WebSocketClient(BASE_URL);
    }
}
