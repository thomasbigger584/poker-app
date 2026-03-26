package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.exception.game.GamePlayerErrorLogException;
import com.twb.pokerapp.mapper.PlayerSessionMapper;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerSessionService {
    private final UserRepository userRepository;
    private final PlayerSessionRepository repository;
    private final PlayerSessionMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public PlayerSessionDTO connectUserToRound(PokerTable table, AppUser user, ConnectionType connectionType, Double buyInAmount) {
        var sessionOpt = repository.findByTableIdAndUsername(table.getId(), user.getUsername());
        
        if (sessionOpt.isPresent() && sessionOpt.get().getSessionState() == SessionState.CONNECTED) {
            var message = String.format("User %s is already connected to table %s", user.getUsername(), table.getId());
            log.warn(message);
            throw new GamePlayerErrorLogException(sessionOpt.get(), message);
        }

        var session = sessionOpt.orElseGet(PlayerSession::new);
        session.setPokerTable(table);
        session.setUser(user);
        session.setConnectionType(connectionType);
        session.setActive(false);
        session.setSessionState(SessionState.CONNECTED);

        if (connectionType == ConnectionType.PLAYER) {
            var position = getNextAvailablePosition(table);
            session.setPosition(position);
            session.setFunds(buyInAmount);

            user.setTotalFunds(user.getTotalFunds() - buyInAmount);
            userRepository.save(user);
        }

        session = repository.save(session);
        return mapper.modelToDto(session);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void disconnectUser(PlayerSession session) {
        if (session.getSessionState() == SessionState.DISCONNECTED) {
            return;
        }

        if (session.getConnectionType() == ConnectionType.PLAYER && session.getFunds() != null) {
            var user = session.getUser();
            user.setTotalFunds(user.getTotalFunds() + session.getFunds());
            userRepository.save(user);
        }

        session.setSessionState(SessionState.DISCONNECTED);
        session.setPokerTable(null);
        session.setRound(null);
        session.setActive(null);
        session.setFunds(null);
        session.setDealer(null);
        session.setCurrent(null);
        session.setConnectionType(null);
        
        repository.save(session);
    }

    private int getNextAvailablePosition(PokerTable table) {
        var sessions = repository.findConnectedPlayersByTableId(table.getId());
        for (var position = 1; position <= table.getMaxPlayers(); position++) {
            final var finalPosition = position;
            if (sessions.stream().noneMatch(s ->
                    s.getPosition() != null && s.getPosition() == finalPosition)) {
                return position;
            }
        }
        return 1;
    }
}
