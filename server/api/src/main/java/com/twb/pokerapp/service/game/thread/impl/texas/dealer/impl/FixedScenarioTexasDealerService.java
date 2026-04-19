package com.twb.pokerapp.service.game.thread.impl.texas.dealer.impl;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.service.game.thread.impl.texas.dealer.TexasDealerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.use-fixed-scenario", havingValue = "true")
public class FixedScenarioTexasDealerService extends TexasDealerService {

    @Override
    protected PlayerSession nextDealerReorder(List<PlayerSession> playerSessions) {
        var nextDealer = playerSessions.getLast();
        setNextDealer(playerSessions, nextDealer);
        return nextDealer;
    }
}
