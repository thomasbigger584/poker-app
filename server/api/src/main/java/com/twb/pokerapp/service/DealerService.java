package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
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
public class DealerService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final PlayerSessionRepository playerSessionRepository;

    public List<PlayerSession> nextDealerReorder(UUID tableId, List<PlayerSession> playerSessions) {
        List<PlayerSession> copyPlayerSessions = new ArrayList<>(playerSessions);

        PlayerSession nextDealer = getNextDealer(copyPlayerSessions);
        setNextDealer(tableId, nextDealer);

        copyPlayerSessions = playerSessionRepository
                .findConnectedPlayersByTableId(tableId);

        return dealerReorder(copyPlayerSessions);
    }

    private PlayerSession getNextDealer(List<PlayerSession> playerSessions) {
        Optional<Pair<Integer, PlayerSession>> currentDealerOpt = getCurrentDealerWithIndex(playerSessions);

        if (currentDealerOpt.isPresent()) {
            Pair<Integer, PlayerSession> currentDealerWithIndex = currentDealerOpt.get();

            int dealerIndex = currentDealerWithIndex.getFirst();
            PlayerSession currentDealer = currentDealerWithIndex.getSecond();

            playerSessions = sortDealerLast(playerSessions, dealerIndex);

            int numPlayers = playerSessions.size();
            for (int index = 0; index < numPlayers; index++) {
                PlayerSession thisPlayerSession = playerSessions.get(index);

                if (thisPlayerSession.getPosition()
                        .equals(currentDealer.getPosition())) {
                    int nextIndex = index + 1;
                    if (nextIndex >= numPlayers) {
                        nextIndex = 0;
                    }
                    return playerSessions.get(nextIndex);
                }
            }
        } else {
            return playerSessions.get(RANDOM.nextInt(playerSessions.size()));
        }
        throw new RuntimeException("Failed to get next dealer");
    }

    private void setNextDealer(UUID tableId, PlayerSession nextDealer) {
        playerSessionRepository.resetDealerForTableId(tableId);
        playerSessionRepository.setDealer(nextDealer.getId());
    }

    private List<PlayerSession> dealerReorder(List<PlayerSession> playerSessions) {
        int dealerIndex = getDealerIndex(playerSessions);
        return sortDealerLast(playerSessions, dealerIndex);
    }

    //todo: maybe tidy with dto
    public Optional<Pair<Integer, PlayerSession>> getCurrentDealerWithIndex(List<PlayerSession> playerSessions) {
        for (int index = 0; index < playerSessions.size(); index++) {
            PlayerSession playerSession = playerSessions.get(index);
            if (Boolean.TRUE.equals(playerSession.getDealer())) {
                return Optional.of(Pair.of(index, playerSession));
            }
        }
        return Optional.empty();
    }

    public PlayerSession getCurrentDealer(List<PlayerSession> playerSessions) {
        for (PlayerSession playerSession : playerSessions) {
            if (Boolean.TRUE.equals(playerSession.getDealer())) {
                return playerSession;
            }
        }
        throw new RuntimeException("No Dealer Found in player sessions (getCurrentDealer)");
    }

    private int getDealerIndex(List<PlayerSession> playerSessions) {
        for (int index = 0; index < playerSessions.size(); index++) {
            PlayerSession playerSession = playerSessions.get(index);
            if (Boolean.TRUE.equals(playerSession.getDealer())) {
                return index;
            }
        }
        throw new RuntimeException("No Dealer Found in player sessions (getDealerIndex)");
    }

    private List<PlayerSession> sortDealerLast(List<PlayerSession> playerSessions, int dealerIndex) {
        int start = dealerIndex + 1;
        if (start > playerSessions.size()) {
            start = 0;
        }
        List<PlayerSession> dealerSortedList = new ArrayList<>();
        for (int index = start; index < playerSessions.size(); index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        for (int index = 0; index < start; index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        return dealerSortedList;
    }
}
