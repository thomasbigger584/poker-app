package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.BettingRoundRefund;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.domain.enumeration.BettingRoundType;
import com.twb.pokerapp.mapper.BettingRoundMapper;
import com.twb.pokerapp.repository.BettingRoundRefundRepository;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.RoundRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.twb.pokerapp.domain.enumeration.BettingRoundState.*;
import static com.twb.pokerapp.repository.RepositoryUtil.getThrowGameInterrupted;

@Slf4j
@Component
@RequiredArgsConstructor
public class BettingRoundService {
    private final RoundRepository roundRepository;
    private final BettingRoundRepository repository;
    private final BettingRoundRefundRepository refundRepository;
    private final BettingRoundMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void reset() {
        repository.findAll()
                .forEach(bettingRound -> setState(bettingRound, FAILED));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void reset(UUID tableId) {
        repository.findCurrentByTableId(tableId)
                .ifPresent(bettingRound -> setState(bettingRound, FAILED));
    }

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

        bettingRound = repository.save(bettingRound);

        return bettingRound;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public BettingRoundRefund createRefund(PlayerSession playerSession, BettingRound bettingRound, double refundAmount) {
        var refund = new BettingRoundRefund();
        refund.setPlayerSession(playerSession);
        refund.setBettingRound(bettingRound);
        refund.setAmount(refundAmount);

        refund = refundRepository.save(refund);
        bettingRound.getBettingRoundRefunds().add(refund);

        return refund;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public BettingRound setState(BettingRound bettingRound, BettingRoundState state) {
        bettingRound.setState(state);
        return repository.save(bettingRound);
    }
}
