package com.twb.pokergame.data.websocket.message.server.payload;

import com.twb.pokergame.data.model.dto.card.CardDTO;
import com.twb.pokergame.data.model.enumeration.CardType;


public class DealCommunityCardDTO {
    private CardType cardType;
    private CardDTO card;

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public CardDTO getCard() {
        return card;
    }

    public void setCard(CardDTO card) {
        this.card = card;
    }

    @Override
    public String toString() {
        return "DealCommunityCardDTO{" +
                "cardType=" + cardType +
                ", card=" + card +
                '}';
    }
}
