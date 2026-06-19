package com.twb.pokerapp.testutils.validator.impl;

import com.twb.pokerapp.mapper.enumeration.CardGroups;
import com.twb.pokerapp.proto.CardType;
import com.twb.pokerapp.proto.ConnectionType;
import com.twb.pokerapp.proto.ServerMessageDTO;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioParams;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioPlayer;
import com.twb.pokerapp.testutils.http.message.PlayersServerMessages;
import com.twb.pokerapp.testutils.sql.SqlClient;
import com.twb.pokerapp.testutils.validator.Validator;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class TexasValidator extends Validator {

    public TexasValidator(ScenarioParams params, SqlClient sqlClient) {
        super(params, sqlClient);
    }

    @Override
    protected void onValidateHandleMessage(ServerMessageDTO message) {
        // todo specific intra-game message assertions
        if (message.getPayloadCase() == ServerMessageDTO.PayloadCase.ROUND_FINISHED) {
            var payload = message.getRoundFinished();
            if (params.isUseFixedScenario()) {
                var winningScenarioPlayers = params.getScenarioPlayers().stream()
                        .filter(scenarioPlayer -> scenarioPlayer.getWinAmount() != null
                                && scenarioPlayer.getWinAmount().compareTo(BigDecimal.ZERO) != 0)
                        .sorted(Comparator.comparing(ScenarioPlayer::getUsername))
                        .toList();
                var roundWinners = payload.getWinnersList().stream()
                        .sorted(Comparator.comparing(roundWinner ->
                                roundWinner.getPlayerSession().getUser().getUsername()))
                        .toList();
                assertEquals(winningScenarioPlayers.size(), roundWinners.size());
                for (var index = 0; index < winningScenarioPlayers.size(); index++) {
                    var scenarioPlayer = winningScenarioPlayers.get(index);
                    var roundWinner = roundWinners.get(index);
                    assertEquals(scenarioPlayer.getUsername(), roundWinner.getPlayerSession().getUser().getUsername());
                    assertEquals(0, scenarioPlayer.getWinAmount().compareTo(new BigDecimal(roundWinner.getAmount())));
                }
            } else {
                log.warn("Not a fixed scenario so cannot assert on ROUND_FINISHED");
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
        var messages = get(listenerMessages, ServerMessageDTO.PayloadCase.DEALER_DETERMINED);
        assertEquals(1, messages.size());
        messages.forEach(message -> {
            var payload = message.getDealerDetermined();

            var playerSessionDto = payload.getPlayerSession();
            assertTrue(playerSessionDto.getDealer());
            assertEquals(ConnectionType.CONNECTION_TYPE_PLAYER, playerSessionDto.getConnectionType());
            assertPlayerSession(playerSessionDto);
        });
    }

    private void assertDealInit(List<ServerMessageDTO> listenerMessages) {
        var messages = get(listenerMessages, ServerMessageDTO.PayloadCase.DEAL_INIT);
        var scenarioPlayersSize = params.getScenarioPlayers().size();
        var noCardsDealt = scenarioPlayersSize * 2;

        assertEquals(noCardsDealt, messages.size());

        for (var index = 1; index <= noCardsDealt; index++) {
            var message = messages.get(index - 1);
            var payload = message.getDealInit();

            assertPlayerSession(payload.getPlayerSession());

            var cardDto = payload.getCard();
            var cardType = index <= (noCardsDealt / 2) ? CardType.CARD_TYPE_PLAYER_CARD_1 : CardType.CARD_TYPE_PLAYER_CARD_2;
            assertCard(cardDto, cardType);
        }
    }

    private void assertDealCommunity(List<ServerMessageDTO> listenerMessages) {
        var messages = get(listenerMessages, ServerMessageDTO.PayloadCase.DEAL_COMMUNITY);

        var expectedCommunityCards = new ArrayList<>(Arrays.stream(CardGroups.FLOP_CARDS).toList());
        expectedCommunityCards.add(CardType.CARD_TYPE_TURN_CARD);
        expectedCommunityCards.add(CardType.CARD_TYPE_RIVER_CARD);

        var noCardsDealt = expectedCommunityCards.size();

        if (params.isUseFixedScenario()) {
            var communityCards = params.getCommunityCards();
            if (communityCards == null) {
                return;
            }
            var communityCardsSplit = communityCards.split(";");
            assertEquals(communityCardsSplit.length, messages.size());

            for (var index = 0; index < communityCardsSplit.length; index++) {
                var communityCardStr = communityCardsSplit[index];
                var message = messages.get(index);

                var payload = message.getDealCommunity();
                var cardType = expectedCommunityCards.get(index);
                var card = payload.getCard();
                assertCard(payload.getCard(), cardType);

                assertEquals(card.getRankChar(), String.valueOf(communityCardStr.charAt(0)));
                assertEquals(card.getSuitChar(), String.valueOf(communityCardStr.charAt(1)));
            }

        } else {
            assertEquals(noCardsDealt, messages.size());

            for (var index = 0; index < noCardsDealt; index++) {
                var message = messages.get(index);
                var cardType = expectedCommunityCards.get(index);

                var payload = message.getDealCommunity();
                assertCard(payload.getCard(), cardType);
            }
        }
    }
}
