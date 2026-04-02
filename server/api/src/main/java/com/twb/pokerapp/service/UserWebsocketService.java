package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.PokerTable;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserWebsocketService {
    private final SimpUserRegistry userRegistry;

    public List<SimpUser> getConnectedUsers(PokerTable table) {
        String destination = "/topic/loops." + table.getId();
        return userRegistry.getUsers().stream()
                .filter(user -> isSubscribedToTable(user, destination)).toList();
    }

    private boolean isSubscribedToTable(SimpUser user, String destination) {
        return user.getSessions().stream()
                .flatMap(session -> session.getSubscriptions().stream())
                .anyMatch(sub -> sub.getDestination().equals(destination));
    }
}
