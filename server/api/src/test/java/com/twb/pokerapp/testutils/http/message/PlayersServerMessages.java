package com.twb.pokerapp.testutils.http.message;

import com.twb.pokerapp.proto.ServerMessageDTO;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class PlayersServerMessages extends HashMap<String, List<ServerMessageDTO>> {

    public PlayersServerMessages() {
    }

    public PlayersServerMessages(AbstractTestUser listenerUser,
                                 List<AbstractTestUser> playerUsers) {
        put(listenerUser.getParams().getUsername(), listenerUser.getReceivedMessages());
        for (var player : playerUsers) {
            put(player.getParams().getUsername(), player.getReceivedMessages());
        }
    }

    @Deprecated
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
            if (message.getPayloadCase() == ServerMessageDTO.PayloadCase.ROUND_FINISHED) {
                roundEncountered++;
            }
            if (roundEncountered == numberOfRounds) {
                return filteredMessages;
            }
        }
        return filteredMessages;
    }
}
