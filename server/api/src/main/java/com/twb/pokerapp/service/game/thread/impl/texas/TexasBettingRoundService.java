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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        do {
            int playerIndex = 0;
            while (true) {
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

                if (playerIndex >= activePlayers.size()) {
                    break;
                }

                PlayerSession currentPlayer = activePlayers.get(playerIndex);
                int playerCount = activePlayers.size();

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

                var playerCountAfterTurn = playerSessionRepository
                        .findActivePlayersByTableId(table.getId(), round.getId()).size();

                if (playerCount == playerCountAfterTurn) {
                    playerIndex++;
                }
            }
        } while (!areAllPlayersPaidUp(table, round, bettingRound));

        setBettingRoundFinished(bettingRound);

        return roundService.updatePot(round, bettingRound);
    }

    private boolean areAllPlayersPaidUp(PokerTable table, Round round, BettingRound bettingRound) {
        var tableId = table.getId();
        var roundId = round.getId();
        var bettingRoundId = bettingRound.getId();

        List<PlayerSession> activePlayers = playerSessionRepository.findActivePlayersByTableId(tableId, roundId);
        if (activePlayers.size() <= 1) {
            return true;
        }

        Map<UUID, Double> contributions = getPlayerContributions(bettingRoundId);

        double highestContribution = 0;
        for (PlayerSession player : activePlayers) {
            double contribution = contributions.getOrDefault(player.getId(), 0.0);
            if (contribution > highestContribution) {
                highestContribution = contribution;
            }
        }

        if (highestContribution == 0) {
            return true; // Everyone has checked
        }

        for (PlayerSession player : activePlayers) {
            if (contributions.getOrDefault(player.getId(), 0.0) < highestContribution) {
                return false; // At least one player has not matched the highest bet
            }
        }
        return true; // All active players have matched the highest bet
    }

    private Map<UUID, Double> getPlayerContributions(UUID bettingRoundId) {
        // Note: This relies on a repository method that fetches actions for the round, ordered by time.
        // If a player calls and then re-raises, we assume the amount on the action is the new total for the round.
        List<PlayerAction> actions = playerActionRepository.findPlayerActionsForContributions(bettingRoundId);
        Map<UUID, Double> contributions = new HashMap<>();
        for (PlayerAction action : actions) {
            contributions.put(action.getPlayerSession().getId(), action.getAmount());
        }
        return contributions;
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
