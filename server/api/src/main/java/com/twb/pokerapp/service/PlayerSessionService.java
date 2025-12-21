package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.mapper.PlayerSessionMapper;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.TableRepository;
import com.twb.pokerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerSessionService {
    private final TableRepository tableRepository;
    private final UserRepository userRepository;
    private final PlayerSessionRepository repository;
    private final PlayerSessionMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public PlayerSessionDTO connectUserToRound(PokerTable table, AppUser user,
                                               ConnectionType connectionType,
                                               Double buyInAmount) {
        var sessionOpt = repository
                .findByTableIdAndUsername(table.getId(), user.getUsername());
        PlayerSession session;
        if (sessionOpt.isPresent()) {
            session = sessionOpt.get();
        } else {
            session = new PlayerSession();
            session.setPokerTable(table);
        }

        session.setConnectionType(connectionType);
        session.setSessionState(SessionState.CONNECTED);

        if (connectionType == ConnectionType.PLAYER) {
            var position = getSessionTablePosition(table);
            session.setPosition(position);
            session.setFunds(buyInAmount);

            user.setTotalFunds(user.getTotalFunds() - buyInAmount);
        }

        user = userRepository.save(user);
        session.setUser(user);

        session = repository.save(session);
        return mapper.modelToDto(session);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void disconnectUser(PlayerSession playerSession) {
        var fundsLeftOver = playerSession.getFunds();
        if (fundsLeftOver == null) {
            fundsLeftOver = 0d;
        }
        var user = playerSession.getUser();
        user.setTotalFunds(user.getTotalFunds() + fundsLeftOver);
        user = userRepository.save(user);

        playerSession.setUser(user);
        playerSession.setDealer(null);
        playerSession.setFunds(null);
        playerSession.setPokerTable(null);
        playerSession.setConnectionType(null);
        playerSession.setSessionState(SessionState.DISCONNECTED);

        repository.save(playerSession);
    }

    private int getSessionTablePosition(PokerTable table) {
        var sessions = repository.findConnectedPlayersByTableId(table.getId());
        var otherPlayersMaxCount = table.getMaxPlayers() - 1;
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
