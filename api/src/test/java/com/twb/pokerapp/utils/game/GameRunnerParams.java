package com.twb.pokerapp.utils.game;

import com.twb.pokerapp.dto.pokertable.TableDTO;
import com.twb.pokerapp.utils.keycloak.KeycloakClients;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRunnerParams {
    private KeycloakClients keycloakClients;
    private int numberOfRounds;
    private GameLatches latches;
    private TableDTO table;
}


