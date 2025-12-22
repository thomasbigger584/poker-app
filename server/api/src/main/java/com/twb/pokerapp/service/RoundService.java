package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.mapper.RoundMapper;
import com.twb.pokerapp.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class RoundService {
    private final RoundRepository repository;
    private final RoundMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public Round create(PokerTable table) {
        var round = new Round();
        round.setRoundState(RoundState.WAITING_FOR_PLAYERS);
        round.setPokerTable(table);
        round.setPot(0d);
        round = repository.save(round);
        return round;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Round updatePot(Round round, BettingRound bettingRound) {
        var newPot = round.getPot() + bettingRound.getPot();
        round.setPot(newPot);
        round = repository.save(round);
        return round;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void setRoundState(Round round, RoundState roundState) {
        round.setRoundState(roundState);
        repository.save(round);
    }
}
