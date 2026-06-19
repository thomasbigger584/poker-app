package com.twb.pokerapp.service.table;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.mapper.TableMapper;
import com.twb.pokerapp.proto.AvailableTableDTO;
import com.twb.pokerapp.proto.CreateTableDTO;
import com.twb.pokerapp.proto.GameType;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.TableRepository;
import com.twb.pokerapp.service.game.GameBeanFactory;
import com.twb.pokerapp.web.exception.ValidationException;
import com.twb.pokerapp.web.websocket.session.DisconnectGraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
            var createTableDto1 = CreateTableDTO.newBuilder()
                    .setName("Poker Table 1")
                    .setGameType(GameType.GAME_TYPE_TEXAS_HOLDEM)
                    .setSpeedMultiplier(1d)
                    .setMinPlayers(2)
                    .setMaxPlayers(6)
                    .setMinBuyin("100")
                    .setMaxBuyin("10000")
                    .build();
            create(createTableDto1);
        }
    }

    @Transactional
    public PokerTable create(CreateTableDTO dto) {
        var gameType = dto.getGameType();
        if (dto.getGameTypeValue() <= 0) {
            throw new ValidationException("gameType", "Game Type is required");
        }
        GameBeanFactory.validationService(gameType, context).validate(dto);
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
            var builder = AvailableTableDTO.newBuilder()
                    .setTable(mapper.modelToDto(table))
                    .setPlayersConnected(playerSessionRepository.countConnectedPlayersByTableId(table.getId()));
            var existingConnectionType = reconnectableTypes.get(table.getId());
            builder.setCurrentUserConnected(existingConnectionType != null);
            if (existingConnectionType != null) {
                builder.setCurrentUserConnectionType(existingConnectionType);
                disconnectGraceService.getRemainingMillis(table.getId(), username)
                        .ifPresent(builder::setReconnectMillisRemaining);
            }
            return builder.build();
        });
    }
}
