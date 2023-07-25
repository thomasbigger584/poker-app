package com.twb.pokergame.domain;

import com.twb.pokergame.domain.enumeration.CardType;
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

    @Column(name = "rank")
    private int rank;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "round_id")
    private Round round; // community card

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return new EqualsBuilder()
                .append(rank, card.rank)
                .append(id, card.id).append(suitType, card.suitType)
                .append(cardType, card.cardType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(rank)
                .append(suitType).append(cardType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", rank=" + rank +
                ", suitType=" + suitType +
                ", cardType=" + cardType +
                '}';
    }
}
