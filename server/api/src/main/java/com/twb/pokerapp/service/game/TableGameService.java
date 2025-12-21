package com.twb.pokerapp.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.exception.game.GamePlayerErrorLogException;
import com.twb.pokerapp.exception.game.GamePlayerLogException;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.TableRepository;
import com.twb.pokerapp.repository.UserRepository;
import com.twb.pokerapp.service.PlayerSessionService;
import com.twb.pokerapp.service.game.thread.GameLogService;
import com.twb.pokerapp.service.game.thread.GameThreadManager;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.twb.pokerapp.repository.RepositoryUtil.getThrowPlayerErrorLog;

@Slf4j
@Component
@RequiredArgsConstructor
public class TableGameService {
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final PlayerSessionRepository playerSessionRepository;
    private final PlayerSessionService playerSessionService;

    private final GameThreadManager threadManager;
    private final ServerMessageFactory messageFactory;
    private final GameLogService gameLogService;
    private final MessageDispatcher dispatcher;
    private final XSync<UUID> mutex;
    private final ApplicationContext context;
    private final TransactionTemplate transactionTemplate;

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
                    var message = "Buy-In amount must be between $%.2f and $%.2f for table %s".formatted(table.getMinBuyin(), table.getMaxBuyin(), tableId);
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
                if (buyInAmount > appUser.getTotalFunds()) {
                    var message = "User %s does not have enough total funds for Buy-In $%.2f, has $%.2f".formatted(username, buyInAmount, appUser.getTotalFunds());
                    throw new RuntimeException(message);
                }
            }
            var playerSessionOpt = playerSessionRepository.findByTableIdAndUsername(tableId, username);
            if (playerSessionOpt.isEmpty()) {
                if (connectionType == ConnectionType.PLAYER) {
                    threadManager.createIfNotExist(table);
                }
                var connectedPlayerSession = playerSessionService.connectUserToRound(tableId, appUser.getId(), connectionType, buyInAmount);
                dispatcher.send(tableId, messageFactory.playerConnected(connectedPlayerSession));
            }
            var allPlayerSessions = playerSessionService.getByTableId(tableId);
            return messageFactory.playerSubscribed(allPlayerSessions);
        });
    }

    public void onPlayerAction(UUID tableId, String username, CreatePlayerActionDTO action) {
        mutex.execute(tableId, () -> transactionTemplate.executeWithoutResult(status -> {
            try {
                var table = getThrowPlayerErrorLog(tableRepository.findById(tableId), "No table found for Table ID: " + tableId);
                var playerSession = getThrowPlayerErrorLog(playerSessionRepository.findByTableIdAndUsername(tableId, username), "No player %s found on table %s".formatted(username, tableId));
                if (playerSession.getConnectionType() == ConnectionType.LISTENER) {
                    throw new GamePlayerLogException(playerSession, "You are a listener on table, cannot perform actions");
                }
                var gameThread = getThrowPlayerErrorLog(threadManager.getIfExists(tableId), playerSession, "No game thread exists for table %s".formatted(tableId));
                var playerTurnLatch = gameThread.getPlayerTurnLatch();
                if (playerTurnLatch == null || !username.equals(playerTurnLatch.playerSession().getUser().getUsername())) {
                    throw new GamePlayerLogException(playerSession, "Not waiting for you to play on table");
                }
                var playerActionService = table.getGameType().getPlayerActionService(context);
                playerActionService.playerAction(playerSession, gameThread, action);
            } catch (GamePlayerLogException e) {
                gameLogService.sendLogMessage(e.getPlayerSession(), e.getMessage());
            } catch (GamePlayerErrorLogException e) {
                if (e.getPlayerSession() == null) {
                    gameLogService.sendErrorMessage(username, e.getMessage());
                } else {
                    gameLogService.sendErrorMessage(e.getPlayerSession(), e.getMessage());
                }
            } catch (Exception e) {
                gameLogService.sendErrorMessage(username, e.getMessage());
                throw e;
            }
        }));
    }

    public void onUserDisconnected(UUID tableId, String username) {
        mutex.execute(tableId, () -> {
            var tableOpt = tableRepository.findById(tableId);
            if (tableOpt.isEmpty()) {
                log.error("No table found for ID {}", tableId);
                return;
            }
            var table = tableOpt.get();
            var playerSessionOpt =
                    playerSessionRepository.findByTableIdAndUsername(tableId, username);
            if (playerSessionOpt.isEmpty()) {
                log.error("No player {} found on table {}", username, tableId);
                return;
            }
            var playerSession = playerSessionOpt.get();
            playerSessionService.disconnectUser(playerSession.getId());

            dispatcher.send(tableId, messageFactory.playerDisconnected(username));

            var threadOpt = threadManager.getIfExists(tableId);
            if (threadOpt.isEmpty()) {
                log.error("No game thread for table ID: {}", tableId);
                return;
            }
            var gameThread = threadOpt.get();
            if (playerSession.getConnectionType() == ConnectionType.PLAYER) {
                var playerActionService = table.getGameType().getPlayerActionService(context);
                var action = new CreatePlayerActionDTO();
                action.setAction(ActionType.FOLD);
                playerActionService.playerAction(playerSession, gameThread, action);
            }
            var playerSessions =
                    playerSessionRepository.findConnectedPlayersByTableId(tableId);
            if (playerSessions.size() < 2) {
                gameThread.stopGame();
            }
        });
    }
}
