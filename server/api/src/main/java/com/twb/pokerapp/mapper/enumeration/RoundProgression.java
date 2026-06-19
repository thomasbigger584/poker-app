package com.twb.pokerapp.mapper.enumeration;

import com.twb.pokerapp.proto.BettingRoundType;
import com.twb.pokerapp.proto.RoundState;

import java.util.Optional;

import static com.twb.pokerapp.proto.BettingRoundType.*;
import static com.twb.pokerapp.proto.RoundState.*;

/**
 * The round state machine — the next state in the deal/bet lifecycle and the betting round type (if
 * any) associated with a state. Extracted from the former {@code RoundState} domain enum's
 * per-constant overrides. There is currently a single progression (the deal/flop/turn/river
 * lifecycle), but this is game-agnostic and a variant could supply its own.
 */
public final class RoundProgression {

    private RoundProgression() {
    }

    /**
     * The next round state, or {@code null} for terminal states (FINISHED / FAILED).
     */
    public static RoundState next(RoundState roundState) {
        return switch (roundState) {
            case ROUND_STATE_WAITING_FOR_PLAYERS -> ROUND_STATE_INIT_DEAL;
            case ROUND_STATE_INIT_DEAL -> ROUND_STATE_INIT_DEAL_BET;
            case ROUND_STATE_INIT_DEAL_BET -> ROUND_STATE_FLOP_DEAL;
            case ROUND_STATE_FLOP_DEAL -> ROUND_STATE_FLOP_DEAL_BET;
            case ROUND_STATE_FLOP_DEAL_BET -> ROUND_STATE_TURN_DEAL;
            case ROUND_STATE_TURN_DEAL -> ROUND_STATE_TURN_DEAL_BET;
            case ROUND_STATE_TURN_DEAL_BET -> ROUND_STATE_RIVER_DEAL;
            case ROUND_STATE_RIVER_DEAL -> ROUND_STATE_RIVER_DEAL_BET;
            case ROUND_STATE_RIVER_DEAL_BET -> ROUND_STATE_EVAL;
            case ROUND_STATE_EVAL -> ROUND_STATE_FINISHED;
            default -> null; // FINISHED, FAILED, UNSPECIFIED, UNRECOGNIZED
        };
    }

    /**
     * The betting round type dealt for a state's betting phase, or empty for non-betting states.
     */
    public static Optional<BettingRoundType> bettingRoundType(RoundState roundState) {
        return switch (roundState) {
            case ROUND_STATE_INIT_DEAL_BET -> Optional.of(BETTING_ROUND_TYPE_DEAL);
            case ROUND_STATE_FLOP_DEAL_BET -> Optional.of(BETTING_ROUND_TYPE_FLOP);
            case ROUND_STATE_TURN_DEAL_BET -> Optional.of(BETTING_ROUND_TYPE_TURN);
            case ROUND_STATE_RIVER_DEAL_BET -> Optional.of(BETTING_ROUND_TYPE_RIVER);
            default -> Optional.empty();
        };
    }
}
