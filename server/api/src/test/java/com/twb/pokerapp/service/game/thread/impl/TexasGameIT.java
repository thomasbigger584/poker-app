package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.game.GameRunner;
import com.twb.pokerapp.testutils.game.GameRunnerParams;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.FirstActionTurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.InvalidActionTurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.OptimisticTurnHandler;
import com.twb.pokerapp.testutils.testcontainers.BaseTestContainersIT;
import com.twb.pokerapp.testutils.validator.impl.TexasValidator;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.validation.ValidationDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class TexasGameIT extends BaseTestContainersIT {

    @Override
    protected void beforeEach() {
        var params = GameRunnerParams.builder()
                .keycloakClients(keycloakClients)
                .numberOfRounds(1)
                .latches(GameLatches.create())
                .table(getTexasHoldemTable())
                .validator(validator)
                .build();
        this.validator = new TexasValidator(params, sqlClient);
        this.runner = new GameRunner(params);
    }

    @Test
    void testGameWithoutPlayerActions() throws Throwable {

        // given
        var turnHandlers = TurnHandler.of(null, null);

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithDefaultActions() throws Throwable {

        // given
        var turnHandlers = TurnHandler.of(
                new FirstActionTurnHandler(),
                new FirstActionTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithOptimisticActions() throws Throwable {

        // given
        var turnHandlers = TurnHandler.of(
                new OptimisticTurnHandler(),
                new OptimisticTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithInvalidActions() throws Throwable {

        // given
        var turnHandlers = TurnHandler.of(
                new OptimisticTurnHandler(),
                new InvalidActionTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        var validationMessages = validator.get(2, messages, ServerMessageType.VALIDATION);
        assertEquals(1, validationMessages.size(), "Expected 1 validation message but got " + validationMessages.size());
        var validationMessage = validationMessages.getFirst();
        var validationDto = (ValidationDTO) validationMessage.getPayload();
        var fields = validationDto.getFields();

        var fieldsExpected = List.of("action", "amount");
        assertEquals(fieldsExpected.size(), fields.size(), "Expected " + fieldsExpected.size() + " fields but got " + fields.size());

        assertTrue(fields.stream()
                        .anyMatch(validationFieldDTO -> fieldsExpected.contains(validationFieldDTO.getField())),
                "Expected fields " + Arrays.toString(fieldsExpected.toArray()) + " but got " + fields);

        validator.validateEndOfRun(messages);
    }


    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private PokerTable getTexasHoldemTable() {
        return sqlClient.getPokerTables()
                .stream()
                .filter(pokerTable -> pokerTable.getGameType() == GameType.TEXAS_HOLDEM)
                .findFirst()
                .orElseThrow();
    }
}
