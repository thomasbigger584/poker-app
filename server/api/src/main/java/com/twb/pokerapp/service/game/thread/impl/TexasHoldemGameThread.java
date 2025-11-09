package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.CardType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.dto.playeraction.PlayerActionDTO;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.service.eval.dto.EvalPlayerHandDTO;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TexasHoldemGameThread extends GameThread {
    private static final Logger logger =
            LoggerFactory.getLogger(TexasHoldemGameThread.class);

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


    @Override
    protected void onPlayerAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {
        switch (createActionDto.getAction()) {
            case CHECK -> checkAction(playerSession, createActionDto);
            case BET -> betAction(playerSession, createActionDto);
            case CALL -> callAction(playerSession, createActionDto);
            case RAISE -> raiseAction(playerSession, createActionDto);
        }
    }

    @Override
    protected RoundState getNextRoundState(RoundState roundState) {
        return roundState.nextTexasHoldemState();
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

                double amountToCall = 0d;
                var nextActions = ActionType.getDefaultActions();

                var prevPlayerActions = playerActionRepository
                        .findPlayerActionsNotFolded(currentBettingRound.getId());

                if (!prevPlayerActions.isEmpty()) {
                    var previousPlayerAction = prevPlayerActions.getLast();
                    nextActions = ActionType.getNextActions(previousPlayerAction.getActionType());

                    switch (previousPlayerAction.getActionType()) {
                        case BET:
                        case CALL:
                        case RAISE: {
                            amountToCall = previousPlayerAction.getAmount();
                            break;
                        }
                    }
                }

                checkRoundInterrupted();
                dispatcher.send(pokerTable, messageFactory.playerTurn(currentPlayer, nextActions, amountToCall));
                waitPlayerTurn(currentPlayer);
                checkRoundInterrupted();
            }

            var playerActionSumAmounts = playerActionRepository.sumAmounts(currentBettingRound.getId());
            playersPaidUp = playerActionSumAmounts != currentBettingRound.getPot();

        } while (playersPaidUp);
    }

    private void checkAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {
        var canPerformCheck = playerActionRepository.findPlayerActionsNotFolded(currentBettingRound.getId())
                .stream().allMatch(action -> action.getActionType() == ActionType.CHECK);
        if (!canPerformCheck) {
            logger.warn("Cannot check as previous actions was not a check");
            dispatcher.send(pokerTable, playerSession, messageFactory.logMessage("Cannot check as previous actions was not a check"));
            return;
        }
        createActionDto.setAmount(0d);
        var actionDto = playerActionService.create(playerSession, currentBettingRound, createActionDto);
        dispatcher.send(pokerTable, messageFactory.playerAction(actionDto));
    }

    private void betAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {

    }

    private void callAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {

    }

    private void raiseAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {

    }

    private void dealPlayerCard(CardType cardType, PlayerSession playerSession) {
        var card = getCard();
        card.setCardType(cardType);

        handService.addPlayerCard(playerSession, currentRound, card);
        dispatcher.send(pokerTable, messageFactory.initDeal(playerSession, card));

        sleepInMs(DEAL_WAIT_MS);
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

        sleepInMs(DEAL_WAIT_MS);
    }

    private void determineNextDealer() {
        var playerSessions = getPlayerSessionsNotZero();
        this.playerSessions = dealerService.nextDealerReorder(params.getTableId(), playerSessions);
        var currentDealer = dealerService.getCurrentDealer(this.playerSessions);
        dispatcher.send(pokerTable, messageFactory.dealerDetermined(currentDealer));
    }

    private void evaluate() {
        var playersNotFolded = playerSessions.stream()
                .filter(playerSession -> !foldedPlayers.contains(playerSession)).toList();
        if (playersNotFolded.size() == 1) {
            evaluateLastPlayerStanding(playersNotFolded);
        } else {
            evaluateMultiPlayersStanding();
        }
        sleepInMs(EVALUATION_WAIT_MS);
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
