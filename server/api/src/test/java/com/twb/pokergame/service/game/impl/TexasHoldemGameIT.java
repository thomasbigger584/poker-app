package com.twb.pokergame.service.game.impl;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TexasHoldemGameIT extends BaseTestContainersIT {

    @Test
    public void testActuator() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/actuator"))
                .GET().build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("response = " + response.body());
    }
}