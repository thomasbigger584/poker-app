package com.twb.pokerapp.service.game.thread.impl.texas.bettinground;

import com.twb.pokerapp.config.TransactionConfiguration;
import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.service.BettingRoundService;
import com.twb.pokerapp.service.RoundService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.texas.TexasPlayerActionService;
import com.twb.pokerapp.service.game.thread.impl.texas.dto.NextActionsDTO;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.twb.pokerapp.repository.RepositoryUtil.getThrowGameInterrupted;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LastAggressorService {
    @Autowired
    private TransactionConfiguration transaction;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private BettingRoundRepository bettingRoundRepository;

    @Autowired
    private PlayerSessionRepository playerSessionRepository;

    @Autowired
    private PlayerActionRepository playerActionRepository;

    @Autowired
    private RoundService roundService;

    @Autowired
    private BettingRoundService bettingRoundService;

    @Autowired
    private TexasPlayerActionService texasPlayerActionService;

    @Autowired
    private MessageDispatcher dispatcher;

    @Autowired
    private ServerMessageFactory messageFactory;

    private final GameThreadParams params;
    private final GameThread gameThread;

    private Round round;
    private BettingRound bettingRound;
    private int playerIndex = 0;
    private UUID lastAggressorId = null;
    private boolean isFirstPass = true;
    private PlayerSession currentPlayer = null;
    private NextActionsDTO nextActions = null;

    public LastAggressorService(GameThreadParams params,
                                GameThread gameThread) {
        this.params = params;
        this.gameThread = gameThread;
    }

    public LastAggressorService onPrePlayerTurn() {
        transaction.getReadTx().executeWithoutResult(status -> {
            round = getThrowGameInterrupted(roundRepository.findCurrentByTableId(params.getTableId()), "Round is empty for table");
            bettingRound = getThrowGameInterrupted(bettingRoundRepository.findCurrentByRoundId(round.getId()), "Betting Round is empty for round");

            var activePlayers = getActivePlayers(params, round);

            // TODO: check what happens with folds, we may need to gracefully handle this inside the loop
            // Re-fetch players only if necessary to handle folds dynamically,
            // but for index stability, it is often safer to keep the list and check 'isActive'
            // inside the loop. For this implementation, we assume activePlayers list
            // stays valid for indexing, but we check if they folded via DB/Status if needed.

            if (playerIndex >= activePlayers.size()) {
                playerIndex = 0; // wrap index
            }

            currentPlayer = activePlayers.get(playerIndex);

            // --- TERMINATION CHECKS ---
            // This means everyone else has had a chance to call/fold, and we are back to the person who bet/raised.
            // They do not act again unless someone else re-raised (which would have updated lastAggressorId).
            if (currentPlayer.getId().equals(lastAggressorId)) {
                throw new LastAggressorBreakException("Returned to last aggressor %s, betting round finished.".formatted(currentPlayer.getId()));
            }
            // If we have circled back to the start and no one has bet (lastAggressorId is null).
            if (lastAggressorId == null && !isFirstPass && playerIndex == 0) {
                throw new LastAggressorBreakException("Checked around, betting round finished.");
            }
            var prevPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
            nextActions = getNextActions(prevPlayerActions);
        });
        return this;
    }

    public LastAggressorService onWaitPlayerTurn() {
        dispatcher.send(params, messageFactory.playerTurn(currentPlayer, bettingRound, nextActions, params.getPlayerTurnWaitMs()));
        waitPlayerTurn(params, gameThread, currentPlayer);
        return this;
    }

    public void onPostPlayerTurn() {
        transaction.getWriteTx().executeWithoutResult(status -> {
            var latestPlayerActionOpt = playerActionRepository
                    .findByBettingRoundAndPlayer(bettingRound.getId(), currentPlayer.getId());

            latestPlayerActionOpt.ifPresent(actionJustTaken ->
                    lastAggressorId = getLastAggressorId(actionJustTaken, lastAggressorId, currentPlayer));

            bettingRound = getBettingRound(bettingRound);
            round = roundService.updatePot(round, bettingRound);

            dispatcher.send(params, messageFactory.bettingRoundUpdated(round, bettingRound));
        });
        playerIndex++;
        transaction.getReadTx().executeWithoutResult(status -> {
            var activePlayers = getActivePlayers(params, round);
            if (playerIndex >= activePlayers.size()) {
                playerIndex = 0;
                isFirstPass = false;
            }
            if (checkIfOnlyOnePlayerActive(params, round)) {
                throw new LastAggressorBreakException("Only one active player, skipping betting round");
            }
        });
    }

    public void finishBettingRound() {
        transaction.getWriteTx().executeWithoutResult(status -> {
            bettingRound = bettingRoundService.setBettingRoundFinished(bettingRound);
            dispatcher.send(params, messageFactory.bettingRoundUpdated(round, bettingRound));
        });
    }

    private List<PlayerSession> getActivePlayers(GameThreadParams params, Round round) {
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

    private void waitPlayerTurn(GameThreadParams params, GameThread gameThread, PlayerSession playerSession) {
        var playerTurnLatch = gameThread.newPlayerTurnLatch(playerSession);
        var latch = playerTurnLatch.playerTurnLatch();
        try {
            var await = latch.await(params.getPlayerTurnWaitMs(), TimeUnit.MILLISECONDS);
            if (!await) {
                var createActionDto = new CreatePlayerActionDTO();
                createActionDto.setAction(ActionType.FOLD);
                transaction.getWriteTx().executeWithoutResult(status -> {
                    var playerSessionManaged = getThrowGameInterrupted(playerSessionRepository.findById(playerSession.getId()), "Player Session not found");
                    texasPlayerActionService.playerAction(playerSessionManaged, gameThread, createActionDto);
                });
            }
        } catch (InterruptedException e) {
            throw new RoundInterruptedException("Failed to wait for player turn latch", e);
        }
    }

    private UUID getLastAggressorId(PlayerAction actionJustTaken, UUID lastAggressorId, PlayerSession currentPlayer) {
        var type = actionJustTaken.getActionType();
        // last aggressor is the current player if the current player has just bet or raised
        if (type == ActionType.BET || type == ActionType.RAISE) {
            lastAggressorId = currentPlayer.getId();
        }
        return lastAggressorId;
    }


    private BettingRound getBettingRound(BettingRound bettingRound) {
        var bettingRoundOpt = bettingRoundRepository.findById(bettingRound.getId());
        if (bettingRoundOpt.isPresent()) {
            bettingRound = bettingRoundOpt.get();
        }
        return bettingRound;
    }

    private boolean checkIfOnlyOnePlayerActive(GameThreadParams params, Round round) {
        var active = playerSessionRepository.findActivePlayersByTableId(params.getTableId(), round.getId());
        return active.size() <= 1;
    }

    static class LastAggressorBreakException extends RuntimeException {
        public LastAggressorBreakException(String message) {
            super(message);
        }
    }
}
