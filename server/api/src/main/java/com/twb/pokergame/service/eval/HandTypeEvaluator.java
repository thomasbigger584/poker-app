package com.twb.pokergame.service.eval;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.enumeration.HandType;
import com.twb.pokergame.domain.enumeration.RankType;
import com.twb.pokergame.domain.enumeration.SuitType;
import org.springframework.stereotype.Component;

import java.util.*;

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

    public HandType evaluate(List<Card> cards) {
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
        } else if (!cards.isEmpty()) {
            return HandType.HIGH_CARD;
        }
        return HandType.EMPTY_HAND;
    }

    private boolean isRoyalFlush(List<Card> cards) {
        if (checkCardNullability(cards)) return false;
        if (checkHandSize(cards, FIVE_CARDS_NEEDED)) return false;

        Map<SuitType, Integer> suitCount = getSuitCount(cards);
        for (Map.Entry<SuitType, Integer> thisSuitCount : suitCount.entrySet()) {
            List<Card> flushCards = getFlushCards(cards, thisSuitCount);
            if (flushCards == null) {
                continue;
            }
            List<RankType> flushedRanks = new ArrayList<>();
            for (Card suitCard : flushCards) {
                flushedRanks.add(suitCard.getRankType());
            }
            if (flushedRanks.containsAll(ROYAL_FLUSH_RANKS)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStraightFlush(List<Card> cards) {
        if (checkCardNullability(cards)) return false;
        if (checkHandSize(cards, FIVE_CARDS_NEEDED)) return false;

        Map<SuitType, Integer> suitCount = getSuitCount(cards);
        for (Map.Entry<SuitType, Integer> thisSuitCount : suitCount.entrySet()) {
            List<Card> flushCards = getFlushCards(cards, thisSuitCount);
            if (flushCards == null) {
                continue;
            }
            if (isStraight(flushCards)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFourOfAKind(List<Card> cards) {
        return isOfAKind(cards, FOUR_CARDS_NEEDED);
    }

    private boolean isFullHouse(List<Card> cards) {
        if (checkCardNullability(cards)) return false;
        if (checkHandSize(cards, FIVE_CARDS_NEEDED)) return false;

        Map<RankType, Integer> rankToCountMap = getRankCount(cards);

        int twosCount = 0;
        int threesCount = 0;
        for (Map.Entry<RankType, Integer> entry : rankToCountMap.entrySet()) {
            Integer suitCount = entry.getValue();
            if (suitCount == TWO_CARDS_NEEDED) {
                twosCount++;
            } else if (suitCount == THREE_CARDS_NEEDED) {
                threesCount++;
            }
        }
        return (twosCount >= 1 && threesCount >= 1);
    }

    private boolean isFlush(List<Card> cards) {
        if (checkCardNullability(cards)) return false;
        if (checkHandSize(cards, FIVE_CARDS_NEEDED)) return false;

        Map<SuitType, Integer> suitToCountMap = getSuitCount(cards);
        for (Map.Entry<SuitType, Integer> suitToCount : suitToCountMap.entrySet()) {
            if (suitToCount.getValue() == FIVE_CARDS_NEEDED) {
                return true;
            }
        }
        return false;
    }

    private boolean isStraight(List<Card> cards) {
        return isStraightHand(cards) != null;
    }

    private List<Card> isStraightHand(List<Card> cards) {
        if (checkCardNullability(cards)) return null;
        if (checkHandSize(cards, FIVE_CARDS_NEEDED)) return null;

        List<Card> copyHand = new ArrayList<>(cards);
        //todo if this doesnt work try commented
//        copyHand.sort(Comparator.comparingInt((ToIntFunction<Card>) value -> value.getRankType().getValue()).thenComparing(Card::getSuitType));
        copyHand.sort(Comparator.comparing(Card::getRankType).thenComparing(Card::getSuitType));


        boolean reachedPartLowStraight = false;
        List<Card> currentStraight = null;
        for (Card card : copyHand) {
            final RankType rank = card.getRankType();
            if (!reachedPartLowStraight) {
                reachedPartLowStraight = containsAllPartialForStraight(currentStraight);
            } else if (rank == RankType.ACE) {
                currentStraight.add(card);
                return new ArrayList<>(currentStraight);
            }
            if (!isRankInCurrentStraight(currentStraight, rank)) {
                if (isLastElementBeforeCurrentRank(currentStraight, rank)) {
                    currentStraight.add(card);
                    if (currentStraight.size() == 5) {
                        return new ArrayList<>(currentStraight);
                    }
                } else {
                    currentStraight = new ArrayList<>();
                    currentStraight.add(card);
                }
            }
        }
        return null;
    }

    private boolean isThreeOfAKind(List<Card> cards) {
        return isOfAKind(cards, THREE_CARDS_NEEDED);
    }

    private boolean isTwoPair(List<Card> cards) {
        if (checkCardNullability(cards)) return false;
        if (checkHandSize(cards, FOUR_CARDS_NEEDED)) return false;

        Map<RankType, Integer> rankToCountMap = getRankCount(cards);

        int count = 0;
        for (Map.Entry<RankType, Integer> entry : rankToCountMap.entrySet()) {
            Integer suitCount = entry.getValue();
            if (suitCount == 2) {
                count++;
                if (count == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<SuitType, Integer> getSuitCount(List<Card> cards) {
        Map<SuitType, Integer> suitToCountMap = new HashMap<>();
        for (Card card : cards) {
            SuitType suit = card.getSuitType();
            Integer count = suitToCountMap.get(suit);
            if (count == null) {
                suitToCountMap.put(suit, 1);
            } else {
                suitToCountMap.put(suit, ++count);
            }
        }
        return suitToCountMap;
    }

    private Map<RankType, Integer> getRankCount(List<Card> cards) {
        Map<RankType, Integer> rankToCountMap = new HashMap<>();
        for (Card card : cards) {
            RankType rank = card.getRankType();
            Integer count = rankToCountMap.get(rank);
            if (count == null) {
                rankToCountMap.put(rank, 1);
            } else {
                rankToCountMap.put(rank, ++count);
            }
        }
        return rankToCountMap;
    }

    private boolean isPair(List<Card> cards) {
        return isOfAKind(cards, TWO_CARDS_NEEDED);
    }

    private boolean isOfAKind(List<Card> cards, int kindness) {
        if (checkCardNullability(cards)) return false;
        if (checkHandSize(cards, kindness)) return false;

        Map<RankType, Integer> rankToCountMap = new HashMap<>();
        for (Card card : cards) {
            RankType rank = card.getRankType();
            Integer count = rankToCountMap.get(rank);
            if (count == null) {
                rankToCountMap.put(rank, 1);
            } else {
                count++;
                if (count == kindness) {
                    return true;
                }
                rankToCountMap.put(rank, count);
            }
        }
        return false;
    }

    private List<Card> getFlushCards(List<Card> cards, Map.Entry<SuitType, Integer> thisSuitCount) {
        SuitType suit = thisSuitCount.getKey();
        int count = thisSuitCount.getValue();
        if (count < FIVE_CARDS_NEEDED) {
            return null;
        }
        List<Card> potentialStraightCards = new ArrayList<>();
        for (Card thisCard : cards) {
            if (thisCard.getSuitType() == suit) {
                potentialStraightCards.add(thisCard);
            }
        }
        return potentialStraightCards;
    }

    private boolean isLastElementBeforeCurrentRank(List<Card> currentStraight, RankType rank) {
        if (currentStraight == null || currentStraight.isEmpty()) {
            return false;
        }
        int size = currentStraight.size();
        Card card = currentStraight.get(size - 1);

        RankType cardRank = card.getRankType();
        int cardRankPosition = cardRank.getPosition();
        int nextCardRankPosition = cardRankPosition + 1;

        Optional<RankType> nextCardRankOpt = RankType.findRankByPosition(nextCardRankPosition);
        if (nextCardRankOpt.isEmpty()) {
            return false;
        }
        RankType nextCardRank = nextCardRankOpt.get();
        return nextCardRank == rank;
    }

    private boolean containsAllPartialForStraight(List<Card> currentStraight) {
        if (currentStraight == null || currentStraight.isEmpty()) {
            return false;
        }
        List<RankType> cardRanks = new ArrayList<>();
        for (Card card : currentStraight) {
            cardRanks.add(card.getRankType());
        }
        return cardRanks.containsAll(PARTIAL_LOWER_STRAIGHT);
    }

    private boolean checkHandSize(List<Card> cards, int expectedSize) {
        return cards.size() < expectedSize;
    }

    private boolean isRankInCurrentStraight(List<Card> currentStraight, RankType rank) {
        if (currentStraight == null || currentStraight.isEmpty()) {
            return false;
        }
        for (Card card : currentStraight) {
            if (card.getRankType() == rank) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCardNullability(List<Card> cards) {
        for (Card card : cards) {
            if (card == null) return true;
        }
        return false;
    }
}
