package com.twb.pokergame.domain.enumeration;

public enum RoundState {
    WAITING_FOR_PLAYERS {
        @Override
        public RoundState nextState() {
            return INIT_DEAL;
        }
    },
    INIT_DEAL {
        @Override
        public RoundState nextState() {
            return INIT_DEAL_BET;
        }
    }, INIT_DEAL_BET {
        @Override
        public RoundState nextState() {
            return FLOP_DEAL;
        }
    }, FLOP_DEAL {
        @Override
        public RoundState nextState() {
            return FLOP_DEAL_BET;
        }
    }, FLOP_DEAL_BET {
        @Override
        public RoundState nextState() {
            return RIVER_DEAL;
        }
    }, RIVER_DEAL {
        @Override
        public RoundState nextState() {
            return RIVER_DEAL_BET;
        }
    }, RIVER_DEAL_BET {
        @Override
        public RoundState nextState() {
            return TURN_DEAL;
        }
    }, TURN_DEAL {
        @Override
        public RoundState nextState() {
            return TURN_DEAL_BET;
        }
    }, TURN_DEAL_BET {
        @Override
        public RoundState nextState() {
            return EVAL;
        }
    }, EVAL {
        @Override
        public RoundState nextState() {
            return FINISH;
        }
    }, FINISH {
        @Override
        public RoundState nextState() {
            return null;
        }
    };

    public abstract RoundState nextState();
}
