package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.CardType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.texas.bettinground.TexasBettingRoundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.twb.pokerapp.service.game.thread.util.SleepUtil.sleepInMs;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TexasGameThread extends GameThread {

    @Autowired
    private TexasDealerService dealerService;

    @Autowired
    private TexasEvaluationService evaluationService;

    @Autowired
    private TexasBettingRoundService texasBettingRoundService;

    @Autowired
    private TexasPlayerActionService texasPlayerActionService;

    public TexasGameThread(GameThreadParams params) {
        super(params);
    }

    @Override
    protected void onInitRound() {
        dealerService.determineNextDealer(params);
    }

    @Override
    protected void onRunRound(RoundState roundState) {
        switch (roundState) {
            case INIT_DEAL -> initDeal();
            case INIT_DEAL_BET, FLOP_DEAL_BET, TURN_DEAL_BET, RIVER_DEAL_BET ->
                    texasBettingRoundService.runBettingRound(params, this);
            case FLOP_DEAL -> dealFlop();
            case TURN_DEAL -> dealCommunityCard(CardType.TURN_CARD);
            case RIVER_DEAL -> dealCommunityCard(CardType.RIVER_CARD);
            case EVAL -> evaluationService.evaluate(params);
        }
    }

    private void initDeal() {
        var activePlayers = playerSessionRepository
                .findActivePlayersByTableId(table.getId(), roundId);

        for (var cardType : CardType.PLAYER_CARDS) {
            for (var playerSession : activePlayers) {
                checkRoundInterrupted();
                dealPlayerCard(cardType, playerSession);
            }
        }
    }

    private void dealPlayerCard(CardType cardType, PlayerSession playerSession) {
        writeTx.executeWithoutResult(status -> {
            var card = getCard();
            card.setCardType(cardType);
            handService.addPlayerCard(table, playerSession, card);
            dispatcher.send(table, messageFactory.initDeal(playerSession, card));
        });
        sleepInMs(params.getDealWaitMs());
    }

    private void dealFlop() {
        for (var cardType : CardType.FLOP_CARDS) {
            checkRoundInterrupted();
            dealCommunityCard(cardType);
        }
    }

    private void dealCommunityCard(CardType cardType) {
        writeTx.executeWithoutResult(status -> {
            var card = getCard();
            card.setCardType(cardType);
            cardService.createCommunityCard(table, card);
            dispatcher.send(table, messageFactory.communityCardDeal(card));
        });
        sleepInMs(params.getDealWaitMs());
    }

    @Override
    protected RoundState getNextRoundState(RoundState roundState) {
        return roundState.nextTexasState();
    }
}
