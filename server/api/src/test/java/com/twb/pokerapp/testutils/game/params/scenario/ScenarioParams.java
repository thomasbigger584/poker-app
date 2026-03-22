package com.twb.pokerapp.testutils.game.params.scenario;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScenarioParams {
    private boolean useFixedScenario;
    private List<ScenarioPlayer> scenarioPlayers;
    private List<String> communityCards;
}
