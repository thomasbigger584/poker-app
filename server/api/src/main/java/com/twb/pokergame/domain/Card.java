package com.twb.pokergame.domain;

import com.twb.pokergame.domain.enumeration.CardType;
import com.twb.pokergame.domain.enumeration.RankType;
import com.twb.pokergame.domain.enumeration.SuitType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "card")
public class Card {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rank_type")
    private RankType rankType;

    @Column(name = "rank_value")
    private int rankValue;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "suit_type")
    private SuitType suitType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CardType cardType;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "hand_id")
    private Hand hand; // player card

    @Nullable
    @ManyToOne
    @JoinColumn(name = "round_id")
    private Round round; // community card

    public Card() {
    }

    public Card(@NotNull RankType rankType,
                @NotNull SuitType suitType, int rankValue) {
        this.rankType = rankType;
        this.suitType = suitType;
        this.rankValue = rankValue;
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public Card(@NotNull Card card) {
        this.rankType = card.getRankType();
        this.rankValue = card.getRankValue();
        this.suitType = card.getSuitType();
        this.cardType = card.getCardType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return new EqualsBuilder()
                .append(rankValue, card.rankValue)
                .append(id, card.id)
                .append(rankType, card.rankType)
                .append(suitType, card.suitType)
                .append(cardType, card.cardType).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(rankType)
                .append(rankValue).append(suitType)
                .append(cardType).toHashCode();
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", rankType=" + rankType +
                ", rankValue=" + rankValue +
                ", suitType=" + suitType +
                ", cardType=" + cardType +
                '}';
    }
}
