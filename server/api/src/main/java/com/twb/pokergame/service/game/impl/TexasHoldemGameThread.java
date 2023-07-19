package com.twb.pokergame.service.game.impl;

import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.old.Card;
import com.twb.pokergame.old.DeckOfCardsFactory;
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
public class TexasHoldemGameThread extends GameThread {
    private static final Logger logger = LoggerFactory.getLogger(TexasHoldemGameThread.class);
    private static final int NO_CARDS_FOR_PLAYER_DEAL = 2;


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
                case INIT_DEAL: {
                    initDeal();
                    break;
                }
                case INIT_DEAL_BET:
                case FLOP_DEAL_BET:
                case RIVER_DEAL_BET:
                case TURN_DEAL_BET: {
//                    pokerTable.performPlayerBetTurn();
                    break;
                }
                case FLOP_DEAL: {
//                    pokerTable.flopDeal();
                    break;
                }
                case RIVER_DEAL: {
//                    pokerTable.riverDeal();
                    break;
                }
                case TURN_DEAL: {
//                    pokerTable.turnDeal();
                    break;
                }
                case EVAL: {
//                    eval();
                    break;
                }
            }
            roundState = roundState.nextState();
            saveRoundState(roundState);
        }
        // finishRound();
    }

    private void init() {
        determineNextDealer();

        shuffleCards();
    }

    private void initDeal() {
        for (int dealIndex = 0; dealIndex < NO_CARDS_FOR_PLAYER_DEAL; dealIndex++) {
            for (PlayerSession playerSession : playerSessions) {
                Card card = getCard();
                //todo: save to database
                dispatcher.send(tableId, messageFactory.initDeal(playerSession, card));
                sleepInMs(400);
            }
        }
    }



    /**
     * If there is not a dealer already selected then pick a random dealer out of the PlayerSession list.
     * If there is a dealer selected, get the next dealer given the position.
     * Making sure to wrap around the list if the next dealer is actually at the start of the list.
     * <p>
     * Once we know the dealer, we want to reorder the list so as to have dealer last but still in order by position
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
