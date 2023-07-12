package com.twb.pokergame.di.network;

import static com.twb.pokergame.BuildConfig.API_BASE_URL;

import com.twb.pokergame.data.auth.AuthStateManager;
import com.twb.pokergame.data.websocket.WebSocketClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module(includes = {AuthModule.class})
@InstallIn(SingletonComponent.class)
public class WebSocketModule {

    @Provides
    @Singleton
    public WebSocketClient webSocketClient(AuthStateManager authStateManager) {
        return new WebSocketClient(API_BASE_URL, authStateManager);
    }
}
