package com.twb.pokerapp.testutils.game.params.scenario;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;

import static com.twb.pokerapp.testutils.TestScenario.DEFAULT_BUY_IN_AMOUNT;

@Data
@Builder
public class ScenarioParams {
    private boolean useFixedScenario;
    private String scenario;
    private double speedMultiplier;
    private int totalRounds;
    private List<ScenarioPlayer> scenarioPlayers;
    private String communityCards;

    public double getMinBuyIn() {
        if (scenarioPlayers == null) {
            return DEFAULT_BUY_IN_AMOUNT;
        }
        var minBuyInOpt = scenarioPlayers.stream()
                .mapToDouble(value ->
                        Optional.ofNullable(value.getBuyIn()).orElse(DEFAULT_BUY_IN_AMOUNT))
                .min();
        if (minBuyInOpt.isPresent()) {
            return minBuyInOpt.getAsDouble();
        }
        return DEFAULT_BUY_IN_AMOUNT;
    }
}
