package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.mapper.RoundMapper;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class RoundService {
    private final RoundRepository repository;
    private final PlayerSessionRepository playerSessionRepository;
    private final RoundMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void reset() {
        repository.findAllCurrent()
                .forEach(round -> setState(round, RoundState.FAILED));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void reset(UUID tableId) {
        repository.findCurrentByTableId(tableId)
                .ifPresent(round -> setState(round, RoundState.FAILED));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Round create(PokerTable table, List<PlayerSession> playerSessions) {
        var round = new Round();
        round.setRoundState(RoundState.WAITING_FOR_PLAYERS);
        round.setPokerTable(table);
        round.setPlayerSessions(playerSessions);
        round = repository.save(round);
        for (var playerSession : playerSessions) {
            playerSession.setActive(true);
            playerSession.setRound(round);
        }
        playerSessionRepository.saveAll(playerSessions);
        return round;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void setState(Round round, RoundState roundState) {
        round.setRoundState(roundState);
        repository.save(round);
    }
}
