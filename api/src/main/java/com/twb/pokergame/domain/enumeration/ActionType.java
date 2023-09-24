package com.twb.pokergame.domain.enumeration;

public enum ActionType {
    CHECK,
    BET,
    CALL,
    RAISE,
    FOLD;

    public static ActionType[] getNextActions(ActionType actionType) {
        if (actionType == null) {
            return new ActionType[]{CHECK, BET};
        }
        return actionType.getNextActions();
    }

    private ActionType[] getNextActions() {
        return switch (this) {
            case CHECK: {
                yield new ActionType[]{CHECK, BET};
            }
            case BET:
            case CALL:
            case RAISE:
            case FOLD: {
                yield new ActionType[]{CALL, RAISE, FOLD};
            }
        };
    }
}
