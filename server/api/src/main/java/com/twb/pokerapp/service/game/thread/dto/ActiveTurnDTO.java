package com.twb.pokerapp.service.game.thread.dto;

import com.twb.pokerapp.proto.PlayerTurnDTO;

/**
 * The player turn currently being awaited on a game thread, captured when the {@code PLAYER_TURN}
 * message is dispatched. Lets a reconnecting client be re-served the live turn (with the remaining
 * wait time computed from {@link #startMillis}) so its action buttons / countdown resume correctly.
 *
 * @param turn        the exact turn payload broadcast to the table (client-facing wait already applied)
 * @param startMillis wall-clock time the turn was dispatched, for computing remaining wait on reconnect
 */
public record ActiveTurnDTO(PlayerTurnDTO turn, long startMillis) {
}
