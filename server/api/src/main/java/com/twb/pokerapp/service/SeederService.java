package com.twb.pokerapp.service;

import com.twb.pokerapp.service.keycloak.KeycloakUserService;
import com.twb.pokerapp.service.player.PlayerSessionService;
import com.twb.pokerapp.service.table.TableService;
import com.twb.pokerapp.service.user.BotUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeederService {
    private final KeycloakUserService keycloakUserService;
    private final BotUserService personaService;
    private final PlayerSessionService playerSessionService;
    private final RoundService roundService;
    private final BettingRoundService bettingRoundService;
    private final TableService tableService;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        keycloakUserService.init();
        personaService.init();
        playerSessionService.reset();
        roundService.reset();
        bettingRoundService.reset();
        tableService.createTestTables();

        // Log is used to notify the tests that the container is up
        // i.e. seeding is done
        log.info("Application seeding completed");
    }
}
