package com.twb.pokerapp.domain.enumeration;

public enum ActionType {
    CHECK,
    BET,
    CALL,
    RAISE,
    FOLD,
    ALL_IN;

    public static ActionType[] getDefaultActions() {
        return CHECK.getNextActions();
    }

    public ActionType[] getNextActions() {
        return switch (this) {
            case CHECK: {
                yield new ActionType[]{CHECK, BET};
            }
            case BET:
            case CALL:
            case RAISE:
            case FOLD:
            case ALL_IN: {
                yield new ActionType[]{CALL, RAISE, ALL_IN, FOLD};
            }
        };
    }

    public static ActionType[] getAllInActions() {
        return new ActionType[]{FOLD, ALL_IN};
    }
}
