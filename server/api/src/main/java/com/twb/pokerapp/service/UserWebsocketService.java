package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BotUser;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserWebsocketService {
    private static final String TOPIC_PREFIX = "/topic/loops.";
    private final SimpUserRegistry userRegistry;

    public List<SimpUser> getConnectedUsers(PokerTable table) {
        var destination = TOPIC_PREFIX + table.getId();
        return userRegistry.getUsers().stream()
                .filter(user -> isSubscribedToTable(user, destination)).toList();
    }

    /**
     * Whether the given username currently has any live websocket session subscribed to the
     * table's topic. Used to verify a user is genuinely gone before disconnecting them after a
     * grace period (handles reconnects that opened a fresh session).
     */
    public boolean isUserConnectedToTable(UUID tableId, String username) {
        var websocketUser = userRegistry.getUser(username);
        if (websocketUser == null) {
            return false;
        }
        return isSubscribedToTable(websocketUser, TOPIC_PREFIX + tableId);
    }

    public boolean isUserDisconnected(PokerTable table, PlayerSession session) {
        return !isUserConnected(table, session);
    }

    private boolean isUserConnected(PokerTable table, PlayerSession session) {
        var user = session.getUser();
        if (user instanceof BotUser) {
            return true;
        }
        var username = user.getUsername();
        var websocketUser = userRegistry.getUser(username);
        if (websocketUser == null) {
            return false;
        }
        var destination = TOPIC_PREFIX + table.getId();
        return isSubscribedToTable(websocketUser, destination);
    }

    private boolean isSubscribedToTable(SimpUser user, String destination) {
        return user.getSessions().stream()
                .flatMap(session -> session.getSubscriptions().stream())
                .anyMatch(sub -> sub.getDestination().equals(destination));
    }
}
