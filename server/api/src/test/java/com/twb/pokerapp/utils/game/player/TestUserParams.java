package com.twb.pokerapp.utils.game.player;

import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.utils.game.GameLatches;
import com.twb.pokerapp.utils.game.turn.TurnHandler;
import com.twb.pokerapp.utils.validator.Validator;
import lombok.Builder;
import lombok.Data;
import org.keycloak.admin.client.Keycloak;

@Data
@Builder
public class TestUserParams {
    private PokerTable table;
    private GameLatches latches;
    private Keycloak keycloak;
    private String username;
    private final String password = "password";
    private TurnHandler turnHandler;
    private Validator validator;
}
