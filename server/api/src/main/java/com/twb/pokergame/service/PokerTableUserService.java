package com.twb.pokergame.service;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.PokerTableUser;
import com.twb.pokergame.domain.User;
import com.twb.pokergame.mapper.PokerTableUserMapper;
import com.twb.pokergame.repository.PokerTableUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class PokerTableUserService {
    private final PokerTableUserRepository repository;
    private final PokerTableUserMapper mapper;

    public PokerTableUser create(PokerTable pokerTable, User user) {
        PokerTableUser pokerTableUser = new PokerTableUser();
        pokerTableUser.setPokerTable(pokerTable);
        pokerTableUser.setUser(user);

        return repository.saveAndFlush(pokerTableUser);
    }

    public void remove(PokerTableUser pokerTableUser) {
        repository.delete(pokerTableUser);
    }

}
