package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.dto.table.AvailableTableDTO;
import com.twb.pokerapp.dto.table.CreateTableDTO;
import com.twb.pokerapp.dto.table.TableDTO;
import com.twb.pokerapp.testutils.testcontainers.BaseTestContainersIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TableResourceIT extends BaseTestContainersIT {
    private static final String ENDPOINT = "/poker-table";

    @Test
    void testCreateAndFetchTable() throws Throwable {
        // given
        var createDto = new CreateTableDTO();
        createDto.setName(UUID.randomUUID().toString());
        createDto.setGameType(GameType.TEXAS_HOLDEM);
        createDto.setMinPlayers(2);
        createDto.setMaxPlayers(6);
        createDto.setMinBuyin(100d);
        createDto.setMaxBuyin(10_000d);

        // when
        var createResponse = adminRestClient.post(TableDTO.class, createDto, ENDPOINT);

        // then
        assertEquals(HttpStatus.CREATED.value(), createResponse.httpResponse().statusCode());

        var createdTableDto = createResponse.resultBody();
        assertNotNull(createdTableDto.getId());
        assertEquals(createDto.getName(), createdTableDto.getName());
        assertEquals(createDto.getGameType(), createdTableDto.getGameType());
        assertEquals(createDto.getMinPlayers(), createdTableDto.getMinPlayers());
        assertEquals(createDto.getMaxPlayers(), createdTableDto.getMaxPlayers());
        assertEquals(createDto.getMinBuyin(), createdTableDto.getMinBuyin());
        assertEquals(createDto.getMaxBuyin(), createdTableDto.getMaxBuyin());

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
        assertEquals(createdTableDto.getMinBuyin(), createdTableFetched.getMinBuyin());
        assertEquals(createdTableDto.getMaxBuyin(), createdTableFetched.getMaxBuyin());
    }
}