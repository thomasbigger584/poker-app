package com.twb.pokergame.di.application.network;

import com.twb.pokergame.data.retrofit.api.PokerTableApi;
import com.twb.pokergame.di.application.network.qualifiers.Unauthenticated;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module(includes = {NetworkModule.class})
@InstallIn(SingletonComponent.class)
public class RetrofitApiModule {

    @Provides
    @Singleton
    public PokerTableApi pokerTableApi(@Unauthenticated Retrofit retrofit) {
        return retrofit.create(PokerTableApi.class);
    }
}
