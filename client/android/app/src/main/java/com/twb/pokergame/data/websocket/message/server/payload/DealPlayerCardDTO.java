package com.twb.pokergame.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokergame.data.model.dto.card.CardDTO;
import com.twb.pokergame.data.model.dto.playersession.PlayerSessionDTO;


public class DealPlayerCardDTO {
    private PlayerSessionDTO playerSession;
    private CardDTO card;

    public PlayerSessionDTO getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(PlayerSessionDTO playerSession) {
        this.playerSession = playerSession;
    }

    public CardDTO getCard() {
        return card;
    }

    public void setCard(CardDTO card) {
        this.card = card;
    }

    @NonNull
    @Override
    public String toString() {
        return "DealPlayerCardDTO{" +
                "playerSession=" + playerSession +
                ", card=" + card +
                '}';
    }
}
