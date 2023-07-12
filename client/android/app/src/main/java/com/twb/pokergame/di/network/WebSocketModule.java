package com.twb.pokergame.di.network;

import static com.twb.pokergame.BuildConfig.API_BASE_URL;

import com.twb.pokergame.data.websocket.WebSocketClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module(includes = {NetworkModule.class})
@InstallIn(SingletonComponent.class)
public class WebSocketModule {

    @Provides
    @Singleton
    public WebSocketClient webSocketClient() {
        return new WebSocketClient(API_BASE_URL);
    }
}
