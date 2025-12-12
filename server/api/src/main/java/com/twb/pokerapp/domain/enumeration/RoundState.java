package com.twb.pokerapp.domain.enumeration;

import java.util.Optional;

import static com.twb.pokerapp.domain.enumeration.BettingRoundType.*;

public enum RoundState {
    WAITING_FOR_PLAYERS {
        @Override
        public RoundState nextTexasState() {
            return INIT_DEAL;
        }
    },
    INIT_DEAL {
        @Override
        public RoundState nextTexasState() {
            return INIT_DEAL_BET;
        }
    }, INIT_DEAL_BET {
        @Override
        public RoundState nextTexasState() {
            return FLOP_DEAL;
        }

        @Override
        public Optional<BettingRoundType> getBettingRoundType() {
            return Optional.of(DEAL);
        }
    }, FLOP_DEAL {
        @Override
        public RoundState nextTexasState() {
            return FLOP_DEAL_BET;
        }

    }, FLOP_DEAL_BET {
        @Override
        public RoundState nextTexasState() {
            return TURN_DEAL;
        }

        @Override
        public Optional<BettingRoundType> getBettingRoundType() {
            return Optional.of(FLOP);
        }
    }, TURN_DEAL {
        @Override
        public RoundState nextTexasState() {
            return TURN_DEAL_BET;
        }
    }, TURN_DEAL_BET {
        @Override
        public RoundState nextTexasState() {
            return RIVER_DEAL;
        }

        @Override
        public Optional<BettingRoundType> getBettingRoundType() {
            return Optional.of(TURN);
        }
    }, RIVER_DEAL {
        @Override
        public RoundState nextTexasState() {
            return RIVER_DEAL_BET;
        }
    }, RIVER_DEAL_BET {
        @Override
        public RoundState nextTexasState() {
            return EVAL;
        }

        @Override
        public Optional<BettingRoundType> getBettingRoundType() {
            return Optional.of(RIVER);
        }
    }, EVAL {
        @Override
        public RoundState nextTexasState() {
            return FINISHED;
        }
    }, FINISHED {
        @Override
        public RoundState nextTexasState() {
            return null;
        }
    };

    public Optional<BettingRoundType> getBettingRoundType() {
        return Optional.empty();
    }

    public abstract RoundState nextTexasState();
}
