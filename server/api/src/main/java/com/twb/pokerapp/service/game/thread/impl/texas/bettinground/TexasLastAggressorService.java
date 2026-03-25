package com.twb.pokerapp.service.game.thread.impl.texas.bettinground;

import com.twb.pokerapp.domain.BettingRound;
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
import com.twb.pokerapp.service.PlayerActionService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.texas.TexasPlayerActionService;
import com.twb.pokerapp.service.game.thread.impl.texas.dto.NextActionsDTO;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.twb.pokerapp.repository.RepositoryUtil.getThrowGameInterrupted;
import static com.twb.pokerapp.util.TransactionUtil.afterCommit;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TexasLastAggressorService {
    @Autowired
    private TransactionTemplate writeTx;

    @Autowired
    @Qualifier("readTx")
    private TransactionTemplate readTx;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private BettingRoundRepository bettingRoundRepository;

    @Autowired
    private PlayerSessionRepository playerSessionRepository;

    @Autowired
    private PlayerActionRepository playerActionRepository;

    @Autowired
    private BettingRoundService bettingRoundService;

    @Autowired
    private TexasPlayerActionService texasPlayerActionService;

    @Autowired
    private PlayerActionService playerActionService;

    @Autowired
    private TexasRoundPotService texasRoundPotService;

    @Autowired
    private MessageDispatcher dispatcher;

    @Autowired
    private ServerMessageFactory messageFactory;

    private final GameThreadParams params;
    private final GameThread gameThread;

    private Round round;
    private BettingRound bettingRound;
    private List<PlayerSession> activePlayers = new ArrayList<>();
    private int playerIndex = 0;
    private UUID lastAggressorId = null;
    private boolean isFirstPass = true;
    private PlayerSession currentPlayer = null;
    private NextActionsDTO nextActions = null;

    public TexasLastAggressorService(GameThreadParams params, GameThread gameThread) {
        this.params = params;
        this.gameThread = gameThread;
    }

    // *****************************************************************************************
    // Public Methods
    // *****************************************************************************************


    public void runPlayerInBettingRound(GameThread gameThread) {
        prePlayerTurn();
        gameThread.checkRoundInterrupted();
        waitPlayerTurn();
        postPlayerTurn();
    }

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    private void prePlayerTurn() {
        readTx.executeWithoutResult(status -> {
            round = getThrowGameInterrupted(roundRepository.findCurrentByTableId(params.getTableId()), "Round is empty for table");
            bettingRound = getThrowGameInterrupted(bettingRoundRepository.findCurrentByRoundId(round.getId()), "Betting Round is empty for round");

            if (activePlayers.isEmpty()) {
                activePlayers = getActivePlayers(params, round);
            }

            if (playerIndex >= activePlayers.size()) {
                log.info("Wrapping index with size: {}...", activePlayers.size());
                playerIndex = 0;
            }

            currentPlayer = activePlayers.get(playerIndex);
            while (!Boolean.TRUE.equals(currentPlayer.getActive())) {
                playerIndex++;
                if (playerIndex >= activePlayers.size()) {
                    playerIndex = 0;
                }
                currentPlayer = activePlayers.get(playerIndex);
            }

            // --- TERMINATION CHECKS ---
            if (currentPlayer.getId().equals(lastAggressorId)) {
                throw new LastAggressorBreakException("Returned to last aggressor %s, betting round finished.".formatted(currentPlayer.getId()));
            }
            // If we have circled back to the start and no one has bet (lastAggressorId is null).
            if (lastAggressorId == null && !isFirstPass && playerIndex == 0) {
                throw new LastAggressorBreakException("Checked around, betting round finished.");
            }
            var prevPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
            nextActions = playerActionService.getNextActions(currentPlayer, prevPlayerActions);
        });
    }

    private void waitPlayerTurn() {
        dispatcher.send(params, messageFactory.playerTurn(currentPlayer, bettingRound, nextActions, params.getPlayerTurnWaitMs()));
        waitPlayerTurn(params, gameThread, currentPlayer);
    }

    private void postPlayerTurn() {
        writeTx.executeWithoutResult(status -> {
            var latestPlayerActionOpt = playerActionRepository.findByBettingRoundAndPlayer(bettingRound.getId(), currentPlayer.getId());
            latestPlayerActionOpt.ifPresentOrElse(actionJustTaken -> {
                if (playerActionService.isAggressive(actionJustTaken)) {
                    lastAggressorId = currentPlayer.getId();
                }
            }, () -> {
                throw new GameInterruptedException("Last Player Action not found for player: " + currentPlayer.getUser().getUsername());
            });
        });
        playerIndex++;
        readTx.executeWithoutResult(status -> {
            refreshActivePlayers();
            if (playerIndex >= activePlayers.size()) {
                playerIndex = 0;
                isFirstPass = false;
            }
            long activeCount = activePlayers.stream()
                    .filter(p -> Boolean.TRUE.equals(p.getActive()))
                    .count();
            if (activeCount <= 1) {
                throw new RoundInterruptedException("Only one active player, skipping betting round");
            }
        });
    }

    public void finishBettingRound() {
        writeTx.executeWithoutResult(status -> {
            var bettingRoundOpt = bettingRoundRepository.findById(bettingRound.getId());
            bettingRoundOpt.ifPresent(bettingRound -> {
                this.round = texasRoundPotService.reconcilePots(round);
                this.bettingRound = bettingRoundService.setBettingRoundFinished(bettingRound);
                var roundPots = this.round.getRoundPots();
                afterCommit(() -> dispatcher.send(params, messageFactory.bettingRoundUpdated(round, bettingRound, roundPots)));
            });
        });
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private List<PlayerSession> getActivePlayers(GameThreadParams params, Round round) {
        var activePlayers = playerSessionRepository.findActivePlayersByTableId(params.getTableId(), round.getId());
        if (activePlayers.isEmpty()) {
            throw new GameInterruptedException("No Active Players found");
        }
        if (activePlayers.size() == 1) {
            throw new RoundInterruptedException("Only one active player, skipping betting round");
        }
        return activePlayers;
    }

    private void refreshActivePlayers() {
        var latestPlayers = playerSessionRepository.findPlayersOnRound(round.getId());
        for (var index = 0; index < activePlayers.size(); index++) {
            var currentPlayer = activePlayers.get(index);
            for (var latestPlayer : latestPlayers) {
                if (currentPlayer.getId().equals(latestPlayer.getId())) {
                    activePlayers.set(index, latestPlayer);
                    break;
                }
            }
        }
    }

    private void waitPlayerTurn(GameThreadParams params, GameThread gameThread, PlayerSession playerSession) {
        var playerTurnLatch = gameThread.newPlayerTurnLatch(playerSession);
        var latch = playerTurnLatch.playerTurnLatch();
        try {
            var await = latch.await(params.getPlayerTurnWaitMs(), TimeUnit.MILLISECONDS);
            if (!await) {
                onPlayerTurnWaited(gameThread, playerSession);
            }
        } catch (InterruptedException e) {
            throw new RoundInterruptedException("Failed to wait for player turn latch", e);
        }
    }

    private void onPlayerTurnWaited(GameThread gameThread, PlayerSession playerSession) {
        var createActionDto = new CreatePlayerActionDTO();
        createActionDto.setAction(ActionType.FOLD);
        writeTx.executeWithoutResult(status -> {
            var playerSessionManaged = getThrowGameInterrupted(playerSessionRepository.findById(playerSession.getId()), "Player Session not found");
            texasPlayerActionService.playerAction(playerSessionManaged, gameThread, createActionDto);
        });
    }

    static class LastAggressorBreakException extends RuntimeException {
        public LastAggressorBreakException(String message) {
            super(message);
        }
    }
}
