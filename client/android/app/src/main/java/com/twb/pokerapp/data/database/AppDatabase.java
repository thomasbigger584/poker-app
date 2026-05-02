package com.twb.pokerapp.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.twb.pokerapp.data.database.converter.UUIDConverter;
import com.twb.pokerapp.data.database.dao.ServerMessageDAO;
import com.twb.pokerapp.data.database.entities.ServerMessageEntity;

@Database(entities = {ServerMessageEntity.class}, version = 1, exportSchema = false)
@TypeConverters({UUIDConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ServerMessageDAO serverMessageDao();
}
