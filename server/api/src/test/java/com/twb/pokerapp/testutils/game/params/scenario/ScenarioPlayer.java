package com.twb.pokerapp.testutils.game.params.scenario;

import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.FixedScenarioTurnHandler;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScenarioPlayer {
    private String username;
    private String handCards;
    private Double buyIn;
    private TurnHandler turnHandler;
    private Double winAmount;

    public static ScenarioPlayer create(String username,
                                        String hand,
                                        double startAmount,
                                        String preFlopActions,
                                        String flopActions,
                                        String turnActions,
                                        String riverActions,
                                        double winAmount
    ) {
        var turnHandler = new FixedScenarioTurnHandler(
                username, preFlopActions, flopActions, turnActions, riverActions);
        return ScenarioPlayer.builder()
                .username(username)
                .handCards(hand)
                .buyIn(startAmount)
                .turnHandler(turnHandler)
                .winAmount(winAmount)
                .build();
    }
}
