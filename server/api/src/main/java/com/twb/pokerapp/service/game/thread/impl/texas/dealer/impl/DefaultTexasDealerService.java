package com.twb.pokerapp.service.game.thread.impl.texas.dealer.impl;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.service.game.exception.GameInterruptedException;
import com.twb.pokerapp.service.game.thread.impl.texas.dealer.TexasDealerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.use-fixed-scenario", havingValue = "false", matchIfMissing = true)
public class DefaultTexasDealerService extends TexasDealerService {

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @Override
    protected PlayerSession nextDealerReorder(List<PlayerSession> playerSessions) {
        var nextDealer = getNextDealer(playerSessions);
        setNextDealer(playerSessions, nextDealer);
        return nextDealer;
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private PlayerSession getNextDealer(List<PlayerSession> playerSessions) {
        playerSessions = new ArrayList<>(playerSessions);

        var currentDealerOpt = getCurrentDealerWithIndex(playerSessions);

        if (currentDealerOpt.isEmpty()) {
            return playerSessions.get(RANDOM.nextInt(playerSessions.size()));
        }

        var currentDealerWithIndex = currentDealerOpt.get();
        var dealerIndex = currentDealerWithIndex.index();
        var currentDealer = currentDealerWithIndex.dealerPlayerSession();

        playerSessions = sortDealerLast(playerSessions, dealerIndex);

        var numPlayers = playerSessions.size();
        for (var index = 0; index < numPlayers; index++) {
            var thisPlayerSession = playerSessions.get(index);

            if (thisPlayerSession.getPosition().equals(currentDealer.getPosition())) {
                var nextIndex = index + 1;
                if (nextIndex >= numPlayers) {
                    nextIndex = 0;
                }
                return playerSessions.get(nextIndex);
            }
        }
        throw new GameInterruptedException("Failed to get next dealer");
    }
}
