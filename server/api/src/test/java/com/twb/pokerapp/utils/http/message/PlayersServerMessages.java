package com.twb.pokerapp.utils.http.message;

import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

public class PlayersServerMessages extends HashMap<String, List<ServerMessageDTO>> {

    public PlayersServerMessages() {
    }

    public PlayersServerMessages(AbstractTestUser listener,
                                 List<AbstractTestUser> players) {
        put(listener.getParams().getUsername(), listener.getReceivedMessages());
        for (AbstractTestUser player : players) {
            put(player.getParams().getUsername(), player.getReceivedMessages());
        }
    }

    public PlayersServerMessages getByNumberOfRounds(int numberOfRounds) {
        PlayersServerMessages messages = new PlayersServerMessages();
        for (Entry<String, List<ServerMessageDTO>> entry : entrySet()) {
            messages.put(entry.getKey(), filterByNumberOfRounds(entry.getValue(), numberOfRounds));
        }
        return messages;
    }

    public List<ServerMessageDTO> getListenerMessages() {
        Optional<Entry<String, List<ServerMessageDTO>>> listenerEntry =
                entrySet().stream().findFirst();
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
        List<ServerMessageDTO> filteredMessages = new ArrayList<>();
        int roundEncountered = 0;
        for (ServerMessageDTO message : receivedMessages) {
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
