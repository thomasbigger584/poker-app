package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.service.BettingRoundService;
import com.twb.pokerapp.service.idepetency.IdempotencyService;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
public abstract class GamePlayerActionService {

    @Autowired
    private BettingRoundService bettingRoundService;

    @Autowired
    private GameLogService gameLogService;

    @Autowired
    private ServerMessageFactory messageFactory;

    @Autowired
    private MessageDispatcher dispatcher;

    @Autowired
    private IdempotencyService idempotencyService;

    public void playerAction(PlayerSession playerSession, GameThread gameThread, CreatePlayerActionDTO createDto) {
        log.info("***************************************************************");
        log.info("GamePlayerActionService.playerAction");
        log.info("playerSession = {}, createDto = {}", playerSession, createDto);
        log.info("***************************************************************");
        var pokerTable = playerSession.getPokerTable();
        if (pokerTable == null) {
            gameLogService.sendLogMessage(playerSession, "Table is null for player session: " + playerSession.getId());
            return;
        }
        var tableId = pokerTable.getId();
        var bettingRound = bettingRoundService.getTableBettingRound(tableId);
        if (createDto.getAmount() == null) {
            createDto.setAmount(0d);
        }
        var bettingRoundId = bettingRound.getId();
        if (idempotencyService.isActionIdempotent(playerSession.getId(), bettingRoundId, createDto.getAction())) {
            gameLogService.sendLogMessage(playerSession, "Player already made action in this round recently");
            return;
        }
        var playerActionOpt = onPlayerAction(playerSession, bettingRound, gameThread, createDto);
        if (playerActionOpt.isPresent()) {
            if (idempotencyService.recordAction(playerSession.getId(), bettingRoundId, createDto.getAction())) {
                var playerAction = playerActionOpt.get();
                dispatcher.send(tableId, messageFactory.playerActioned(playerAction));
            }
            gameThread.onPostPlayerAction(createDto);
        }
    }

    protected abstract Optional<PlayerAction> onPlayerAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread, CreatePlayerActionDTO createDto);
}
