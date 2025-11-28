package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class TexasDealerService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final PlayerSessionRepository playerSessionRepository;

    public PlayerSession nextDealerReorder(UUID tableId, List<PlayerSession> playerSessions) {
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

            if (thisPlayerSession.getPosition()
                    .equals(currentDealer.getPosition())) {
                var nextIndex = index + 1;
                if (nextIndex >= numPlayers) {
                    nextIndex = 0;
                }
                return playerSessions.get(nextIndex);
            }
        }
        throw new RuntimeException("Failed to get next dealer");
    }

    private List<PlayerSession> dealerReorder(List<PlayerSession> playerSessions) {
        var dealerIndex = getDealerIndex(playerSessions);
        return sortDealerLast(playerSessions, dealerIndex);
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

    public PlayerSession getCurrentDealer(List<PlayerSession> playerSessions) {
        for (var playerSession : playerSessions) {
            if (Boolean.TRUE.equals(playerSession.getDealer())) {
                return playerSession;
            }
        }
        throw new RuntimeException("No Dealer Found in player sessions (getCurrentDealer)");
    }

    private int getDealerIndex(List<PlayerSession> playerSessions) {
        for (var index = 0; index < playerSessions.size(); index++) {
            var playerSession = playerSessions.get(index);
            if (Boolean.TRUE.equals(playerSession.getDealer())) {
                return index;
            }
        }
        throw new RuntimeException("No Dealer Found in player sessions (getDealerIndex)");
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
