package com.twb.pokerapp.testutils.game;

import com.twb.pokerapp.dto.table.TableDTO;
import com.twb.pokerapp.testutils.keycloak.KeycloakClients;
import com.twb.pokerapp.testutils.validator.Validator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRunnerParams {
    private final KeycloakClients keycloakClients;
    private final int numberOfRounds;
    private final GameLatches latches;
    private final TableDTO table;
    private final Validator validator;
    private final double buyinAmount;
}


