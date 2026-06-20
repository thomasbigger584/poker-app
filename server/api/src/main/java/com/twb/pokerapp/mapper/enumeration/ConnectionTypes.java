package com.twb.pokerapp.mapper.enumeration;

import com.twb.pokerapp.proto.ConnectionType;

/**
 * Translates between the proto {@link ConnectionType} and the short token carried in the
 * {@code X-Connection-Type} STOMP handshake header (e.g. {@code "PLAYER"} / {@code "LISTENER"}). The
 * wire token is the legacy short enum name, kept stable so the Android client and tests stay
 * compatible while the server uses the proto enum internally.
 */
public final class ConnectionTypes {

    private static final String PREFIX = "CONNECTION_TYPE_";

    private ConnectionTypes() {
    }

    /**
     * Parse the short header token into a proto connection type.
     */
    public static ConnectionType fromWire(String token) {
        return ConnectionType.valueOf(PREFIX + token);
    }

    /**
     * The short header token for a connection type (inverse of {@link #fromWire}).
     */
    public static String toWire(ConnectionType connectionType) {
        return connectionType.getValueDescriptor().getName().substring(PREFIX.length());
    }
}
