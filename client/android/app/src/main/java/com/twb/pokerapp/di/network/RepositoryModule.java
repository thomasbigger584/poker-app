package com.twb.pokerapp.di.network;

import com.twb.pokerapp.data.repository.PokerTableRepository;
import com.twb.pokerapp.data.retrofit.api.PokerTableApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

// @Module annotation which will make this class a module
// to inject dependency to other class within it's scope.
// @InstallIn(SingletonComponent::class) this will make
// this class to inject dependencies across the entire application.

@Module(includes = {ApiModule.class})
@InstallIn(SingletonComponent.class)
public class RepositoryModule {

    @Provides
    @Singleton
    public PokerTableRepository pokerTableRepository(PokerTableApi api) {
        return new PokerTableRepository(api);
    }
}
