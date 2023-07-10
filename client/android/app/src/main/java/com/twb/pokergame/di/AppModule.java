package com.twb.pokergame.di;

import com.twb.pokergame.data.repository.CryptocurrencyRepository;
import com.twb.pokergame.data.repository.CryptocurrencyRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

// @Module annotation which will make this class a module
// to inject dependency to other class within it's scope.
// @InstallIn(SingletonComponent::class) this will make
// this class to inject dependencies across the entire application.

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public CryptocurrencyRepository cryptocurrencyRepository() {
        return new CryptocurrencyRepositoryImpl();
    }
}
