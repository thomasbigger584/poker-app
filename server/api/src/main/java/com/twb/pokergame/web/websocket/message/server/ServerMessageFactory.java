package com.twb.pokergame.web.websocket.message.server;

import com.twb.pokergame.web.websocket.message.server.body.ChatMessageDTO;
import com.twb.pokergame.web.websocket.message.server.body.PlayerConnectedDTO;
import com.twb.pokergame.web.websocket.message.server.body.PlayerDisconnectedDTO;
import org.springframework.stereotype.Component;

@Component
public class ServerMessageFactory {

    public ServerMessage playerConnected(String username) {
        PlayerConnectedDTO dto = PlayerConnectedDTO.builder()
                .username(username)
                .build();
        return ServerMessage.builder()
                .type(ServerMessageType.PLAYER_CONNECTED)
                .body(dto).build();
    }

    public ServerMessage playerDisconnected(String username) {
        PlayerDisconnectedDTO dto = PlayerDisconnectedDTO.builder()
                .username(username)
                .build();

        return ServerMessage.builder()
                .type(ServerMessageType.PLAYER_DISCONNECTED)
                .body(dto).build();
    }

    public ServerMessage chatMessage(String username, String message) {
        ChatMessageDTO dto = ChatMessageDTO.builder()
                .username(username)
                .message(message)
                .date(System.currentTimeMillis())
                .build();

        return ServerMessage.builder()
                .type(ServerMessageType.CHAT)
                .body(dto).build();
    }
}
