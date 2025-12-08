package com.twb.pokerapp.data.model.dto.table;


import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.UUID;

public class TableDTO {
    private static final String KEY_TABLE_ID = "table_id";
    private static final String KEY_TABLE_NAME = "table_name";
    private static final String KEY_GAME_TYPE = "game_type";

    private UUID id;
    private String name;
    private String gameType;

    public static TableDTO fromBundle(Bundle bundle) {
        TableDTO table = new TableDTO();
        table.setId(UUID.fromString(bundle.getString(KEY_TABLE_ID)));
        table.setName(bundle.getString(KEY_TABLE_NAME));
        table.setGameType(bundle.getString(KEY_GAME_TYPE));
        return table;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TABLE_ID, id.toString());
        bundle.putString(KEY_TABLE_NAME, name);
        bundle.putString(KEY_GAME_TYPE, gameType);
        return bundle;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    @NonNull
    @Override
    public String toString() {
        return "TableDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gameType='" + gameType + '\'' +
                '}';
    }
}
