package com.twb.pokergame.web.websocket.message.server;

import com.twb.pokergame.web.websocket.message.server.payload.ChatMessageDTO;
import com.twb.pokergame.web.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokergame.web.websocket.message.server.payload.PlayerConnectedDTO;
import com.twb.pokergame.web.websocket.message.server.payload.PlayerDisconnectedDTO;
import org.springframework.stereotype.Component;

@Component
public class ServerMessageFactory {

    public ServerMessage playerConnected(String username) {
        PlayerConnectedDTO payload = PlayerConnectedDTO.builder()
                .username(username)
                .build();
        return ServerMessage.create(ServerMessageType.PLAYER_CONNECTED, payload);
    }

    public ServerMessage logMessage(String message) {
        LogMessageDTO payload = LogMessageDTO.builder()
                .message(message)
                .build();
        return ServerMessage.create(ServerMessageType.LOG, payload);
    }

    public ServerMessage playerDisconnected(String username) {
        PlayerDisconnectedDTO payload = PlayerDisconnectedDTO.builder()
                .username(username)
                .build();
        return ServerMessage.create(ServerMessageType.PLAYER_DISCONNECTED, payload);
    }

    public ServerMessage chatMessage(String username, String message) {
        ChatMessageDTO payload = ChatMessageDTO.builder()
                .username(username)
                .message(message)
                .build();
        return ServerMessage.create(ServerMessageType.CHAT, payload);
    }
}
