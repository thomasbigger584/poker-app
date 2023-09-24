package com.twb.pokergame.domain;


import com.twb.pokergame.domain.enumeration.ActionType;
import com.twb.pokergame.domain.enumeration.RoundState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "player_action")
public class PlayerAction {

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "round_state")
    private RoundState roundState;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private ActionType actionType;

    @Column(name = "amount")
    private Double amount;
}
