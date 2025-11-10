package com.twb.pokerapp.service.eval;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.HandType;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Evaluates the type of poker hand from a list of cards.
 */
@Component
public class HandTypeEvaluator {
    private static final int FIVE_CARDS_NEEDED = 5;
    private static final int FOUR_CARDS_NEEDED = 4;
    private static final int THREE_CARDS_NEEDED = 3;
    private static final int TWO_CARDS_NEEDED = 2;

    /**
     * Evaluates the HandType of a given list of cards.
     * <p>
     * This method checks the given list of cards and determines the highest-ranking hand that can be made.
     * It sequentially checks for the presence of each hand type from highest to lowest rank.
     *
     * @param cards the list of cards to evaluate
     * @return the evaluated hand type
     */
    public HandType evaluate(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return HandType.EMPTY_HAND;
        }

        var analysis = new HandAnalysis(cards);

        if (isRoyalFlush(analysis)) {
            return HandType.ROYAL_FLUSH;
        } else if (isStraightFlush(analysis)) {
            return HandType.STRAIGHT_FLUSH;
        } else if (isFourOfAKind(analysis)) {
            return HandType.FOUR_OF_A_KIND;
        } else if (isFullHouse(analysis)) {
            return HandType.FULL_HOUSE;
        } else if (isFlush(analysis)) {
            return HandType.FLUSH;
        } else if (isStraight(analysis)) {
            return HandType.STRAIGHT;
        } else if (isThreeOfAKind(analysis)) {
            return HandType.THREE_OF_A_KIND;
        } else if (isTwoPair(analysis)) {
            return HandType.TWO_PAIR;
        } else if (isPair(analysis)) {
            return HandType.PAIR;
        } else {
            return HandType.HIGH_CARD;
        }
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    /**
     * Checks if the given cards form a Royal Flush.
     * <p>
     * A Royal Flush is a hand that contains the Ten, Jack, Queen, King, and Ace all of the same suit.
     * This method first verifies that the hand has at least five cards. Then it checks each suit
     * for a flush and verifies if it contains all the ranks required for a Royal Flush.
     *
     * @param analysis the analyzed hand
     * @return true if the cards form a Royal Flush, false otherwise
     */
    private boolean isRoyalFlush(HandAnalysis analysis) {
        if (!analysis.hasMinimumCards(FIVE_CARDS_NEEDED)) {
            return false;
        }

        for (var flushCards : analysis.getCardsBySuit().values()) {
            if (flushCards.size() >= FIVE_CARDS_NEEDED) {
                var ranks = flushCards.stream()
                        .map(Card::getRankType)
                        .collect(Collectors.toSet());

                if (ranks.containsAll(HandAnalysis.ROYAL_FLUSH_RANKS)) return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given cards form a Straight Flush.
     * <p>
     * A Straight Flush is a hand that contains five cards in sequence, all of the same suit.
     * This method first verifies that the hand has at least five cards. Then it checks each suit
     * for a flush and verifies if any of these flushes form a straight.
     *
     * @param analysis the analyzed hand
     * @return true if the cards form a Straight Flush, false otherwise
     */
    private boolean isStraightFlush(HandAnalysis analysis) {
        if (!analysis.hasMinimumCards(FIVE_CARDS_NEEDED)) {
            return false;
        }

        for (var flushCards : analysis.getCardsBySuit().values()) {
            if (flushCards.size() >= FIVE_CARDS_NEEDED && isStraight(new HandAnalysis(flushCards))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given cards form Four of a Kind.
     * <p>
     * Four of a Kind is a hand that contains four cards of the same rank.
     * This method checks if there are at least four cards of any rank.
     *
     * @param analysis the analyzed hand
     * @return true if the cards form Four of a Kind, false otherwise
     */
    private boolean isFourOfAKind(HandAnalysis analysis) {
        return analysis.hasMinimumCards(FOUR_CARDS_NEEDED) &&
                analysis.getCounts().get(0) >= FOUR_CARDS_NEEDED;
    }

    /**
     * Checks if the given cards form a Full House.
     * <p>
     * A Full House is a hand that contains three cards of one rank and two cards of another rank.
     * This method first verifies that the hand has at least five cards. Then it checks for the presence
     * of a Three of a Kind and a Pair.
     *
     * @param analysis the analyzed hand
     * @return true if the cards form a Full House, false otherwise
     */
    private boolean isFullHouse(HandAnalysis analysis) {
        if (!analysis.hasMinimumCards(FIVE_CARDS_NEEDED)) {
            return false;
        }
        // Check for a 3-of-a-kind and a pair, which can also be two 3-of-a-kinds.
        // The counts list is sorted, so we just need to check the first two elements.
        var counts = analysis.getCounts();
        return counts.size() >= 2 &&
                counts.get(0) >= THREE_CARDS_NEEDED && counts.get(1) >= TWO_CARDS_NEEDED;
    }

    /**
     * Checks if the given cards form a Flush.
     * <p>
     * A Flush is a hand that contains five cards all of the same suit, but not in sequence.
     * This method first verifies that the hand has at least five cards. Then it checks each suit
     * for the presence of at least five cards.
     *
     * @param analysis the analyzed hand
     * @return true if the cards form a Flush, false otherwise
     */
    private boolean isFlush(HandAnalysis analysis) {
        if (!analysis.hasMinimumCards(FIVE_CARDS_NEEDED)) {
            return false;
        }
        return analysis.getCardsBySuit().values().stream()
                .anyMatch(list -> list.size() >= FIVE_CARDS_NEEDED);
    }

    /**
     * Checks if the given cards form a Straight.
     * <p>
     * A Straight is a hand that contains five cards in sequence, but not all of the same suit.
     * This method verifies if there are any five consecutive cards in the hand, including
     * the special case of an Ace-to-Five straight.
     *
     * @param analysis the analyzed hand
     * @return true if the cards form a Straight, false otherwise
     */
    private boolean isStraight(HandAnalysis analysis) {
        return getStraightRanks(analysis).isPresent();
    }

    /**
     * Finds a Straight in the given cards.
     * <p>
     * This method first verifies that the hand has at least five cards. It then checks for any
     * sequence of five consecutive cards, including the special case of an Ace-to-Five straight.
     *
     * @param analysis the analyzed hand
     * @return an Optional containing the list of ranks forming a Straight if found, or an empty Optional otherwise
     */
    private Optional<List<RankType>> getStraightRanks(HandAnalysis analysis) {
        if (!analysis.hasMinimumCards(FIVE_CARDS_NEEDED)) {
            return Optional.empty();
        }

        var uniqueRanks = analysis.getRankCounts().keySet();

        var sortedRanks = new ArrayList<>(uniqueRanks);
        sortedRanks.sort(Comparator.comparingInt(RankType::getPosition));

        for (var index = 0; index <= sortedRanks.size() - FIVE_CARDS_NEEDED; index++) {
            var potentialStraight = sortedRanks.subList(index, index + FIVE_CARDS_NEEDED);
            if (isConsecutive(potentialStraight)) {
                return Optional.of(potentialStraight);
            }
        }

        // Check for Ace-low straight (A, 2, 3, 4, 5)
        if (uniqueRanks.containsAll(HandAnalysis.PARTIAL_LOWER_STRAIGHT) && uniqueRanks.contains(RankType.ACE)) {
            var lowAceStraight = new ArrayList<>(HandAnalysis.PARTIAL_LOWER_STRAIGHT);
            lowAceStraight.add(RankType.ACE);
            return Optional.of(lowAceStraight);
        }

        return Optional.empty();
    }

    /**
     * Checks if a list of ranks are consecutive.
     * <p>
     * This method verifies that each rank in the list is one position higher than the previous rank.
     *
     * @param ranks the list of ranks to check
     * @return true if the ranks are consecutive, false otherwise
     */
    private boolean isConsecutive(List<RankType> ranks) {
        for (var index = 1; index < ranks.size(); index++) {
            var position = ranks.get(index).getPosition();
            var prevPosition = ranks.get(index - 1).getPosition();
            if (position != prevPosition + 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given cards form Three of a Kind.
     * <p>
     * Three of a Kind is a hand that contains three cards of the same rank.
     * This method checks if there are at least three cards of any rank.
     *
     * @param analysis the analyzed hand
     * @return true if the cards form Three of a Kind, false otherwise
     */
    private boolean isThreeOfAKind(HandAnalysis analysis) {
        return analysis.hasMinimumCards(THREE_CARDS_NEEDED) &&
                analysis.getCounts().get(0) >= THREE_CARDS_NEEDED;
    }

    /**
     * Checks if the given cards form Two Pair.
     * <p>
     * Two Pair is a hand that contains two cards of one rank and two cards of another rank.
     * This method first verifies that the hand has at least four cards. Then it checks for the presence
     * of two pairs.
     *
     * @param analysis the analyzed hand
     * @return true if the cards form Two Pair, false otherwise
     */
    private boolean isTwoPair(HandAnalysis analysis) {
        if (!analysis.hasMinimumCards(FOUR_CARDS_NEEDED)) {
            return false;
        }
        // The counts list is sorted, so we just need to check the first two elements.
        var counts = analysis.getCounts();
        return counts.size() >= 2 &&
                counts.get(0) >= TWO_CARDS_NEEDED && counts.get(1) >= TWO_CARDS_NEEDED;
    }

    /**
     * Checks if the given cards form a Pair.
     * <p>
     * A Pair is a hand that contains two cards of the same rank.
     * This method checks if there are at least two cards of any rank.
     *
     * @param analysis the analyzed hand
     * @return true if the cards form a Pair, false otherwise
     */
    private boolean isPair(HandAnalysis analysis) {
        return analysis.hasMinimumCards(TWO_CARDS_NEEDED) &&
                analysis.getCounts().get(0) >= TWO_CARDS_NEEDED;
    }

    /**
     * A pre-computation analysis of a list of cards.
     * <p>
     * This class groups cards by suit and counts occurrences of each rank upon instantiation.
     * This avoids re-calculating these groupings for each hand type evaluation, improving performance.
     */
    @Getter
    private static class HandAnalysis {
        private static final List<RankType> PARTIAL_LOWER_STRAIGHT =
                List.of(RankType.DEUCE, RankType.TREY, RankType.FOUR, RankType.FIVE);
        private static final List<RankType> ROYAL_FLUSH_RANKS =
                List.of(RankType.TEN, RankType.JACK, RankType.QUEEN, RankType.KING, RankType.ACE);


        private final List<Card> originalCards;
        private final Map<SuitType, List<Card>> cardsBySuit;
        private final Map<RankType, Integer> rankCounts;
        private final List<Integer> counts;

        /**
         * Constructs a HandAnalysis object by processing a list of cards.
         *
         * @param cards The list of cards to be analyzed. Must not be null.
         */
        public HandAnalysis(List<Card> cards) {
            this.originalCards = Objects.requireNonNull(cards);
            this.cardsBySuit = new HashMap<>();
            this.rankCounts = new HashMap<>();

            for (var card : cards) {
                if (card != null) {
                    cardsBySuit.computeIfAbsent(card.getSuitType(), k -> new ArrayList<>()).add(card);
                    rankCounts.merge(card.getRankType(), 1, Integer::sum);
                }
            }
            this.counts = new ArrayList<>(rankCounts.values());
            this.counts.sort(Comparator.reverseOrder());
        }

        /**
         * Checks if the hand contains at least a minimum number of cards.
         *
         * @param count the minimum number of cards required.
         * @return true if the hand size is greater than or equal to the count, false otherwise.
         */
        public boolean hasMinimumCards(int count) {
            return originalCards.size() >= count;
        }
    }
}
