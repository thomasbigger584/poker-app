package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.RoundPot;
import com.twb.pokerapp.mapper.RoundPotMapper;
import com.twb.pokerapp.repository.RoundPotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoundPotService {
    private final RoundPotRepository repository;
    private final RoundPotMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void distributeSliceToPots(Round round, double amount, List<PlayerSession> eligiblePlayers) {
        var pots = round.getRoundPots();

        if (!pots.isEmpty()) {
            var lastPot = pots.getLast();
            if (CollectionUtils.isEqualCollection(lastPot.getEligiblePlayers(), eligiblePlayers)) {
                lastPot.setPotAmount(lastPot.getPotAmount() + amount);
                return;
            }
        }

        pots.add(create(round, amount, eligiblePlayers, pots.size()));
    }

    private RoundPot create(Round round, double amount, List<PlayerSession> eligiblePlayers, int potIndex) {
        var roundPot = new RoundPot();
        roundPot.setRound(round);
        roundPot.setPotAmount(amount);
        roundPot.setEligiblePlayers(new ArrayList<>(eligiblePlayers));
        roundPot.setPotIndex(potIndex);

        return repository.save(roundPot);
    }
}
