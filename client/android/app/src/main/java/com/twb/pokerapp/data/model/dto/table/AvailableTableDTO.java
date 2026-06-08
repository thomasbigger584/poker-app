package com.twb.pokerapp.data.model.dto.table;

import androidx.annotation.NonNull;

import java.util.Objects;

public class AvailableTableDTO {
    private TableDTO table;
    private Integer playersConnected;
    private boolean currentUserConnected;
    private String currentUserConnectionType;
    private Long reconnectMillisRemaining;

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

    public boolean isCurrentUserConnected() {
        return currentUserConnected;
    }

    public void setCurrentUserConnected(boolean currentUserConnected) {
        this.currentUserConnected = currentUserConnected;
    }

    public String getCurrentUserConnectionType() {
        return currentUserConnectionType;
    }

    public void setCurrentUserConnectionType(String currentUserConnectionType) {
        this.currentUserConnectionType = currentUserConnectionType;
    }

    public Long getReconnectMillisRemaining() {
        return reconnectMillisRemaining;
    }

    public void setReconnectMillisRemaining(Long reconnectMillisRemaining) {
        this.reconnectMillisRemaining = reconnectMillisRemaining;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (AvailableTableDTO) o;
        return currentUserConnected == that.currentUserConnected
                && Objects.equals(table, that.table)
                && Objects.equals(playersConnected, that.playersConnected)
                && Objects.equals(currentUserConnectionType, that.currentUserConnectionType)
                && Objects.equals(reconnectMillisRemaining, that.reconnectMillisRemaining);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, playersConnected, currentUserConnected, currentUserConnectionType, reconnectMillisRemaining);
    }

    @NonNull
    @Override
    public String toString() {
        return "AvailableTableDTO{" +
                "table=" + table +
                ", playersConnected=" + playersConnected +
                ", currentUserConnected=" + currentUserConnected +
                ", currentUserConnectionType='" + currentUserConnectionType + '\'' +
                ", reconnectMillisRemaining=" + reconnectMillisRemaining +
                '}';
    }
}
