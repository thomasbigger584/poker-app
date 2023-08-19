package com.twb.pokergame.domain;


import com.twb.pokergame.domain.enumeration.ConnectionType;
import com.twb.pokergame.domain.enumeration.SessionState;
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

    @Nullable
    @ManyToOne
    @JoinColumn(name = "poker_table_id")
    private PokerTable pokerTable;

    @Column(name = "position")
    private Integer position;

    @Column(name = "dealer")
    private Boolean dealer;

    @Column(name = "funds")
    private Double funds;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "session_state")
    private SessionState sessionState;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type")
    private ConnectionType connectionType;

    @OneToMany(mappedBy = "playerSession")
    private List<Hand> hands = new ArrayList<>();

    @OneToMany(mappedBy = "playerSession")
    private List<PlayerAction> playerActions = new ArrayList<>();

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
                ", user=" + user +
                ", position=" + position +
                ", dealer=" + dealer +
                ", funds=" + funds +
                ", sessionState=" + sessionState +
                ", connectionType=" + connectionType +
                '}';
    }
}
