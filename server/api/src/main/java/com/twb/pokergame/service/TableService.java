package com.twb.pokergame.service;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.domain.enumeration.GameType;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.dto.pokertable.CreateTableDTO;
import com.twb.pokergame.dto.pokertable.TableDTO;
import com.twb.pokergame.mapper.TableMapper;
import com.twb.pokergame.repository.RoundRepository;
import com.twb.pokergame.repository.TableRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class TableService {
    private final TableRepository repository;
    private final TableMapper mapper;

    private final RoundRepository roundRepository;
    private final RoundService roundService;

    @PostConstruct
    public void init() {

        // on application restart, complete all rounds previously saved
        // only doing this here to ensure ordering with creating dummy poker table data
        List<Round> allRounds = roundRepository.findAllNotCompleted();
        for (Round round : allRounds) {
            round.setRoundState(RoundState.COMPLETED);
            roundRepository.save(round);

            roundService.create(round.getPokerTable());
        }


        List<PokerTable> allPokerTables = repository.findAll();
        if (allPokerTables.isEmpty()) {
            CreateTableDTO createTableDto1 = new CreateTableDTO();
            createTableDto1.setName("Poker Table 1");
            createTableDto1.setGameType(GameType.TEXAS_HOLDEM);
            create(createTableDto1);

            CreateTableDTO createTableDto2 = new CreateTableDTO();
            createTableDto2.setName("Poker Table 2");
            createTableDto2.setGameType(GameType.BLACKJACK);
            create(createTableDto2);
        }
    }

    public PokerTable create(CreateTableDTO dto) {
        PokerTable table = mapper.createDtoToModel(dto);
        table = repository.save(table);

        roundService.create(table);

        return table;
    }

    @Transactional(readOnly = true)
    public Page<TableDTO> getAll(Pageable pageable) {
        Page<PokerTable> page = repository.findAll(pageable);
        return page.map(mapper::modelToDto);
    }
}
