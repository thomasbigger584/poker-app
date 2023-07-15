package com.twb.pokergame.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokergame.domain.AppUser;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.repository.TableRepository;
import com.twb.pokergame.repository.UserRepository;
import com.twb.pokergame.service.game.runnable.GameRunnableFactory;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final UserRepository userRepository;
    private final TableRepository tableRepository;

    private final GameRunnableFactory runnableFactory;
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;
    private final XSync<UUID> mutex;

    public void onPlayerConnected(UUID tableId, String username) {
        mutex.execute(tableId, () -> {
            Optional<PokerTable> pokerTableOpt = tableRepository.findById(tableId);
            if (pokerTableOpt.isEmpty()) {
                logger.warn("Failed to connect user {} to poker table {} as poker table not found", username, tableId);
                return;
            }

            Optional<AppUser> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                logger.warn("Failed to connect user {} to poker table {} as user not found", username, tableId);
                return;
            }

            PokerTable pokerTable = pokerTableOpt.get();
            AppUser appUser = userOpt.get();

            runnableFactory.createIfNotExist(pokerTableOpt.get());

//            pokerTableUserService.create(pokerTable, user);

            ServerMessageDTO message = messageFactory.playerConnected(appUser.getUsername());
            dispatcher.send(tableId, message);
        });
    }

    public void onPlayerDisconnected(UUID tableId, String username) {
        mutex.execute(tableId, () -> {
//            List<Round> pokerTableUsers = roundRepository.findByPokerTableId(tableId);
//            if (pokerTableUsers.isEmpty()) {
//                logger.info("No PokerTableUsers so deleting runnable");
//                runnableFactory.delete(tableId);
//            } else {
//                for (Round pokerTableUser : pokerTableUsers) {
//                    User user = pokerTableUser.getUser();
//                    if (user.getUsername().equals(username)) {
//                        pokerTableUserRepository.delete(pokerTableUser);
//                        break;
//                    }
//                }
//                pokerTableUsers = roundRepository.findByPokerTableId(tableId);
//                if (pokerTableUsers.isEmpty()) {
//                    logger.info("No PokerTableUsers so deleting runnable");
//                    runnableFactory.delete(tableId);
//                }
//            }
            ServerMessageDTO message = messageFactory.playerDisconnected(username);
            dispatcher.send(tableId, message);
        });
    }
}
