package com.twb.pokerapp.data.model.dto.table;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.UUID;

public class TableDTO {
    private static final String KEY_TABLE_ID = "table_id";
    private static final String KEY_TABLE_NAME = "table_name";
    private static final String KEY_GAME_TYPE = "game_type";
    private static final String KEY_SPEED_MULTIPLIER = "speed_multiplier";
    private static final String KEY_TOTAL_ROUNDS = "total_rounds";
    private static final String KEY_MIN_PLAYERS = "min_players";
    private static final String KEY_MAX_PLAYERS = "max_players";
    private static final String KEY_MIN_BUYIN = "min_buyin";
    private static final String KEY_MAX_BUYIN = "max_buyin";

    private UUID id;
    private String name;
    private String gameType;
    @Nullable
    private Double speedMultiplier;
    @Nullable
    private Integer totalRounds;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Double minBuyin;
    private Double maxBuyin;

    public static TableDTO fromBundle(Bundle bundle) {
        var table = new TableDTO();
        table.setId(UUID.fromString(bundle.getString(KEY_TABLE_ID)));
        table.setName(bundle.getString(KEY_TABLE_NAME));
        table.setGameType(bundle.getString(KEY_GAME_TYPE));
        var speedMultiplier = bundle.getSerializable(KEY_SPEED_MULTIPLIER);
        if (speedMultiplier != null) {
            table.setSpeedMultiplier((Double) speedMultiplier);
        }
        var totalRounds = bundle.getSerializable(KEY_TOTAL_ROUNDS);
        if (totalRounds != null) {
            table.setTotalRounds((Integer) totalRounds);
        }
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
        if (speedMultiplier != null) {
            bundle.putDouble(KEY_SPEED_MULTIPLIER, speedMultiplier);
        }
        if (totalRounds != null) {
            bundle.putInt(KEY_TOTAL_ROUNDS, totalRounds);
        }
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

    public Double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(Double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableDTO tableDTO = (TableDTO) o;
        return Objects.equals(id, tableDTO.id) && Objects.equals(name, tableDTO.name) && Objects.equals(gameType, tableDTO.gameType) && Objects.equals(speedMultiplier, tableDTO.speedMultiplier) && Objects.equals(totalRounds, tableDTO.totalRounds) && Objects.equals(minPlayers, tableDTO.minPlayers) && Objects.equals(maxPlayers, tableDTO.maxPlayers) && Objects.equals(minBuyin, tableDTO.minBuyin) && Objects.equals(maxBuyin, tableDTO.maxBuyin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, gameType, speedMultiplier, totalRounds, minPlayers, maxPlayers, minBuyin, maxBuyin);
    }

    @NonNull
    @Override
    public String toString() {
        return "TableDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gameType='" + gameType + '\'' +
                ", speedMultiplier=" + speedMultiplier +
                ", totalRounds=" + totalRounds +
                ", minPlayers=" + minPlayers +
                ", maxPlayers=" + maxPlayers +
                ", minBuyin=" + minBuyin +
                ", maxBuyin=" + maxBuyin +
                '}';
    }
}
