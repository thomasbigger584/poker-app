package com.twb.pokerapp.testutils.validator.impl;

import com.google.common.base.Splitter;
import com.twb.pokerapp.domain.enumeration.CardType;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioParams;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioPlayer;
import com.twb.pokerapp.testutils.http.message.PlayersServerMessages;
import com.twb.pokerapp.testutils.sql.SqlClient;
import com.twb.pokerapp.testutils.validator.Validator;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.DealCommunityCardDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.DealPlayerCardDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.DealerDeterminedDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.RoundFinishedDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class TexasValidator extends Validator {

    public TexasValidator(ScenarioParams params, SqlClient sqlClient) {
        super(params, sqlClient);
    }

    @Override
    protected void onValidateHandleMessage(ServerMessageDTO message) {
        switch (message.getType()) {
            // todo specific intra-game message assertions

            case ROUND_FINISHED -> {
                var payload = (RoundFinishedDTO) message.getPayload();
                if (params.isUseFixedScenario()) {
                    var winningScenarioPlayers = params.getScenarioPlayers().stream()
                            .filter(scenarioPlayer -> scenarioPlayer.getWinAmount() != null
                                    && scenarioPlayer.getWinAmount() != 0d)
                            .sorted(Comparator.comparing(ScenarioPlayer::getUsername))
                            .toList();
                    var roundWinners = payload.getWinners().stream()
                            .sorted(Comparator.comparing(roundWinner ->
                                    roundWinner.getPlayerSession().getUser().getUsername()))
                            .toList();
                    assertEquals(winningScenarioPlayers.size(), roundWinners.size());
                    for (var index = 0; index < winningScenarioPlayers.size(); index++) {
                        var scenarioPlayer = winningScenarioPlayers.get(index);
                        var roundWinner = roundWinners.get(index);
                        assertEquals(scenarioPlayer.getUsername(), roundWinner.getPlayerSession().getUser().getUsername());
                        assertEquals(scenarioPlayer.getWinAmount(), roundWinner.getAmount());
                    }
                } else {
                    log.warn("Not a fixed scenario so cannot assert on ROUND_FINISHED");
                }
            }
        }
    }

    @Override
    protected void onValidateEndOfRun(PlayersServerMessages messages) {
        var listenerMessages = messages.getListenerMessages();
        assertDealerDetermined(listenerMessages);
        assertDealInit(listenerMessages);
        assertDealCommunity(listenerMessages);
    }

    private void assertDealerDetermined(List<ServerMessageDTO> listenerMessages) {
        var messages = get(listenerMessages, ServerMessageType.DEALER_DETERMINED);
        assertEquals(1, messages.size());
        messages.forEach(message -> {
            var payload = (DealerDeterminedDTO) message.getPayload();

            var playerSessionDto = payload.getPlayerSession();
            assertTrue(playerSessionDto.getDealer());
            assertEquals(ConnectionType.PLAYER, playerSessionDto.getConnectionType());
            assertPlayerSession(playerSessionDto);
        });
    }

    private void assertDealInit(List<ServerMessageDTO> listenerMessages) {
        var messages = get(listenerMessages, ServerMessageType.DEAL_INIT);
        var scenarioPlayersSize = params.getScenarioPlayers().size();
        var noCardsDealt = scenarioPlayersSize * 2;

        assertEquals(noCardsDealt, messages.size());

        for (var index = 1; index <= noCardsDealt; index++) {
            var message = messages.get(index - 1);
            var payload = (DealPlayerCardDTO) message.getPayload();

            assertPlayerSession(payload.getPlayerSession());

            var cardDto = payload.getCard();
            var cardType = index <= (noCardsDealt / 2) ? CardType.PLAYER_CARD_1 : CardType.PLAYER_CARD_2;
            assertCard(cardDto, cardType);
        }
    }

    private void assertDealCommunity(List<ServerMessageDTO> listenerMessages) {
        var messages = get(listenerMessages, ServerMessageType.DEAL_COMMUNITY);

        var expectedCommunityCards = new ArrayList<>(Arrays.stream(CardType.FLOP_CARDS).toList());
        expectedCommunityCards.add(CardType.TURN_CARD);
        expectedCommunityCards.add(CardType.RIVER_CARD);

        var noCardsDealt = expectedCommunityCards.size();

        if (params.isUseFixedScenario()) {
            var communityCards = params.getCommunityCards();
            if (communityCards == null) {
                return;
            }
            var communityCardsSplit = communityCards.split(";");
            assertEquals(communityCardsSplit.length, messages.size());

            for (int index = 0; index < communityCardsSplit.length; index++) {
                var communityCardStr = communityCardsSplit[index];
                var message = messages.get(index);
                assertInstanceOf(DealCommunityCardDTO.class, message.getPayload());

                var payload = (DealCommunityCardDTO) message.getPayload();
                var cardType = expectedCommunityCards.get(index);
                var card = payload.getCard();
                assertCard(payload.getCard(), cardType);

                assertEquals(card.getRankChar(), communityCardStr.charAt(0));
                assertEquals(card.getSuitChar(), communityCardStr.charAt(1));
            }

        } else {
            assertEquals(noCardsDealt, messages.size());

            for (var index = 0; index < noCardsDealt; index++) {
                var message = messages.get(index);
                var cardType = expectedCommunityCards.get(index);

                var payload = (DealCommunityCardDTO) message.getPayload();
                assertCard(payload.getCard(), cardType);
            }
        }
    }
}