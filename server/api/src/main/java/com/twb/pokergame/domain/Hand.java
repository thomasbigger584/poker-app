package com.twb.pokergame.domain;

import com.twb.pokergame.domain.enumeration.HandType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "hand")
public class Hand {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Nullable
    @Enumerated(EnumType.STRING)
    @Column(name = "hand_type")
    private HandType handType;

    @Nullable
    @Column(name = "winner")
    private Boolean winner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_session_id")
    private PlayerSession playerSession;

    @ManyToOne(optional = false)
    @JoinColumn(name = "round_id")
    private Round round;

    //this will be a small amount (i.e. 2 for TexasHoldem)
    @OneToMany(mappedBy = "hand", fetch = FetchType.EAGER)
    private List<Card> cards = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hand hand = (Hand) o;
        return new EqualsBuilder().append(id, hand.id)
                .append(handType, hand.handType).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(handType).toHashCode();
    }

    @Override
    public String toString() {
        return "Hand{" +
                "id=" + id +
                ", handType=" + handType +
                '}';
    }
}
