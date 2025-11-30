package com.twb.pokerapp.domain;


import com.twb.pokerapp.domain.enumeration.ActionType;
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
@Table(name = "player_action")
public class PlayerAction extends Auditable {

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private ActionType actionType;

    @Column(name = "amount")
    private Double amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var playerAction = (PlayerAction) o;
        return new EqualsBuilder().append(id, playerAction.id)
                .append(actionType, playerAction.actionType)
                .append(amount, playerAction.amount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(actionType).append(amount).toHashCode();
    }

    @Override
    public String toString() {
        return "PlayerAction{" +
                "id=" + id +
                ", actionType=" + actionType +
                ", amount=" + amount +
                '}';
    }
}
