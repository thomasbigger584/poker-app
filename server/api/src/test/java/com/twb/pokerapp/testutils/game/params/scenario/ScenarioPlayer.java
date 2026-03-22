package com.twb.pokerapp.testutils.game.params.scenario;

import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScenarioPlayer {
    private String username;
    private String handCards;
    private Double buyIn;
    private Double winAmount;
    private TurnHandler turnHandler;
}
