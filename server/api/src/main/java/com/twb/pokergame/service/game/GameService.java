package com.twb.pokergame.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokergame.domain.AppUser;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.repository.RoundRepository;
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

    private final GameThreadFactory threadFactory;
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

            Optional<Round> currentRoundOpt = roundRepository.findCurrentByTableId(tableId);
            if (currentRoundOpt.isEmpty()) {
                String message = String.format("Fail to connect user %s to table as there is no current round created for table %s", username, tableId);
                throw new RuntimeException(message);
            }

            PokerTable pokerTable = pokerTableOpt.get();
            AppUser appUser = userOpt.get();
            Round currentRound = currentRoundOpt.get();

            GameThread thread = threadFactory.createIfNotExist(pokerTable);
            PlayerSessionDTO connectedPlayerSession = playerSessionService.connectUserToRound(appUser, currentRound);
            thread.playerConnected();

            List<PlayerSessionDTO> playerSessionDtos = playerSessionService.getPlayerSessionsByTableId(tableId);

            // send to all clients that this player has connected
            dispatcher.send(tableId, messageFactory.playerConnected(connectedPlayerSession));

            return messageFactory.playerSubscribed(playerSessionDtos);
        });
    }

    public void onPlayerDisconnected(UUID tableId, String username) {
        mutex.execute(tableId, () -> {
            playerSessionService.disconnectUser(tableId, username);
            Optional<GameThread> threadOpt = threadFactory.getIfExists(tableId);
            if (threadOpt.isPresent()) {

                GameThread gameThread = threadOpt.get();
                gameThread.playerDisconnected();
            }

            ServerMessageDTO message =
                    messageFactory.playerDisconnected(username);
            dispatcher.send(tableId, message);
        });
    }
}
