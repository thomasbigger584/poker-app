package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.BettingRoundType;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Slf4j
public class FixedScenarioTurnHandler implements TurnHandler {
    private final Map<BettingRoundType, Queue<CreatePlayerActionDTO>> bettingRoundActions;

    public FixedScenarioTurnHandler(String username,
                                    String preFlopActionsStr,
                                    String flopActionsStr,
                                    String turnActionsStr,
                                    String riverActionsStr
    ) {
        this.bettingRoundActions = new EnumMap<>(BettingRoundType.class);
        parseRound(username, preFlopActionsStr, BettingRoundType.DEAL);
        parseRound(username, flopActionsStr, BettingRoundType.FLOP);
        parseRound(username, turnActionsStr, BettingRoundType.TURN);
        parseRound(username, riverActionsStr, BettingRoundType.RIVER);
    }

    private void parseRound(String username, String playerActionStr, BettingRoundType bettingRoundType) {
        if (playerActionStr == null || playerActionStr.isBlank() || "None".equalsIgnoreCase(playerActionStr)) {
            return;
        }
        var actions = playerActionStr.split(";");
        for (var actionStr : actions) {
            var parts = actionStr.split(":");
            var actionUsername = parts[0].trim();
            if (!actionUsername.equalsIgnoreCase(username)) {
                continue;
            }
            var actionStrVal = parts[1].trim();

            var bettingRoundActions = this.bettingRoundActions
                    .computeIfAbsent(bettingRoundType, k -> new LinkedList<>());

            var action = new CreatePlayerActionDTO();
            var actionType = ActionType.valueOf(actionStrVal);
            action.setAction(actionType);

            if (parts.length > 2) {
                var amount = Double.parseDouble(parts[2].trim());
                action.setAmount(amount);
            }
            bettingRoundActions.add(action);
        }
    }

    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        var bettingRoundType = playerTurn.getBettingRound().getType();
        var actions = bettingRoundActions.get(bettingRoundType);
        if (actions == null || actions.isEmpty()) {
            throw new IllegalStateException("No action defined for " + bettingRoundType + " for user " + user.getUsername());
        }
        var action = actions.poll();
        log.info("Handling action for user {}: {} - {}", user.getUsername(), bettingRoundType, action);
        if (action != null) {
            user.sendPlayerAction(action);
        }
    }
}
