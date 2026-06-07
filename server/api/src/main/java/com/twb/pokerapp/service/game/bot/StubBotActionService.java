package com.twb.pokerapp.service.game.bot;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.repository.CardRepository;
import com.twb.pokerapp.repository.HandRepository;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.service.game.deck.DeckFactory;
import com.twb.pokerapp.service.game.eval.RankEvaluator;
import com.twb.pokerapp.service.game.thread.impl.texas.dto.NextActionsDTO;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Logical bot decision engine. It estimates the bot's win probability with a Monte-Carlo
 * simulation backed by the native {@link RankEvaluator}, then mixes that equity with pot odds
 * and a dose of randomness to pick an action.
 * <p>
 * <b>Equity:</b> on every turn we know the bot's two hole cards and the 0-5 community cards on
 * the board. We complete the board and deal random hole cards to the live opponents from the
 * remaining deck many times over, scoring each completed 7-card showdown with the evaluator
 * ({@code lower rank == stronger}). The fraction of trials the bot wins (ties split) is its
 * equity in [0, 1].
 * <p>
 * <b>Policy:</b> with nothing owed it value-bets strong equity (and occasionally bluffs); facing
 * a bet it continues only when its equity beats the pot odds, raising more often as equity climbs.
 * Randomness is applied throughout so the bot is not perfectly predictable. There is no persona or
 * play-style modelling here — that is reserved for a future LLM-backed implementation.
 * <p>
 * If the native evaluator is unavailable or the bot's cards cannot be read, it degrades gracefully
 * to a safe "calling station" fallback rather than failing the game thread.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StubBotActionService implements BotActionService {

    private static final int SIMULATIONS = 250;
    private static final int BOARD_SIZE = 5;
    private static final int HOLE_SIZE = 2;
    private static final int MAX_OPPONENTS = 5;

    // Equity thresholds driving the action mix (win-probability in [0, 1]).
    private static final double VALUE_BET_EQUITY = 0.58; // bet for value when checked to above this
    private static final double RAISE_EQUITY = 0.68;     // start considering raises when facing a bet
    private static final double STRONG_EQUITY = 0.85;    // monster: commit bigger / more often
    private static final double BLUFF_FREQUENCY = 0.10;  // chance to fire with weak holdings
    private static final double CALL_MARGIN = 0.0;       // equity slack required over pot odds to continue

    // Bet/raise sizing relative to the pot (and a floor relative to stack for tiny/empty pots).
    private static final double STACK_FLOOR_FRACTION = 0.05;
    private static final double STRONG_SIZE_MULTIPLIER = 1.3;

    private final RankEvaluator rankEvaluator;
    private final HandRepository handRepository;
    private final CardRepository cardRepository;
    private final PlayerActionRepository playerActionRepository;
    private final PlayerSessionRepository playerSessionRepository;

    @Override
    public CreatePlayerActionDTO decideAction(PlayerSession botSession, BettingRound bettingRound, NextActionsDTO nextActions) {
        var availableActions = nextActions.nextActions();
        var funds = botSession.getFunds();
        var callCost = nextActions.amountToCall() == null ? BigDecimal.ZERO : nextActions.amountToCall();

        CreatePlayerActionDTO decision;
        try {
            var round = bettingRound.getRound();
            var tableId = round.getPokerTable().getId();
            var opponents = countOpponents(tableId, round.getId(), botSession.getId());
            var equity = estimateEquity(botSession, round, opponents);
            var pot = estimatePot(round.getId());
            var random = ThreadLocalRandom.current();

            if (contains(availableActions, ActionType.CHECK)) {
                decision = actWhenChecked(equity, pot, funds, random);
            } else {
                decision = actWhenFacingBet(equity, pot, callCost, funds, availableActions, random);
            }

            log.debug("Bot {} decided {} amount {} (equity={}, pot={}, callCost={}, opponents={}, available={})",
                    botSession.getUser().getUsername(), decision.getAction(), decision.getAmount(),
                    String.format("%.3f", equity), pot, callCost, opponents, Arrays.toString(availableActions));
        } catch (RuntimeException | LinkageError ex) {
            // Native lib missing, cards not yet dealt, etc. Never kill the game thread over a bot turn.
            log.warn("Bot {} could not evaluate equity ({}), using safe fallback action",
                    botSession.getUser().getUsername(), ex.toString());
            decision = fallbackDecision(availableActions, callCost, funds);
        }
        return decision;
    }

    // *****************************************************************************************
    // Decision policy
    // *****************************************************************************************

    /**
     * Nothing is owed (the only options are CHECK and BET). Value-bet when strong, bluff
     * occasionally, otherwise check back.
     */
    private CreatePlayerActionDTO actWhenChecked(double equity, BigDecimal pot, BigDecimal funds, Random random) {
        boolean bet;
        if (equity >= VALUE_BET_EQUITY) {
            var betProbability = clamp(0.40 + (equity - VALUE_BET_EQUITY) * 1.5, 0.0, 0.95);
            bet = random.nextDouble() < betProbability;
        } else {
            bet = random.nextDouble() < BLUFF_FREQUENCY;
        }
        if (bet) {
            return betOrAllIn(betSize(pot, funds, equity, random), funds);
        }
        return action(ActionType.CHECK, null);
    }

    /**
     * Facing a bet (CALL / RAISE / ALL_IN / FOLD, in some subset). Continue only when equity beats
     * the pot odds, raising more often as equity climbs; fold otherwise (with the odd bluff-raise).
     */
    private CreatePlayerActionDTO actWhenFacingBet(double equity, BigDecimal pot, BigDecimal callCost,
                                                   BigDecimal funds, ActionType[] availableActions, Random random) {
        var canCall = contains(availableActions, ActionType.CALL);
        var canRaise = contains(availableActions, ActionType.RAISE);
        var potOdds = potOdds(callCost, pot);
        var continueThreshold = potOdds + CALL_MARGIN;

        // Can't afford a full call: the only live options are fold or shove.
        if (!canCall) {
            if (equity >= continueThreshold || random.nextDouble() < BLUFF_FREQUENCY) {
                return action(ActionType.ALL_IN, funds);
            }
            return action(ActionType.FOLD, null);
        }

        // Pot odds say we're behind: usually fold, occasionally bluff-raise.
        if (equity < continueThreshold) {
            if (canRaise && random.nextDouble() < BLUFF_FREQUENCY) {
                return raiseOrAllIn(raiseSize(pot, callCost, funds, equity, random), callCost, funds);
            }
            return action(ActionType.FOLD, null);
        }

        // Equity beats the price: continue. Decide whether to raise for value or just call.
        boolean raise = false;
        if (canRaise && equity >= RAISE_EQUITY) {
            var raiseProbability = clamp(0.30 + (equity - RAISE_EQUITY) * 1.5, 0.0, 0.90);
            raise = random.nextDouble() < raiseProbability;
        }
        if (canRaise && equity >= STRONG_EQUITY && random.nextDouble() < 0.70) {
            raise = true; // monster: lean towards getting value in
        }
        if (raise) {
            return raiseOrAllIn(raiseSize(pot, callCost, funds, equity, random), callCost, funds);
        }
        return action(ActionType.CALL, callCost);
    }

    /**
     * Used only when equity could not be computed: passive calling station that stays in cheaply
     * and shoves rather than folds when it can no longer cover a call (mirrors the old stub).
     */
    private CreatePlayerActionDTO fallbackDecision(ActionType[] availableActions, BigDecimal callCost, BigDecimal funds) {
        if (contains(availableActions, ActionType.CHECK)) {
            return action(ActionType.CHECK, null);
        }
        if (contains(availableActions, ActionType.CALL)) {
            return action(ActionType.CALL, callCost);
        }
        if (contains(availableActions, ActionType.ALL_IN)) {
            return action(ActionType.ALL_IN, funds);
        }
        return action(ActionType.FOLD, null);
    }

    // *****************************************************************************************
    // Bet/raise sizing
    // *****************************************************************************************

    /**
     * Opening bet size: a randomised half-to-full pot, floored to a slice of the stack so the bot
     * still opens for a sensible amount pre-flop where the pot is effectively empty (no blinds).
     */
    private BigDecimal betSize(BigDecimal pot, BigDecimal funds, double equity, Random random) {
        var sizeFraction = 0.5 + random.nextDouble() * 0.5; // 50%-100% of pot
        var potComponent = pot.multiply(BigDecimal.valueOf(sizeFraction));
        var floor = funds.multiply(BigDecimal.valueOf(STACK_FLOOR_FRACTION));
        var size = potComponent.max(floor);
        if (equity >= STRONG_EQUITY) {
            size = size.multiply(BigDecimal.valueOf(STRONG_SIZE_MULTIPLIER));
        }
        return clampAmount(size, funds);
    }

    /**
     * Raise size as the <em>incremental</em> chips for this action (call portion + raise on top),
     * which is what the action layer expects. The raise-on-top is a randomised half-to-full pot,
     * never smaller than the call itself, so it is always a legal raise.
     */
    private BigDecimal raiseSize(BigDecimal pot, BigDecimal callCost, BigDecimal funds, double equity, Random random) {
        var extraFraction = 0.5 + random.nextDouble() * 0.5; // 50%-100% of pot on top
        var extra = pot.multiply(BigDecimal.valueOf(extraFraction));
        var minExtra = callCost.max(funds.multiply(BigDecimal.valueOf(STACK_FLOOR_FRACTION)));
        extra = extra.max(minExtra);
        if (equity >= STRONG_EQUITY) {
            extra = extra.multiply(BigDecimal.valueOf(STRONG_SIZE_MULTIPLIER));
        }
        return clampAmount(callCost.add(extra), funds);
    }

    private CreatePlayerActionDTO betOrAllIn(BigDecimal size, BigDecimal funds) {
        if (size.compareTo(funds) >= 0) {
            return action(ActionType.ALL_IN, funds);
        }
        return action(ActionType.BET, size);
    }

    private CreatePlayerActionDTO raiseOrAllIn(BigDecimal incremental, BigDecimal callCost, BigDecimal funds) {
        if (incremental.compareTo(funds) >= 0) {
            return action(ActionType.ALL_IN, funds);
        }
        if (incremental.compareTo(callCost) <= 0) {
            // Sizing collapsed to a non-raise (tiny pot/stack) — just call instead.
            return action(ActionType.CALL, callCost);
        }
        return action(ActionType.RAISE, incremental);
    }

    private BigDecimal clampAmount(BigDecimal amount, BigDecimal funds) {
        var rounded = amount.setScale(2, RoundingMode.HALF_UP);
        if (rounded.compareTo(funds) > 0) {
            return funds;
        }
        if (rounded.compareTo(BigDecimal.ZERO) <= 0) {
            return funds; // degenerate: shove rather than emit a zero bet
        }
        return rounded;
    }

    private double potOdds(BigDecimal callCost, BigDecimal pot) {
        var denominator = pot.add(callCost);
        if (denominator.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0; // free to continue
        }
        return callCost.divide(denominator, 6, RoundingMode.HALF_UP).doubleValue();
    }

    // *****************************************************************************************
    // Hand strength (Monte-Carlo equity via the native evaluator)
    // *****************************************************************************************

    /**
     * Estimates win probability by repeatedly completing the board and dealing the opponents random
     * holdings from the remaining deck, scoring each 7-card showdown with the native evaluator.
     */
    private double estimateEquity(PlayerSession botSession, Round round, int opponents) {
        var hand = handRepository.findForPlayerAndRound(botSession.getId(), round.getId())
                .orElseThrow(() -> new IllegalStateException("No hand dealt for bot " + botSession.getId()));
        var holeCards = hand.getCards();
        if (holeCards.size() != HOLE_SIZE) {
            throw new IllegalStateException("Expected " + HOLE_SIZE + " hole cards but found " + holeCards.size());
        }

        var hole = holeCards.stream().mapToInt(Card::getRankValue).toArray();
        var board = cardRepository.findCommunityCardsForRound(round.getId()).stream()
                .mapToInt(Card::getRankValue).toArray();

        var known = new HashSet<Integer>();
        for (var value : hole) {
            known.add(value);
        }
        for (var value : board) {
            known.add(value);
        }
        var deck = DeckFactory.CARDS.stream()
                .mapToInt(Card::getRankValue)
                .filter(value -> !known.contains(value))
                .toArray();

        var boardNeeded = BOARD_SIZE - board.length;
        // Never ask the deck for more cards than it holds.
        var safeOpponents = Math.min(opponents, (deck.length - boardNeeded) / HOLE_SIZE);
        if (safeOpponents < 1) {
            safeOpponents = 1;
        }
        var draws = boardNeeded + safeOpponents * HOLE_SIZE;

        var random = ThreadLocalRandom.current();
        var hero = new int[7];
        var villain = new int[7];
        hero[0] = hole[0];
        hero[1] = hole[1];

        var totalScore = 0.0;
        for (var sim = 0; sim < SIMULATIONS; sim++) {
            sampleWithoutReplacement(deck, draws, random);

            // Complete the shared board (known community cards + freshly sampled ones).
            for (var i = 0; i < board.length; i++) {
                hero[HOLE_SIZE + i] = board[i];
            }
            for (var i = 0; i < boardNeeded; i++) {
                hero[HOLE_SIZE + board.length + i] = deck[i];
            }
            System.arraycopy(hero, HOLE_SIZE, villain, HOLE_SIZE, BOARD_SIZE);

            var heroRank = rankEvaluator.getRank(hero);
            var ties = 0;
            var beaten = false;
            for (var opp = 0; opp < safeOpponents; opp++) {
                villain[0] = deck[boardNeeded + opp * HOLE_SIZE];
                villain[1] = deck[boardNeeded + opp * HOLE_SIZE + 1];
                var villainRank = rankEvaluator.getRank(villain);
                if (villainRank < heroRank) { // lower rank is stronger
                    beaten = true;
                    break;
                }
                if (villainRank == heroRank) {
                    ties++;
                }
            }
            if (beaten) {
                continue;
            }
            totalScore += ties == 0 ? 1.0 : 1.0 / (ties + 1);
        }
        return totalScore / SIMULATIONS;
    }

    /**
     * Partial Fisher-Yates: shuffles {@code count} distinct random entries into the front of
     * {@code deck} (selection sampling without replacement), touching only those positions.
     */
    private void sampleWithoutReplacement(int[] deck, int count, Random random) {
        var limit = Math.min(count, deck.length);
        for (var i = 0; i < limit; i++) {
            var j = i + random.nextInt(deck.length - i);
            var tmp = deck[i];
            deck[i] = deck[j];
            deck[j] = tmp;
        }
    }

    // *****************************************************************************************
    // Game-state lookups
    // *****************************************************************************************

    /**
     * Total chips committed to the round so far (across every street), used for pot odds and
     * bet sizing. Summed from the round's player actions; folded players' contributions count.
     */
    private BigDecimal estimatePot(UUID roundId) {
        return playerActionRepository.findByRoundId(roundId).stream()
                .map(playerAction -> playerAction.getAmount() == null ? BigDecimal.ZERO : playerAction.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int countOpponents(UUID tableId, UUID roundId, UUID selfSessionId) {
        var others = playerSessionRepository.findActivePlayersByTableId(tableId, roundId).stream()
                .filter(session -> !session.getId().equals(selfSessionId))
                .count();
        return (int) Math.min(MAX_OPPONENTS, Math.max(1, others));
    }

    // *****************************************************************************************
    // Helpers
    // *****************************************************************************************

    private CreatePlayerActionDTO action(ActionType actionType, BigDecimal amount) {
        var dto = new CreatePlayerActionDTO();
        dto.setAction(actionType);
        dto.setAmount(amount);
        return dto;
    }

    private boolean contains(ActionType[] actions, ActionType actionType) {
        return Arrays.asList(actions).contains(actionType);
    }

    private static double clamp(double value, double lo, double hi) {
        return Math.max(lo, Math.min(hi, value));
    }
}
