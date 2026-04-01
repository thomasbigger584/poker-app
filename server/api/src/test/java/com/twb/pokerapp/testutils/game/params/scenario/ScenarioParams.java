package com.twb.pokerapp.testutils.game.params.scenario;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScenarioParams {
    private boolean useFixedScenario;
    private String scenario;
    private List<ScenarioPlayer> scenarioPlayers;
    private String communityCards;
}
