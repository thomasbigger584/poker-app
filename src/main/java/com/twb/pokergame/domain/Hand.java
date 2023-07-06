package com.twb.pokergame.domain;

import lombok.Getter;

import java.util.*;

// first 2 cards are player cards
@Getter
public class Hand extends ArrayList<Card> implements Comparable<Hand> {
    private static final List<Integer> PARTIAL_LOWER_STRAIGHT =
            Arrays.asList(Card.DEUCE, Card.TREY, Card.FOUR, Card.FIVE);
    private static final List<Integer> ROYAL_FLUSH_RANKS =
            Arrays.asList(Card.TEN, Card.JACK, Card.QUEEN, Card.KING, Card.ACE);

    private static final int SEVEN_CARDS_NEEDED = 7;
    private static final int FIVE_CARDS_NEEDED = 5;
    private static final int FOUR_CARDS_NEEDED = 4;
    private static final int THREE_CARDS_NEEDED = 3;
    private static final int TWO_CARDS_NEEDED = 2;
    private static final int ONE_CARD_NEEDED = 1;
    private Integer rank;

    public Hand() {
        for (int index = 0; index < TWO_CARDS_NEEDED; index++) {
            add(null);
        }
    }

    public Hand(List<Card> hand) {
        super(hand);
    }

    public void setCommunityCards(List<Card> playableCards) {
        if (size() != SEVEN_CARDS_NEEDED) {
            addAll(new ArrayList<>(playableCards));
        }
    }

    public void calculateRank() {
//        this.rank = SevenCardHandEvaluator.getRank(this);
    }

    public void update(Card card) {
        if (get(0) != null && get(1) != null) {
            set(0, null);
            set(1, null);
        }
        if (get(0) == null) {
            set(0, card);
        } else if (get(1) == null) {
            set(1, card);
        }
    }

    public HandType getType() {
        if (isRoyalFlush()) {
            return HandType.ROYAL_FLUSH;
        } else if (isStraightFlush()) {
            return HandType.STRAIGHT_FLUSH;
        } else if (isFourOfAKind()) {
            return HandType.FOUR_OF_A_KIND;
        } else if (isFullHouse()) {
            return HandType.FULL_HOUSE;
        } else if (isFlush()) {
            return HandType.FLUSH;
        } else if (isStraight()) {
            return HandType.STRAIGHT;
        } else if (isThreeOfAKind()) {
            return HandType.THREE_OF_A_KIND;
        } else if (isTwoPair()) {
            return HandType.TWO_PAIR;
        } else if (isPair()) {
            return HandType.PAIR;
        } else if (size() >= ONE_CARD_NEEDED) {
            return HandType.HIGH_CARD;
        }
        return HandType.EMPTY_HAND;
    }

    private boolean isRoyalFlush() {
        if (checkCardNullability()) return false;
        if (checkHandSize(FIVE_CARDS_NEEDED)) return false;

        Map<Integer, Integer> suitCount = getSuitCount();
        for (Map.Entry<Integer, Integer> thisSuitCount : suitCount.entrySet()) {
            List<Card> flushCards = getFlushCards(thisSuitCount);
            if (flushCards == null) {
                continue;
            }
            List<Integer> flushedRanks = new ArrayList<>();
            for (Card suitCard : flushCards) {
                flushedRanks.add(suitCard.getRank());
            }
            if (flushedRanks.containsAll(ROYAL_FLUSH_RANKS)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStraightFlush() {
        if (checkCardNullability()) return false;
        if (checkHandSize(FIVE_CARDS_NEEDED)) return false;

        Map<Integer, Integer> suitCount = getSuitCount();
        for (Map.Entry<Integer, Integer> thisSuitCount : suitCount.entrySet()) {
            List<Card> flushCards = getFlushCards(thisSuitCount);
            if (flushCards == null) {
                continue;
            }
            Hand potentialStraightFlush = new Hand(flushCards);
            if (potentialStraightFlush.isStraight()) {
                return true;
            }
        }
        return false;
    }

    private boolean isFourOfAKind() {
        return isOfAKind(FOUR_CARDS_NEEDED);
    }

    private boolean isFullHouse() {
        if (checkCardNullability()) return false;
        if (checkHandSize(FIVE_CARDS_NEEDED)) return false;

        Map<Integer, Integer> rankToCountMap = getRankCount();

        int twosCount = 0;
        int threesCount = 0;
        for (Map.Entry<Integer, Integer> entry : rankToCountMap.entrySet()) {
            Integer suitCount = entry.getValue();
            if (suitCount == TWO_CARDS_NEEDED) {
                twosCount++;
            } else if (suitCount == THREE_CARDS_NEEDED) {
                threesCount++;
            }
        }
        return (twosCount >= 1 && threesCount >= 1);
    }

    private boolean isFlush() {
        if (checkCardNullability()) return false;
        if (checkHandSize(FIVE_CARDS_NEEDED)) return false;

        Map<Integer, Integer> suitToCountMap = getSuitCount();
        for (Map.Entry<Integer, Integer> suitToCount : suitToCountMap.entrySet()) {
            if (suitToCount.getValue() == FIVE_CARDS_NEEDED) {
                return true;
            }
        }
        return false;
    }

    private boolean isStraight() {
        return isStraightHand() != null;
    }

    private Hand isStraightHand() {
        if (checkCardNullability()) return null;
        if (checkHandSize(FIVE_CARDS_NEEDED)) return null;

        List<Card> copyHand = new ArrayList<>(this);
        Collections.sort(copyHand, (o1, o2) -> {
            int rankComp = Integer.compare(o1.getRank(), o2.getRank());
            if (rankComp != 0) {
                return rankComp;
            }
            return Integer.compare(o1.getSuit(), o2.getSuit());
        });

        boolean reachedPartLowStraight = false;
        List<Card> currentStraight = null;
        for (Card card : copyHand) {
            final int rank = card.getRank();
            if (!reachedPartLowStraight) {
                reachedPartLowStraight = containsAllPartialForStraight(currentStraight);
            } else if (rank == Card.ACE) {
                currentStraight.add(card);
                return new Hand(currentStraight);
            }
            if (!isRankInCurrentStraight(currentStraight, rank)) {
                if (isLastElementBeforeCurrentRank(currentStraight, rank)) {
                    currentStraight.add(card);
                    if (currentStraight.size() == 5) {
                        return new Hand(currentStraight);
                    }
                } else {
                    currentStraight = new ArrayList<>();
                    currentStraight.add(card);
                }
            }
        }
        return null;
    }

    private boolean isThreeOfAKind() {
        return isOfAKind(THREE_CARDS_NEEDED);
    }

    private boolean isTwoPair() {
        if (checkCardNullability()) return false;
        if (checkHandSize(FOUR_CARDS_NEEDED)) return false;

        Map<Integer, Integer> rankToCountMap = getRankCount();

        int count = 0;
        for (Map.Entry<Integer, Integer> entry : rankToCountMap.entrySet()) {
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

    private Map<Integer, Integer> getSuitCount() {
        Map<Integer, Integer> suitToCountMap = new HashMap<>();
        for (Card card : this) {
            int suit = card.getSuit();
            Integer count = suitToCountMap.get(suit);
            if (count == null) {
                suitToCountMap.put(suit, 1);
            } else {
                suitToCountMap.put(suit, ++count);
            }
        }
        return suitToCountMap;
    }

    private Map<Integer, Integer> getRankCount() {
        Map<Integer, Integer> rankToCountMap = new HashMap<>();
        for (Card card : this) {
            int rank = card.getRank();
            Integer count = rankToCountMap.get(rank);
            if (count == null) {
                rankToCountMap.put(rank, 1);
            } else {
                rankToCountMap.put(rank, ++count);
            }
        }
        return rankToCountMap;
    }

    private boolean isPair() {
        return isOfAKind(TWO_CARDS_NEEDED);
    }

    private boolean isOfAKind(int kindness) {
        if (checkCardNullability()) return false;
        if (checkHandSize(kindness)) return false;

        Map<Integer, Integer> rankToCountMap = new HashMap<>();
        for (Card card : this) {
            int rank = card.getRank();
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

    private List<Card> getFlushCards(Map.Entry<Integer, Integer> thisSuitCount) {
        final int suit = thisSuitCount.getKey();
        final int count = thisSuitCount.getValue();
        if (count < FIVE_CARDS_NEEDED) {
            return null;
        }
        List<Card> potentialStraightCards = new ArrayList<>();
        for (Card thisCard : this) {
            if (thisCard.getSuit() == suit) {
                potentialStraightCards.add(thisCard);
            }
        }
        return potentialStraightCards;
    }

    private boolean isLastElementBeforeCurrentRank(List<Card> currentStraight, int rank) {
        if (currentStraight == null || currentStraight.isEmpty()) {
            return false;
        }
        int size = currentStraight.size();
        Card card = currentStraight.get(size - 1);
        return (card.getRank() + 1 == rank);
    }

    private boolean containsAllPartialForStraight(List<Card> currentStraight) {
        if (currentStraight == null || currentStraight.isEmpty()) {
            return false;
        }
        List<Integer> cardRanks = new ArrayList<>();
        for (Card card : currentStraight) {
            cardRanks.add(card.getRank());
        }
        return cardRanks.containsAll(PARTIAL_LOWER_STRAIGHT);
    }

    private boolean checkHandSize(int expectedSize) {
        return size() < expectedSize;
    }

    private boolean isRankInCurrentStraight(List<Card> currentStraight, int rank) {
        if (currentStraight == null || currentStraight.isEmpty()) {
            return false;
        }
        for (Card card : currentStraight) {
            if (card.getRank() == rank) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCardNullability() {
        for (Card card : this) {
            if (card == null) return true;
        }
        return false;
    }

    @Override
    public int compareTo(Hand otherHand) {
        if (getRank() == null || otherHand.getRank() == null) {
            return 0;
        }
        return getRank().compareTo(otherHand.getRank());
    }
}
