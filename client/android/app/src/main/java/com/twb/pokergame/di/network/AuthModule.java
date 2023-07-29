package com.twb.pokergame.di.network;

import android.content.Context;

import com.twb.pokergame.data.auth.AuthConfiguration;
import com.twb.pokergame.data.auth.AuthService;
import com.twb.pokergame.data.auth.AuthStateManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AuthModule {

    @Provides
    @Singleton
    public AuthConfiguration authConfiguration(@ApplicationContext Context context) {
        return new AuthConfiguration(context);
    }

    @Provides
    @Singleton
    public AuthStateManager authStateManager(@ApplicationContext Context context) {
        return new AuthStateManager(context);
    }

    @Provides
    @Singleton
    public AuthService authService(@ApplicationContext Context context,
                                   AuthStateManager authStateManager, AuthConfiguration authConfiguration) {
        return new AuthService(context, authStateManager, authConfiguration);
    }
}
