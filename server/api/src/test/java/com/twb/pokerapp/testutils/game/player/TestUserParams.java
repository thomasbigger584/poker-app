package com.twb.pokerapp.testutils.game.player;

import com.twb.pokerapp.dto.table.TableDTO;
import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.testutils.validator.Validator;
import lombok.Builder;
import lombok.Data;
import org.keycloak.admin.client.Keycloak;

@Data
@Builder
public class TestUserParams {
    private TableDTO table;
    private GameLatches latches;
    private Keycloak keycloak;
    private String username;
    private final String password = "password";
    private TurnHandler turnHandler;
    private Validator validator;
}
