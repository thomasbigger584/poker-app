package com.twb.pokerapp.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.TableRepository;
import com.twb.pokerapp.repository.UserRepository;
import com.twb.pokerapp.service.PlayerSessionService;
import com.twb.pokerapp.service.game.thread.GameThreadManager;
import com.twb.pokerapp.service.game.thread.dto.PlayerTurnLatchDTO;
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

    private final PlayerSessionService playerSessionService;

    private final GameThreadManager threadManager;
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;
    private final XSync<UUID> mutex;
    private final ApplicationContext context;

    public ServerMessageDTO onUserConnected(UUID tableId, ConnectionType connectionType, String username) {
        return mutex.evaluate(tableId, () -> {
            var tableOpt = tableRepository.findById(tableId);
            if (tableOpt.isEmpty()) {
                var message = String.format("Failed to connect user %s to table %s as table not found", username, tableId);
                throw new RuntimeException(message);
            }
            var table = tableOpt.get();

            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                var message = String.format("Failed to connect user %s to table %s as user not found", username, tableId);
                throw new RuntimeException(message);
            }
            var appUser = userOpt.get();

            var playerSessionOpt = playerSessionRepository.findByTableIdAndUsername(tableId, username);
            if (playerSessionOpt.isPresent()) {
                var message = String.format("User %s already connected to table %s", username, tableId);
                throw new RuntimeException(message);
            }

            if (connectionType == ConnectionType.PLAYER) {
                threadManager.createIfNotExist(table);
            }

            var connectedPlayerSession = playerSessionService.connectUserToRound(appUser, connectionType, table);
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

            PlayerTurnLatchDTO playerTurnLatch = gameThread.getPlayerTurnLatch();
            if (!username.equals(playerTurnLatch.playerSession().getUser().getUsername())) {
                log.error("Not waiting for {} to play on table {}", username, tableId);
                return;
            }

            var gameType = table.getGameType();
            var playerActionService = gameType.getPlayerActionService(context);
            var actioned = playerActionService.playerAction(table, playerSession, gameThread, action);

            if (actioned) {
                gameThread.onPostPlayerAction(action);
            }
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

            var playerActionService = table.getGameType().getPlayerActionService(context);
            var createActionDto = new CreatePlayerActionDTO();
            createActionDto.setAction(ActionType.FOLD);

            playerActionService.playerAction(table, playerSession, gameThread, createActionDto);

            var playerSessions =
                    playerSessionRepository.findConnectedPlayersByTableIdNoLock(tableId);
            if (CollectionUtils.isEmpty(playerSessions)) {
                gameThread.interrupt();
            }
            var message = messageFactory.playerDisconnected(username);
            dispatcher.send(tableId, message);

            if (playerSessions.size() < 2) {
                gameThread.interrupt();
            }
        });
    }
}
