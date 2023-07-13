package com.twb.pokergame.service.game;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.repository.PokerTableRepository;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.dto.MessageType;
import com.twb.pokergame.web.websocket.message.dto.WebSocketMessageDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class PokerGameRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PokerGameRunnable.class);

    private final String pokerTableId;

    @Autowired
    private PokerTableRepository pokerTableRepository;

    @Autowired
    private MessageDispatcher dispatcher;

    @Override
    public void run() {
        logger.info("Starting Game Runnable: {} ", pokerTableId);

        for (int index = 0; index < 30; index++) {
            System.out.println("index = " + index);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            List<PokerTable> allPokerTables = pokerTableRepository.findAll();
            System.out.println(index + " - allPokerTables.size() = " + allPokerTables.size());

            dispatcher.send(pokerTableId, WebSocketMessageDTO.builder()
                    .type(MessageType.PLAYER_CONNECT).content("poker tables: " + allPokerTables.size())
                    .sender(String.valueOf(index)).build());
        }


        logger.info("Finishing Game Runnable: {} ", pokerTableId);
    }
}
