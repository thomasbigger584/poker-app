package com.twb.pokergame.service.game.impl;

import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.enumeration.CardType;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.old.CardDTO;
import com.twb.pokergame.service.game.GameThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Scope("prototype")
public final class TexasHoldemGameThread extends GameThread {
    private static final Logger logger =
            LoggerFactory.getLogger(TexasHoldemGameThread.class);

    public TexasHoldemGameThread(UUID tableId) {
        super(tableId);
    }

    @Override
    protected void onRun() {
        init();

        RoundState roundState = RoundState.INIT_DEAL;
        saveRoundState(roundState);

        while (roundState != RoundState.FINISH) {
            switch (roundState) {
                case INIT_DEAL -> initDeal();
                case INIT_DEAL_BET, FLOP_DEAL_BET, RIVER_DEAL_BET, TURN_DEAL_BET -> waitPlayerTurn();
                case FLOP_DEAL -> dealFlop();
                case TURN_DEAL -> dealTurn();
                case RIVER_DEAL -> dealRiver();
                case EVAL -> eval();
            }
            roundState = roundState.nextState();
            saveRoundState(roundState);
        }
        finishRound();
    }

    private void init() {
        determineNextDealer();
        shuffleCards();
    }

    private void initDeal() {
        for (CardType cardType : CardType.PLAYER_CARDS) {
            for (PlayerSession playerSession : playerSessions) {
                dealPlayerCard(cardType, playerSession);
            }
        }
    }

    private void dealFlop() {
        for (CardType cardType : CardType.FLOP_CARDS) {
            dealCommunityCard(cardType);
        }
    }

    private void dealTurn() {
        dealCommunityCard(CardType.TURN_CARD);
    }

    private void dealRiver() {
        dealCommunityCard(CardType.RIVER_CARD);
    }

    private void dealPlayerCard(CardType cardType, PlayerSession playerSession) {
        CardDTO card = getCard();
        handService.addPlayerCard(playerSession, currentRound, card, cardType);
        dispatcher.send(tableId, messageFactory.initDeal(playerSession, card));
        sleepInMs(WAIT_MS);
    }

    private void dealCommunityCard(CardType cardType) {
        CardDTO card = getCard();
        cardService.createCommunityCard(currentRound, card, cardType);
        dispatcher.send(tableId, messageFactory.communityCardDeal(card, cardType));
        sleepInMs(WAIT_MS);
    }

    private void waitPlayerTurn() {
        //todo waitPlayerTurn
    }

    private void eval() {
        //todo eval
    }

    private void finishRound() {
        //todo finishRound
    }

    /**
     * If there is not a dealer already selected then pick a random dealer out of the PlayerSession list.
     * If there is a dealer selected, get the next dealer given the position.
     * Making sure to wrap around the list if the next dealer is actually at the start of the list.
     * <p>
     * Once we know the dealer, we want to reorder the list to have dealer last but still in order by position
     */
    private void determineNextDealer() {

        //get current dealer if already set
        PlayerSession currentDealer = null;
        for (PlayerSession playerSession : playerSessions) {
            if (Boolean.TRUE.equals(playerSession.getDealer())) {
                currentDealer = playerSession;
                break;
            }
        }

        // get next dealer
        if (currentDealer == null) {
            currentDealer = playerSessions.get(RANDOM.nextInt(playerSessions.size()));
        } else {
            int numPlayers = playerSessions.size();
            for (int index = 0; index < numPlayers; index++) {
                PlayerSession thisPlayerSession = playerSessions.get(index);
                if (thisPlayerSession.getPosition()
                        .equals(currentDealer.getPosition())) {
                    int nextIndex = index + 1;
                    if (nextIndex >= numPlayers) {
                        nextIndex = 0;
                    }
                    currentDealer = playerSessions.get(nextIndex);
                }
            }
        }

        //save to database and list
        for (PlayerSession playerSession : playerSessions) {
            playerSession.setDealer(currentDealer.getId().equals(playerSession.getId()));
        }
        playerSessionRepository.saveAllAndFlush(playerSessions);

        //reorder list for dealer last
        int dealerIndex = -1;
        for (int index = 0; index < playerSessions.size(); index++) {
            PlayerSession playerSession = playerSessions.get(index);
            if (playerSession.getDealer()) {
                dealerIndex = index;
                break;
            }
        }

        if (dealerIndex == -1) {
            throw new RuntimeException("Failed to sort player sessions by dealer and position, dealerIndex is -1");
        }

        int startIndex = dealerIndex + 1;
        if (startIndex >= playerSessions.size()) {
            startIndex = 0;
        }

        // sort playerSessions by positions and dealer last
        List<PlayerSession> dealerSortedList = new ArrayList<>();
        for (int index = startIndex; index < playerSessions.size(); index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        for (int index = 0; index < startIndex; index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        playerSessions = dealerSortedList;

        dispatcher.send(tableId, messageFactory.dealerDetermined(currentDealer));
    }

}