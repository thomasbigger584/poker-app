package com.twb.pokergame.di.network;

import com.google.gson.Gson;
import com.twb.pokergame.data.auth.AuthStateManager;
import com.twb.pokergame.data.websocket.WebSocketClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module(includes = NetworkModule.class)
@InstallIn(SingletonComponent.class)
public class WebSocketModule {

    @Provides
    @Singleton
    public WebSocketClient webSocketClient(AuthStateManager authStateManager, Gson gson) {
        return new WebSocketClient(authStateManager, gson);
    }
}
