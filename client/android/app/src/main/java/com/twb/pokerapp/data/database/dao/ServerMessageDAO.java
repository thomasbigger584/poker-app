package com.twb.pokerapp.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.twb.pokerapp.data.database.entities.ServerMessageEntity;

import java.util.List;
import java.util.UUID;

import io.reactivex.Single;

@Dao
public interface ServerMessageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ServerMessageEntity message);

    @Query("""
            SELECT *
            FROM ServerMessageEntity
            WHERE tableId = :tableId
            ORDER BY timestamp ASC
            """)
    Single<List<ServerMessageEntity>> getMessagesByTableId(UUID tableId);
}
