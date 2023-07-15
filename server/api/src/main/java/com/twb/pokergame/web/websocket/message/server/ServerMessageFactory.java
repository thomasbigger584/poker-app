package com.twb.pokergame.web.websocket.message.server;

import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.web.websocket.message.server.payload.ChatMessageDTO;
import com.twb.pokergame.web.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokergame.web.websocket.message.server.payload.PlayerConnectedDTO;
import com.twb.pokergame.web.websocket.message.server.payload.PlayerDisconnectedDTO;
import org.springframework.stereotype.Component;

@Component
public class ServerMessageFactory {

    public ServerMessageDTO playerConnected(PlayerSessionDTO dto) {
        PlayerConnectedDTO payload = PlayerConnectedDTO.builder()
                .session(dto)
                .build();
        return ServerMessageDTO.create(ServerMessageType.PLAYER_CONNECTED, payload);
    }

    public ServerMessageDTO logMessage(String message) {
        LogMessageDTO payload = LogMessageDTO.builder()
                .message(message)
                .build();
        return ServerMessageDTO.create(ServerMessageType.LOG, payload);
    }

    public ServerMessageDTO playerDisconnected(String username) {
        PlayerDisconnectedDTO payload = PlayerDisconnectedDTO.builder()
                .username(username)
                .build();
        return ServerMessageDTO.create(ServerMessageType.PLAYER_DISCONNECTED, payload);
    }

    public ServerMessageDTO chatMessage(String username, String message) {
        ChatMessageDTO payload = ChatMessageDTO.builder()
                .username(username)
                .message(message)
                .build();
        return ServerMessageDTO.create(ServerMessageType.CHAT, payload);
    }
}
