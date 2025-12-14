package com.twb.pokerapp.service.table;

import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.dto.table.AvailableTableDTO;
import com.twb.pokerapp.dto.table.CreateTableDTO;
import com.twb.pokerapp.mapper.TableMapper;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.repository.TableRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Component
@Transactional
@RequiredArgsConstructor
public class TableService {
    private final ApplicationContext context;

    private final TableRepository repository;
    private final TableMapper mapper;

    private final RoundRepository roundRepository;
    private final PlayerSessionRepository playerSessionRepository;

    @PostConstruct
    public void init() {
        finishAllUnfinishedRounds();
        createTestTables();
    }

    private void finishAllUnfinishedRounds() {
        // on application restart, complete all rounds previously saved
        // only doing this here to ensure ordering with creating dummy poker table data
        var unfinishedRounds = roundRepository.findAllNotFinished();
        for (var round : unfinishedRounds) {
            round.setRoundState(RoundState.FINISHED);
            roundRepository.save(round);
        }
    }

    private void createTestTables() {
        var allTables = repository.findAll();
        if (allTables.isEmpty()) {
            var createTableDto1 = new CreateTableDTO();
            createTableDto1.setName("Poker Table 1");
            createTableDto1.setGameType(GameType.TEXAS_HOLDEM);
            createTableDto1.setMinPlayers(2);
            createTableDto1.setMaxPlayers(6);
            createTableDto1.setMinBuyin(100d);
            createTableDto1.setMaxBuyin(10_000d);
            create(createTableDto1);
        }
    }

    public PokerTable create(CreateTableDTO dto) {
        dto.getGameType().getValidationService(context).validate(dto);
        var table = mapper.createDtoToModel(dto);
        table = repository.save(table);
        return table;
    }

    @Transactional(readOnly = true)
    public Page<AvailableTableDTO> getAllAvailable(Pageable pageable) {
        var page = repository.findAll(pageable);
        return page.map(table -> {
            var availableTableDTO = new AvailableTableDTO();
            availableTableDTO.setTable(mapper.modelToDto(table));
            availableTableDTO.setPlayersConnected(playerSessionRepository.countConnectedPlayersByTableId(table.getId()));
            return availableTableDTO;
        });
    }
}
