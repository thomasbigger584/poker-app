package com.twb.pokergame.domain;

import com.twb.pokergame.domain.enumeration.GameType;
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
@Table(name = "poker_table")
public class PokerTable {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "game_type")
    private GameType gameType;

    @OneToMany(mappedBy = "pokerTable", cascade = CascadeType.ALL)
    private List<Round> rounds = new ArrayList<>();

    @OneToMany(mappedBy = "pokerTable", cascade = CascadeType.ALL)
    private List<PlayerSession> playerSessions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokerTable that = (PokerTable) o;
        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).toHashCode();
    }

    @Override
    public String toString() {
        return "PokerTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gameType=" + gameType +
                '}';
    }
}
