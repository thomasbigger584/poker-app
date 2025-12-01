package com.twb.pokerapp.testutils.http.message;

import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class PlayersServerMessages extends HashMap<String, List<ServerMessageDTO>> {

    public PlayersServerMessages() {
    }

    public PlayersServerMessages(AbstractTestUser listener,
                                 List<AbstractTestUser> players) {
        put(listener.getParams().getUsername(), listener.getReceivedMessages());
        for (var player : players) {
            put(player.getParams().getUsername(), player.getReceivedMessages());
        }
    }

    public PlayersServerMessages getByNumberOfRounds(int numberOfRounds) {
        var messages = new PlayersServerMessages();
        for (var entry : entrySet()) {
            messages.put(entry.getKey(), filterByNumberOfRounds(entry.getValue(), numberOfRounds));
        }
        return messages;
    }

    public List<ServerMessageDTO> getListenerMessages() {
        var listenerEntry = entrySet().stream().findFirst();
        if (listenerEntry.isPresent()) {
            return listenerEntry.get().getValue();
        }
        return Lists.newArrayList();
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    private List<ServerMessageDTO> filterByNumberOfRounds(List<ServerMessageDTO> receivedMessages, int numberOfRounds) {
        receivedMessages.sort(Comparator.comparing(ServerMessageDTO::getTimestamp));
        var filteredMessages = new ArrayList<ServerMessageDTO>();
        var roundEncountered = 0;
        for (var message : receivedMessages) {
            filteredMessages.add(message);
            if (message.getType().equals(ServerMessageType.ROUND_FINISHED)) {
                roundEncountered++;
            }
            if (roundEncountered == numberOfRounds) {
                return filteredMessages;
            }
        }
        return filteredMessages;
    }
}
