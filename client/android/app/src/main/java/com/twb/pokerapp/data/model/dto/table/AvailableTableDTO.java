package com.twb.pokerapp.data.model.dto.table;


import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.UUID;

public class AvailableTableDTO {
    private TableDTO table;
    private Integer playersConnected;

    public TableDTO getTable() {
        return table;
    }

    public void setTable(TableDTO table) {
        this.table = table;
    }

    public Integer getPlayersConnected() {
        return playersConnected;
    }

    public void setPlayersConnected(Integer playersConnected) {
        this.playersConnected = playersConnected;
    }

    @NonNull
    @Override
    public String toString() {
        return "AvailableTableDTO{" +
                "table=" + table +
                ", playersConnected=" + playersConnected +
                '}';
    }
}
