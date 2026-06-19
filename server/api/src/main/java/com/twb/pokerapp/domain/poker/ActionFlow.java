package com.twb.pokerapp.domain.poker;

import com.twb.pokerapp.proto.ActionType;

import static com.twb.pokerapp.proto.ActionType.*;

/**
 * Betting-action progression rules — which actions a player may take next, and whether an action is
 * aggressive. Extracted from the former {@code ActionType} domain enum so the proto enum stays a
 * pure value type.
 */
public final class ActionFlow {

    private ActionFlow() {
    }

    /** The actions available when no one has acted yet (a player may check or open with a bet). */
    public static ActionType[] defaultActions() {
        return nextActions(ACTION_TYPE_CHECK);
    }

    /** The actions available to the next player given the previous action in the betting round. */
    public static ActionType[] nextActions(ActionType previousAction) {
        return switch (previousAction) {
            case ACTION_TYPE_CHECK -> new ActionType[]{ACTION_TYPE_CHECK, ACTION_TYPE_BET};
            case ACTION_TYPE_BET, ACTION_TYPE_CALL, ACTION_TYPE_RAISE, ACTION_TYPE_FOLD, ACTION_TYPE_ALL_IN ->
                    new ActionType[]{ACTION_TYPE_CALL, ACTION_TYPE_RAISE, ACTION_TYPE_ALL_IN, ACTION_TYPE_FOLD};
            default -> throw new IllegalArgumentException("No next actions defined for " + previousAction);
        };
    }

    /** When a player cannot cover the amount to call, they may only fold or go all-in. */
    public static ActionType[] allInActions() {
        return new ActionType[]{ACTION_TYPE_FOLD, ACTION_TYPE_ALL_IN};
    }

    /** A bet or a raise grows the pot and is considered aggressive. */
    public static boolean isAggressive(ActionType actionType) {
        return actionType == ACTION_TYPE_BET || actionType == ACTION_TYPE_RAISE;
    }
}
