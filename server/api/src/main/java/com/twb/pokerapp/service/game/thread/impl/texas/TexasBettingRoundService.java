package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.service.BettingRoundService;
import com.twb.pokerapp.service.RoundService;
import com.twb.pokerapp.service.game.thread.GameLogService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TexasBettingRoundService {
    private final PlayerSessionRepository playerSessionRepository;
    private final PlayerActionRepository playerActionRepository;
    private final RoundRepository roundRepository;
    private final BettingRoundRepository bettingRoundRepository;
    private final GameLogService gameLogService;
    private final RoundService roundService;
    private final MessageDispatcher dispatcher;
    private final ServerMessageFactory messageFactory;
    private final TexasPlayerActionService texasPlayerActionService;
    private final BettingRoundService bettingRoundService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void runBettingRound(GameThreadParams params, GameThread gameThread) {
        var roundOpt = roundRepository.findCurrentByTableId(params.getTableId());
        if (roundOpt.isEmpty()) {
            throw new IllegalStateException("Round not found");
        }
        var round = roundOpt.get();
        var bettingRoundOpt = bettingRoundRepository.findLatestInProgress(params.getTableId());
        if (bettingRoundOpt.isEmpty()) {
            throw new IllegalStateException("Latest Betting Round not found for Table ID: " + params.getTableId());
        }
        var bettingRound = bettingRoundOpt.get();

        do {
            int playerIndex = 0;
            while (true) {
                var activePlayers = playerSessionRepository
                        .findActivePlayersByTableId(params.getTableId(), round.getId());
                if (activePlayers.isEmpty()) {
                    gameLogService.sendLogMessage(params.getTableId(), "No Active Players found");
                    throw new GameInterruptedException("No Active Players found");
                }
                if (activePlayers.size() == 1) {
                    gameLogService.sendLogMessage(params.getTableId(), "Only one active player in betting round, so skipping");
                    throw new RoundInterruptedException("Only one active player in betting round, so skipping");
                }

                if (playerIndex >= activePlayers.size()) {
                    break;
                }

                var currentPlayer = activePlayers.get(playerIndex);

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

                dispatcher.send(params.getTableId(), messageFactory.playerTurn(currentPlayer, previousPlayerAction, bettingRound, nextActions, amountToCall));
                waitPlayerTurn(params, gameThread, currentPlayer);

                bettingRound = bettingRoundService.refreshBettingRound(bettingRound.getId());

                playerIndex++;
            }
        } while (!areAllPlayersPaidUp(params, round, bettingRound));

        setBettingRoundFinished(bettingRound);

        round = roundService.updatePot(round, bettingRound);
        log.info("Round pot for after betting updated to {}", round.getPot());
    }

    private boolean areAllPlayersPaidUp(GameThreadParams params, Round round, BettingRound bettingRound) {
        var activePlayers = playerSessionRepository.findActivePlayersByTableId(params.getTableId(), round.getId());
        if (activePlayers.size() <= 1) {
            return true;
        }

        var contributions = getPlayerContributions(bettingRound);

        var highestContribution = 0d;
        for (PlayerSession player : activePlayers) {
            var contribution = contributions.getOrDefault(player.getId(), 0d);
            if (contribution > highestContribution) {
                highestContribution = contribution;
            }
        }

        if (highestContribution == 0) {
            return true; // Everyone has checked
        }

        for (var player : activePlayers) {
            if (contributions.getOrDefault(player.getId(), 0.0) < highestContribution) {
                return false; // At least one player has not matched the highest bet
            }
        }
        return true; // All active players have matched the highest bet
    }

    private Map<UUID, Double> getPlayerContributions(BettingRound bettingRound) {
        // Note: This relies on a repository method that fetches actions for the round, ordered by time.
        // If a player calls and then re-raises, we assume the amount on the action is the new total for the round.
        var actions = playerActionRepository.findPlayerActionsForContributions(bettingRound.getId());
        var contributions = new HashMap<UUID, Double>();
        for (var action : actions) {
            contributions.put(action.getPlayerSession().getId(), action.getAmount());
        }
        return contributions;
    }

    private void waitPlayerTurn(GameThreadParams params, GameThread gameThread, PlayerSession playerSession) {
        var playerTurnLatch = gameThread.newPlayerTurnLatch(playerSession);
        var latch = playerTurnLatch.playerTurnLatch();
        try {
            var await = latch.await(params.getPlayerTurnWaitMs(), TimeUnit.MILLISECONDS);
            if (!await) {
                var createActionDto = new CreatePlayerActionDTO();
                createActionDto.setAction(ActionType.FOLD);
                texasPlayerActionService.playerAction(playerSession, gameThread, createActionDto);
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
