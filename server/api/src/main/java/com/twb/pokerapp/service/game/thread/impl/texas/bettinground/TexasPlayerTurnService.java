package com.twb.pokerapp.service.game.thread.impl.texas.bettinground;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.service.game.exception.GameInterruptedException;
import com.twb.pokerapp.service.game.exception.RoundInterruptedException;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.service.BettingRoundService;
import com.twb.pokerapp.service.PlayerActionService;
import com.twb.pokerapp.service.UserWebsocketService;
import com.twb.pokerapp.service.game.thread.GamePlayerTurnService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.texas.TexasPlayerActionService;
import com.twb.pokerapp.service.game.thread.impl.texas.dealer.TexasDealerService;
import com.twb.pokerapp.service.game.thread.impl.texas.dto.NextActionsDTO;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.twb.pokerapp.repository.RepositoryUtil.getThrowGameInterrupted;
import static com.twb.pokerapp.util.TransactionUtil.afterCommit;

@Slf4j
@Component("texasPlayerTurnService")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TexasPlayerTurnService implements GamePlayerTurnService {

    @Autowired
    private TransactionTemplate writeTx;

    @Autowired
    @Qualifier("readTx")
    private TransactionTemplate readTx;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private TexasDealerService dealerService;

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

    @Autowired
    private UserWebsocketService userWebsocketService;

    private final GameThread gameThread;
    private final GameThreadParams params;

    private Round round;
    private BettingRound bettingRound;
    private List<PlayerSession> activePlayers = new ArrayList<>();
    private int playerIndex = 0;
    private UUID lastAggressorId = null;
    private boolean isFirstPass = true;
    private PlayerSession currentPlayer = null;
    private NextActionsDTO nextActions = null;

    public TexasPlayerTurnService(GameThread gameThread) {
        this.gameThread = gameThread;
        this.params = gameThread.getParams();
    }

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @Override
    public boolean executeTurn(GameThread gameThread) {
        if (!prePlayerTurn()) {
            return false;
        }
        gameThread.checkRoundInterrupted();
        runPlayerTurn();
        postPlayerTurn();

        return true;
    }

    @Override
    public void finish() {
        if (bettingRound == null) {
            throw new GameInterruptedException("Betting round not found so cannot finish");
        }
        writeTx.executeWithoutResult(status -> {
            var bettingRoundOpt = bettingRoundRepository.findById(bettingRound.getId());
            bettingRoundOpt.ifPresent(br -> {
                this.round = texasRoundPotService.reconcilePots(round, br);
                this.bettingRound = bettingRoundService.setState(br, BettingRoundState.FINISHED);
                var roundPots = this.round.getRoundPots();
                afterCommit(() -> dispatcher.send(params.getTable(), messageFactory.bettingRoundUpdated(round, br, roundPots)));
            });
        });
    }

    // *****************************************************************************************
    // Internal Methods
    // *****************************************************************************************

    private boolean prePlayerTurn() {
        var shouldContinue = new AtomicBoolean(true);
        var table = params.getTable();
        readTx.executeWithoutResult(status -> {
            round = getThrowGameInterrupted(roundRepository.findCurrentByTableId(table.getId()), "Round is empty for table");
            bettingRound = getThrowGameInterrupted(bettingRoundRepository.findCurrentByRoundId(round.getId()), "Betting Round is empty for round");
            if (activePlayers.isEmpty()) {
                activePlayers = getActivePlayers(round);
            } else {
                refreshActivePlayers();
            }

            List<PlayerSession> actionablePlayers = activePlayers.stream()
                    .filter(this::isActionable).toList();

            if (actionablePlayers.isEmpty()) {
                log.debug("No actionable players found, skipping betting round.");
                shouldContinue.set(false);
                return;
            }

            if (actionablePlayers.size() == 1) {
                var loneActionablePlayer = actionablePlayers.getFirst();
                var roundActions = playerActionRepository.findByRoundId(round.getId());
                var contributions = new HashMap<UUID, BigDecimal>();
                for (var action : roundActions) {
                    if (action.getAmount() != null) {
                        contributions.merge(action.getPlayerSession().getId(), action.getAmount(), BigDecimal::add);
                    }
                }
                var maxContribution = contributions.values()
                        .stream()
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                var playerContribution = contributions
                        .getOrDefault(loneActionablePlayer.getId(), BigDecimal.ZERO);

                if (playerContribution.compareTo(maxContribution) >= 0) {
                    log.debug("Only one actionable player {} and they have matched max contribution {}, skipping betting round.",
                            loneActionablePlayer.getUser().getUsername(), maxContribution);
                    shouldContinue.set(false);
                    return;
                }
            }

            if (playerIndex >= activePlayers.size()) {
                log.debug("Wrapping index with size: {}...", activePlayers.size());
                playerIndex = 0;
                isFirstPass = false;
            }

            // Termination Check: Last Aggressor
            if (activePlayers.get(playerIndex).getId().equals(lastAggressorId)) {
                log.debug("Returned to last aggressor {}, betting round finished.", lastAggressorId);
                shouldContinue.set(false);
                return;
            }

            // Skip Non-Actionable Players (folded or all-in)
            var startPlayerIndex = playerIndex;
            while (!isActionable(activePlayers.get(playerIndex))) {
                playerIndex++;
                if (playerIndex >= activePlayers.size()) {
                    playerIndex = 0;
                    isFirstPass = false;
                }
                if (activePlayers.get(playerIndex).getId().equals(lastAggressorId)) {
                    log.debug("Skipped to last aggressor {}, betting round finished.", lastAggressorId);
                    shouldContinue.set(false);
                    return;
                }
                if (playerIndex == startPlayerIndex) {
                    log.debug("No actionable players found, betting round finished.");
                    shouldContinue.set(false);
                    return;
                }
            }
            currentPlayer = activePlayers.get(playerIndex);
            if (lastAggressorId == null && !isFirstPass) {
                log.debug("Checked around, betting round finished.");
                shouldContinue.set(false);
                return;
            }
            var prevPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
            nextActions = playerActionService.getNextActions(currentPlayer, prevPlayerActions);
        });
        return shouldContinue.get();
    }

    private void runPlayerTurn() {
        if (isPlayerDisconnected(currentPlayer)) {
            log.debug("Player {} disconnected, auto-folding/checking...", currentPlayer.getUser().getUsername());
            handleDisconnectedPlayerTurn();
        } else {
            waitPlayerTurn();
        }
    }

    private void handleDisconnectedPlayerTurn() {
        writeTx.executeWithoutResult(status -> {
            var playerSessionManaged = getThrowGameInterrupted(playerSessionRepository.findById(currentPlayer.getId()), "Player Session not found during handling disconnected during player turn: " + currentPlayer.getId());
            texasPlayerActionService.onExecuteAutoAction(playerSessionManaged, bettingRound, gameThread);
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
            var activeCount = activePlayers.stream()
                    .filter(p -> Boolean.TRUE.equals(p.getActive()))
                    .count();
            if (activeCount <= 1) {
                throw new RoundInterruptedException("Only one active player, skipping betting round");
            }
        });
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private boolean isActionable(PlayerSession playerSession) {
        return Boolean.TRUE.equals(playerSession.getActive()) &&
                playerSession.getFunds() != null && playerSession.getFunds().compareTo(BigDecimal.ZERO) > 0
                && playerSession.getSessionState() == SessionState.CONNECTED
                && playerSession.getConnectionType() == ConnectionType.PLAYER;
    }

    private List<PlayerSession> getActivePlayers(Round round) {
        var table = params.getTable();
        var players = playerSessionRepository
                .findActivePlayersByTableId(table.getId(), round.getId());
        if (players.isEmpty()) {
            throw new GameInterruptedException("No Active Players found");
        }
        if (players.size() == 1) {
            throw new RoundInterruptedException("Only one active player, skipping betting round");
        }
        return dealerService.sortDealerLast(players);
    }

    private boolean isPlayerDisconnected(PlayerSession playerSession) {
        return userWebsocketService.isUserDisconnected(params.getTable(), playerSession);
    }

    private void refreshActivePlayers() {
        var latestPlayers = playerSessionRepository.findPlayersOnRound(round.getId());
        for (var index = 0; index < activePlayers.size(); index++) {
            var session = activePlayers.get(index);
            for (var latestPlayer : latestPlayers) {
                if (session.getId().equals(latestPlayer.getId())) {
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
        writeTx.executeWithoutResult(status -> {
            var playerSessionManaged = getThrowGameInterrupted(playerSessionRepository.findById(playerSession.getId()), "Player Session not found during player turn waited: " + playerSession.getId());
            texasPlayerActionService.onExecuteAutoAction(playerSessionManaged, bettingRound, gameThread);
        });
    }
}
