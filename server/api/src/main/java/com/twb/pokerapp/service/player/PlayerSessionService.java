package com.twb.pokerapp.service.player;

import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.PhysicalUser;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.mapper.PlayerSessionMapper;
import com.twb.pokerapp.proto.ConnectionType;
import com.twb.pokerapp.proto.PlayerSessionDTO;
import com.twb.pokerapp.proto.SessionState;
import com.twb.pokerapp.proto.TransactionHistoryType;
import com.twb.pokerapp.repository.HandRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.UserRepository;
import com.twb.pokerapp.service.TransactionHistoryService;
import com.twb.pokerapp.service.game.exception.GamePlayerErrorLogException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerSessionService {
    private final UserRepository userRepository;
    private final PlayerSessionRepository repository;
    private final HandRepository handRepository;
    private final PlayerSessionMapper mapper;
    private final TransactionHistoryService transactionHistoryService;

    @Transactional(propagation = Propagation.MANDATORY)
    public void reset() {
        repository.findAll().forEach(this::disconnectUser);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public PlayerSessionDTO connectUserToRound(PokerTable table, AppUser user, ConnectionType connectionType, BigDecimal buyInAmount) {
        var sessionOpt = repository.findByTableIdAndUsername(table.getId(), user.getUsername());

        if (sessionOpt.isPresent() && sessionOpt.get().getSessionState() == SessionState.SESSION_STATE_CONNECTED) {
            var message = String.format("User %s is already connected to table %s", user.getUsername(), table.getId());
            log.warn(message);
            throw new GamePlayerErrorLogException(sessionOpt.get(), message);
        }

        var session = sessionOpt.orElseGet(PlayerSession::new);
        session.setPokerTable(table);
        session.setUser(user);
        session.setConnectionType(connectionType);
        session.setActive(false);
        session.setSessionState(SessionState.SESSION_STATE_CONNECTED);

        if (connectionType == ConnectionType.CONNECTION_TYPE_PLAYER) {
            var position = getNextAvailablePosition(table);
            session.setPosition(position);
            session.setFunds(buyInAmount);

            if (user instanceof PhysicalUser physicalUser) {
                physicalUser.setTotalFunds(physicalUser.getTotalFunds().subtract(buyInAmount));
                userRepository.save(physicalUser);
                transactionHistoryService.create(physicalUser, buyInAmount.negate(), TransactionHistoryType.TRANSACTION_HISTORY_TYPE_BUYIN);
            }
        }

        session = repository.save(session);
        return mapper.modelToDto(session);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void disconnectUser(PlayerSession session) {
        if (session.getSessionState() == SessionState.SESSION_STATE_DISCONNECTED) {
            return;
        }
        log.info("Disconnecting user {}", session.getUser().getUsername());
        var sessionFundsRemaining = session.getFunds();
        if (session.getConnectionType() == ConnectionType.CONNECTION_TYPE_PLAYER
                && sessionFundsRemaining != null) {
            var user = session.getUser();
            if (user instanceof PhysicalUser physicalUser) {
                var totalNewFunds = physicalUser.getTotalFunds().add(sessionFundsRemaining);
                physicalUser.setTotalFunds(totalNewFunds);
                userRepository.save(physicalUser);
                transactionHistoryService.create(physicalUser, sessionFundsRemaining, TransactionHistoryType.TRANSACTION_HISTORY_TYPE_CASHOUT);
            }
        }

        session.setSessionState(SessionState.SESSION_STATE_DISCONNECTED);
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
        var connectedPositions = repository.findConnectedPlayersByTableId(table.getId()).stream()
                .map(PlayerSession::getPosition)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
        // A player who left mid-hand keeps their seat reserved until that hand finishes, so it can't
        // be re-occupied while the hand is still resolving (the leaver auto-folds out of it).
        var reservedPositions = handRepository.findOccupiedPositionsInCurrentRound(table.getId());

        for (var position = 1; position <= table.getMaxPlayers(); position++) {
            if (!connectedPositions.contains(position) && !reservedPositions.contains(position)) {
                return position;
            }
        }
        throw new GamePlayerErrorLogException("Table %s is full".formatted(table.getId()));
    }
}
