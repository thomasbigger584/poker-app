package com.twb.pokerapp.domain.enumeration;

public enum ActionType {
    CHECK,
    BET,
    CALL,
    RAISE,
    FOLD;

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
            case FOLD: {
                yield new ActionType[]{CALL, RAISE, FOLD};
            }
        };
    }

    public double getAmountToCall(double previousAmount) {
        return switch (this) {
            case FOLD:
            case CHECK:
                yield 0d;
            case BET:
            case CALL:
            case RAISE: {
                yield previousAmount;
            }
        };
    }
}
