package com.twb.pokerapp.data.model.dto.table;


import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.UUID;

public class TableDTO {
    private static final String KEY_TABLE_ID = "table_id";
    private static final String KEY_TABLE_NAME = "table_name";
    private static final String KEY_GAME_TYPE = "game_type";
    private static final String KEY_MIN_PLAYERS = "min_players";
    private static final String KEY_MAX_PLAYERS = "max_players";
    private static final String KEY_MIN_BUYIN = "min_buyin";
    private static final String KEY_MAX_BUYIN = "max_buyin";

    private UUID id;
    private String name;
    private String gameType;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Double minBuyin;
    private Double maxBuyin;

    public static TableDTO fromBundle(Bundle bundle) {
        TableDTO table = new TableDTO();
        table.setId(UUID.fromString(bundle.getString(KEY_TABLE_ID)));
        table.setName(bundle.getString(KEY_TABLE_NAME));
        table.setGameType(bundle.getString(KEY_GAME_TYPE));
        table.setMinPlayers(bundle.getInt(KEY_MIN_PLAYERS));
        table.setMaxPlayers(bundle.getInt(KEY_MAX_PLAYERS));
        table.setMinBuyin(bundle.getDouble(KEY_MIN_BUYIN));
        table.setMaxBuyin(bundle.getDouble(KEY_MAX_BUYIN));

        return table;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TABLE_ID, id.toString());
        bundle.putString(KEY_TABLE_NAME, name);
        bundle.putString(KEY_GAME_TYPE, gameType);
        bundle.putInt(KEY_MIN_PLAYERS, minPlayers);
        bundle.putInt(KEY_MAX_PLAYERS, maxPlayers);
        bundle.putDouble(KEY_MIN_BUYIN, minBuyin);
        bundle.putDouble(KEY_MAX_BUYIN, maxBuyin);
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

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(Integer minPlayers) {
        this.minPlayers = minPlayers;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Double getMinBuyin() {
        return minBuyin;
    }

    public void setMinBuyin(Double minBuyin) {
        this.minBuyin = minBuyin;
    }

    public Double getMaxBuyin() {
        return maxBuyin;
    }

    public void setMaxBuyin(Double maxBuyin) {
        this.maxBuyin = maxBuyin;
    }

    @NonNull
    @Override
    public String toString() {
        return "TableDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gameType='" + gameType + '\'' +
                ", minPlayers=" + minPlayers +
                ", maxPlayers=" + maxPlayers +
                ", minBuyin=" + minBuyin +
                ", maxBuyin=" + maxBuyin +
                '}';
    }
}
