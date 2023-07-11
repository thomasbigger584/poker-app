package com.twb.pokergame.service;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.enumeration.GameType;
import com.twb.pokergame.dto.pokertable.PokerTableDTO;
import com.twb.pokergame.mapper.PokerTableMapper;
import com.twb.pokergame.repository.PokerTableRepository;
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
public class PokerTableService {
    private final PokerTableRepository repository;
    private final PokerTableMapper mapper;

    @PostConstruct
    public void init() {
        List<PokerTable> allTables = repository.findAll();
        if (allTables.isEmpty()) {
            PokerTable pokerTable = new PokerTable();
            pokerTable.setName("Poker Table");
            pokerTable.setGameType(GameType.TEXAS_HOLDEM);

            repository.save(pokerTable);
        }
    }

    @Transactional(readOnly = true)
    public Page<PokerTableDTO> getAll(Pageable pageable) {
        Page<PokerTable> page = repository.findAll(pageable);
        return page.map(mapper::modelToDto);
    }
}
