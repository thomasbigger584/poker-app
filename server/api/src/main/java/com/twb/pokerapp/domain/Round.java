package com.twb.pokerapp.domain;

import com.twb.pokerapp.domain.enumeration.RoundState;
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
@Table(name = "round")
public class Round {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "round_state")
    private RoundState roundState;

    @ManyToOne(optional = false)
    @JoinColumn(name = "poker_table_id")
    private PokerTable pokerTable;

    @OneToMany(mappedBy = "round")
    private List<Card> communityCards = new ArrayList<>();

    @OneToMany(mappedBy = "round")
    private List<BettingRound> bettingRounds = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var round = (Round) o;
        return new EqualsBuilder().append(id, round.id)
                .append(roundState, round.roundState).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(roundState).toHashCode();
    }

    @Override
    public String toString() {
        return "Round{" +
                "id=" + id +
                ", roundState=" + roundState +
                '}';
    }
}
