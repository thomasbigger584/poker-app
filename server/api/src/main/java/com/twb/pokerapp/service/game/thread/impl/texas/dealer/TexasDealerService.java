package com.twb.pokerapp.service.game.thread.impl.texas.dealer;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

import static com.twb.pokerapp.util.TransactionUtil.afterCommit;

public abstract class TexasDealerService {
    protected static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    protected PlayerSessionRepository playerSessionRepository;

    @Autowired
    private ServerMessageFactory messageFactory;

    @Autowired
    private MessageDispatcher dispatcher;

    @Transactional
    public void determineNextDealer(GameThreadParams params) {
        var table = params.getTable();
        var playerSessions = playerSessionRepository.findConnectedPlayersByTableId(table.getId());
        if (CollectionUtils.isEmpty(playerSessions)) {
            throw new GameInterruptedException("No Players Connected");
        }
        var nextDealer = nextDealerReorder(playerSessions);
        afterCommit(() -> dispatcher.send(params, messageFactory.dealerDetermined(nextDealer)));
    }

    protected void setNextDealer(List<PlayerSession> playerSessions, PlayerSession nextDealer) {
        for (var playerSession : playerSessions) {
            if (playerSession.getId().equals(nextDealer.getId())) {
                playerSession.setDealer(true);
                nextDealer.setDealer(true);
            } else {
                playerSession.setDealer(false);
            }
        }
    }

    protected abstract PlayerSession nextDealerReorder(List<PlayerSession> playerSessions);
}
