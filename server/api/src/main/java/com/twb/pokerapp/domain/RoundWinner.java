package com.twb.pokerapp.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "round_winner")
public class RoundWinner extends Auditable {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_session_id")
    private PlayerSession playerSession;

    @ManyToOne(optional = false)
    @JoinColumn(name = "round_id")
    private Round round;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "hand_id")
    private Hand hand;

    @Column(name = "amount")
    private BigDecimal amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (RoundWinner) o;
        return new EqualsBuilder().append(id, that.id).append(amount, that.amount).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(amount).toHashCode();
    }

    @Override
    public String toString() {
        return "RoundWinner{" +
                "id=" + id +
                ", amount=" + amount +
                '}';
    }
}
