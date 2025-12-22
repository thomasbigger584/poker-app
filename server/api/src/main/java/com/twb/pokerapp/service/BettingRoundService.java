package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.enumeration.BettingRoundType;
import com.twb.pokerapp.mapper.BettingRoundMapper;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.twb.pokerapp.domain.enumeration.BettingRoundState.FINISHED;
import static com.twb.pokerapp.domain.enumeration.BettingRoundState.IN_PROGRESS;
import static com.twb.pokerapp.repository.RepositoryUtil.getThrowGameInterrupted;

@Slf4j
@Component
@RequiredArgsConstructor
public class BettingRoundService {
    private final RoundRepository roundRepository;
    private final BettingRoundRepository repository;
    private final BettingRoundMapper mapper;

    @Transactional
    public BettingRound create(UUID tableId, BettingRoundType state) {
        if (state == null) {
            throw new IllegalStateException("Could not create betting round as betting round state is null");
        }
        var round = getThrowGameInterrupted(roundRepository.findCurrentByTableId(tableId), "Round Not Found");
        var bettingRound = new BettingRound();
        bettingRound.setRound(round);
        bettingRound.setType(state);
        bettingRound.setState(IN_PROGRESS);
        bettingRound.setPot(0d);

        bettingRound = repository.save(bettingRound);

        return bettingRound;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public BettingRound setBettingRoundFinished(BettingRound bettingRound) {
        bettingRound.setState(FINISHED);
        return repository.save(bettingRound);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public BettingRound updatePot(BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        bettingRound.setPot(bettingRound.getPot() + createActionDto.getAmount());
        bettingRound = repository.save(bettingRound);
        return bettingRound;
    }
}
