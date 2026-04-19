package com.twb.pokerapp.domain;

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
@Table(name = "betting_round_refund")
public class BettingRoundRefund extends Auditable {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_session_id")
    private PlayerSession playerSession;

    @ManyToOne(optional = false)
    @JoinColumn(name = "betting_round_id")
    private BettingRound bettingRound;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (BettingRoundRefund) o;
        return new EqualsBuilder().append(id, that.id)
                .append(amount, that.amount).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(amount).toHashCode();
    }

    @Override
    public String toString() {
        return "BettingRoundRefund{" +
                "id=" + id +
                ", amount=" + amount +
                '}';
    }
}
