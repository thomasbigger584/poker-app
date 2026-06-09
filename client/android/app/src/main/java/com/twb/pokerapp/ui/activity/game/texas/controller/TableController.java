package com.twb.pokerapp.ui.activity.game.texas.controller;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twb.pokerapp.R;
import com.twb.pokerapp.databinding.ActivityGameTexasBinding;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.data.model.dto.roundpot.RoundPotDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.BettingRoundUpdatedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealCommunityCardDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealPlayerCardDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.RoundFinishedDTO;
import com.twb.pokerapp.ui.layout.texas.CardPairLayout;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TableController {
    private static final String TAG = TableController.class.getSimpleName();
    private static final int TABLE_SIZE = 6;
    private final CardPairLayout[] cardPairLayouts = new CardPairLayout[TABLE_SIZE];
    private final Map<Integer, CardPairLayout> positionCardPairs = new HashMap<>();
    private final ActivityGameTexasBinding binding;

    public TableController(ActivityGameTexasBinding binding) {
        this.binding = binding;
        cardPairLayouts[0] = binding.playerCardPairLayout;
        cardPairLayouts[1] = binding.tablePlayer1CardPairLayout;
        cardPairLayouts[2] = binding.tablePlayer2CardPairLayout;
        cardPairLayouts[3] = binding.tablePlayer3CardPairLayout;
        cardPairLayouts[4] = binding.tablePlayer4CardPairLayout;
        cardPairLayouts[5] = binding.tablePlayer5CardPairLayout;
    }

    public void connectCurrentPlayer(PlayerSessionDTO playerSession) {
        var playerPosition = playerSession.getPosition();

        positionCardPairs.clear();

        var index = 0;
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

    public void foldPlayer(PlayerSessionDTO playerSession) {
        var cardPairLayout = getCardPairLayout(playerSession.getPosition());
        cardPairLayout.fold();
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
        binding.communityCardLayout.dealCard(dealCommunityCard.getCard());
    }

    /**
     * Clears the community board so an in-progress hand can be re-dealt from a reconnect snapshot
     * without stacking duplicate cards (cards are appended, not replaced).
     */
    public void resetCommunityCards() {
        binding.communityCardLayout.reset();
    }

    public void updateBettingRound(BettingRoundUpdatedDTO bettingRoundUpdated) {
        renderPots(bettingRoundUpdated.getRoundPots());
    }

    /**
     * Renders one chip pill per pot, ordered by the server's {@code potIndex}. The main pot sits on
     * top with side pots stacked beneath, so multiple pills read naturally as "there are side pots"
     * without needing labels.
     */
    private void renderPots(List<RoundPotDTO> roundPots) {
        var container = binding.potContainer;
        container.removeAllViews();
        if (roundPots == null || roundPots.isEmpty()) {
            return;
        }

        var sortedPots = roundPots.stream()
                .sorted(Comparator.comparingInt(pot ->
                        pot.getPotIndex() == null ? 0 : pot.getPotIndex()))
                .collect(Collectors.toList());

        var context = container.getContext();
        var topMargin = Math.round(4 * context.getResources().getDisplayMetrics().density);
        for (var index = 0; index < sortedPots.size(); index++) {
            var pot = sortedPots.get(index);
            var amount = pot.getPotAmount() == null ? 0d : pot.getPotAmount();

            var pill = new TextView(context);
            pill.setBackgroundResource(R.drawable.bg_pot_pill);
            pill.setText(context.getString(R.string.currency_format, amount));
            pill.setTextColor(0xFFFFFFFF);
            pill.setTypeface(pill.getTypeface(), Typeface.BOLD);
            pill.setGravity(Gravity.CENTER_VERTICAL);
            pill.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_chip_gold, 0, 0, 0);
            pill.setCompoundDrawablePadding(dpToPx(context, 6));
            pill.setPadding(dpToPx(context, 12), dpToPx(context, 3),
                    dpToPx(context, 14), dpToPx(context, 3));

            // The main pot (first) is slightly larger so it reads as the headline amount.
            pill.setTextSize(TypedValue.COMPLEX_UNIT_SP, index == 0 ? 16f : 13f);

            var params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if (index > 0) {
                params.topMargin = topMargin;
            }
            container.addView(pill, params);
        }
    }

    private static int dpToPx(android.content.Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public void update(RoundFinishedDTO roundFinished) {
        hidePlayerTurns();
        binding.communityCardLayout.reset();
        binding.potContainer.removeAllViews();
        for (var posCardPairEntry : positionCardPairs.entrySet()) {
            var position = posCardPairEntry.getKey();
            var cardPairLayout = posCardPairEntry.getValue();
            cardPairLayout.reset();
            var winnerAtPositionList = roundFinished.getWinners()
                    .stream()
                    .filter(roundWinnerDTO -> Objects.equals(roundWinnerDTO.getPlayerSession().getPosition(), position))
                    .collect(Collectors.toList());
            if (winnerAtPositionList.size() == 1) {
                var winnerAtPosition = winnerAtPositionList.get(0);
                cardPairLayout.updateDetails(winnerAtPosition.getPlayerSession());
                cardPairLayout.showWinner();
            }
        }
    }

    @NonNull
    public CardPairLayout getPlayerCardPairLayout() {
        return cardPairLayouts[0];
    }

    /**
     * Usernames of every player currently seated at the table (a seat counts as taken when its
     * {@link CardPairLayout} holds a player session). Used to hide bots that are already playing.
     */
    @NonNull
    public Set<String> getSeatedUsernames() {
        var seatedUsernames = new HashSet<String>();
        for (var cardPairLayout : cardPairLayouts) {
            var playerSession = cardPairLayout.getPlayerSession();
            if (playerSession != null && playerSession.getUser() != null) {
                seatedUsernames.add(playerSession.getUser().getUsername());
            }
        }
        return seatedUsernames;
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
