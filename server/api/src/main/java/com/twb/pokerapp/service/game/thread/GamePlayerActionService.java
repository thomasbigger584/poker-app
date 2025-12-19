package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.service.BettingRoundService;
import com.twb.pokerapp.service.RoundService;
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
    private RoundService roundService;

    @Autowired
    private BettingRoundService bettingRoundService;

    @Autowired
    private GameLogService gameLogService;

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private ServerMessageFactory messageFactory;

    @Autowired
    private MessageDispatcher dispatcher;

    public void playerAction(PlayerSession playerSession, GameThread gameThread, CreatePlayerActionDTO createDto) {
        log.info("***************************************************************");
        log.info("GamePlayerActionService.playerAction");
        log.info("playerSession = {}, createDto = {}", playerSession, createDto);
        log.info("***************************************************************");
        if (playerSession.getConnectionType() != ConnectionType.PLAYER) {
            gameLogService.sendLogMessage(playerSession, "You attempted to make a player action but are not a player");
            return;
        }
        var table = playerSession.getPokerTable();
        if (table == null) {
            gameLogService.sendLogMessage(playerSession, "Table is null for player session: " + playerSession.getId());
            return;
        }
        var roundOpt = roundService.getRoundByTable(table.getId());
        if (roundOpt.isEmpty()) {
            gameLogService.sendLogMessage(playerSession, "Round is null for table: " + table.getId());
            return;
        }
        var round = roundOpt.get();
        if (checkIdempotency(playerSession, round, createDto)) return;
        var bettingRoundOpt = bettingRoundService.getTableBettingRound(table.getId());
        if (bettingRoundOpt.isEmpty()) {
            gameLogService.sendLogMessage(playerSession, "Betting Round is null for table: " + table.getId());
            return;
        }
        var bettingRound = bettingRoundOpt.get();

        if (createDto.getAmount() == null) {
            createDto.setAmount(0d);
        }

        var playerActionOpt = onPlayerAction(playerSession, bettingRound, gameThread, createDto);

        if (playerActionOpt.isPresent()) {
            var playerAction = playerActionOpt.get();

            dispatcher.send(table, messageFactory.playerActioned(playerAction));
            gameThread.onPostPlayerAction(createDto);
        }
    }

    private boolean checkIdempotency(PlayerSession playerSession, Round round, CreatePlayerActionDTO createDto) {
        if (idempotencyService.isActionIdempotent(playerSession.getId(), round.getId(), createDto.getAction())) {
            gameLogService.sendLogMessage(playerSession, "Player already made action in this round recently");
            return true;
        }
        idempotencyService.recordAction(playerSession.getId(), round.getId(), createDto.getAction());
        return false;
    }

    protected abstract Optional<PlayerAction> onPlayerAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread, CreatePlayerActionDTO createDto);
}
