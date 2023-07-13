package com.twb.pokergame.service.game;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.repository.PokerTableRepository;
import com.twb.pokergame.service.game.runnable.GameRunnable;
import com.twb.pokergame.service.game.runnable.GameRunnableFactory;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PokerGameService {
    private static final Logger logger = LoggerFactory.getLogger(PokerGameService.class);

    private final PokerTableRepository pokerTableRepository;
    private final GameRunnableFactory runnableFactory;


    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;

    //todo: may need to synchronize these methods with pokerTableId `value`
    // to get around multi threading of different games

    public void onPlayerConnected(String pokerTableId, String username) {
        UUID uuid = UUID.fromString(pokerTableId);

        Optional<PokerTable> pokerTableOpt = pokerTableRepository.findById(uuid);
        if (pokerTableOpt.isEmpty()) {
            logger.warn("Failed to connect user {} to poker table {} as not found", username, uuid);
            return;
        }

        GameRunnable runnable = runnableFactory.get(pokerTableOpt.get());
        runnable.onPlayerConnected(username);
    }

    public void onPlayerDisconnected(String pokerTableId, String username) {
        Optional<GameRunnable> runnableOpt = runnableFactory.getIfExists(pokerTableId);
        if (runnableOpt.isPresent()) {
            GameRunnable runnable = runnableOpt.get();
            runnable.onPlayerDisconnected(username);
        }
    }

}
