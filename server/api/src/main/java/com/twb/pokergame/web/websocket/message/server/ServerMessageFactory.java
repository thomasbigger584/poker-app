package com.twb.pokergame.web.websocket.message.server;

import com.twb.pokergame.web.websocket.message.server.body.ChatMessageDTO;
import com.twb.pokergame.web.websocket.message.server.body.LogMessageDTO;
import com.twb.pokergame.web.websocket.message.server.body.PlayerConnectedDTO;
import com.twb.pokergame.web.websocket.message.server.body.PlayerDisconnectedDTO;
import org.springframework.stereotype.Component;

@Component
public class ServerMessageFactory {

    public ServerMessage playerConnected(String username) {
        PlayerConnectedDTO dto = PlayerConnectedDTO.builder()
                .username(username)
                .build();
        return ServerMessage.create(ServerMessageType.PLAYER_CONNECTED, dto);
    }

    public ServerMessage logMessage(String message) {
        LogMessageDTO dto = LogMessageDTO.builder()
                .message(message)
                .build();
        return ServerMessage.create(ServerMessageType.LOG, dto);
    }

    public ServerMessage playerDisconnected(String username) {
        PlayerDisconnectedDTO dto = PlayerDisconnectedDTO.builder()
                .username(username)
                .build();
        return ServerMessage.create(ServerMessageType.PLAYER_DISCONNECTED, dto);
    }

    public ServerMessage chatMessage(String username, String message) {
        ChatMessageDTO dto = ChatMessageDTO.builder()
                .username(username)
                .message(message)
                .build();
        return ServerMessage.create(ServerMessageType.CHAT, dto);
    }
}
