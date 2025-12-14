package com.twb.pokerapp.testutils.game;

import com.twb.pokerapp.dto.table.TableDTO;
import com.twb.pokerapp.testutils.keycloak.KeycloakClients;
import com.twb.pokerapp.testutils.validator.Validator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRunnerParams {
    private KeycloakClients keycloakClients;
    private int numberOfRounds;
    private GameLatches latches;
    private TableDTO table;
    private final Validator validator;
}


