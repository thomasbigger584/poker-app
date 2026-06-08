package com.twb.pokerapp.di.database;

import android.content.Context;

import androidx.room.Room;

import com.twb.pokerapp.data.database.AppDatabase;
import com.twb.pokerapp.data.database.dao.ServerMessageDAO;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "poker_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    public ServerMessageDAO provideServerMessageDao(AppDatabase database) {
        return database.serverMessageDao();
    }
}
