package com.twb.pokergame.service.game.impl;

import com.twb.pokergame.domain.PlayerSession;
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

    public TexasHoldemGameThread(UUID tableId) {
        super(tableId);
    }



    @Override
    protected void onRun() {

        PlayerSession dealerSession = determineNextDealer();
        dispatcher.send(tableId, messageFactory.dealerDetermined(dealerSession));








    }


    /**
     * If there is not a dealer already selected then pick a random dealer out of the PlayerSession list.
     * If there is a dealer selected, get the next dealer given the position.
     * Making sure to wrap around the list if the next dealer is actually at the start of the list.
     * <p>
     * Once we know the dealer, we want to sort the playerSession list with the dealer
     * to the start and then position after
     *
     * @return PlayerSession - the dealer player session
     */
    private PlayerSession determineNextDealer() {
        PlayerSession currentDealer = null;
        for (PlayerSession playerSession : playerSessions) {
            if (Boolean.TRUE.equals(playerSession.getDealer())) {
                currentDealer = playerSession;
                break;
            }
        }
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
        for (PlayerSession playerSession : playerSessions) {
            playerSession.setDealer(currentDealer.getId().equals(playerSession.getId()));
        }
        playerSessionRepository.saveAllAndFlush(playerSessions);

        int dealerIndex = -1;

        //find dealer index
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

        // sort playerSessions by dealer first, then position
        List<PlayerSession> dealerSortedList = new ArrayList<>();
        for (int index = dealerIndex; index < playerSessions.size(); index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        for (int index = 0; index < dealerIndex; index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        playerSessions = dealerSortedList;

        return currentDealer;
    }

}
