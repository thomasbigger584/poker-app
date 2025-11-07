package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.*;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.CardType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.service.eval.dto.EvalPlayerHandDTO;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

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
    protected RoundState getNextRoundState(RoundState roundState) {
        return roundState.nextTexasHoldemState();
    }

    private void initDeal() {
        for (CardType cardType : CardType.PLAYER_CARDS) {
            for (PlayerSession playerSession : playerSessions) {
                if (!isPlayerFolded(playerSession)) { // cannot fold before all cards are dealt ?
                    checkRoundInterrupted();
                    dealPlayerCard(cardType, playerSession);
                }
            }
        }
    }

    private void runBettingRound() {
        PlayerSession previousPlayer = null;
        for (PlayerSession currentPlayer : playerSessions) {
            if (!isPlayerFolded(currentPlayer)) {
                checkRoundInterrupted();

                double amountToCall = 0d;
                ActionType[] nextActions = ActionType.getDefaultActions();

                if (previousPlayer != null) {
                    Optional<BettingRound> bettingRoundOpt = bettingRoundRepository.findById(currentBettingRound.getId());
                    if (bettingRoundOpt.isPresent()) {
                        currentBettingRound = bettingRoundOpt.get();

                        List<PlayerAction> previousActions = playerActionRepository
                                .findByBettingRoundAndPlayerSession(currentBettingRound.getId(), previousPlayer.getId());

                        PlayerAction previousPlayerAction = previousActions.getLast();
                        nextActions = ActionType.getNextActions(previousPlayerAction.getActionType());

                        switch (previousPlayerAction.getActionType()) {
                            case BET:
                            case CALL: {
                                amountToCall = previousPlayerAction.getAmount();
                                break;
                            }
                            case RAISE: {
                                // todo: need to handle raise
                                throw new NotImplementedException("Raise not implemented");
                            }
                        }
                    } else {
                        throw new IllegalStateException("Betting round is not present");
                    }
                }
                dispatcher.send(params.getTableId(), messageFactory.playerTurn(currentPlayer, nextActions, amountToCall));
                previousPlayer = currentPlayer;
                waitPlayerTurn(currentPlayer);
            }
        }
    }

    private void dealPlayerCard(CardType cardType, PlayerSession playerSession) {
        Card card = getCard();
        card.setCardType(cardType);

        handService.addPlayerCard(playerSession, currentRound, card);
        dispatcher.send(params.getTableId(), messageFactory.initDeal(playerSession, card));

        sleepInMs(DEAL_WAIT_MS);
    }

    private void dealFlop() {
        for (CardType cardType : CardType.FLOP_CARDS) {
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
        Card card = getCard();
        card.setCardType(cardType);

        cardService.createCommunityCard(currentRound, card);
        dispatcher.send(params.getTableId(), messageFactory.communityCardDeal(card));

        sleepInMs(DEAL_WAIT_MS);
    }

    private void determineNextDealer() {
        List<PlayerSession> playerSessions = getPlayerSessionsNotZero();
        this.playerSessions = dealerService.nextDealerReorder(params.getTableId(), playerSessions);
        PlayerSession currentDealer = dealerService.getCurrentDealer(this.playerSessions);
        dispatcher.send(params.getTableId(), messageFactory.dealerDetermined(currentDealer));
    }

    private void evaluate() {
        List<PlayerSession> playersNotFolded = playerSessions.stream()
                .filter(playerSession -> !foldedPlayers.contains(playerSession)).toList();
        if (playersNotFolded.size() == 1) {
            evaluateLastPlayerStanding(playersNotFolded);
        } else {
            evaluateMultiPlayersStanding();
        }
        sleepInMs(EVALUATION_WAIT_MS);
    }

    private void evaluateLastPlayerStanding(List<PlayerSession> playersNotFolded) {
        List<Hand> savingHands = new ArrayList<>();

        PlayerSession winner = playersNotFolded.getFirst();
        Optional<Hand> winnerHandOpt = handRepository
                .findHandForRound(winner.getId(), currentRound.getId());
        if (winnerHandOpt.isPresent()) {
            Hand hand = winnerHandOpt.get();
            hand.setWinner(true);
        }
        for (PlayerSession foldedPlayer : foldedPlayers) {
            Optional<Hand> foldedHandOpt = handRepository
                    .findHandForRound(foldedPlayer.getId(), currentRound.getId());
            if (foldedHandOpt.isPresent()) {
                Hand hand = foldedHandOpt.get();
                hand.setWinner(false);
            }
        }
        handRepository.saveAllAndFlush(savingHands);
        sendLogMessage(String.format("%s wins round", winner.getUser().getUsername()));
    }

    private void evaluateMultiPlayersStanding() {
        List<Card> communityCards = cardRepository
                .findCommunityCardsForRound(currentRound.getId());

        List<EvalPlayerHandDTO> playerHandsList = new ArrayList<>();
        for (PlayerSession playerSession : playerSessions) {

            List<Card> playableCards = new ArrayList<>(communityCards);

            Optional<Hand> playerHandOpt = handRepository
                    .findHandForRound(playerSession.getId(), currentRound.getId());

            if (playerHandOpt.isPresent()) {
                Hand hand = playerHandOpt.get();

                List<Card> playerCards = cardRepository
                        .findCardsForHand(hand.getId());
                playableCards.addAll(playerCards);

                EvalPlayerHandDTO playerHand = new EvalPlayerHandDTO();
                playerHand.setPlayerSession(playerSession);
                playerHand.setCards(playableCards);
                playerHandsList.add(playerHand);
            }
        }
        handEvaluator.evaluate(playerHandsList);

        savePlayerHandEvaluation(playerHandsList);

        List<EvalPlayerHandDTO> winners =
                playerHandsList.stream().filter(EvalPlayerHandDTO::isWinner).toList();

        handleWinners(winners);
    }

    //todo: move this into handEvaluator (?)
    private void savePlayerHandEvaluation(List<EvalPlayerHandDTO> playerHandsList) {
        List<Hand> savingHands = new ArrayList<>();
        for (EvalPlayerHandDTO playerHand : playerHandsList) {
            PlayerSession playerSession = playerHand.getPlayerSession();
            Optional<Hand> handOpt = handRepository
                    .findHandForRound(playerSession.getId(), currentRound.getId());
            if (handOpt.isPresent()) {
                Hand hand = handOpt.get();
                hand.setHandType(playerHand.getHandType());
                hand.setWinner(playerHand.isWinner());
                savingHands.add(hand);
            }
        }
        handRepository.saveAllAndFlush(savingHands);
    }
}
