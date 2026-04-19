package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public boolean isUserDisconnected(PokerTable table, PlayerSession session) {
        return !isUserConnected(table, session);
    }

    private boolean isUserConnected(PokerTable table, PlayerSession session) {
        var username = session.getUser().getUsername();
        var user = userRegistry.getUser(username);
        if (user == null) {
            return false;
        }
        var destination = TOPIC_PREFIX + table.getId();
        return isSubscribedToTable(user, destination);
    }

    private boolean isSubscribedToTable(SimpUser user, String destination) {
        return user.getSessions().stream()
                .flatMap(session -> session.getSubscriptions().stream())
                .anyMatch(sub -> sub.getDestination().equals(destination));
    }
}
