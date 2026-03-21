package com.twb.pokerapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "fixed_scenario")
public class FixedScenario {

    @Id
    @NotNull
    @Column(name = "id")
    private UUID id;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "player_hands", columnDefinition = "jsonb")
    private List<String> playerHands = new ArrayList<>();

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "community_cards", columnDefinition = "jsonb")
    private List<String> communityCards = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var fixedScenario = (FixedScenario) o;
        return new EqualsBuilder()
                .append(id, fixedScenario.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).toHashCode();
    }

    @Override
    public String toString() {
        return "FixedScenario{" +
                "id=" + id +
                '}';
    }
}
