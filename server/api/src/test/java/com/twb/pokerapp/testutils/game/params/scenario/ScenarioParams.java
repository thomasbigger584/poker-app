package com.twb.pokerapp.testutils.game.params.scenario;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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

    public BigDecimal getMinBuyIn() {
        if (scenarioPlayers == null) {
            return DEFAULT_BUY_IN_AMOUNT;
        }
        return scenarioPlayers.stream()
                .map(ScenarioPlayer::getBuyIn)
                .filter(java.util.Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(DEFAULT_BUY_IN_AMOUNT);
    }
}
