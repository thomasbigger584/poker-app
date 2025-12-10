package com.twb.pokerapp.di.network;

import com.twb.pokerapp.data.retrofit.api.TableApi;
import com.twb.pokerapp.di.network.qualifiers.Authenticated;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module(includes = {NetworkModule.class})
@InstallIn(SingletonComponent.class)
public class ApiModule {

    @Provides
    @Singleton
    public TableApi tableApi(@Authenticated Retrofit retrofit) {
        return retrofit.create(TableApi.class);
    }
}
