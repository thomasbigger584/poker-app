package com.twb.pokerapp.service.game.thread.dto;

import com.twb.pokerapp.proto.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Internal, mutable representation of a player action as it flows through the game engine (bots
 * build it, the action services mutate and apply it). It is deliberately kept in pure Java types
 * ({@link ActionType} + {@link BigDecimal}) so the behaviour-rich domain enum and exact-decimal
 * arithmetic stay out of the protobuf wire layer. Inbound {@code CreatePlayerActionDTO} protobuf
 * messages are converted into this command at the websocket controller boundary.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerActionCommand {
    private ActionType action;
    private BigDecimal amount;
}
