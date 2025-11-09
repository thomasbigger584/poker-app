package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.dto.round.RoundDTO;
import com.twb.pokerapp.exception.NotFoundException;
import com.twb.pokerapp.mapper.RoundMapper;
import com.twb.pokerapp.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class RoundService {
    private final RoundRepository repository;
    private final RoundMapper mapper;

    public Round create(PokerTable pokerTable) {
        var round = new Round();
        round.setRoundState(RoundState.WAITING_FOR_PLAYERS);
        round.setPokerTable(pokerTable);

        round = repository.saveAndFlush(round);

        return round;
    }

    @Transactional(readOnly = true)
    public RoundDTO getCurrent(UUID tableId) {
        var roundOpt = repository.findCurrentByTableId(tableId);
        if (roundOpt.isEmpty()) {
            throw new NotFoundException("Round not found for table: " + tableId);
        }
        return mapper.modelToDto(roundOpt.get());
    }

    @Transactional(readOnly = true)
    public List<RoundDTO> getByTableId(UUID tableId) {
        return repository.findByTableId(tableId)
                .stream()
                .map(mapper::modelToDto)
                .toList();
    }
}
