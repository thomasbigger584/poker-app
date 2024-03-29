package com.twb.pokerapp.utils.game.player;

import com.twb.pokerapp.utils.game.player.AbstractTestUser.CountdownLatches;
import com.twb.pokerapp.utils.game.player.AbstractTestUser.PlayerTurnHandler;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TestUserParams {
    private UUID tableId;
    private CountdownLatches latches;
    private String username;
    private String password;
    private PlayerTurnHandler turnHandler;
}
