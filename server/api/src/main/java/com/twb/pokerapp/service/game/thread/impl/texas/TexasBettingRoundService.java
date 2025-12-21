package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.service.BettingRoundService;
import com.twb.pokerapp.service.PlayerActionService;
import com.twb.pokerapp.service.RoundService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.texas.dto.NextActionsDTO;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TexasBettingRoundService {
    private final PlayerSessionRepository playerSessionRepository;
    private final RoundService roundService;
    private final MessageDispatcher dispatcher;
    private final ServerMessageFactory messageFactory;
    private final TexasPlayerActionService texasPlayerActionService;
    private final PlayerActionService playerActionService;
    private final BettingRoundService bettingRoundService;

    public void runBettingRound(GameThreadParams params, GameThread gameThread) {
        Round round;
        BettingRound bettingRound;
        var playerIndex = 0;
        var startIndex = 0;
        UUID lastAggressorId = null;
        var isFirstPass = true;

        // Betting Loop
        while (true) {
            round = getRound(params);
            bettingRound = getBettingRound(round);

            var activePlayers = getActivePlayers(params, round);

            // TODO: check what happens with folds, we may need to gracefully handle this inside the loop
            // Re-fetch players only if necessary to handle folds dynamically,
            // but for index stability, it is often safer to keep the list and check 'isActive'
            // inside the loop. For this implementation, we assume activePlayers list
            // stays valid for indexing, but we check if they folded via DB/Status if needed.

            if (playerIndex >= activePlayers.size()) {
                playerIndex = 0; // wrap index
            }

            var currentPlayer = activePlayers.get(playerIndex);

            // --- TERMINATION CHECKS ---
            // This means everyone else has had a chance to call/fold, and we are back to the person who bet/raised.
            // They do not act again unless someone else re-raised (which would have updated lastAggressorId).
            if (currentPlayer.getId().equals(lastAggressorId)) {
                log.info("Returned to last aggressor {}, betting round finished.", currentPlayer.getId());
                break;
            }
            // If we have circled back to the start and no one has bet (lastAggressorId is null).
            if (lastAggressorId == null && !isFirstPass && playerIndex == startIndex) {
                log.info("Checked around, betting round finished.");
                break;
            }

            var latestPlayerAction = awaitPlayerTurnAction(params, gameThread, currentPlayer, bettingRound);

            if (latestPlayerAction.isPresent()) {
                var actionJustTaken = latestPlayerAction.get();
                lastAggressorId = getLastAggressorId(actionJustTaken, lastAggressorId, currentPlayer);
            }

            bettingRound = getBettingRound(bettingRound);
            round = roundService.updatePot(round, bettingRound);

            dispatcher.send(params, messageFactory.bettingRoundUpdated(round, bettingRound));

            playerIndex++;
            if (playerIndex >= activePlayers.size()) {
                playerIndex = 0;
                isFirstPass = false;
            }

            if (checkIfOnlyOnePlayerActive(params, round)) {
                break;
            }
        }

        bettingRound = bettingRoundService.setBettingRoundFinished(bettingRound);
        dispatcher.send(params, messageFactory.bettingRoundUpdated(round, bettingRound));
    }

    private Round getRound(GameThreadParams params) {
        var roundOpt = roundService.getRoundByTable(params.getTableId());
        if (roundOpt.isEmpty()) {
            throw new GameInterruptedException("Round is empty for table " + params.getTableId());
        }
        return roundOpt.get();
    }

    private BettingRound getBettingRound(Round round) {
        var bettingRoundOpt = bettingRoundService.getCurrentBettingRound(round.getId());
        if (bettingRoundOpt.isEmpty()) {
            throw new GameInterruptedException("Betting Round is empty for round " + round.getId());
        }
        return bettingRoundOpt.get();
    }

    private BettingRound getBettingRound(BettingRound bettingRound) {
        var bettingRoundOpt = bettingRoundService.getBettingRound(bettingRound.getId());
        if (bettingRoundOpt.isPresent()) {
            bettingRound = bettingRoundOpt.get();
        }
        return bettingRound;
    }

    private List<PlayerSession> getActivePlayers(GameThreadParams params, Round round) {
        //todo: does this need to be a new transaction with a service like the rest of them?
        var activePlayers = playerSessionRepository
                .findActivePlayersByTableId(params.getTableId(), round.getId());
        if (activePlayers.isEmpty()) {
            throw new GameInterruptedException("No Active Players found");
        }
        if (activePlayers.size() == 1) {
            throw new RoundInterruptedException("Only one active player, skipping betting round");
        }
        return activePlayers;
    }

    private Optional<PlayerAction> awaitPlayerTurnAction(GameThreadParams params, GameThread gameThread, PlayerSession currentPlayer, BettingRound bettingRound) {
        var prevPlayerActions = playerActionService.refreshPlayerActionsNotFolded(bettingRound.getId());
        var nextActions = getNextActions(prevPlayerActions);

        dispatcher.send(params, messageFactory.playerTurn(currentPlayer, bettingRound, nextActions, params.getPlayerTurnWaitMs()));
        waitPlayerTurn(params, gameThread, currentPlayer);

        return playerActionService.getLatestByBettingRoundAndPlayer(bettingRound.getId(), currentPlayer.getId());
    }

    private UUID getLastAggressorId(PlayerAction actionJustTaken, UUID lastAggressorId, PlayerSession currentPlayer) {
        var type = actionJustTaken.getActionType();
        // last aggressor is the current player if the current player has just bet or raised
        if (type == ActionType.BET || type == ActionType.RAISE) {
            lastAggressorId = currentPlayer.getId();
        }
        return lastAggressorId;
    }

    private NextActionsDTO getNextActions(List<PlayerAction> prevPlayerActions) {
        var amountToCall = 0d;
        var nextActions = ActionType.getDefaultActions();

        if (!prevPlayerActions.isEmpty()) {
            var previousPlayerAction = prevPlayerActions.getFirst();
            var previousPlayerActionType = previousPlayerAction.getActionType();

            nextActions = ActionType.getNextActions(previousPlayerActionType);
            amountToCall = previousPlayerActionType.getAmountToCall(previousPlayerAction.getAmount());
        }
        return new NextActionsDTO(amountToCall, nextActions);
    }

    private boolean checkIfOnlyOnePlayerActive(GameThreadParams params, Round round) {
        var active = playerSessionRepository.findActivePlayersByTableId(params.getTableId(), round.getId());
        return active.size() <= 1;
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
            throw new RoundInterruptedException("Failed to wait for player turn latch", e);
        }
    }
}
