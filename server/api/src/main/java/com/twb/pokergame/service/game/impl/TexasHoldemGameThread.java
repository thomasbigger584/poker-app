package com.twb.pokergame.service.game.impl;

import com.twb.pokergame.domain.AppUser;
import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.Hand;
import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.enumeration.CardType;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.service.eval.dto.EvalPlayerHandDTO;
import com.twb.pokergame.service.game.GameThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Scope("prototype")
public final class TexasHoldemGameThread extends GameThread {
    private static final Logger logger =
            LoggerFactory.getLogger(TexasHoldemGameThread.class);

    public TexasHoldemGameThread(UUID tableId) {
        super(tableId);
    }

    @Override
    protected void onRoundInit() {
        determineNextDealer();
    }

    @Override
    protected void onRunRound(RoundState roundState) {
        switch (roundState) {
            case INIT_DEAL -> initDeal();
            case INIT_DEAL_BET,
                    FLOP_DEAL_BET, TURN_DEAL_BET, RIVER_DEAL_BET -> waitPlayerTurn();
            case FLOP_DEAL -> dealFlop();
            case TURN_DEAL -> dealTurn();
            case RIVER_DEAL -> dealRiver();
            case EVAL -> eval();
        }
    }

    @Override
    protected RoundState getNextRoundState(RoundState roundState) {
        return roundState.nextTexasHoldemState();
    }

    private void initDeal() {
        for (CardType cardType : CardType.PLAYER_CARDS) {
            for (PlayerSession playerSession : playerSessions) {
                dealPlayerCard(cardType, playerSession);
            }
        }
    }

    private void dealFlop() {
        for (CardType cardType : CardType.FLOP_CARDS) {
            dealCommunityCard(cardType);
        }
    }

    private void dealTurn() {
        dealCommunityCard(CardType.TURN_CARD);
    }

    private void dealRiver() {
        dealCommunityCard(CardType.RIVER_CARD);
    }

    private void dealPlayerCard(CardType cardType, PlayerSession playerSession) {
        Card card = getCard();
        card.setCardType(cardType);

        handService.addPlayerCard(playerSession, currentRound, card);
        dispatcher.send(tableId, messageFactory.initDeal(playerSession, card));

        sleepInMs(DEAL_WAIT_MS);
    }

    private void dealCommunityCard(CardType cardType) {
        Card card = getCard();
        card.setCardType(cardType);

        cardService.createCommunityCard(currentRound, card);
        dispatcher.send(tableId, messageFactory.communityCardDeal(card));

        sleepInMs(DEAL_WAIT_MS);
    }

    /**
     * If there is not a dealer already selected then pick a random dealer out of the PlayerSession list.
     * If there is a dealer selected, get the next dealer given the position.
     * Making sure to wrap around the list if the next dealer is actually at the start of the list.
     * <p>
     * Once we know the dealer, we want to reorder the list to have dealer last but still in order by position
     */
    private void determineNextDealer() {

        //get current dealer if already set
        PlayerSession currentDealer = null;
        for (PlayerSession playerSession : playerSessions) {
            if (Boolean.TRUE.equals(playerSession.getDealer())) {
                currentDealer = playerSession;
                break;
            }
        }

        // get next dealer
        if (currentDealer == null) {
            currentDealer = playerSessions.get(RANDOM.nextInt(playerSessions.size()));
        } else {
            int numPlayers = playerSessions.size();
            for (int index = 0; index < numPlayers; index++) {
                PlayerSession thisPlayerSession = playerSessions.get(index);
                if (thisPlayerSession.getPosition()
                        .equals(currentDealer.getPosition())) {
                    int nextIndex = index + 1;
                    if (nextIndex >= numPlayers) {
                        nextIndex = 0;
                    }
                    currentDealer = playerSessions.get(nextIndex);
                }
            }
        }

        //save to database and list
        for (PlayerSession playerSession : playerSessions) {
            playerSession.setDealer(currentDealer.getId().equals(playerSession.getId()));
        }
        playerSessions = playerSessionRepository.saveAllAndFlush(playerSessions);
        checkAtLeastOnePlayerConnected();

        //reorder list for dealer last
        int dealerIndex = -1;
        for (int index = 0; index < playerSessions.size(); index++) {
            PlayerSession playerSession = playerSessions.get(index);
            if (playerSession.getDealer()) {
                dealerIndex = index;
                break;
            }
        }

        if (dealerIndex == -1) {
            fail("Failed to sort player sessions by dealer and position, dealerIndex is -1");
            return;
        }

        int startIndex = dealerIndex + 1;
        if (startIndex >= playerSessions.size()) {
            startIndex = 0;
        }

        // sort playerSessions by positions and dealer last
        List<PlayerSession> dealerSortedList = new ArrayList<>();
        for (int index = startIndex; index < playerSessions.size(); index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        for (int index = 0; index < startIndex; index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        playerSessions = dealerSortedList;
        checkAtLeastOnePlayerConnected();

        dispatcher.send(tableId, messageFactory.dealerDetermined(currentDealer));
    }

    private void waitPlayerTurn() {
        //todo waitPlayerTurn
    }

    private void eval() {
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

        if (winners.size() == 1) {
            handleSinglePlayerWin(winners);
        } else {
            handleMultiplePlayerWin(winners);
        }

        sleepInMs(EVALUATION_WAIT_MS);
    }

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

    private void handleSinglePlayerWin(List<EvalPlayerHandDTO> winners) {
        EvalPlayerHandDTO winnerEvalHandDTO = winners.get(0);
        PlayerSession playerSession = winnerEvalHandDTO.getPlayerSession();
        String username = playerSession.getUser().getUsername();
        String handTypeStr = winnerEvalHandDTO.getHandType().getValue();

        sendLogMessage(String.format("%s wins round with a %s", username, handTypeStr));
    }

    private void handleMultiplePlayerWin(List<EvalPlayerHandDTO> winners) {
        String winnerNames = getReadableWinners(winners);
        String handTypeStr = winners.get(0).getHandType().getValue();

        sendLogMessage(String.format("%s draws round with a %s", winnerNames, handTypeStr));
    }

    private String getReadableWinners(List<EvalPlayerHandDTO> winners) {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < winners.size(); index++) {
            EvalPlayerHandDTO eval = winners.get(index);
            AppUser user = eval.getPlayerSession().getUser();
            sb.append(user.getUsername());
            if (index < winners.size() - 3) {
                sb.append(", ");
            } else if (index == winners.size() - 2) {
                sb.append(" & ");
            }
        }
        return sb.toString();
    }
}
