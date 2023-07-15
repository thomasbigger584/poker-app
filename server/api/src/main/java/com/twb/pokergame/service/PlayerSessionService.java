package com.twb.pokergame.service;

import com.twb.pokergame.domain.AppUser;
import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.domain.enumeration.ConnectionState;
import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.mapper.PlayerSessionMapper;
import com.twb.pokergame.repository.PlayerSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class PlayerSessionService {
    private final PlayerSessionRepository repository;
    private final PlayerSessionMapper mapper;

    public PlayerSessionDTO connectUserToRound(AppUser user, Round round) {
        PokerTable pokerTable = round.getPokerTable();

        Optional<PlayerSession> sessionOpt = repository
                .findByTableIdAndUsername(pokerTable.getId(), user.getUsername());

        PlayerSession session;
        if (sessionOpt.isPresent()) {
            session = sessionOpt.get();
        } else {
            session = new PlayerSession();
            session.setUser(user);
            session.setPokerTable(pokerTable);
        }

        session.setRound(round);
//          playerSession.setPosition(); //: todo: calculate position
        session.setConnectionState(ConnectionState.CONNECTED);
        session = repository.saveAndFlush(session);
        return mapper.modelToDto(session);
    }
}
