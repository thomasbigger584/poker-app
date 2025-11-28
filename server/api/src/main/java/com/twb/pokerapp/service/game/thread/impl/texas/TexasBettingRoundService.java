package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.*;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.service.RoundService;
import com.twb.pokerapp.service.game.thread.GameLogService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@Transactional
@RequiredArgsConstructor
public class TexasBettingRoundService {
    private final PlayerSessionRepository playerSessionRepository;
    private final PlayerActionRepository playerActionRepository;
    private final BettingRoundRepository bettingRoundRepository;
    private final GameLogService gameLogService;
    private final RoundService roundService;
    private final MessageDispatcher dispatcher;
    private final ServerMessageFactory messageFactory;
    private final TexasPlayerActionService texasPlayerActionService;

    public Round runBettingRound(GameThreadParams params, PokerTable table, Round round, BettingRound bettingRound, GameThread gameThread) {
        var playersPaidUp = false;
        do {
            var activePlayers = playerSessionRepository
                    .findActivePlayersByTableId(table.getId(), round.getId());
            if (activePlayers.isEmpty()) {
                gameLogService.sendLogMessage(table, "No Active Players found");
                throw new GameInterruptedException("No Active Players found");
            }
            if (activePlayers.size() == 1) {
                gameLogService.sendErrorMessage(table, "Only one active player in betting round, so skipping");
                throw new RoundInterruptedException("Only one active player in betting round, so skipping");
            }

            for (var currentPlayer : activePlayers) {
                var amountToCall = 0d;
                var nextActions = ActionType.getDefaultActions();

                var prevPlayerActions = playerActionRepository
                        .findPlayerActionsNotFolded(bettingRound.getId());

                PlayerAction previousPlayerAction = null;
                if (!prevPlayerActions.isEmpty()) {
                    previousPlayerAction = prevPlayerActions.getFirst();
                    var previousPlayerActionType = previousPlayerAction.getActionType();

                    nextActions = ActionType.getNextActions(previousPlayerActionType);
                    amountToCall = previousPlayerActionType.getAmountToCall(previousPlayerAction.getAmount());
                }
                gameThread.checkRoundInterrupted();
                dispatcher.send(table, messageFactory.playerTurn(currentPlayer, previousPlayerAction, bettingRound, nextActions, amountToCall));
                waitPlayerTurn(params, gameThread, table, currentPlayer);

                var updatedBettingRoundOpt = bettingRoundRepository.findById(bettingRound.getId());
                if (updatedBettingRoundOpt.isPresent()) bettingRound = updatedBettingRoundOpt.get();
            }

            var playerActionSumAmounts = playerActionRepository.sumAmounts(bettingRound.getId());
            playersPaidUp = playerActionSumAmounts != bettingRound.getPot();

        } while (playersPaidUp);

        setBettingRoundFinished(bettingRound);

        return roundService.updatePot(round, bettingRound);
    }

    private void waitPlayerTurn(GameThreadParams params, GameThread gameThread, PokerTable table, PlayerSession playerSession) {
        var playerTurnLatch = gameThread.newPlayerTurnLatch(playerSession);
        var latch = playerTurnLatch.playerTurnLatch();
        try {
            var await = latch.await(params.getPlayerTurnWaitMs(), TimeUnit.MILLISECONDS);
            if (!await) {
                var createActionDto = new CreatePlayerActionDTO();
                createActionDto.setAction(ActionType.FOLD);
                texasPlayerActionService.playerAction(table, playerSession, gameThread, createActionDto);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to wait for player turn latch", e);
        }
    }

    private void setBettingRoundFinished(BettingRound bettingRound) {
        bettingRound.setState(BettingRoundState.FINISHED);
        bettingRoundRepository.saveAndFlush(bettingRound);
    }
}
