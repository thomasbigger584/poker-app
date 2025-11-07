package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.mapper.BettingRoundMapper;
import com.twb.pokerapp.repository.BettingRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class BettingRoundService {
    private final BettingRoundRepository repository;
    private final BettingRoundMapper mapper;

    public BettingRound create(Round round) {
        BettingRoundState state = round.getRoundState().getBettingRoundState();
        if (state == null) {
            throw new IllegalStateException("Could not create betting round as betting round state is null");
        }

        BettingRound bettingRound = new BettingRound();
        bettingRound.setRound(round);
        bettingRound.setState(state);
        bettingRound.setPot(0d);

        bettingRound = repository.save(bettingRound);

        return bettingRound;
    }
}
