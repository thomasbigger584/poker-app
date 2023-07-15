package com.twb.pokergame.service;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.dto.round.RoundDTO;
import com.twb.pokergame.exception.NotFoundException;
import com.twb.pokergame.mapper.RoundMapper;
import com.twb.pokergame.repository.RoundRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class RoundService {
    private final RoundRepository repository;
    private final RoundMapper mapper;

    public Round createSingle(PokerTable pokerTable) {
        List<Round> previousRounds =
                repository.findByPokerTableId(pokerTable.getId());

        for (Round round : previousRounds) {
            round.setRoundState(RoundState.COMPLETED);
            repository.save(round);
        }

        return create(pokerTable);
    }

    public Round create(PokerTable pokerTable) {
        Round round = new Round();
        round.setRoundState(RoundState.INIT);
        round.setPokerTable(pokerTable);

        round = repository.saveAndFlush(round);
        pokerTable.getRounds().add(round);

        return round;
    }

    private void completePreviousRounds(List<Round> previousRounds) {
        for (Round round : previousRounds) {
            round.setRoundState(RoundState.COMPLETED);
            repository.save(round);
        }
    }

    @Transactional(readOnly = true)
    public RoundDTO getCurrent(UUID tableId) {
        Optional<Round> roundOpt = repository.findCurrentByTableId(tableId);
        if (roundOpt.isEmpty()) {
            throw new NotFoundException("Round not found for table: " + tableId);
        }
        return mapper.modelToDto(roundOpt.get());
    }
}
