package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.service.game.exception.GamePlayerLogException;
import com.twb.pokerapp.service.game.thread.dto.PlayerActionCommand;
import com.twb.pokerapp.service.idempotency.IdempotencyService;
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
    protected RoundRepository roundRepository;

    @Autowired
    protected BettingRoundRepository bettingRoundRepository;

    @Autowired
    private IdempotencyService idempotencyService;

    @Transactional(propagation = Propagation.MANDATORY)
    public void playerAction(PlayerSession playerSession, GameThread gameThread, PlayerActionCommand command) {
        playerAction(playerSession, gameThread, command, true);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void playerAction(PlayerSession playerSession, GameThread gameThread, PlayerActionCommand command, boolean enforceIdempotency) {
        if (gameThread.isStopping()) {
            log.warn("Game Thread is stopping so ignoring player action {} for user {}", command.getAction(), playerSession.getUser().getUsername());
            return;
        }
        log.debug("Player Action: {} - {}", playerSession.getUser().getUsername(), command);

        var table = getThrowPlayerErrorLog(Optional.ofNullable(playerSession.getPokerTable()), playerSession, "Table Not Found");
        var round = getThrowPlayerLog(roundRepository.findCurrentByTableId(table.getId()), playerSession, "Round Not Found");
        if (enforceIdempotency) {
            checkIdempotency(playerSession, round, command);
        }
        var bettingRound = getThrowPlayerLog(bettingRoundRepository.findCurrentByTableId(table.getId()), playerSession, "Betting Round Not Found");
        var playerAction = onPlayerAction(playerSession, bettingRound, gameThread, command);
        gameThread.onPostPlayerAction(playerAction);
    }

    private void checkIdempotency(PlayerSession playerSession, Round round, PlayerActionCommand command) {
        if (idempotencyService.isActionIdempotent(playerSession.getId(), round.getId(), command.getAction())) {
            throw new GamePlayerLogException(playerSession, "You already made action in this round recently");
        }
        idempotencyService.recordAction(playerSession.getId(), round.getId(), command.getAction());
    }

    public abstract void onExecuteAutoAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread);

    protected abstract PlayerAction onPlayerAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread, PlayerActionCommand command);
}
