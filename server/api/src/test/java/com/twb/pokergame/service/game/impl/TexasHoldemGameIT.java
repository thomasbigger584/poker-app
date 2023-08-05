package com.twb.pokergame.service.game.impl;

import com.twb.pokergame.dto.pokertable.TableDTO;
import com.twb.pokergame.testcontainers.BaseTestContainersIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TexasHoldemGameIT extends BaseTestContainersIT {

    @Test
    public void testGetPokerTables() throws Exception {
        ApiHttpResponse<TableDTO[]> tableResponse = get(TableDTO[].class, "/poker-table");
        assertEquals(HttpStatus.OK.value(), tableResponse.getHttpResponse().statusCode());
    }
}