package com.twb.pokergame.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokergame.domain.AppUser;
import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.repository.PlayerSessionRepository;
import com.twb.pokergame.repository.RoundRepository;
import com.twb.pokergame.repository.TableRepository;
import com.twb.pokergame.repository.UserRepository;
import com.twb.pokergame.service.PlayerSessionService;
import com.twb.pokergame.service.game.runnable.GameRunnableFactory;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final RoundRepository roundRepository;

    private final PlayerSessionService playerSessionService;
    private final PlayerSessionRepository playerSessionRepository;

    private final GameRunnableFactory runnableFactory;
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;
    private final XSync<UUID> mutex;

    public void onPlayerConnected(UUID tableId, String username) {
        mutex.execute(tableId, () -> {
            Optional<PokerTable> pokerTableOpt = tableRepository.findById(tableId);
            if (pokerTableOpt.isEmpty()) {
                logger.warn("Failed to connect user {} to table {} as table not found", username, tableId);
                return;
            }

            Optional<AppUser> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                logger.warn("Failed to connect user {} to table {} as user not found", username, tableId);
                return;
            }

            Optional<Round> currentRoundOpt = roundRepository.findCurrentByTableId(tableId);
            if (currentRoundOpt.isEmpty()) {
                logger.warn("Fail to connect user {} to table as there is no current" +
                        " round created for table {}", username, tableId);
                return;
            }

            PokerTable pokerTable = pokerTableOpt.get();
            AppUser appUser = userOpt.get();
            Round currentRound = currentRoundOpt.get();

            runnableFactory.createIfNotExist(pokerTable);
            PlayerSessionDTO sessionDto = playerSessionService.connectUserToRound(appUser, currentRound);

            ServerMessageDTO message = messageFactory.playerConnected(sessionDto);
            dispatcher.send(tableId, message);
        });
    }

    public void onPlayerDisconnected(UUID tableId, String username) {
        mutex.execute(tableId, () -> {
            playerSessionService.disconnectUser(tableId, username);
            List<PlayerSession> tableConnections =
                    playerSessionRepository.findByTableId(tableId);
            if (tableConnections.isEmpty()) {
                runnableFactory.delete(tableId);
            }
            ServerMessageDTO message =
                    messageFactory.playerDisconnected(username);
            dispatcher.send(tableId, message);
        });
    }
}
