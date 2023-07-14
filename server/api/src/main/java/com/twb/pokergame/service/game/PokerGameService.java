package com.twb.pokergame.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.PokerTableUser;
import com.twb.pokergame.domain.User;
import com.twb.pokergame.repository.PokerTableRepository;
import com.twb.pokergame.repository.PokerTableUserRepository;
import com.twb.pokergame.repository.UserRepository;
import com.twb.pokergame.service.PokerTableUserService;
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
public class PokerGameService {
    private static final Logger logger = LoggerFactory.getLogger(PokerGameService.class);

    private final UserRepository userRepository;
    private final PokerTableRepository pokerTableRepository;
    private final PokerTableUserRepository pokerTableUserRepository;
    private final PokerTableUserService pokerTableUserService;

    private final GameRunnableFactory runnableFactory;
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;
    private final XSync<UUID> mutex;

    public void onPlayerConnected(UUID pokerTableId, String username) {
        mutex.execute(pokerTableId, () -> {
            Optional<PokerTable> pokerTableOpt = pokerTableRepository.findById(pokerTableId);
            if (pokerTableOpt.isEmpty()) {
                logger.warn("Failed to connect user {} to poker table {} as poker table not found", username, pokerTableId);
                return;
            }

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                logger.warn("Failed to connect user {} to poker table {} as user not found", username, pokerTableId);
                return;
            }

            PokerTable pokerTable = pokerTableOpt.get();
            User user = userOpt.get();

            runnableFactory.createIfNotExist(pokerTableOpt.get());
            pokerTableUserService.create(pokerTable, user);

            ServerMessageDTO message = messageFactory.playerConnected(user.getUsername());
            dispatcher.send(pokerTableId, message);
        });
    }

    public void onPlayerDisconnected(UUID pokerTableId, String username) {
        mutex.execute(pokerTableId, () -> {
            List<PokerTableUser> pokerTableUsers = pokerTableUserRepository.findByPokerTableId(pokerTableId);
            if (pokerTableUsers.isEmpty()) {
                logger.info("No PokerTableUsers so deleting runnable");
                runnableFactory.delete(pokerTableId);
            } else {
                for (PokerTableUser pokerTableUser : pokerTableUsers) {
                    User user = pokerTableUser.getUser();
                    if (user.getUsername().equals(username)) {
                        pokerTableUserRepository.delete(pokerTableUser);
                        break;
                    }
                }
                pokerTableUsers = pokerTableUserRepository.findByPokerTableId(pokerTableId);
                if (pokerTableUsers.isEmpty()) {
                    logger.info("No PokerTableUsers so deleting runnable");
                    runnableFactory.delete(pokerTableId);
                }
            }
            ServerMessageDTO message = messageFactory.playerDisconnected(username);
            dispatcher.send(pokerTableId, message);
        });
    }
}
