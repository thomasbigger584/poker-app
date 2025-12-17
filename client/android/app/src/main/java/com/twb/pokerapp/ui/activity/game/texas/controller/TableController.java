package com.twb.pokerapp.ui.activity.game.texas.controller;

import android.app.Activity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealCommunityCardDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealPlayerCardDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.RoundFinishedDTO;
import com.twb.pokerapp.ui.layout.texas.CardPairLayout;
import com.twb.pokerapp.ui.layout.texas.CommunityCardLayout;

import java.util.HashMap;
import java.util.Map;

public class TableController {
    private static final String TAG = TableController.class.getSimpleName();
    private static final int TABLE_SIZE = 6;
    private final CardPairLayout[] cardPairLayouts = new CardPairLayout[TABLE_SIZE];
    private final Map<Integer, CardPairLayout> positionCardPairs = new HashMap<>();

    private final CommunityCardLayout communityCardLayout;
    private final TextView potSizeText;

    public TableController(Activity activity) {
        cardPairLayouts[0] = activity.findViewById(R.id.playerCardPairLayout);
        cardPairLayouts[1] = activity.findViewById(R.id.tablePlayer1CardPairLayout);
        cardPairLayouts[2] = activity.findViewById(R.id.tablePlayer2CardPairLayout);
        cardPairLayouts[3] = activity.findViewById(R.id.tablePlayer3CardPairLayout);
        cardPairLayouts[4] = activity.findViewById(R.id.tablePlayer4CardPairLayout);
        cardPairLayouts[5] = activity.findViewById(R.id.tablePlayer5CardPairLayout);
        communityCardLayout = activity.findViewById(R.id.communityCardLayout);
        potSizeText = activity.findViewById(R.id.potSizeText);
    }

    public void connectCurrentPlayer(PlayerSessionDTO playerSession) {
        var playerPosition = playerSession.getPosition();

        positionCardPairs.clear();

        int index = 0;
        for (var thisPosition = playerPosition; thisPosition <= TABLE_SIZE; thisPosition++, index++) {
            positionCardPairs.put(thisPosition, cardPairLayouts[index]);
        }
        for (var thisPosition = 1; thisPosition < playerPosition; thisPosition++, index++) {
            positionCardPairs.put(thisPosition, cardPairLayouts[index]);
        }
        connectPlayer(playerSession, cardPairLayouts[0]);
    }

    public void connectOtherPlayer(PlayerSessionDTO playerSession) {
        var cardPairLayout = getCardPairLayout(playerSession.getPosition());
        connectPlayer(playerSession, cardPairLayout);
    }

    private void connectPlayer(PlayerSessionDTO playerSession, CardPairLayout cardPairLayout) {
        cardPairLayout.updateDetails(playerSession);
        var dealer = playerSession.getDealer();
        cardPairLayout.updateDealerChip(dealer != null && dealer);
    }

    public void updateDetails(PlayerSessionDTO playerSession) {
        var cardPairLayout = getCardPairLayout(playerSession.getPosition());
        cardPairLayout.updateDetails(playerSession);
    }

    public void disconnectOtherPlayer(String username) {
        var cardPairLayout = findCardPairLayout(username);
        if (cardPairLayout != null) {
            cardPairLayout.deleteDetails();
        }
    }

    public void dealerDetermined(PlayerSessionDTO playerSession) {
        for (var posCardPairEntry : positionCardPairs.entrySet()) {
            var position = posCardPairEntry.getKey();
            var cardPairLayout = posCardPairEntry.getValue();
            cardPairLayout.updateDealerChip(position.equals(playerSession.getPosition()));
        }
    }

    public void updatePlayerTurn(PlayerSessionDTO playerSession) {
        for (var posCardPairEntry : positionCardPairs.entrySet()) {
            var position = posCardPairEntry.getKey();
            var cardPairLayout = posCardPairEntry.getValue();
            cardPairLayout.updateTurnPlayer(position.equals(playerSession.getPosition()));
        }
    }

    public void hidePlayerTurns() {
        for (var posCardPairEntry : positionCardPairs.entrySet()) {
            var cardPairLayout = posCardPairEntry.getValue();
            cardPairLayout.updateTurnPlayer(false);
        }
    }

    public void dealCurrentPlayerCard(DealPlayerCardDTO dealPlayerCard) {
        var playerSession = dealPlayerCard.getPlayerSession();
        var cardPairLayout = getCardPairLayout(playerSession.getPosition());
        if (playerSession.getUser().getUsername().equals(cardPairLayout.getUsername())) {
            cardPairLayout.updateCardImageView(dealPlayerCard.getCard());
        }
    }

    public void dealOtherPlayerCard(DealPlayerCardDTO dealPlayerCard) {
        var playerSession = dealPlayerCard.getPlayerSession();
        var cardPairLayout = getCardPairLayout(playerSession.getPosition());
        if (playerSession.getUser().getUsername().equals(cardPairLayout.getUsername())) {
            cardPairLayout.updateCardImageView();
        }
    }

    public void dealCommunityCard(DealCommunityCardDTO dealCommunityCard) {
        communityCardLayout.dealCard(dealCommunityCard.getCard());
    }

    public void reset(RoundFinishedDTO roundFinished) {
        hidePlayerTurns();
        communityCardLayout.reset();
        for (var cardPairLayout : cardPairLayouts) {
            cardPairLayout.reset();
        }
    }

    // ------------------------------------------------------------------------------

    @Nullable
    private CardPairLayout findCardPairLayout(String username) {
        for (var cardPairLayout : cardPairLayouts) {
            if (username.equals(cardPairLayout.getUsername())) {
                return cardPairLayout;
            }
        }
        return null;
    }

    @NonNull
    private CardPairLayout getCardPairLayout(int position) {
        if (!positionCardPairs.containsKey(position)) {
            throw new RuntimeException("Position is not part of the table for dealing: " + position);
        }
        var cardPairLayout = positionCardPairs.get(position);
        if (cardPairLayout == null) {
            throw new RuntimeException("Card pair layout is null for position: " + position);
        }
        return cardPairLayout;
    }
}
