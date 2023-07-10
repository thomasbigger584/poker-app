package com.twb.pokergame.data.model;

import android.os.Bundle;

import androidx.annotation.NonNull;

public class PokerTable {
    private static final String KEY_TABLE_ID = "table_id";
    private static final String KEY_TABLE_NAME = "table_name";
    private static final String KEY_GAME_TYPE = "game_type";

    private String id;
    private String name;
    private String gameType;

    public static PokerTable fromBundle(Bundle bundle) {
        PokerTable pokerTable = new PokerTable();
        pokerTable.setId(bundle.getString(KEY_TABLE_ID));
        pokerTable.setName(bundle.getString(KEY_TABLE_NAME));
        pokerTable.setGameType(bundle.getString(KEY_GAME_TYPE));
        return pokerTable;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TABLE_ID, id);
        bundle.putString(KEY_TABLE_NAME, name);
        bundle.putString(KEY_GAME_TYPE, gameType);
        return bundle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        return "PokerTable{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gameType='" + gameType + '\'' +
                '}';
    }
}
