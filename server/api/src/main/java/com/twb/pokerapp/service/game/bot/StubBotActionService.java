package com.twb.pokerapp.service.game.bot;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.service.game.thread.impl.texas.dto.NextActionsDTO;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Placeholder bot decision logic until an LLM-backed implementation lands.
 * <p>
 * Plays a passive "calling station" style: it prefers to {@link ActionType#CHECK}
 * when nothing is owed, otherwise {@link ActionType#CALL}, and only
 * {@link ActionType#FOLD}s when it can neither check nor call (e.g. it cannot
 * cover the bet and the only options are fold or all-in). It never bets or raises.
 * This keeps every betting round terminating cleanly and lets hands always reach
 * showdown, which is exactly what we want for validating the bot game loop.
 */
@Slf4j
@Component
public class StubBotActionService implements BotActionService {

    @Override
    public CreatePlayerActionDTO decideAction(PlayerSession botSession, BettingRound bettingRound, NextActionsDTO nextActions) {
        var availableActions = nextActions.nextActions();

        var createDto = new CreatePlayerActionDTO();
        if (contains(availableActions, ActionType.CHECK)) {
            createDto.setAction(ActionType.CHECK);
        } else if (contains(availableActions, ActionType.CALL)) {
            createDto.setAction(ActionType.CALL);
            createDto.setAmount(nextActions.amountToCall());
        } else {
            createDto.setAction(ActionType.FOLD);
        }

        log.debug("Bot {} decided action {} (available: {})",
                botSession.getUser().getUsername(), createDto.getAction(), Arrays.toString(availableActions));
        return createDto;
    }

    private boolean contains(ActionType[] actions, ActionType actionType) {
        return Arrays.asList(actions).contains(actionType);
    }
}
