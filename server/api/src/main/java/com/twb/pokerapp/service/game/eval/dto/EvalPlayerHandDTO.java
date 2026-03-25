package com.twb.pokerapp.service.game.eval.dto;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.HandType;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class EvalPlayerHandDTO implements Comparable<EvalPlayerHandDTO> {
    private static final Comparator<EvalPlayerHandDTO> RANK_COMPARATOR =
            Comparator.comparing(EvalPlayerHandDTO::getRank, Comparator.nullsLast(Comparator.naturalOrder()));

    private PlayerSession playerSession;
    private Hand hand;
    private List<Card> cards;
    private Integer rank;
    private HandType handType;

    @Override
    public int compareTo(@NonNull EvalPlayerHandDTO o) {
        return RANK_COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return "PlayerHandDTO{" +
                "playerSession=" + playerSession.getUser().getUsername() +
                ", hand=" + hand +
                ", cards=" + cards +
                ", rank=" + rank +
                ", handType=" + handType +
                '}';
    }
}
