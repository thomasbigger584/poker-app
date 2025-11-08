package com.twb.pokerapp.utils.game.player;

import com.twb.pokerapp.dto.pokertable.TableDTO;
import com.twb.pokerapp.utils.game.GameLatches;
import com.twb.pokerapp.utils.game.turn.TurnHandler;
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
}
