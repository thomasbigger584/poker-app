package com.twb.pokerapp.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.twb.pokerapp.data.database.dao.ServerMessageDAO;
import com.twb.pokerapp.data.database.entities.ServerMessageEntity;

@Database(entities = {ServerMessageEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ServerMessageDAO serverMessageDao();
}
