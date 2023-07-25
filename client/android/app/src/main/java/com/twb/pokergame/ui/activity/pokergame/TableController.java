package com.twb.pokergame.ui.activity.pokergame;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.twb.pokergame.R;
import com.twb.pokergame.data.model.Card;
import com.twb.pokergame.data.model.dto.CardDTO;
import com.twb.pokergame.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.data.websocket.message.server.payload.DealPlayerCardDTO;
import com.twb.pokergame.ui.layout.CardPairLayout;

import java.util.HashMap;
import java.util.Map;

public class TableController {
    private static final String TAG = TableController.class.getSimpleName();
    private static final int TABLE_SIZE = 6;
    private final CardPairLayout[] cardPairLayouts = new CardPairLayout[TABLE_SIZE];
    private final Map<Integer, CardPairLayout> positionCardPairs = new HashMap<>();

    public TableController(Activity activity) {
        cardPairLayouts[0] = activity.findViewById(R.id.playerCardPairLayout);
        cardPairLayouts[1] = activity.findViewById(R.id.tablePlayer1CardPairLayout);
        cardPairLayouts[2] = activity.findViewById(R.id.tablePlayer2CardPairLayout);
        cardPairLayouts[3] = activity.findViewById(R.id.tablePlayer3CardPairLayout);
        cardPairLayouts[4] = activity.findViewById(R.id.tablePlayer4CardPairLayout);
        cardPairLayouts[5] = activity.findViewById(R.id.tablePlayer5CardPairLayout);
    }

    public void connectCurrentPlayer(PlayerSessionDTO playerSession) {
        int playerPosition = playerSession.getPosition();

        positionCardPairs.clear();

        int index = 0;
        for (int thisPosition = playerPosition; thisPosition <= 6; thisPosition++, index++) {
            positionCardPairs.put(thisPosition, cardPairLayouts[index]);
        }
        for (int thisPosition = 1; thisPosition < playerPosition; thisPosition++, index++) {
            positionCardPairs.put(thisPosition, cardPairLayouts[index]);
        }
        cardPairLayouts[0].updateDetails(playerSession);

        Boolean dealer = playerSession.getDealer();
        cardPairLayouts[0].updateDealerChip(dealer != null && dealer);
    }

    public void connectOtherPlayer(PlayerSessionDTO playerSession) {
        int playerPosition = playerSession.getPosition();
        CardPairLayout cardPairLayout = positionCardPairs.get(playerPosition);
        if (cardPairLayout == null) {
            throw new RuntimeException("No CardPairLayout set for position");
        }
        cardPairLayout.updateDetails(playerSession);

        Boolean dealer = playerSession.getDealer();
        cardPairLayout.updateDealerChip(dealer != null && dealer);
    }

    public void disconnectOtherPlayer(String username) {
        CardPairLayout cardPairLayout = findCardPairLayoutByUsername(username);
        if (cardPairLayout != null) {
            cardPairLayout.deleteDetails();
        }
    }

    public void dealerDetermined(PlayerSessionDTO playerSession) {
        for (Map.Entry<Integer, CardPairLayout> posCardPairEntry : positionCardPairs.entrySet()) {
            Integer position = posCardPairEntry.getKey();
            CardPairLayout cardPairLayout = posCardPairEntry.getValue();
            cardPairLayout.updateDealerChip(position.equals(playerSession.getPosition()));
        }
    }

    public void dealCurrentPlayerCard(DealPlayerCardDTO dealPlayerCard) {
        PlayerSessionDTO playerSession = dealPlayerCard.getPlayerSession();
        int position = playerSession.getPosition();

        if (positionCardPairs.containsKey(position)) {

            CardPairLayout cardPairLayout = positionCardPairs.get(position);
            Card card = new Card(dealPlayerCard.getCard());
            cardPairLayout.updateCardImageView(card);
        } else {
            Log.e(TAG, "dealCurrentPlayerCard: Position is not a part of the table for dealing");
        }
    }

    //todo: dont show actual card, but show it turned around
    public void dealOtherPlayerCard(DealPlayerCardDTO dealPlayerCard) {
        PlayerSessionDTO playerSession = dealPlayerCard.getPlayerSession();
        int position = playerSession.getPosition();

        if (positionCardPairs.containsKey(position)) {
            CardPairLayout cardPairLayout = positionCardPairs.get(position);
            Card card = new Card(dealPlayerCard.getCard());
            cardPairLayout.updateCardImageView(card);
        } else {
            Log.e(TAG, "dealCurrentPlayerCard: Position is not a part of the table for dealing");
        }
    }


    // ------------------------------------------------------------------------------

    @Nullable
    private CardPairLayout findCardPairLayoutByUsername(String username) {
        for (CardPairLayout cardPairLayout : cardPairLayouts) {
            if (username.equals(cardPairLayout.getUsername())) {
                return cardPairLayout;
            }
        }
        return null;
    }
}