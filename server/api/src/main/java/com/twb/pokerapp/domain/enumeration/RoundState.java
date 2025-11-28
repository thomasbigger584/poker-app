package com.twb.pokerapp.domain.enumeration;

import jakarta.annotation.Nullable;

import static com.twb.pokerapp.domain.enumeration.BettingRoundType.*;

public enum RoundState {
    WAITING_FOR_PLAYERS {
        @Override
        public RoundState nextTexasHoldemState() {
            return INIT_DEAL;
        }
    },
    INIT_DEAL {
        @Override
        public RoundState nextTexasHoldemState() {
            return INIT_DEAL_BET;
        }
    }, INIT_DEAL_BET {
        @Override
        public RoundState nextTexasHoldemState() {
            return FLOP_DEAL;
        }

        @Override
        public BettingRoundType getBettingRoundType() {
            return DEAL;
        }
    }, FLOP_DEAL {
        @Override
        public RoundState nextTexasHoldemState() {
            return FLOP_DEAL_BET;
        }

    }, FLOP_DEAL_BET {
        @Override
        public RoundState nextTexasHoldemState() {
            return TURN_DEAL;
        }

        @Override
        public BettingRoundType getBettingRoundType() {
            return FLOP;
        }
    }, TURN_DEAL {
        @Override
        public RoundState nextTexasHoldemState() {
            return TURN_DEAL_BET;
        }
    }, TURN_DEAL_BET {
        @Override
        public RoundState nextTexasHoldemState() {
            return RIVER_DEAL;
        }

        @Override
        public BettingRoundType getBettingRoundType() {
            return TURN;
        }
    }, RIVER_DEAL {
        @Override
        public RoundState nextTexasHoldemState() {
            return RIVER_DEAL_BET;
        }
    }, RIVER_DEAL_BET {
        @Override
        public RoundState nextTexasHoldemState() {
            return EVAL;
        }

        @Override
        public BettingRoundType getBettingRoundType() {
            return RIVER;
        }
    }, EVAL {
        @Override
        public RoundState nextTexasHoldemState() {
            return FINISH;
        }
    }, FINISH {
        @Override
        public RoundState nextTexasHoldemState() {
            return null;
        }
    };

    public @Nullable BettingRoundType getBettingRoundType() {
        return null;
    }

    public abstract RoundState nextTexasHoldemState();
}
