package com.twb.pokergame.domain;


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
@Table(name = "player_session")
public class PlayerSession {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "poker_table_id")
    private PokerTable pokerTable;

    @Column(name = "position")
    private Integer position;

    @Column(name = "dealer")
    private Boolean dealer;

    @Column(name = "funds")
    private Double funds;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSession that = (PlayerSession) o;
        return new EqualsBuilder()
                .append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).toHashCode();
    }

    @Override
    public String toString() {
        return "PlayerSession{" +
                "id=" + id +
                ", position=" + position +
                ", dealer=" + dealer +
                ", funds=" + funds +
                '}';
    }
}
