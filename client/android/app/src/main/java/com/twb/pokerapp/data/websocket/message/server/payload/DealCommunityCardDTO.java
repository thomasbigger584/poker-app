package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.card.CardDTO;


public class DealCommunityCardDTO {
    private CardDTO card;

    public CardDTO getCard() {
        return card;
    }

    public void setCard(CardDTO card) {
        this.card = card;
    }

    @NonNull
    @Override
    public String toString() {
        return "DealCommunityCardDTO{" +
                "card=" + card +
                '}';
    }
}
