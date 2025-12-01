package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.mapper.PlayerSessionMapper;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PlayerSessionService {
    private final PlayerSessionRepository repository;
    private final PlayerSessionMapper mapper;

    public PlayerSessionDTO connectUserToRound(AppUser user, ConnectionType connectionType, PokerTable table) {
        var tableId = table.getId();
        var username = user.getUsername();

        var sessionOpt = repository
                .findByTableIdAndUsername(tableId, username);

        PlayerSession session;
        if (sessionOpt.isPresent()) {
            session = sessionOpt.get();
        } else {
            session = new PlayerSession();
            session.setUser(user);
            session.setPokerTable(table);
        }

        session.setConnectionType(connectionType);
        session.setSessionState(SessionState.CONNECTED);

        if (connectionType == ConnectionType.PLAYER) {
            var position = getSessionTablePosition(table);
            session.setPosition(position);
            session.setFunds(1000d); // todo: dynamically set funds
        }

        session = repository.saveAndFlush(session);
        return mapper.modelToDto(session);
    }

    //todo: think about what to do with funds, it should be persisted elsewhere,
    // probably on AppUser or separate Bank table
    public void disconnectUser(PlayerSession playerSession) {
        playerSession.setDealer(null);
        playerSession.setFunds(null);
        playerSession.setPokerTable(null);
        playerSession.setConnectionType(null);
        playerSession.setSessionState(SessionState.DISCONNECTED);

        repository.saveAndFlush(playerSession);
    }

    private int getSessionTablePosition(PokerTable table) {
        var sessions = repository.findConnectedPlayersByTableId(table.getId());
        var otherPlayersMaxCount = table.getGameType().getMaxPlayerCount() - 1;
        for (var position = 1; position <= otherPlayersMaxCount; position++) {
            if (!isPositionAlreadyTaken(sessions, position)) {
                return position;
            }
        }
        if (otherPlayersMaxCount == 0) {
            return 1;
        }
        //return a suitable position if no positions yet taken
        if (otherPlayersMaxCount % 2 == 0) {
            return (otherPlayersMaxCount / 2);
        }
        return (otherPlayersMaxCount / 2) + 1;
    }

    private boolean isPositionAlreadyTaken(List<PlayerSession> sessions, int position) {
        for (var session : sessions) {
            var thisPosition = session.getPosition();
            if (thisPosition != null && thisPosition == position) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<PlayerSessionDTO> getByTableId(UUID tableId) {
        return repository.findConnectedByTableId(tableId)
                .stream().map(mapper::modelToDto).toList();
    }
}
