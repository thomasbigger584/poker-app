package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.dto.pokertable.CreateTableDTO;
import com.twb.pokerapp.dto.pokertable.TableDTO;
import com.twb.pokerapp.mapper.TableMapper;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.repository.TableRepository;
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

    @PostConstruct
    public void init() {

        // on application restart, complete all rounds previously saved
        // only doing this here to ensure ordering with creating dummy poker table data
        List<Round> allRounds = roundRepository.findAllNotFinished();
        for (Round round : allRounds) {
            round.setRoundState(RoundState.FINISH);
            roundRepository.save(round);
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
        return table;
    }

    @Transactional(readOnly = true)
    public Page<TableDTO> getAll(Pageable pageable) {
        Page<PokerTable> page = repository.findAll(pageable);
        return page.map(mapper::modelToDto);
    }
}
