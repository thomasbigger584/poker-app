package com.twb.pokerapp.testutils.validator.impl;

import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.domain.enumeration.CardType;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.dto.round.RoundDTO;
import com.twb.pokerapp.testutils.game.GameRunnerParams;
import com.twb.pokerapp.testutils.http.message.PlayersServerMessages;
import com.twb.pokerapp.testutils.sql.SqlClient;
import com.twb.pokerapp.testutils.validator.Validator;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TexasValidator extends Validator {

    public TexasValidator(GameRunnerParams params, SqlClient sqlClient) {
        super(params, sqlClient);
    }

    @Override
    protected void onValidateHandleMessage(ServerMessageDTO message) {
        // todo specific intra-game message assertions
    }

    @Override
    protected void onValidateEndOfRun(PlayersServerMessages messages) {
        var listenerMessages = messages.getListenerMessages();
        assertDealerDetermined(listenerMessages);
        assertDealInit(listenerMessages);
        assertDealCommunity(listenerMessages);
        assertBettingRoundWithPlayerActions(listenerMessages);
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
        var noCardsDealt = 2 * 2;

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
        assertEquals(noCardsDealt, messages.size());

        for (var index = 0; index < noCardsDealt; index++) {
            var message = messages.get(index);
            var cardType = expectedCommunityCards.get(index);

            var payload = (DealCommunityCardDTO) message.getPayload();
            assertCard(payload.getCard(), cardType);
        }
    }

    private void assertBettingRoundWithPlayerActions(List<ServerMessageDTO> listenerMessages) {
        var playerActionsByBettingRound = listenerMessages.stream()
                .filter(serverMessageDTO -> serverMessageDTO.getType() == ServerMessageType.PLAYER_ACTIONED)
                .map(serverMessageDTO -> (PlayerActionedDTO) serverMessageDTO.getPayload())
                .collect(Collectors.groupingBy(this::playerActionBettingRoundGroupByKey));

        var bettingRoundsUpdatedById = listenerMessages.stream()
                .filter(serverMessageDTO -> serverMessageDTO.getType() == ServerMessageType.BETTING_ROUND_UPDATED)
                .map(serverMessageDTO -> (BettingRoundUpdatedDTO) serverMessageDTO.getPayload())
                .collect(Collectors.groupingBy(this::bettingRoundUpdatedGroupByKey));

        for (var playerActionsEntry : playerActionsByBettingRound.entrySet()) {
            var bettingRoundId = playerActionsEntry.getKey();
            var playerActionedList = playerActionsEntry.getValue();
            var bettingRoundpdatedList = bettingRoundsUpdatedById.get(bettingRoundId);
            assertEquals(playerActionedList.size() + 1, bettingRoundpdatedList.size());

            for (var index = 0; index < playerActionedList.size(); index++) {
                var playerActionedDto = playerActionedList.get(index);
                var playerActionDto = playerActionedDto.getAction();
                assertPlayerAction(playerActionDto);

                var bettingRoundUpdatedDto = bettingRoundpdatedList.get(index);
                var bettingRoundDto = bettingRoundUpdatedDto.getBettingRound();
                assertEquals(BettingRoundState.IN_PROGRESS, bettingRoundDto.getState());
                var bettingRound = assertBettingRound(bettingRoundDto);

                var roundDto = bettingRoundUpdatedDto.getRound();
                var round = assertRound(roundDto);

            }

            var finishedBettingRoundUpdated = bettingRoundpdatedList.getLast();
            var finishedBettingRoundDto = finishedBettingRoundUpdated.getBettingRound();
            assertEquals(BettingRoundState.FINISHED, finishedBettingRoundDto.getState());
            var finishedBettingRound = assertBettingRound(finishedBettingRoundDto);

            var finishedRoundDto = finishedBettingRoundUpdated.getRound();
            var finishedRound = assertRound(finishedRoundDto);

        }

        System.out.println("listenerMessages = " + listenerMessages);
    }

    private UUID playerActionBettingRoundGroupByKey(PlayerActionedDTO playerActionedDto) {
        return playerActionedDto.getAction().getBettingRound().getId();
    }

    private UUID bettingRoundUpdatedGroupByKey(BettingRoundUpdatedDTO bettingRoundUpdatedDto) {
        return bettingRoundUpdatedDto.getBettingRound().getId();
    }
}
