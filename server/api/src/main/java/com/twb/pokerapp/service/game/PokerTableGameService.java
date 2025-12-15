package com.twb.pokerapp.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.repository.TableRepository;
import com.twb.pokerapp.repository.UserRepository;
import com.twb.pokerapp.service.PlayerSessionService;
import com.twb.pokerapp.service.game.thread.GameThreadManager;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class PokerTableGameService {
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final PlayerSessionRepository playerSessionRepository;
    private final RoundRepository roundRepository;

    private final PlayerSessionService playerSessionService;

    private final GameThreadManager threadManager;
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;
    private final XSync<UUID> mutex;
    private final ApplicationContext context;

    public ServerMessageDTO onUserConnected(UUID tableId, ConnectionType connectionType, String username, Double buyInAmount) {
        return mutex.evaluate(tableId, () -> {
            var tableOpt = tableRepository.findById(tableId);
            if (tableOpt.isEmpty()) {
                var message = "Failed to connect user %s to table %s as table not found".formatted(username, tableId);
                throw new RuntimeException(message);
            }
            var table = tableOpt.get();

            if (connectionType == ConnectionType.PLAYER) {
                if (buyInAmount < table.getMinBuyin() || buyInAmount > table.getMaxBuyin()) {
                    var message = "Buy-In amount must be between %.2f and %.2f for table %s".formatted(table.getMinBuyin(), table.getMaxBuyin(), tableId);
                    throw new RuntimeException(message);
                }
            }

            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                var message = "Failed to connect user %s to table %s as user not found".formatted(username, tableId);
                throw new RuntimeException(message);
            }
            var appUser = userOpt.get();
            if (connectionType == ConnectionType.PLAYER) {
                if (buyInAmount < appUser.getTotalFunds()) {
                    var message = "User %s does not have enough total funds for Buy-In %.2f, has %.2f".formatted(username, buyInAmount, appUser.getTotalFunds());
                    throw new RuntimeException(message);
                }
            }

            var playerSessionOpt = playerSessionRepository.findByTableIdAndUsername(tableId, username);
            if (playerSessionOpt.isPresent()) {
                var message = "User %s already connected to table %s".formatted(username, tableId);
                throw new RuntimeException(message);
            }

            if (connectionType == ConnectionType.PLAYER) {
                threadManager.createIfNotExist(table);
            }

            var connectedPlayerSession = playerSessionService.connectUserToRound(appUser, connectionType, table, buyInAmount);
            var allPlayerSessions = playerSessionService.getByTableId(tableId);

            dispatcher.send(tableId, messageFactory.playerConnected(connectedPlayerSession));
            return messageFactory.playerSubscribed(allPlayerSessions);
        });
    }

    public void onPlayerAction(UUID tableId, String username, CreatePlayerActionDTO action) {
        mutex.execute(tableId, () -> {
            var tableOpt = tableRepository.findById(tableId);
            if (tableOpt.isEmpty()) {
                log.error("No table {} found for id", tableId);
                return;
            }
            var table = tableOpt.get();

            var gameThreadOpt = threadManager.getIfExists(tableId);
            if (gameThreadOpt.isEmpty()) {
                log.error("No game thread exists for table {}", tableId);
                return;
            }
            var gameThread = gameThreadOpt.get();
            var playerSessionOpt = playerSessionRepository
                    .findByTableIdAndUsername(tableId, username);
            if (playerSessionOpt.isEmpty()) {
                log.error("No player {} found on table {}", username, tableId);
                return;
            }
            var playerSession = playerSessionOpt.get();
            if (playerSession.getConnectionType() == ConnectionType.LISTENER) {
                log.error("Player {} is a listener on table", playerSession.getId());
                return;
            }

            var playerTurnLatch = gameThread.getPlayerTurnLatch();
            if (playerTurnLatch == null
                    || !username.equals(playerTurnLatch.playerSession().getUser().getUsername())) {
                log.error("Not waiting for {} to play on table {}", username, tableId);
                return;
            }

            var gameType = table.getGameType();
            var playerActionService = gameType.getPlayerActionService(context);
            playerActionService.playerAction(playerSession, gameThread, action);
        });
    }

    public void onUserDisconnected(UUID tableId, String username) {
        mutex.execute(tableId, () -> {
            var tableOpt = tableRepository.findById(tableId);
            if (tableOpt.isEmpty()) {
                log.error("No table found for ID {}", tableId);
                return;
            }
            var table = tableOpt.get();

            var threadOpt = threadManager.getIfExists(tableId);
            if (threadOpt.isEmpty()) {
                log.error("No game thread for table ID: {}", tableId);
                return;
            }
            var gameThread = threadOpt.get();

            var playerSessionOpt =
                    playerSessionRepository.findByTableIdAndUsername(tableId, username);
            if (playerSessionOpt.isEmpty()) {
                log.error("No player {} found on table {}", username, tableId);
                return;
            }
            var playerSession = playerSessionOpt.get();

            var roundOpt = roundRepository.findCurrentByTableId(tableId);

            if (roundOpt.isPresent()) {
                var playerActionService = table.getGameType().getPlayerActionService(context);
                var action = new CreatePlayerActionDTO();
                action.setAction(ActionType.FOLD);

                playerActionService.playerAction(playerSession, gameThread, action);
            }

            playerSessionService.disconnectUser(playerSession);

            var playerSessions =
                    playerSessionRepository.findConnectedPlayersByTableId_Lock(tableId);
            if (CollectionUtils.isEmpty(playerSessions)) {
                gameThread.stopGame();
            }
            var message = messageFactory.playerDisconnected(username);
            dispatcher.send(tableId, message);

            if (playerSessions.size() < 2) {
                gameThread.stopGame();
            }
        });
    }
}
