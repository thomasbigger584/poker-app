package com.twb.pokergame.domain.enumeration;

public enum ActionType {
    CHECK,
    BET,
    CALL,
    RAISE,
    FOLD;

    public ActionType[] getNextActions() {
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
