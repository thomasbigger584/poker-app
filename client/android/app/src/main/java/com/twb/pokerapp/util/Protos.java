package com.twb.pokerapp.util;

import android.content.Context;

import com.twb.pokerapp.R;
import com.twb.pokerapp.proto.ActionType;
import com.twb.pokerapp.proto.ConnectionType;
import com.twb.pokerapp.proto.GameType;
import com.twb.pokerapp.proto.PlayerSessionDTO;
import com.twb.pokerapp.proto.PlayerSubscribedDTO;
import com.twb.pokerapp.proto.ServerMessageDTO;

import java.math.BigDecimal;

/**
 * Small adapters between the shared protobuf contract and the Android UI.
 *
 * <p>Protobuf carries money as exact-decimal strings, enums as prefixed values
 * ({@code GAME_TYPE_TEXAS_HOLDEM}), and immutable messages — these helpers bridge those to the
 * doubles, legacy names, and convenience lookups the UI works in. They replace the hand-written
 * convenience methods the old DTOs used to carry.
 */
public final class Protos {
    private Protos() {
    }

    // -- money (BigDecimal carried as string) --------------------------------------

    /** Parse a protobuf money string to a double, treating null/blank/garbage as 0. */
    public static double money(String value) {
        if (value == null || value.isEmpty()) {
            return 0d;
        }
        try {
            return new BigDecimal(value).doubleValue();
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    /** Encode a double as an exact-decimal money string for the wire. */
    public static String moneyStr(double value) {
        return BigDecimal.valueOf(value).toPlainString();
    }

    // -- enum names ----------------------------------------------------------------

    /**
     * The proto enum value with its {@code *_TYPE_} prefix stripped, e.g.
     * {@code ACTION_TYPE_FOLD -> FOLD}, {@code GAME_TYPE_TEXAS_HOLDEM -> TEXAS_HOLDEM}. This is the
     * legacy Java enum name the rest of the app (and the server) speaks.
     */
    public static String shortName(Enum<?> protoEnum) {
        var name = protoEnum.name();
        var marker = "_TYPE_";
        var idx = name.indexOf(marker);
        return idx >= 0 ? name.substring(idx + marker.length()) : name;
    }

    /** Legacy game-type name ("TEXAS_HOLDEM") -> proto enum; unknown maps to UNSPECIFIED. */
    public static GameType gameType(String legacyName) {
        if (legacyName == null || legacyName.isEmpty()) {
            return GameType.GAME_TYPE_UNSPECIFIED;
        }
        try {
            return GameType.valueOf("GAME_TYPE_" + legacyName);
        } catch (IllegalArgumentException e) {
            return GameType.GAME_TYPE_UNSPECIFIED;
        }
    }

    /** Legacy action name ("FOLD") -> proto enum; unknown maps to UNSPECIFIED. */
    public static ActionType actionType(String legacyName) {
        if (legacyName == null || legacyName.isEmpty()) {
            return ActionType.ACTION_TYPE_UNSPECIFIED;
        }
        try {
            return ActionType.valueOf("ACTION_TYPE_" + legacyName);
        } catch (IllegalArgumentException e) {
            return ActionType.ACTION_TYPE_UNSPECIFIED;
        }
    }

    /** Legacy connection-type name ("PLAYER" / "LISTENER") for the proto enum. */
    public static String connectionTypeName(ConnectionType connectionType) {
        return shortName(connectionType);
    }

    /**
     * Resolves the human-facing game-type label from {@code R.array.game_types_str_array}, matching
     * against {@code R.array.game_types_array} by legacy name. Mirrors the old
     * {@code TableDTO.getGameTypeDisplayName(context)}.
     */
    public static String gameTypeDisplayName(Context context, GameType gameType) {
        var key = shortName(gameType);
        var gameTypes = context.getResources().getStringArray(R.array.game_types_array);
        var gameTypeDisplays = context.getResources().getStringArray(R.array.game_types_str_array);
        for (var index = 0; index < gameTypes.length; index++) {
            if (gameTypes[index].equalsIgnoreCase(key) && index < gameTypeDisplays.length) {
                return gameTypeDisplays[index];
            }
        }
        return key;
    }

    // -- websocket envelope --------------------------------------------------------

    /**
     * Mirrors the old {@code ServerMessageType.isTurnEndingMessage()}: every server message ends the
     * current player's turn except chat/log/connect/disconnect (and the empty sentinel).
     */
    public static boolean isTurnEnding(ServerMessageDTO.PayloadCase payloadCase) {
        switch (payloadCase) {
            case CHAT:
            case LOG:
            case PLAYER_CONNECTED:
            case PLAYER_DISCONNECTED:
            case PAYLOAD_NOT_SET:
                return false;
            default:
                return true;
        }
    }

    /**
     * The session belonging to {@code username} in a PLAYER_SUBSCRIBED snapshot. Mirrors the old
     * {@code PlayerSubscribedDTO.getCurrentPlayerSession(username)}.
     */
    public static PlayerSessionDTO currentPlayerSession(PlayerSubscribedDTO subscribed, String username) {
        for (var playerSession : subscribed.getPlayerSessionsList()) {
            if (username.equals(playerSession.getUser().getUsername())) {
                return playerSession;
            }
        }
        throw new RuntimeException("Failed to find current player in sessions");
    }
}
