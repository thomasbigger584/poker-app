package com.twb.pokergame.web.websocket.message.server;

import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.web.websocket.message.server.payload.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServerMessageFactory {

    public ServerMessageDTO playerSubscribed(List<PlayerSessionDTO> playerSessions) {
        PlayerSubscribedDTO payload = PlayerSubscribedDTO.builder()
                .playerSessions(playerSessions)
                .build();
        return ServerMessageDTO.create(ServerMessageType.PLAYER_SUBSCRIBED, payload);
    }

    public ServerMessageDTO playerConnected(PlayerSessionDTO playerSession) {
        PlayerConnectedDTO payload = PlayerConnectedDTO.builder()
                .playerSession(playerSession)
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
