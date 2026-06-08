package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.CardType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.texas.bettinground.TexasBettingRoundService;
import com.twb.pokerapp.service.game.thread.impl.texas.dealer.TexasDealerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component("texasGameThread")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TexasGameThread extends GameThread {

    @Autowired
    private TexasDealerService dealerService;

    @Autowired
    private TexasEvaluationService evaluationService;

    @Autowired
    private TexasBettingRoundService texasBettingRoundService;

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
                    texasBettingRoundService.runBettingRound(this);
            case FLOP_DEAL -> dealFlop();
            case TURN_DEAL -> dealCommunityCard(CardType.TURN_CARD);
            case RIVER_DEAL -> dealCommunityCard(CardType.RIVER_CARD);
            case EVAL -> evaluationService.evaluate(params);
        }
    }

    private void initDeal() {
        var activePlayers = playerSessionRepository
                .findActivePlayersByTableId(table.getId(), roundId);

        // A player who isn't actually connected at deal time (e.g. dropped during the disconnect
        // grace window, where the DB session is still CONNECTED but the socket is gone) can't be
        // dealt cards. Remove them from the round entirely so they can't linger as an active
        // player with no hole cards; otherwise they could reach a showdown / win with no hand.
        var dealtPlayers = new ArrayList<PlayerSession>();
        for (var playerSession : activePlayers) {
            checkRoundInterrupted();
            if (userWebsocketService.isUserDisconnected(table, playerSession)) {
                log.debug("Deactivating disconnected player for round: {}", playerSession.getUser().getUsername());
                deactivatePlayerForRound(playerSession);
            } else {
                dealtPlayers.add(playerSession);
            }
        }

        for (var cardType : CardType.PLAYER_CARDS) {
            for (var playerSession : dealtPlayers) {
                checkRoundInterrupted();
                dealPlayerCard(cardType, playerSession);
            }
        }
    }

    private void deactivatePlayerForRound(PlayerSession playerSession) {
        writeTx.executeWithoutResult(status ->
                playerSessionRepository.findById(playerSession.getId()).ifPresent(session -> {
                    session.setActive(false);
                    playerSessionRepository.save(session);
                }));
    }

    private void dealPlayerCard(CardType cardType, PlayerSession playerSession) {
        var playerCard = writeTx.execute(status -> {
            var card = getCard();
            card.setCardType(cardType);
            handService.addPlayerCard(table, playerSession, card);
            return card;
        });
        dispatcher.send(table, messageFactory.initDeal(playerSession, playerCard));
        gameSpeedService.sleep(table, params.getDealWaitMs());
    }

    private void dealFlop() {
        for (var cardType : CardType.FLOP_CARDS) {
            checkRoundInterrupted();
            dealCommunityCard(cardType);
        }
    }

    private void dealCommunityCard(CardType cardType) {
        var communityCard = writeTx.execute(status -> {
            var card = getCard();
            card.setCardType(cardType);
            cardService.createCommunityCard(table, card);
            return card;
        });
        dispatcher.send(table, messageFactory.communityCardDeal(communityCard));
        gameSpeedService.sleep(table, params.getDealWaitMs());
    }

    @Override
    protected RoundState getNextRoundState(RoundState roundState) {
        return roundState.nextTexasState();
    }
}
