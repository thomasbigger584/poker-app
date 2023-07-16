package com.twb.pokergame.data.model.dto.playersession;

import com.twb.pokergame.data.model.dto.appuser.AppUserDTO;
import com.twb.pokergame.data.model.dto.pokertable.TableDTO;
import com.twb.pokergame.data.model.dto.round.RoundDTO;

import java.util.UUID;

public class PlayerSessionDTO {
    private UUID id;
    private AppUserDTO user;
    private TableDTO pokerTable;
    private RoundDTO round;
    private Integer position;
    private Double funds;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AppUserDTO getUser() {
        return user;
    }

    public void setUser(AppUserDTO user) {
        this.user = user;
    }

    public TableDTO getPokerTable() {
        return pokerTable;
    }

    public void setPokerTable(TableDTO pokerTable) {
        this.pokerTable = pokerTable;
    }

    public RoundDTO getRound() {
        return round;
    }

    public void setRound(RoundDTO round) {
        this.round = round;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Double getFunds() {
        return funds;
    }

    public void setFunds(Double funds) {
        this.funds = funds;
    }
}
