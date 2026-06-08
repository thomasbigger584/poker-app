package com.twb.pokerapp.dto.table;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import lombok.Data;

@Data
public class AvailableTableDTO {
    private TableDTO table;
    private int playersConnected;

    /**
     * Whether the requesting user already has a live (still CONNECTED) session at this table — e.g.
     * they dropped and are within the disconnect grace window, or backgrounded the app. When true
     * the client offers "Reconnect" (straight back into the game) instead of "Connect".
     */
    private boolean currentUserConnected;

    /**
     * The connection type of the user's existing session (PLAYER / LISTENER), so a reconnect
     * re-subscribes with the same role. Null when {@link #currentUserConnected} is false.
     */
    private ConnectionType currentUserConnectionType;

    /**
     * Milliseconds left in the disconnect grace window before the user's seat is given up. Present
     * only when the user dropped and the countdown is running; null when still genuinely connected
     * (seat held indefinitely). The client uses it to flip "Reconnect" back to "Connect" on expiry.
     */
    private Long reconnectMillisRemaining;
}
