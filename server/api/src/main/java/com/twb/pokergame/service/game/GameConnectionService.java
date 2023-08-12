package com.twb.pokergame.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokergame.domain.AppUser;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.repository.TableRepository;
import com.twb.pokergame.repository.UserRepository;
import com.twb.pokergame.service.PlayerSessionService;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GameConnectionService {
    private static final Logger logger = LoggerFactory.getLogger(GameConnectionService.class);

    private final UserRepository userRepository;
    private final TableRepository tableRepository;

    private final PlayerSessionService playerSessionService;

    private final GameThreadManager threadManager;
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;
    private final XSync<UUID> mutex;

    public ServerMessageDTO onPlayerSubscribed(UUID tableId, String username) {
        return mutex.evaluate(tableId, () -> {
            Optional<PokerTable> pokerTableOpt = tableRepository.findById(tableId);
            if (pokerTableOpt.isEmpty()) {
                String message = String.format("Failed to connect user %s to table %s as table not found", username, tableId);
                throw new RuntimeException(message);
            }

            Optional<AppUser> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                String message = String.format("Failed to connect user %s to table %s as user not found", username, tableId);
                throw new RuntimeException(message);
            }

            PokerTable pokerTable = pokerTableOpt.get();
            AppUser appUser = userOpt.get();

            threadManager.createIfNotExist(pokerTable);

            PlayerSessionDTO connectedPlayerSession = playerSessionService.connectUserToRound(appUser, pokerTable);
            List<PlayerSessionDTO> allPlayerSessions = playerSessionService.getByTableId(tableId);

            // send to all clients that this table has connected
            dispatcher.send(tableId, messageFactory.playerConnected(connectedPlayerSession));
            return messageFactory.playerSubscribed(allPlayerSessions);
        });
    }

    /*
     * Transactional is required here to manage player disconnects within the game thread.
     * When the last person disconnects the query will see a size of 0 and fail/finish and interrupt game
     */
    @Transactional
    public void onPlayerDisconnected(UUID tableId, String username) {
        mutex.execute(tableId, () -> {
            playerSessionService.disconnectUser(tableId, username);
            ServerMessageDTO message =
                    messageFactory.playerDisconnected(username);
            dispatcher.send(tableId, message);
            threadManager.getIfExists(tableId)
                    .ifPresent(gameThread -> gameThread.onPlayerDisconnected(username));
        });
    }
}
