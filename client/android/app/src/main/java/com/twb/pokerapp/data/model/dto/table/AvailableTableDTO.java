package com.twb.pokerapp.data.model.dto.table;

import androidx.annotation.NonNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (AvailableTableDTO) o;
        return Objects.equals(table, that.table) && Objects.equals(playersConnected, that.playersConnected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, playersConnected);
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
