package com.twb.pokergame.data.model.enumeration;

public enum CommunityCardType {
    NONE(-1),
    BURN_PRE_FLOP(0),
    FLOP_1(1),
    FLOP_2(2),
    FLOP_3(3),
    BURN_PRE_RIVER(4),
    RIVER(5),
    BURN_PRE_TURN(6),
    TURN(7);

    private final int position;

    CommunityCardType(int position) {
        this.position = position;
    }

    public static CommunityCardType ofPosition(int position) {
        switch (position) {
            case 0: {
                return BURN_PRE_FLOP;
            }
            case 1: {
                return FLOP_1;
            }
            case 2: {
                return FLOP_2;
            }
            case 3: {
                return FLOP_3;
            }
            case 4: {
                return BURN_PRE_RIVER;
            }
            case 5: {
                return RIVER;
            }
            case 6: {
                return BURN_PRE_TURN;
            }
            case 7: {
                return TURN;
            }
        }
        return NONE;
    }

    public boolean isBurn() {
        return position == BURN_PRE_FLOP.position || position == BURN_PRE_RIVER.position || position == BURN_PRE_TURN.position;
    }

    public boolean isFlop() {
        return position == FLOP_1.position || position == FLOP_2.position || position == FLOP_3.position;
    }

    public boolean isRiver() {
        return position == RIVER.position;
    }

    public boolean isTurn() {
        return position == TURN.position;
    }

    public boolean isPlayable() {
        return isFlop() || isRiver() || isTurn();
    }

    public int getPosition() {
        return position;
    }
}
