package com.twb.pokergame.service;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.enumeration.GameType;
import com.twb.pokergame.repository.PokerTableRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PokerTableService {
    private final PokerTableRepository repository;

    @PostConstruct
    public void init() {
        PokerTable pokerTable = new PokerTable();
        pokerTable.setName("Poker Table");
        pokerTable.setGameType(GameType.TEXAS_HOLDEM);

        repository.save(pokerTable);
    }
}
