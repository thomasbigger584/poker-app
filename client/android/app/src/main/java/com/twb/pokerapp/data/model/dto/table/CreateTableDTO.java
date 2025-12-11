package com.twb.pokerapp.data.model.dto.table;

public class CreateTableDTO {

    private String name;

    private String gameType;

    private Integer minPlayers;

    private Integer maxPlayers;

    private Double minBuyin;

    private Double maxBuyin;

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
}
