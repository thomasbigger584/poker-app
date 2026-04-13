package com.twb.pokerapp.domain;

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
@Table(name = "round_pot")
public class RoundPot extends Auditable {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pot_amount")
    private Double potAmount;

    @Column(name = "pot_index")
    private Integer potIndex; // 0 for Main Pot, 1+ for Side Pots

    @ManyToOne(optional = false)
    @JoinColumn(name = "round_id")
    private Round round;

    @ManyToMany
    @JoinTable(
            name = "round_pot_eligible_players",
            joinColumns = @JoinColumn(name = "round_pot_id"),
            inverseJoinColumns = @JoinColumn(name = "player_session_id")
    )
    private List<PlayerSession> eligiblePlayers = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var roundPot = (RoundPot) o;
        return new EqualsBuilder().append(id, roundPot.id)
                .append(potAmount, roundPot.potAmount)
                .append(potIndex, roundPot.potIndex)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(potAmount).append(potIndex).toHashCode();
    }
}