package com.twb.pokerapp.service;

import com.twb.pokerapp.service.table.TableService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeederService {
    private final PlayerSessionService playerSessionService;
    private final RoundService roundService;
    private final BettingRoundService bettingRoundService;
    private final TableService tableService;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        playerSessionService.reset();
        roundService.reset();
        bettingRoundService.reset();
        tableService.createTestTables();
    }
}
