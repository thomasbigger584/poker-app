package com.twb.pokerapp.di.network;

import com.twb.pokerapp.data.auth.AuthConfiguration;
import com.twb.pokerapp.data.auth.AuthService;
import com.twb.pokerapp.data.websocket.WebSocketClient;
import com.twb.pokerapp.di.network.qualifiers.Authenticated;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;

@Module(includes = NetworkModule.class)
@InstallIn(SingletonComponent.class)
public class WebSocketModule {

    @Provides
    @Singleton
    public WebSocketClient webSocketClient(AuthService authService,
                                           AuthConfiguration authConfiguration,
                                           @Authenticated OkHttpClient okHttpClient) {
        return new WebSocketClient(authService, authConfiguration, okHttpClient);
    }
}
