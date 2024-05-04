package com.twb.pokerapp.utils.game;

import com.twb.pokerapp.dto.pokertable.TableDTO;
import lombok.Builder;
import lombok.Data;
import org.keycloak.admin.client.Keycloak;

@Data
@Builder
public class GameRunnerParams {
    private Keycloak keycloak;
    private int numberOfRounds;
    private GameLatches latches;
    private TableDTO table;
}


