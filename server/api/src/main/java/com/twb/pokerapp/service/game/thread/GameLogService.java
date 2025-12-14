package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GameLogService {
    private final MessageDispatcher dispatcher;
    private final ServerMessageFactory messageFactory;

    // *****************************************************************************************
    // Log Methods
    // *****************************************************************************************

    public void sendLogMessage(PokerTable table, String message) {
        dispatcher.send(table, messageFactory.logMessage(message));
    }

    public void sendLogMessage(UUID tableId, String message) {
        dispatcher.send(tableId, messageFactory.logMessage(message));
    }

    public void sendLogMessage(PlayerSession playerSession, String message) {
        String username = playerSession.getUser().getUsername();
        dispatcher.send(username, messageFactory.logMessage(message));
    }

    // *****************************************************************************************
    // Error Methods
    // *****************************************************************************************

    public void sendErrorMessage(PokerTable table, String message) {
        dispatcher.send(table, messageFactory.errorMessage(message));
    }

    public void sendErrorMessage(UUID tableId, String message) {
        dispatcher.send(tableId, messageFactory.errorMessage(message));
    }
}
