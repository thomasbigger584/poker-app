package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TexasDealerService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final PlayerSessionRepository playerSessionRepository;
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;

    @Transactional
    public void determineNextDealer(GameThreadParams params) {
        var playerSessions = playerSessionRepository.findConnectedPlayersByTableId(params.getTableId());
        if (CollectionUtils.isEmpty(playerSessions)) {
            throw new GameInterruptedException("No Players Connected");
        }
        var currentDealer = nextDealerReorder(params.getTableId(), playerSessions);
        dispatcher.send(params, messageFactory.dealerDetermined(currentDealer));
    }

    private PlayerSession nextDealerReorder(UUID tableId, List<PlayerSession> playerSessions) {
        var nextDealer = getNextDealer(playerSessions);
        setNextDealer(tableId, nextDealer);
        return nextDealer;
    }

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

    private Optional<DealerWithIndexDTO> getCurrentDealerWithIndex(List<PlayerSession> playerSessions) {
        for (var index = 0; index < playerSessions.size(); index++) {
            var playerSession = playerSessions.get(index);
            if (Boolean.TRUE.equals(playerSession.getDealer())) {
                return Optional.of(new DealerWithIndexDTO(index, playerSession));
            }
        }
        return Optional.empty();
    }

    private List<PlayerSession> sortDealerLast(List<PlayerSession> playerSessions, int dealerIndex) {
        var start = dealerIndex + 1;
        if (start > playerSessions.size()) {
            start = 0;
        }
        var dealerSortedList = new ArrayList<PlayerSession>();
        for (var index = start; index < playerSessions.size(); index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        for (var index = 0; index < start; index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        return dealerSortedList;
    }

    private void setNextDealer(UUID tableId, PlayerSession nextDealer) {
        playerSessionRepository.resetDealerForTableId(tableId);
        playerSessionRepository.setDealer(nextDealer.getId(), true);
    }

    private record DealerWithIndexDTO(int index, PlayerSession dealerPlayerSession) {
    }
}
