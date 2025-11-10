package com.twb.pokerapp.domain;

import com.twb.pokerapp.domain.enumeration.BettingRoundState;
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
@Table(name = "betting_round")
public class BettingRound {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private BettingRoundState state;

    @Column(name = "pot")
    private Double pot;

    @ManyToOne(optional = false)
    @JoinColumn(name = "round_id")
    private Round round;

    @OneToMany(mappedBy = "bettingRound")
    private List<PlayerAction> playerActions = new ArrayList<>();

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var bettingRound = (BettingRound) o;
        return new EqualsBuilder().append(id, bettingRound.id)
                .append(state, bettingRound.state)
                .append(pot, bettingRound.pot).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(state).append(pot).toHashCode();
    }

    @Override
    public String toString() {
        return "BettingRound{" +
                "id=" + id +
                ", bettingRoundState=" + state +
                ", pot=" + pot +
                '}';
    }
}
