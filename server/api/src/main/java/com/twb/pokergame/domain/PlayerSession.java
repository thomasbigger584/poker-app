package com.twb.pokergame.domain;


import com.twb.pokergame.domain.enumeration.ConnectionState;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "round_id")
    private Round round;

    @Column(name = "position")
    private Integer position;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "connection_state")
    private ConnectionState connectionState;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PlayerSession that = (PlayerSession) o;

        return new EqualsBuilder().append(id, that.id)
                .append(connectionState, that.connectionState).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(connectionState).toHashCode();
    }
}
