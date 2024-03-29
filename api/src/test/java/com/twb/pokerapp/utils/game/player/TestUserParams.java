package com.twb.pokerapp.utils.game.player;

import com.twb.pokerapp.dto.pokertable.TableDTO;
import com.twb.pokerapp.utils.game.player.AbstractTestUser.CountdownLatches;
import com.twb.pokerapp.utils.game.player.AbstractTestUser.PlayerTurnHandler;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestUserParams {
    private TableDTO table;
    private CountdownLatches latches;
    private String username;
    private String password;
    private PlayerTurnHandler turnHandler;
}
