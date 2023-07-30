package com.twb.pokergame.service.eval.dto;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.enumeration.HandType;
import com.twb.pokergame.domain.enumeration.RankType;
import com.twb.pokergame.domain.enumeration.SuitType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EvalPlayerHandDTO implements Comparable<EvalPlayerHandDTO> {
    private PlayerSession playerSession;
    private List<Card> cards;
    private Integer rank;
    private HandType handType;
    private boolean winner = false;

    @Override
    public int compareTo(@NotNull EvalPlayerHandDTO otherHand) {
        if (getRank() == null || otherHand.getRank() == null) {
            return 0;
        }
        return getRank().compareTo(otherHand.getRank());
    }

    public String getReadableCards() {
        StringBuilder sb = new StringBuilder("[");
        for (int index = 0; index < cards.size(); index++) {
            Card card = cards.get(index);
            RankType rankType = card.getRankType();
            SuitType suitType = card.getSuitType();
            sb.append(rankType.getRankChar());
            sb.append(suitType.getSuitChar());
            if (index < cards.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "PlayerHandDTO{" +
                "playerSession=" + playerSession.getUser().getUsername() +
                ", cards=" + cards +
                ", rank=" + rank +
                ", handType=" + handType +
                ", winner=" + winner +
                '}';
    }
}