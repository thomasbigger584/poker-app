package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.dto.table.CreateTableDTO;
import com.twb.pokerapp.dto.table.TableDTO;
import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.game.GameRunner;
import com.twb.pokerapp.testutils.game.GameRunnerParams;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.FirstActionTurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.IdepodentTurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.InvalidActionTurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.OptimisticTurnHandler;
import com.twb.pokerapp.testutils.testcontainers.BaseTestContainersIT;
import com.twb.pokerapp.testutils.validator.impl.TexasValidator;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.validation.ValidationDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TexasGameIT extends BaseTestContainersIT {

    @Override
    protected void beforeEach() throws Exception {
        var params = GameRunnerParams.builder()
                .keycloakClients(keycloakClients)
                .numberOfRounds(1)
                .latches(GameLatches.create())
                .table(createTable())
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
    void testGameWithSingleOptimisticActions() throws Throwable {

        // given
        var turnHandlers = TurnHandler.of(
                new OptimisticTurnHandler(),
                new FirstActionTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithSingleFirstActionActions() throws Throwable {

        // given
        var turnHandlers = TurnHandler.of(
                new FirstActionTurnHandler(),
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

    @Test
    void testGameWithIdepodentActions() throws Throwable {

        // given
        var turnHandlers = TurnHandler.of(
                new OptimisticTurnHandler(),
                new IdepodentTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        var logMessages = validator.get(2, messages, ServerMessageType.LOG);
        assertFalse(logMessages.isEmpty(), "Expected log messages");

        assertTrue(logMessages.stream().anyMatch(logDto -> {
            var payload = (LogMessageDTO) logDto.getPayload();
            return payload.getMessage().contains("Player already made action in this round recently");
        }));

        validator.validateEndOfRun(messages);
    }


    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private TableDTO createTable() throws Exception {
        var createDto = new CreateTableDTO();
        createDto.setName(UUID.randomUUID().toString());
        createDto.setGameType(GameType.TEXAS_HOLDEM);
        createDto.setMinPlayers(2);
        createDto.setMaxPlayers(6);
        createDto.setMinBuyin(100d);
        createDto.setMaxBuyin(10_000d);

        var createResponse = adminRestClient.post(TableDTO.class, createDto, "/poker-table");
        assertEquals(HttpStatus.CREATED.value(), createResponse.httpResponse().statusCode());
        return createResponse.resultBody();
    }
}
