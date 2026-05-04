package com.twb.pokerapp.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.TableRepository;
import com.twb.pokerapp.repository.UserRepository;
import com.twb.pokerapp.service.PlayerSessionService;
import com.twb.pokerapp.service.game.exception.GamePlayerErrorLogException;
import com.twb.pokerapp.service.game.exception.GamePlayerLogException;
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

import java.math.BigDecimal;
import java.util.UUID;

import static com.twb.pokerapp.repository.RepositoryUtil.getThrowPlayerErrorLog;
import static com.twb.pokerapp.util.TransactionUtil.afterCommit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TableGameService {
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final PlayerSessionRepository playerSessionRepository;
    private final PlayerSessionService playerSessionService;
    private final BettingRoundRepository bettingRoundRepository;

    private final GameThreadManager threadManager;
    private final ServerMessageFactory messageFactory;
    private final GameLogService gameLogService;
    private final MessageDispatcher dispatcher;
    private final XSync<UUID> mutex;
    private final ApplicationContext context;
    private final TransactionTemplate writeTx;

    public ServerMessageDTO onUserConnected(UUID tableId, ConnectionType connectionType, String username, BigDecimal buyInAmount) {
        return mutex.evaluate(tableId, () -> {
            var allPlayerSessions = writeTx.execute(status -> {
                var table = getThrowPlayerErrorLog(tableRepository.findById(tableId), "No table found for Table ID: " + tableId);
                if (connectionType == ConnectionType.PLAYER) {
                    if (buyInAmount.compareTo(table.getMinBuyin()) < 0 || buyInAmount.compareTo(table.getMaxBuyin()) > 0) {
                        var message = "Buy-In amount must be between $%.2f and $%.2f for table %s".formatted(table.getMinBuyin(), table.getMaxBuyin(), tableId);
                        throw new GamePlayerErrorLogException(message);
                    }
                }
                var user = getThrowPlayerErrorLog(userRepository.findByUsername(username), "Failed to connect user %s to table %s as user not found".formatted(username, tableId));
                if (connectionType == ConnectionType.PLAYER) {
                    if (buyInAmount.compareTo(user.getTotalFunds()) > 0) {
                        var message = "User %s does not have enough total funds for Buy-In $%.2f, has $%.2f".formatted(username, buyInAmount, user.getTotalFunds());
                        throw new GamePlayerErrorLogException(message);
                    }
                }
                var playerSessionOpt = playerSessionRepository.findByTableIdAndUsername(tableId, username);
                if (playerSessionOpt.isEmpty()) {
                    if (connectionType == ConnectionType.PLAYER) {
                        threadManager.createIfNotExist(table);
                    }
                    var playerSession = playerSessionService.connectUserToRound(table, user, connectionType, buyInAmount);
                    afterCommit(() -> dispatcher.send(tableId, messageFactory.playerConnected(playerSession)));
                }
                return playerSessionRepository.findConnectedByTableId(tableId);
            });
            if (allPlayerSessions == null) {
                throw new GamePlayerErrorLogException("No player sessions during subscribe as all player sessions are null");
            }
            return messageFactory.playerSubscribed(allPlayerSessions);
        });
    }

    public void onPlayerAction(UUID tableId, String username, CreatePlayerActionDTO action) {
        mutex.execute(tableId, () -> {
            try {
                writeTx.executeWithoutResult(status -> {
                    var table = getThrowPlayerErrorLog(tableRepository.findById(tableId), "No table found for Table ID: " + tableId);
                    var playerSession = getThrowPlayerErrorLog(playerSessionRepository.findByTableIdAndUsername(tableId, username), "Your session is not found on table %s".formatted(tableId));
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
                });
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
        });
    }

    public void onUserDisconnected(UUID tableId, String username) {
        mutex.execute(tableId, () -> writeTx.executeWithoutResult(status -> {
            var tableOpt = tableRepository.findById(tableId);
            if (tableOpt.isEmpty()) {
                log.warn("No table found for table: {}", tableId);
                return;
            }
            var table = tableOpt.get();
            var playerSessionOpt = playerSessionRepository.findByTableIdAndUsername(tableId, username);
            if (playerSessionOpt.isEmpty()) {
                log.warn("No session found for user {} on table {}", username, tableId);
                return;
            }
            var playerSession = playerSessionOpt.get();

            playerSessionService.disconnectUser(playerSession);

            afterCommit(() -> dispatcher.send(tableId, messageFactory.playerDisconnected(username)));

            var threadOpt = threadManager.getIfExists(tableId);
            if (threadOpt.isEmpty()) {
                log.debug("No game thread for table ID: {}", tableId);
                return;
            }
            var gameThread = threadOpt.get();
            if (playerSession.getConnectionType() == ConnectionType.PLAYER) {
                var playerTurnLatch = gameThread.getPlayerTurnLatch();
                if (playerTurnLatch != null && username.equals(playerTurnLatch.playerSession().getUser().getUsername())) {
                    var bettingRoundOpt = bettingRoundRepository.findCurrentByTableId(tableId);
                    bettingRoundOpt.ifPresent(bettingRound -> {
                        table.getGameType().getPlayerActionService(context)
                                .onExecuteAutoAction(playerSession, bettingRound, gameThread);
                    });
                }
            }
        }));
    }
}
