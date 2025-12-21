package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.exception.game.GamePlayerLogException;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.service.idepetency.IdempotencyService;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.twb.pokerapp.repository.RepositoryUtil.getThrowPlayerErrorLog;
import static com.twb.pokerapp.repository.RepositoryUtil.getThrowPlayerLog;

@Slf4j
public abstract class GamePlayerActionService {

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private BettingRoundRepository bettingRoundRepository;

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private ServerMessageFactory messageFactory;

    @Autowired
    private MessageDispatcher dispatcher;

    @Transactional(propagation = Propagation.MANDATORY)
    public void playerAction(PlayerSession playerSession, GameThread gameThread, CreatePlayerActionDTO createDto) {
        log.info("Player Action: {} - {}", playerSession.getUser().getUsername(), createDto);

        var table = getThrowPlayerErrorLog(Optional.ofNullable(playerSession.getPokerTable()), playerSession, "Table Not Found");
        var round = getThrowPlayerLog(roundRepository.findCurrentByTableId(table.getId()), playerSession, "Round Not Found");

        checkIdempotency(playerSession, round, createDto);

        var bettingRound = getThrowPlayerLog(bettingRoundRepository.findCurrentByTableId(table.getId()), playerSession, "Betting Round Not Found");

        var playerAction = onPlayerAction(playerSession, bettingRound, gameThread, createDto);

        dispatcher.send(table, messageFactory.playerActioned(playerAction));
        gameThread.onPostPlayerAction(createDto);
    }

    private void checkIdempotency(PlayerSession playerSession, Round round, CreatePlayerActionDTO createDto) {
        if (idempotencyService.isActionIdempotent(playerSession.getId(), round.getId(), createDto.getAction())) {
            throw new GamePlayerLogException(playerSession, "You already made action in this round recently");
        }
        idempotencyService.recordAction(playerSession.getId(), round.getId(), createDto.getAction());
    }

    protected abstract PlayerAction onPlayerAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread, CreatePlayerActionDTO createDto);
}
