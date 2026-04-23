package com.twb.pokerapp.di.network;

import com.twb.pokerapp.data.retrofit.api.AppUserApi;
import com.twb.pokerapp.data.retrofit.api.HealthApi;
import com.twb.pokerapp.data.retrofit.api.TableApi;
import com.twb.pokerapp.data.retrofit.api.TransactionHistoryApi;
import com.twb.pokerapp.di.network.qualifiers.Authenticated;
import com.twb.pokerapp.di.network.qualifiers.Unauthenticated;

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
    public HealthApi healthApi(@Unauthenticated Retrofit retrofit) {
        return retrofit.create(HealthApi.class);
    }

    @Provides
    @Singleton
    public TableApi tableApi(@Authenticated Retrofit retrofit) {
        return retrofit.create(TableApi.class);
    }

    @Provides
    @Singleton
    public AppUserApi appUserApi(@Authenticated Retrofit retrofit) {
        return retrofit.create(AppUserApi.class);
    }

    @Provides
    @Singleton
    public TransactionHistoryApi transactionHistoryApi(@Authenticated Retrofit retrofit) {
        return retrofit.create(TransactionHistoryApi.class);
    }

}
