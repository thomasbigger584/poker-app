package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PokerTable;
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

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class BettingRoundService {
    private final RoundRepository roundRepository;
    private final BettingRoundRepository repository;
    private final BettingRoundMapper mapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BettingRound create(PokerTable pokerTable, BettingRoundType state) {
        if (state == null) {
            throw new IllegalStateException("Could not create betting round as betting round state is null");
        }
        var roundOpt = roundRepository.findCurrentByTableId(pokerTable.getId());
        if (roundOpt.isEmpty()) {
            throw new IllegalStateException("Could not create betting round as round not found");
        }
        var bettingRound = new BettingRound();
        bettingRound.setRound(roundOpt.get());
        bettingRound.setType(state);
        bettingRound.setState(IN_PROGRESS);
        bettingRound.setPot(0d);

        bettingRound = repository.save(bettingRound);

        return bettingRound;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BettingRound updatePot(BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        bettingRound.setPot(bettingRound.getPot() + createActionDto.getAmount());
        bettingRound = repository.saveAndFlush(bettingRound);
        return bettingRound;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public BettingRound getTableBettingRound(UUID tableId) {
        return repository.findCurrentByTableId(tableId)
                .orElseThrow(() -> new IllegalStateException("BettingRound not found for table"));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public BettingRound getCurrentBettingRound(UUID roundId) {
        return repository.findCurrentByRoundId(roundId)
                .orElseThrow(() -> new IllegalStateException("BettingRound not found for current"));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public BettingRound getBettingRound(UUID bettingRoundId) {
        return repository.findById(bettingRoundId)
                .orElseThrow(() -> new IllegalStateException("BettingRound not found after update"));
    }

    public void setBettingRoundFinished(BettingRound bettingRound) {
        bettingRound.setState(FINISHED);
        repository.saveAndFlush(bettingRound);
    }
}
