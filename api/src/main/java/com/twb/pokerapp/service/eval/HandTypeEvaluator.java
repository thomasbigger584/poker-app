package com.twb.pokerapp.service.eval;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.HandType;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Evaluates the type of poker hand from a list of cards.
 */
@Component
public class HandTypeEvaluator {
    private static final List<RankType> PARTIAL_LOWER_STRAIGHT =
            Arrays.asList(RankType.DEUCE, RankType.TREY, RankType.FOUR, RankType.FIVE);
    private static final List<RankType> ROYAL_FLUSH_RANKS =
            Arrays.asList(RankType.TEN, RankType.JACK, RankType.QUEEN, RankType.KING, RankType.ACE);

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

        if (isRoyalFlush(cards)) {
            return HandType.ROYAL_FLUSH;
        } else if (isStraightFlush(cards)) {
            return HandType.STRAIGHT_FLUSH;
        } else if (isFourOfAKind(cards)) {
            return HandType.FOUR_OF_A_KIND;
        } else if (isFullHouse(cards)) {
            return HandType.FULL_HOUSE;
        } else if (isFlush(cards)) {
            return HandType.FLUSH;
        } else if (isStraight(cards)) {
            return HandType.STRAIGHT;
        } else if (isThreeOfAKind(cards)) {
            return HandType.THREE_OF_A_KIND;
        } else if (isTwoPair(cards)) {
            return HandType.TWO_PAIR;
        } else if (isPair(cards)) {
            return HandType.PAIR;
        } else {
            return HandType.HIGH_CARD;
        }
    }

    /**
     * Checks if the given cards form a Royal Flush.
     * <p>
     * A Royal Flush is a hand that contains the Ten, Jack, Queen, King, and Ace all of the same suit.
     * This method first verifies that the hand has at least five cards. Then it checks each suit
     * for a flush and verifies if it contains all the ranks required for a Royal Flush.
     *
     * @param cards the list of cards to check
     * @return true if the cards form a Royal Flush, false otherwise
     */
    private boolean isRoyalFlush(List<Card> cards) {
        if (!isValidHand(cards, FIVE_CARDS_NEEDED)) {
            return false;
        }

        Map<SuitType, List<Card>> flushCardsBySuit = getFlushCardsBySuit(cards);
        for (List<Card> flushCards : flushCardsBySuit.values()) {
            if (flushCards.size() >= FIVE_CARDS_NEEDED) {
                List<RankType> ranks = new ArrayList<>();
                for (Card card : flushCards) {
                    ranks.add(card.getRankType());
                }
                if (ranks.containsAll(ROYAL_FLUSH_RANKS)) {
                    return true;
                }
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
     * @param cards the list of cards to check
     * @return true if the cards form a Straight Flush, false otherwise
     */
    private boolean isStraightFlush(List<Card> cards) {
        if (!isValidHand(cards, FIVE_CARDS_NEEDED)) {
            return false;
        }

        Map<SuitType, List<Card>> flushCardsBySuit = getFlushCardsBySuit(cards);
        for (List<Card> flushCards : flushCardsBySuit.values()) {
            if (flushCards.size() >= FIVE_CARDS_NEEDED && isStraight(flushCards)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given cards form Four of a Kind.
     * <p>
     * Four of a Kind is a hand that contains four cards of the same rank.
     * This method checks if there are exactly four cards of any rank.
     *
     * @param cards the list of cards to check
     * @return true if the cards form Four of a Kind, false otherwise
     */
    private boolean isFourOfAKind(List<Card> cards) {
        return isOfAKind(cards, FOUR_CARDS_NEEDED);
    }

    /**
     * Checks if the given cards form a Full House.
     * <p>
     * A Full House is a hand that contains three cards of one rank and two cards of another rank.
     * This method first verifies that the hand has at least five cards. Then it checks for the presence
     * of a Three of a Kind and a Pair.
     *
     * @param cards the list of cards to check
     * @return true if the cards form a Full House, false otherwise
     */
    private boolean isFullHouse(List<Card> cards) {
        if (!isValidHand(cards, FIVE_CARDS_NEEDED)) {
            return false;
        }

        Map<RankType, Long> rankCounts = getRankCounts(cards);

        boolean hasThreeOfAKind = rankCounts.values().stream().anyMatch(count -> count == THREE_CARDS_NEEDED);
        boolean hasPair = rankCounts.values().stream().anyMatch(count -> count == TWO_CARDS_NEEDED);

        return hasThreeOfAKind && hasPair;
    }

    /**
     * Checks if the given cards form a Flush.
     * <p>
     * A Flush is a hand that contains five cards all of the same suit, but not in sequence.
     * This method first verifies that the hand has at least five cards. Then it checks each suit
     * for the presence of at least five cards.
     *
     * @param cards the list of cards to check
     * @return true if the cards form a Flush, false otherwise
     */
    private boolean isFlush(List<Card> cards) {
        if (!isValidHand(cards, FIVE_CARDS_NEEDED)) {
            return false;
        }

        return getFlushCardsBySuit(cards)
                .values().stream()
                .anyMatch(flushCards -> flushCards.size() >= FIVE_CARDS_NEEDED);
    }

    /**
     * Checks if the given cards form a Straight.
     * <p>
     * A Straight is a hand that contains five cards in sequence, but not all of the same suit.
     * This method verifies if there are any five consecutive cards in the hand, including
     * the special case of an Ace-to-Five straight.
     *
     * @param cards the list of cards to check
     * @return true if the cards form a Straight, false otherwise
     */
    private boolean isStraight(List<Card> cards) {
        return getStraightCards(cards).isPresent();
    }

    /**
     * Finds a Straight in the given cards.
     * <p>
     * This method first verifies that the hand has at least five cards. It then checks for any
     * sequence of five consecutive cards, including the special case of an Ace-to-Five straight.
     *
     * @param cards the list of cards to check
     * @return an Optional containing the list of cards forming a Straight if found, or an empty Optional otherwise
     */
    private Optional<List<Card>> getStraightCards(List<Card> cards) {
        if (!isValidHand(cards, FIVE_CARDS_NEEDED)) {
            return Optional.empty();
        }

        Set<RankType> uniqueRanks = new HashSet<>();
        for (Card card : cards) {
            uniqueRanks.add(card.getRankType());
        }

        List<RankType> sortedRanks = new ArrayList<>(uniqueRanks);
        sortedRanks.sort(Comparator.comparingInt(RankType::getPosition));

        for (int index = 0; index <= sortedRanks.size() - FIVE_CARDS_NEEDED; index++) {
            List<RankType> potentialStraight = sortedRanks.subList(index, index + FIVE_CARDS_NEEDED);
            if (isConsecutive(potentialStraight)) {
                return Optional.of(getCardsByRanks(cards, potentialStraight));
            }
        }

        if (uniqueRanks.containsAll(PARTIAL_LOWER_STRAIGHT) && uniqueRanks.contains(RankType.ACE)) {
            List<RankType> lowAceStraight = new ArrayList<>(PARTIAL_LOWER_STRAIGHT);
            lowAceStraight.add(RankType.ACE);
            return Optional.of(getCardsByRanks(cards, lowAceStraight));
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
        for (int index = 1; index < ranks.size(); index++) {
            int position = ranks.get(index).getPosition();
            int prevPosition = ranks.get(index - 1).getPosition();
            if (position != prevPosition + 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the list of cards matching the specified ranks.
     * <p>
     * This method finds and returns the cards that match the specified list of ranks.
     *
     * @param cards the list of cards to search
     * @param ranks the list of ranks to match
     * @return the list of matching cards
     */
    private List<Card> getCardsByRanks(List<Card> cards, List<RankType> ranks) {
        List<Card> result = new ArrayList<>();
        for (RankType rank : ranks) {
            for (Card card : cards) {
                if (card.getRankType() == rank) {
                    result.add(card);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Checks if the given cards form Three of a Kind.
     * <p>
     * Three of a Kind is a hand that contains three cards of the same rank.
     * This method checks if there are exactly three cards of any rank.
     *
     * @param cards the list of cards to check
     * @return true if the cards form Three of a Kind, false otherwise
     */
    private boolean isThreeOfAKind(List<Card> cards) {
        return isOfAKind(cards, THREE_CARDS_NEEDED);
    }

    /**
     * Checks if the given cards form Two Pair.
     * <p>
     * Two Pair is a hand that contains two cards of one rank and two cards of another rank.
     * This method first verifies that the hand has at least four cards. Then it checks for the presence
     * of two pairs.
     *
     * @param cards the list of cards to check
     * @return true if the cards form Two Pair, false otherwise
     */
    private boolean isTwoPair(List<Card> cards) {
        if (!isValidHand(cards, FOUR_CARDS_NEEDED)) {
            return false;
        }

        long pairCount = getRankCounts(cards)
                .values().stream()
                .filter(count -> count == TWO_CARDS_NEEDED)
                .count();
        return pairCount >= 2;
    }

    /**
     * Checks if the given cards form a Pair.
     * <p>
     * A Pair is a hand that contains two cards of the same rank.
     * This method checks if there are exactly two cards of any rank.
     *
     * @param cards the list of cards to check
     * @return true if the cards form a Pair, false otherwise
     */
    private boolean isPair(List<Card> cards) {
        return isOfAKind(cards, TWO_CARDS_NEEDED);
    }

    /**
     * Checks if the given cards form a specified number of a kind.
     * <p>
     * This method verifies if there are exactly the specified number of cards of any rank.
     *
     * @param cards the list of cards to check
     * @param kind  the number of a kind to check for
     * @return true if the cards form the specified number of a kind, false otherwise
     */
    private boolean isOfAKind(List<Card> cards, int kind) {
        if (!isValidHand(cards, kind)) {
            return false;
        }
        return getRankCounts(cards)
                .values().stream()
                .anyMatch(count -> count == kind);
    }

    /**
     * Groups cards by their suit and returns a map from suit to list of cards.
     * <p>
     * This method categorizes the cards by their suit, which is useful for checking flushes.
     *
     * @param cards the list of cards to group
     * @return a map from suit type to list of cards of that suit
     */
    private Map<SuitType, List<Card>> getFlushCardsBySuit(List<Card> cards) {
        Map<SuitType, List<Card>> flushCardsBySuit = new HashMap<>();
        for (Card card : cards) {
            if (card == null) {
                continue;
            }
            flushCardsBySuit
                    .computeIfAbsent(card.getSuitType(), k -> new ArrayList<>())
                    .add(card);
        }
        return flushCardsBySuit;
    }

    /**
     * Counts the occurrences of each rank in the given list of cards.
     * <p>
     * This method creates a map from each rank to the number of times it appears in the hand.
     *
     * @param cards the list of cards to count
     * @return a map from rank type to count of cards of that rank
     */
    private Map<RankType, Long> getRankCounts(List<Card> cards) {
        Map<RankType, Long> rankCounts = new HashMap<>();
        for (Card card : cards) {
            if (card == null) {
                continue;
            }
            rankCounts.merge(card.getRankType(), 1L, Long::sum);
        }
        return rankCounts;
    }

    /**
     * Validates that the hand has the expected number of cards and no null values.
     * <p>
     * This method checks that the list of cards is not null, has at least the expected number of cards,
     * and does not contain any null values.
     *
     * @param cards        the list of cards to validate
     * @param expectedSize the expected minimum number of cards
     * @return true if the hand is valid, false otherwise
     */
    private boolean isValidHand(List<Card> cards, int expectedSize) {
        if (cards == null || cards.size() < expectedSize) {
            return false;
        }
        for (Card card : cards) {
            if (card == null) {
                return false;
            }
        }
        return true;
    }
}
