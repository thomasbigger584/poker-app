package com.twb.pokerapp.service.game.thread.impl.texas.dealer;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.texas.dealer.impl.DefaultTexasDealerService;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.twb.pokerapp.util.TransactionUtil.afterCommit;

public abstract class TexasDealerService {
    protected static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    protected PlayerSessionRepository playerSessionRepository;

    @Autowired
    private ServerMessageFactory messageFactory;

    @Autowired
    private MessageDispatcher dispatcher;

    // *****************************************************************************************
    // Public Methods
    // *****************************************************************************************

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

    public List<PlayerSession> sortDealerLast(List<PlayerSession> playerSessions) {
        var dealerWithIndexOpt = getCurrentDealerWithIndex(playerSessions);
        if (dealerWithIndexOpt.isPresent()) {
            var dealerWithIndex = dealerWithIndexOpt.get();
            return sortDealerLast(playerSessions, dealerWithIndex.index());
        }
        return playerSessions;
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    protected Optional<DealerWithIndexDTO> getCurrentDealerWithIndex(List<PlayerSession> playerSessions) {
        for (var index = 0; index < playerSessions.size(); index++) {
            var playerSession = playerSessions.get(index);
            if (Boolean.TRUE.equals(playerSession.getDealer())) {
                return Optional.of(new DealerWithIndexDTO(index, playerSession));
            }
        }
        return Optional.empty();
    }

    protected List<PlayerSession> sortDealerLast(List<PlayerSession> playerSessions, int dealerIndex) {
        var start = dealerIndex + 1;
        if (start > playerSessions.size()) {
            start = 0;
        }
        var dealerSortedList = new ArrayList<PlayerSession>();
        for (var index = start; index < playerSessions.size(); index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        for (var index = 0; index < start; index++) {
            dealerSortedList.add(playerSessions.get(index));
        }
        return dealerSortedList;
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

    protected record DealerWithIndexDTO(int index, PlayerSession dealerPlayerSession) {
    }
}
