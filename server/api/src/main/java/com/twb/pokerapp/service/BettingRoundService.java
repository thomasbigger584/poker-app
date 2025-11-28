package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.domain.enumeration.BettingRoundType;
import com.twb.pokerapp.mapper.BettingRoundMapper;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.twb.pokerapp.domain.enumeration.BettingRoundState.IN_PROGRESS;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class BettingRoundService {
    private final BettingRoundRepository repository;
    private final BettingRoundMapper mapper;

    public BettingRound create(Round round, BettingRoundType state) {
        if (state == null) {
            throw new IllegalStateException("Could not create betting round as betting round state is null");
        }
        var bettingRound = new BettingRound();
        bettingRound.setRound(round);
        bettingRound.setType(state);
        bettingRound.setState(IN_PROGRESS);
        bettingRound.setPot(0d);

        bettingRound = repository.save(bettingRound);

        return bettingRound;
    }

    public BettingRound updatePot(BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        double newRoundPot = bettingRound.getPot() + createActionDto.getAmount();
        bettingRound.setPot(newRoundPot);

        bettingRound = repository.saveAndFlush(bettingRound);

        return bettingRound;
    }
}
