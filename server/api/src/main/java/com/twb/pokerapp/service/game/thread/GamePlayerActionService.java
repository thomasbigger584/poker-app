package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class GamePlayerActionService {

    @Autowired
    private BettingRoundRepository bettingRoundRepository;

    @Autowired
    private GameLogService gameLogService;

    @Autowired
    private MessageDispatcher dispatcher;

    public void playerAction(PlayerSession playerSession, GameThread gameThread, CreatePlayerActionDTO createDto) {
        var pokerTable = playerSession.getPokerTable();
        if (pokerTable == null) {
            gameLogService.sendLogMessage(playerSession, "Table is null for player session: " + playerSession);
            return;
        }
        var tableId = pokerTable.getId();
        var bettingRoundOpt = bettingRoundRepository.findLatestInProgress(tableId);
        if (bettingRoundOpt.isEmpty()) {
            gameLogService.sendLogMessage(playerSession, "Latest Betting Round not found for Table ID: " + tableId);
            return;
        }
        var bettingRound = bettingRoundOpt.get();

        if (createDto.getAmount() == null) {
            createDto.setAmount(0d);
        }

        var actioned = onPlayerAction(playerSession, bettingRound, gameThread, createDto);
        if (actioned) {
            // todo send PLAYER_ACTIONED message
            gameThread.onPostPlayerAction(createDto);
        }
    }

    protected abstract boolean onPlayerAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread, CreatePlayerActionDTO createDto);
}
