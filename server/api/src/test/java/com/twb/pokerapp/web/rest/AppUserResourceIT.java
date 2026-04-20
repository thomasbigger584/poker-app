package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.dto.appuser.AppUserDTO;
import com.twb.pokerapp.testutils.TestEnvironment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static com.twb.pokerapp.configuration.Constants.INITIAL_USER_FUNDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppUserResourceIT {
    private static final String ENDPOINT = "/app-user";
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
    void testGetCurrentUser() throws Throwable {
        // given
        var username = "user1";
        var userRestClient = env.getUserRestClient(username);

        // when
        var response = userRestClient.get(AppUserDTO.class, ENDPOINT + "/current");

        // then
        assertEquals(HttpStatus.OK.value(), response.httpResponse().statusCode());
        AppUserDTO appUserDTO = response.resultBody();
        assertNotNull(appUserDTO);
        assertNotNull(appUserDTO.getId());
        assertNotNull(appUserDTO.getUsername());
        assertEquals(username, appUserDTO.getUsername());
    }

    @Test
    void testResetFunds() throws Throwable {
        // given
        var username = "user1";
        var currentFunds = BigDecimal.valueOf(1000);
        env.getSqlClient().updateUsersTotalFunds(username, currentFunds);

        var userRestClient = env.getUserRestClient(username);

        // when
        var response = userRestClient.post(AppUserDTO.class, null, ENDPOINT + "/reset-funds");

        // then
        assertEquals(HttpStatus.OK.value(), response.httpResponse().statusCode());
        AppUserDTO appUserDTO = response.resultBody();
        assertNotNull(appUserDTO);
        assertEquals(0, INITIAL_USER_FUNDS.compareTo(appUserDTO.getTotalFunds()));

        // Verify by fetching again
        var getResponse = userRestClient.get(AppUserDTO.class, ENDPOINT + "/current");
        assertEquals(HttpStatus.OK.value(), getResponse.httpResponse().statusCode());
        AppUserDTO fetchedAppUserDTO = getResponse.resultBody();
        assertNotNull(fetchedAppUserDTO);
        assertEquals(0, INITIAL_USER_FUNDS.compareTo(fetchedAppUserDTO.getTotalFunds()));
    }
}
