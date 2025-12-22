package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.repository.TableRepository;
import com.twb.pokerapp.service.game.thread.GameThreadManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppStatsService {
    private final GameThreadManager threadManager;
    private final SimpUserRegistry userRegistry;

    private final TableRepository tableRepository;
    private final RoundRepository roundRepository;
    private final BettingRoundRepository bettingRoundRepository;
    private final PlayerSessionRepository playerSessionRepository;

    @Scheduled(fixedRate = 15000)
    @Transactional(readOnly = true)
    public void logAppStats() {
        int activeThreads = threadManager.getActiveThreadCount();
        int connectedUsers = userRegistry.getUserCount();
        int connectedPlayers = playerSessionRepository.countConnectedPlayers(ConnectionType.PLAYER);
        int connectedListeners = playerSessionRepository.countConnectedPlayers(ConnectionType.LISTENER);
        long tableCount = tableRepository.count();
        long roundCount = roundRepository.count();
        long bettingRoundCount = bettingRoundRepository.count();

        log.info("App Stats: Active Threads: {}, Users: {}, Players: {}, Listeners: {}, Tables: {}, Rounds: {}, BettingRounds: {}",
                activeThreads, connectedUsers, connectedPlayers, connectedListeners, tableCount, roundCount, bettingRoundCount);
    }
}
