package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.proto.AvailableTableListResponse;
import com.twb.pokerapp.proto.CreateTableDTO;
import com.twb.pokerapp.proto.GameType;
import com.twb.pokerapp.proto.TableDTO;
import com.twb.pokerapp.testutils.TestEnvironment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TableResourceIT {
    private static final String ENDPOINT = "/poker-table";
    private final static TestEnvironment env = new TestEnvironment();

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @BeforeAll
    static void beforeAll() {
        env.start();
    }

    @AfterEach
    void afterEach() {
        env.afterEach();
    }

    @AfterAll
    static void afterAll() {
        env.close();
    }

    // *****************************************************************************************
    // Test Methods
    // *****************************************************************************************

    @Test
    void testCreateAndFetchTable() throws Throwable {
        // given
        var createDto = CreateTableDTO.newBuilder()
                .setName(UUID.randomUUID().toString())
                .setGameType(GameType.GAME_TYPE_TEXAS_HOLDEM)
                .setMinPlayers(2)
                .setMaxPlayers(6)
                .setMinBuyin(BigDecimal.valueOf(100).toPlainString())
                .setMaxBuyin(BigDecimal.valueOf(10_000).toPlainString())
                .build();

        // when
        var adminRestClient = env.getAdminRestClient();
        var createResponse = adminRestClient.post(TableDTO.class, createDto, ENDPOINT);

        // then
        assertEquals(HttpStatus.CREATED.value(), createResponse.httpResponse().statusCode());

        var createdTableDto = createResponse.resultBody();
        assertFalse(createdTableDto.getId().isEmpty());
        assertEquals(createDto.getName(), createdTableDto.getName());
        assertEquals(createDto.getGameType(), createdTableDto.getGameType());
        assertEquals(createDto.getMinPlayers(), createdTableDto.getMinPlayers());
        assertEquals(createDto.getMaxPlayers(), createdTableDto.getMaxPlayers());
        assertEquals(0, new BigDecimal(createDto.getMinBuyin()).compareTo(new BigDecimal(createdTableDto.getMinBuyin())));
        assertEquals(0, new BigDecimal(createDto.getMaxBuyin()).compareTo(new BigDecimal(createdTableDto.getMaxBuyin())));

        var getResponse = adminRestClient.get(AvailableTableListResponse.class, ENDPOINT);
        assertEquals(HttpStatus.OK.value(), getResponse.httpResponse().statusCode());

        var tables = getResponse.resultBody().getTablesList();
        var createdTableFetchedOpt = tables.stream()
                .filter(availableTableDto -> availableTableDto.getTable().getId().equals(createdTableDto.getId())).findFirst();
        assertTrue(createdTableFetchedOpt.isPresent());

        var createdAvailableTableFetched = createdTableFetchedOpt.get();
        var createdTableFetched = createdAvailableTableFetched.getTable();

        assertEquals(createdTableDto.getId(), createdTableFetched.getId());
        assertEquals(createdTableDto.getName(), createdTableFetched.getName());
        assertEquals(createdTableDto.getGameType(), createdTableFetched.getGameType());
        assertEquals(createdTableDto.getMinPlayers(), createdTableFetched.getMinPlayers());
        assertEquals(createdTableDto.getMaxPlayers(), createdTableFetched.getMaxPlayers());
        assertEquals(0, new BigDecimal(createdTableDto.getMinBuyin()).compareTo(new BigDecimal(createdTableFetched.getMinBuyin())));
        assertEquals(0, new BigDecimal(createdTableDto.getMaxBuyin()).compareTo(new BigDecimal(createdTableFetched.getMaxBuyin())));
    }
}
