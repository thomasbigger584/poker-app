package com.twb.pokerapp.service.table;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.dto.table.AvailableTableDTO;
import com.twb.pokerapp.dto.table.CreateTableDTO;
import com.twb.pokerapp.mapper.TableMapper;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.TableRepository;
import com.twb.pokerapp.web.websocket.session.DisconnectGraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component

@RequiredArgsConstructor
public class TableService {
    private final ApplicationContext context;

    private final TableRepository repository;
    private final TableMapper mapper;

    private final PlayerSessionRepository playerSessionRepository;
    private final DisconnectGraceService disconnectGraceService;

    @Transactional(propagation = Propagation.MANDATORY)
    public void createTestTables() {
        var allTables = repository.findAll();
        if (allTables.isEmpty()) {
            var createTableDto1 = new CreateTableDTO();
            createTableDto1.setName("Poker Table 1");
            createTableDto1.setGameType(GameType.TEXAS_HOLDEM);
            createTableDto1.setSpeedMultiplier(1d);
            createTableDto1.setTotalRounds(null);
            createTableDto1.setMinPlayers(2);
            createTableDto1.setMaxPlayers(6);
            createTableDto1.setMinBuyin(BigDecimal.valueOf(100));
            createTableDto1.setMaxBuyin(BigDecimal.valueOf(10_000));
            create(createTableDto1);
        }
    }

    @Transactional
    public PokerTable create(CreateTableDTO dto) {
        dto.getGameType().getValidationService(context).validate(dto);
        var table = mapper.createDtoToModel(dto);
        table = repository.save(table);
        return table;
    }

    @Transactional(readOnly = true)
    public Page<AvailableTableDTO> getAllAvailable(Pageable pageable, String username) {
        // Tables where this user still has a live session (dropped within the grace window, or
        // backgrounded) — so the client can offer "Reconnect" straight back into the game.
        var reconnectableTypes = playerSessionRepository.findConnectedByUsername(username).stream()
                .filter(session -> session.getPokerTable() != null)
                .collect(Collectors.toMap(
                        session -> session.getPokerTable().getId(),
                        PlayerSession::getConnectionType,
                        (existing, ignored) -> existing));

        var page = repository.findAll(pageable);
        return page.map(table -> {
            var availableTableDTO = new AvailableTableDTO();
            availableTableDTO.setTable(mapper.modelToDto(table));
            availableTableDTO.setPlayersConnected(playerSessionRepository.countConnectedPlayersByTableId(table.getId()));
            var existingConnectionType = reconnectableTypes.get(table.getId());
            availableTableDTO.setCurrentUserConnected(existingConnectionType != null);
            availableTableDTO.setCurrentUserConnectionType(existingConnectionType);
            if (existingConnectionType != null) {
                disconnectGraceService.getRemainingMillis(table.getId(), username)
                        .ifPresent(availableTableDTO::setReconnectMillisRemaining);
            }
            return availableTableDTO;
        });
    }
}
