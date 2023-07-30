package com.twb.pokergame.data.model.dto.playersession;

import androidx.annotation.NonNull;

import com.twb.pokergame.data.model.dto.appuser.AppUserDTO;
import com.twb.pokergame.data.model.dto.hand.HandDTO;
import com.twb.pokergame.data.model.dto.pokertable.TableDTO;

import java.util.UUID;

public class PlayerSessionDTO {
    private UUID id;
    private AppUserDTO user;
    private TableDTO pokerTable;
    private Integer position;
    private Boolean dealer;
    private Double funds;
    private String sessionState;
    private HandDTO currentHand;

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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getDealer() {
        return dealer;
    }

    public void setDealer(Boolean dealer) {
        this.dealer = dealer;
    }

    public Double getFunds() {
        return funds;
    }

    public void setFunds(Double funds) {
        this.funds = funds;
    }

    public String getSessionState() {
        return sessionState;
    }

    public void setSessionState(String sessionState) {
        this.sessionState = sessionState;
    }

    public HandDTO getCurrentHand() {
        return currentHand;
    }

    public void setCurrentHand(HandDTO currentHand) {
        this.currentHand = currentHand;
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayerSessionDTO{" +
                "id=" + id +
                ", user=" + user +
                ", pokerTable=" + pokerTable +
                ", position=" + position +
                ", dealer=" + dealer +
                ", funds=" + funds +
                ", sessionState='" + sessionState + '\'' +
                ", currentHand=" + currentHand +
                '}';
    }
}
