package com.twb.pokerapp.service.game.bot;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.service.game.thread.impl.texas.dto.NextActionsDTO;
import com.twb.pokerapp.service.game.thread.dto.PlayerActionCommand;

/**
 * Decides which action a bot player should take on its turn, in place of waiting
 * for a human to submit one over the websocket.
 * <p>
 * The current implementation is a deterministic stub. The intent is to later swap
 * in an LLM-backed implementation that drives the decision from the bot's
 * {@link com.twb.pokerapp.domain.enumeration.Persona} instructions (play style / temperament)
 * and the live game state.
 */
public interface BotActionService {

    /**
     * @param botSession   the bot's managed player session (current funds, position, etc.)
     * @param bettingRound the betting round the decision is being made for
     * @param nextActions  the actions currently available to the bot, plus the amount to call
     * @return a valid {@link PlayerActionCommand} the game engine can apply
     */
    PlayerActionCommand decideAction(PlayerSession botSession, BettingRound bettingRound, NextActionsDTO nextActions);
}
