package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.proto.BettingRoundType;
import com.twb.pokerapp.proto.ActionType;
import com.twb.pokerapp.proto.CreatePlayerActionDTO;
import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.EnumMap;
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
        parseRound(username, preFlopActionsStr, BettingRoundType.BETTING_ROUND_TYPE_DEAL);
        parseRound(username, flopActionsStr, BettingRoundType.BETTING_ROUND_TYPE_FLOP);
        parseRound(username, turnActionsStr, BettingRoundType.BETTING_ROUND_TYPE_TURN);
        parseRound(username, riverActionsStr, BettingRoundType.BETTING_ROUND_TYPE_RIVER);
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

            var queue = this.bettingRoundActions
                    .computeIfAbsent(bettingRoundType, k -> new ArrayDeque<>());

            var builder = CreatePlayerActionDTO.newBuilder()
                    .setAction(ActionType.valueOf("ACTION_TYPE_" + parts[1].trim()));

            if (parts.length > 2) {
                builder.setAmount(new BigDecimal(parts[2].trim()).toPlainString());
            }
            queue.add(builder.build());
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
        log.debug("Handling action for user {}: {} - {}", user.getUsername(), bettingRoundType, action);
        if (action != null) {
            user.sendPlayerAction(action);
        }
    }
}
