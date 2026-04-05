package com.twb.pokerapp.service;

import com.twb.pokerapp.service.keycloak.KeycloakUserService;
import com.twb.pokerapp.service.table.TableService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeederService {
    private final KeycloakUserService keycloakUserService;
    private final PlayerSessionService playerSessionService;
    private final RoundService roundService;
    private final BettingRoundService bettingRoundService;
    private final TableService tableService;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        keycloakUserService.init();
        playerSessionService.reset();
        roundService.reset();
        bettingRoundService.reset();
        tableService.createTestTables();
    }
}
