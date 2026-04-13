package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.dto.table.AvailableTableDTO;
import com.twb.pokerapp.dto.table.CreateTableDTO;
import com.twb.pokerapp.dto.table.TableDTO;
import com.twb.pokerapp.testutils.TestEnvironment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
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
        var createDto = new CreateTableDTO();
        createDto.setName(UUID.randomUUID().toString());
        createDto.setGameType(GameType.TEXAS_HOLDEM);
        createDto.setMinPlayers(2);
        createDto.setMaxPlayers(6);
        createDto.setMinBuyin(BigDecimal.valueOf(100));
        createDto.setMaxBuyin(BigDecimal.valueOf(10_000));

        // when
        var adminRestClient = env.getAdminRestClient();
        var createResponse = adminRestClient.post(TableDTO.class, createDto, ENDPOINT);

        // then
        assertEquals(HttpStatus.CREATED.value(), createResponse.httpResponse().statusCode());

        var createdTableDto = createResponse.resultBody();
        assertNotNull(createdTableDto.getId());
        assertEquals(createDto.getName(), createdTableDto.getName());
        assertEquals(createDto.getGameType(), createdTableDto.getGameType());
        assertEquals(createDto.getMinPlayers(), createdTableDto.getMinPlayers());
        assertEquals(createDto.getMaxPlayers(), createdTableDto.getMaxPlayers());
        assertEquals(0, createDto.getMinBuyin().compareTo(createdTableDto.getMinBuyin()));
    assertEquals(0, createDto.getMaxBuyin().compareTo(createdTableDto.getMaxBuyin()));

        var getResponse = adminRestClient.get(AvailableTableDTO[].class, ENDPOINT);
        assertEquals(HttpStatus.OK.value(), getResponse.httpResponse().statusCode());

        var tables = getResponse.resultBody();
        var createdTableFetchedOpt = Arrays.stream(tables)
                .filter(availableTableDto -> availableTableDto.getTable().getId().equals(createdTableDto.getId())).findFirst();
        assertTrue(createdTableFetchedOpt.isPresent());

        var createdAvailableTableFetched = createdTableFetchedOpt.get();
        var createdTableFetched = createdAvailableTableFetched.getTable();

        assertEquals(createdTableDto.getId(), createdTableFetched.getId());
        assertEquals(createdTableDto.getName(), createdTableFetched.getName());
        assertEquals(createdTableDto.getGameType(), createdTableFetched.getGameType());
        assertEquals(createdTableDto.getMinPlayers(), createdTableFetched.getMinPlayers());
        assertEquals(createdTableDto.getMaxPlayers(), createdTableFetched.getMaxPlayers());
        assertEquals(0, createdTableDto.getMinBuyin().compareTo(createdTableFetched.getMinBuyin()));
    assertEquals(0, createdTableDto.getMaxBuyin().compareTo(createdTableFetched.getMaxBuyin()));
    }
}