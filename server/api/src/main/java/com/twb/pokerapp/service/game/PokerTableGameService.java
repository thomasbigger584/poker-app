package com.twb.pokerapp.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.TableRepository;
import com.twb.pokerapp.repository.UserRepository;
import com.twb.pokerapp.service.PlayerSessionService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadManager;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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

    public ServerMessageDTO onUserConnected(UUID tableId, ConnectionType connectionType, String username) {
        return mutex.evaluate(tableId, () -> {
            var pokerTableOpt = tableRepository.findById(tableId);
            if (pokerTableOpt.isEmpty()) {
                var message = String.format("Failed to connect user %s to table %s as table not found", username, tableId);
                throw new RuntimeException(message);
            }

            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                var message = String.format("Failed to connect user %s to table %s as user not found", username, tableId);
                throw new RuntimeException(message);
            }

            var playerSessionOpt = playerSessionRepository.findByTableIdAndUsername(tableId, username);
            if (playerSessionOpt.isPresent()) {
                var message = String.format("User %s already connected to table %s", username, tableId);
                throw new RuntimeException(message);
            }

            var pokerTable = pokerTableOpt.get();
            var appUser = userOpt.get();

            if (connectionType == ConnectionType.PLAYER) {
                threadManager.createIfNotExist(pokerTable);
            }

            var connectedPlayerSession = playerSessionService.connectUserToRound(appUser, connectionType, pokerTable);
            var allPlayerSessions = playerSessionService.getByTableId(tableId);

            dispatcher.send(tableId, messageFactory.playerConnected(connectedPlayerSession));
            return messageFactory.playerSubscribed(allPlayerSessions);
        });
    }

    public void onPlayerAction(UUID tableId, String username, CreatePlayerActionDTO action) {
        mutex.execute(tableId, () -> {
            threadManager.getIfExists(tableId)
                    .ifPresent(thread -> thread.playerAction(username, action));
        });
    }

    public void onUserDisconnected(UUID tableId, String username) {
        mutex.execute(tableId, () -> {
            playerSessionService.disconnectUser(tableId, username);
            var threadOpt = threadManager.getIfExists(tableId);
            if (threadOpt.isPresent()) {
                GameThread thread = threadOpt.get();

                var playerSessions =
                        playerSessionRepository.findConnectedPlayersByTableIdNoLock(tableId);
                if (CollectionUtils.isEmpty(playerSessions)) {
                    thread.interrupt();
                } else {
                    var createPlayerActionDTO = new CreatePlayerActionDTO();
                    createPlayerActionDTO.setAction(ActionType.FOLD);
                    thread.playerAction(username, createPlayerActionDTO);
                }
                var message = messageFactory.playerDisconnected(username);
                dispatcher.send(tableId, message);
                if (playerSessions.size() < 2) {
                    thread.interrupt();
                }
            }
        });
    }
}
