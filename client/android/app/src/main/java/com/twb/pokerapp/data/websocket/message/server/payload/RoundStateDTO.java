package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.data.model.dto.card.CardDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.data.model.dto.round.RoundDTO;
import com.twb.pokerapp.data.model.dto.roundpot.RoundPotDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Snapshot of the in-progress hand, carried on {@link PlayerSubscribedDTO} when (re)subscribing
 * mid-round so the table re-renders exactly where the hand left off. Null when no hand is in play.
 */
public class RoundStateDTO {
    private RoundDTO round;
    private PlayerSessionDTO dealer;
    private List<DealPlayerCardDTO> playerCards = new ArrayList<>();
    private List<CardDTO> communityCards = new ArrayList<>();
    private BettingRoundDTO bettingRound;
    private List<RoundPotDTO> roundPots = new ArrayList<>();
    private List<PlayerSessionDTO> foldedPlayers = new ArrayList<>();
    private PlayerTurnDTO currentTurn;

    public RoundDTO getRound() {
        return round;
    }

    public void setRound(RoundDTO round) {
        this.round = round;
    }

    public PlayerSessionDTO getDealer() {
        return dealer;
    }

    public void setDealer(PlayerSessionDTO dealer) {
        this.dealer = dealer;
    }

    public List<DealPlayerCardDTO> getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(List<DealPlayerCardDTO> playerCards) {
        this.playerCards = playerCards;
    }

    public List<CardDTO> getCommunityCards() {
        return communityCards;
    }

    public void setCommunityCards(List<CardDTO> communityCards) {
        this.communityCards = communityCards;
    }

    public BettingRoundDTO getBettingRound() {
        return bettingRound;
    }

    public void setBettingRound(BettingRoundDTO bettingRound) {
        this.bettingRound = bettingRound;
    }

    public List<RoundPotDTO> getRoundPots() {
        return roundPots;
    }

    public void setRoundPots(List<RoundPotDTO> roundPots) {
        this.roundPots = roundPots;
    }

    public List<PlayerSessionDTO> getFoldedPlayers() {
        return foldedPlayers;
    }

    public void setFoldedPlayers(List<PlayerSessionDTO> foldedPlayers) {
        this.foldedPlayers = foldedPlayers;
    }

    public PlayerTurnDTO getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(PlayerTurnDTO currentTurn) {
        this.currentTurn = currentTurn;
    }

    @NonNull
    @Override
    public String toString() {
        return "RoundStateDTO{" +
                "round=" + round +
                ", dealer=" + dealer +
                ", playerCards=" + playerCards +
                ", communityCards=" + communityCards +
                ", bettingRound=" + bettingRound +
                ", roundPots=" + roundPots +
                ", foldedPlayers=" + foldedPlayers +
                ", currentTurn=" + currentTurn +
                '}';
    }
}
