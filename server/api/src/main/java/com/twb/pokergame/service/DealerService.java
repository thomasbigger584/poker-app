package com.twb.pokergame.service;

import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.repository.PlayerSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class DealerService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final PlayerSessionRepository playerSessionRepository;

    public List<PlayerSession> nextDealerReorder(List<PlayerSession> playerSessions) {
        List<PlayerSession> copyPlayerSessions = new ArrayList<>(playerSessions);

        PlayerSession nextDealer = getNextDealer(copyPlayerSessions);
        setNextDealer(playerSessions, nextDealer);

        int dealerIndex = getDealerIndex(copyPlayerSessions);
        dealerIndex = dealerIndex + 1;
        if (dealerIndex >= copyPlayerSessions.size()) {
            dealerIndex = 0;
        }
        return sortDealerLast(copyPlayerSessions, dealerIndex);
    }

    private PlayerSession getNextDealer(List<PlayerSession> copyPlayerSessions) {
        Optional<Pair<Integer, PlayerSession>> currentDealerOpt = getCurrentDealerWithIndex(copyPlayerSessions);

        PlayerSession currentDealer;
        if (currentDealerOpt.isPresent()) {
            Pair<Integer, PlayerSession> currentDealerWithIndex = currentDealerOpt.get();

            int dealerIndex = currentDealerWithIndex.getFirst();
            currentDealer = currentDealerWithIndex.getSecond();

            copyPlayerSessions = sortDealerLast(copyPlayerSessions, dealerIndex);

            int numPlayers = copyPlayerSessions.size();
            for (int index = 0; index < numPlayers; index++) {
                PlayerSession thisPlayerSession = copyPlayerSessions.get(index);

                if (thisPlayerSession.getPosition()
                        .equals(currentDealer.getPosition())) {
                    int nextIndex = index + 1;
                    if (nextIndex >= numPlayers) {
                        nextIndex = 0;
                    }
                    return copyPlayerSessions.get(nextIndex);
                }
            }
        } else {
            return copyPlayerSessions.get(RANDOM.nextInt(copyPlayerSessions.size()));
        }
        throw new RuntimeException("Failed to get next dealer");
    }

    private void setNextDealer(List<PlayerSession> playerSessions, PlayerSession nextDealer) {
        for (PlayerSession playerSession : playerSessions) {
            playerSession.setDealer(nextDealer.getId().equals(playerSession.getId()));
        }
        playerSessionRepository.saveAllAndFlush(playerSessions);
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
        throw new RuntimeException("No Dealer Found in player sessions");
    }

    private int getDealerIndex(List<PlayerSession> playerSessions) {
        for (int index = 0; index < playerSessions.size(); index++) {
            PlayerSession playerSession = playerSessions.get(index);
            if (playerSession.getDealer()) {
                return index;
            }
        }
        throw new RuntimeException("No Dealer Found in player sessions");
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
