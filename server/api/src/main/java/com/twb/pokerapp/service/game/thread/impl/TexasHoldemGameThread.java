package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.CardType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.service.eval.dto.EvalPlayerHandDTO;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TexasHoldemGameThread extends GameThread {
    public TexasHoldemGameThread(GameThreadParams params) {
        super(params);
    }

    @Override
    protected void onInitRound() {
        determineNextDealer();
    }

    @Override
    protected void onRunRound(RoundState roundState) {
        switch (roundState) {
            case INIT_DEAL -> initDeal();
            case INIT_DEAL_BET, FLOP_DEAL_BET, TURN_DEAL_BET, RIVER_DEAL_BET -> runBettingRound();
            case FLOP_DEAL -> dealFlop();
            case TURN_DEAL -> dealTurn();
            case RIVER_DEAL -> dealRiver();
            case EVAL -> evaluate();
        }
    }

    private void initDeal() {
        for (var cardType : CardType.PLAYER_CARDS) {
            for (var playerSession : playerSessions) {
                if (!isPlayerFolded(playerSession)) { // cannot fold before all cards are dealt ?
                    checkRoundInterrupted();
                    dealPlayerCard(cardType, playerSession);
                }
            }
        }
    }

    private void runBettingRound() {
        var playersPaidUp = false;
        do {
            var activePlayers = playerSessionRepository
                    .findActivePlayersByTableId(pokerTable.getId(), currentRound.getId());
            if (activePlayers.isEmpty()) {
                throw new RoundInterruptedException("No Active Players found");
            }
            if (activePlayers.size() == 1) {
                dispatcher.send(pokerTable, messageFactory.logMessage("Only one active player in betting round, so skipping"));
                return;
            }

            for (var currentPlayer : activePlayers) {
                var amountToCall = 0d;
                var nextActions = ActionType.getDefaultActions();

                var prevPlayerActions = playerActionRepository
                        .findPlayerActionsNotFolded(currentBettingRound.getId());

                PlayerAction previousPlayerAction = null;
                if (!prevPlayerActions.isEmpty()) {
                    previousPlayerAction = prevPlayerActions.getFirst();
                    nextActions = ActionType.getNextActions(previousPlayerAction.getActionType());
                    amountToCall = getAmountToCall(previousPlayerAction);
                }
                checkRoundInterrupted();
                dispatcher.send(pokerTable, messageFactory.playerTurn(currentPlayer, previousPlayerAction, nextActions, amountToCall));
                waitPlayerTurn(currentPlayer);
                checkRoundInterrupted();
            }

            var playerActionSumAmounts = playerActionRepository.sumAmounts(currentBettingRound.getId());
            playersPaidUp = playerActionSumAmounts != currentBettingRound.getPot();

        } while (playersPaidUp);

        currentRound = roundService.updatePot(currentRound, currentBettingRound);
    }

    private double getAmountToCall(PlayerAction previousPlayerAction) {
        return switch (previousPlayerAction.getActionType()) {
            case FOLD:
            case CHECK:
                yield 0d;
            case BET:
            case CALL:
            case RAISE: {
                yield previousPlayerAction.getAmount();
            }
        };
    }

    @Override
    protected boolean onPlayerAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {
        return switch (createActionDto.getAction()) {
            case CHECK -> checkAction(playerSession, createActionDto);
            case BET -> betAction(playerSession, createActionDto);
            case CALL -> callAction(playerSession, createActionDto);
            case RAISE -> raiseAction(playerSession, createActionDto);
            default ->
                    throw new UnsupportedOperationException("Unsupported Player Action: " + createActionDto.getAction());
        };
    }

    private boolean checkAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {
        var canPerformCheck = playerActionRepository.findPlayerActionsNotFolded(currentBettingRound.getId())
                .stream().allMatch(action -> action.getActionType() == ActionType.CHECK);
        if (!canPerformCheck) {
            log.warn("Cannot check as previous actions was not a check");
            dispatcher.send(pokerTable, playerSession, messageFactory.errorMessage("Cannot check as previous actions was not a check"));
            return false;
        }
        createActionDto.setAmount(0d);
        playerActionService.create(playerSession, currentBettingRound, createActionDto);
        return true;
    }

    private boolean betAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {
        if (createActionDto.getAmount() <= 0) {
            log.warn("Cannot bet as amount is less than or equal to zero");
            dispatcher.send(pokerTable, playerSession, messageFactory.errorMessage("Cannot bet as amount is less than or equal to zero"));
            return false;
        }
        var lastPlayerActions = playerActionRepository.findPlayerActionsNotFolded(currentBettingRound.getId());
        if (!lastPlayerActions.isEmpty()) {
            var lastPlayerAction = lastPlayerActions.getFirst();
            if (List.of(ActionType.BET, ActionType.CALL, ActionType.RAISE).contains(lastPlayerAction.getActionType())) {
                log.warn("Cannot bet as previous action was not a check");
                dispatcher.send(pokerTable, playerSession, messageFactory.errorMessage("Cannot bet as previous action was not a check"));
                return false;
            }
        }
        playerActionService.create(playerSession, currentBettingRound, createActionDto);
        currentBettingRound = bettingRoundService.updatePot(currentBettingRound, createActionDto);
        return true;
    }

    private boolean callAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {
        var lastPlayerActions = playerActionRepository.findPlayerActionsNotFolded(currentBettingRound.getId());
        if (lastPlayerActions.isEmpty()) {
            log.warn("Cannot call as there was no previous action");
            dispatcher.send(pokerTable, playerSession, messageFactory.errorMessage("Cannot call as there was no previous action"));
            return false;
        }
        var lastPlayerAction = lastPlayerActions.getFirst();
        if (lastPlayerAction.getActionType() == ActionType.CHECK) {
            log.warn("Cannot call as previous action was a check");
            dispatcher.send(pokerTable, playerSession, messageFactory.errorMessage("Cannot call as previous action was a check"));
            return false;
        }
        createActionDto.setAmount(getAmountToCall(lastPlayerAction));
        playerActionService.create(playerSession, currentBettingRound, createActionDto);
        currentBettingRound = bettingRoundService.updatePot(currentBettingRound, createActionDto);
        return true;
    }

    private boolean raiseAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {
        throw new UnsupportedOperationException("Raise action not implemented yet");
    }

    private void dealPlayerCard(CardType cardType, PlayerSession playerSession) {
        var card = getCard();
        card.setCardType(cardType);

        handService.addPlayerCard(playerSession, currentRound, card);
        dispatcher.send(pokerTable, messageFactory.initDeal(playerSession, card));

        sleepInMs(params.getDealWaitMs());
    }

    private void dealFlop() {
        for (var cardType : CardType.FLOP_CARDS) {
            checkRoundInterrupted();
            dealCommunityCard(cardType);
        }
    }

    private void dealTurn() {
        dealCommunityCard(CardType.TURN_CARD);
    }

    private void dealRiver() {
        dealCommunityCard(CardType.RIVER_CARD);
    }

    private void dealCommunityCard(CardType cardType) {
        var card = getCard();
        card.setCardType(cardType);

        cardService.createCommunityCard(currentRound, card);
        dispatcher.send(pokerTable, messageFactory.communityCardDeal(card));

        sleepInMs(params.getDealWaitMs());
    }

    @Override
    protected RoundState getNextRoundState(RoundState roundState) {
        return roundState.nextTexasHoldemState();
    }

    private void determineNextDealer() {
        var playerSessions = getPlayerSessionsNotZero();
        this.playerSessions = dealerService.nextDealerReorder(params.getTableId(), playerSessions);
        var currentDealer = dealerService.getCurrentDealer(this.playerSessions);
        dispatcher.send(pokerTable, messageFactory.dealerDetermined(currentDealer));
    }

    // *****************************************************************************************
    // Evaluation
    // *****************************************************************************************

    private void evaluate() {
        var playersNotFolded = playerSessions.stream()
                .filter(playerSession -> !foldedPlayers.contains(playerSession)).toList();
        if (playersNotFolded.size() == 1) {
            evaluateLastPlayerStanding(playersNotFolded);
        } else {
            evaluateMultiPlayersStanding();
        }
        sleepInMs(params.getEvalWaitMs());
    }

    private void evaluateLastPlayerStanding(List<PlayerSession> playersNotFolded) {
        var savingHands = new ArrayList<Hand>();

        var winner = playersNotFolded.getFirst();
        var winnerHandOpt = handRepository.findHandForRound(winner.getId(), currentRound.getId());
        winnerHandOpt.ifPresent(hand -> hand.setWinner(true));

        for (var foldedPlayer : foldedPlayers) {
            var foldedHandOpt = handRepository.findHandForRound(foldedPlayer.getId(), currentRound.getId());
            foldedHandOpt.ifPresent(hand -> hand.setWinner(false));
        }
        handRepository.saveAllAndFlush(savingHands);
        sendLogMessage(String.format("%s wins round", winner.getUser().getUsername()));
    }

    private void evaluateMultiPlayersStanding() {
        var communityCards = cardRepository.findCommunityCardsForRound(currentRound.getId());

        var playerHandsList = new ArrayList<EvalPlayerHandDTO>();
        for (PlayerSession playerSession : playerSessions) {

            var playableCards = new ArrayList<>(communityCards);

            var playerHandOpt = handRepository
                    .findHandForRound(playerSession.getId(), currentRound.getId());

            if (playerHandOpt.isPresent()) {
                var hand = playerHandOpt.get();

                var playerCards = cardRepository.findCardsForHand(hand.getId());
                playableCards.addAll(playerCards);

                var playerHand = new EvalPlayerHandDTO();
                playerHand.setPlayerSession(playerSession);
                playerHand.setCards(playableCards);
                playerHandsList.add(playerHand);
            }
        }
        handEvaluator.evaluate(playerHandsList);

        savePlayerHandEvaluation(playerHandsList);

        var winners = playerHandsList.stream()
                .filter(EvalPlayerHandDTO::isWinner).toList();

        handleWinners(winners);
    }

    //todo: move this into handEvaluator (?)
    private void savePlayerHandEvaluation(List<EvalPlayerHandDTO> playerHandsList) {
        var savingHands = new ArrayList<Hand>();
        for (var playerHand : playerHandsList) {
            var playerSession = playerHand.getPlayerSession();
            var handOpt = handRepository
                    .findHandForRound(playerSession.getId(), currentRound.getId());
            if (handOpt.isPresent()) {
                var hand = handOpt.get();
                hand.setHandType(playerHand.getHandType());
                hand.setWinner(playerHand.isWinner());
                savingHands.add(hand);
            }
        }
        handRepository.saveAllAndFlush(savingHands);
    }
}
