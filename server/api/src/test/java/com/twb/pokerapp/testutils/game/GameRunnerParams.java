package com.twb.pokerapp.testutils.game;

import com.twb.pokerapp.domain.PokerTable;
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
    private PokerTable table;
    private final Validator validator;
}


