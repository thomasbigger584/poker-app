package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.service.BettingRoundService;
import com.twb.pokerapp.service.PlayerActionService;
import com.twb.pokerapp.service.game.thread.GameLogService;
import com.twb.pokerapp.service.game.thread.GamePlayerActionService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component("texasPlayerActionService")
public class TexasPlayerActionService implements GamePlayerActionService {
    private final PlayerActionRepository playerActionRepository;
    private final BettingRoundRepository bettingRoundRepository;

    private final PlayerActionService playerActionService;
    private final BettingRoundService bettingRoundService;
    private final GameLogService gameLogService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean playerAction(PlayerSession playerSession, GameThread gameThread, CreatePlayerActionDTO createDto) {
        var pokerTable = playerSession.getPokerTable();
        if (pokerTable == null) {
            log.error("Table is null for player session: {}", playerSession);
            return false;
        }
        var tableId = pokerTable.getId();
        var bettingRoundOpt = bettingRoundRepository.findLatestInProgress(tableId);
        if (bettingRoundOpt.isEmpty()) {
            log.error("Latest Betting Round not found for Table ID: {}", tableId);
            return false;
        }
        var bettingRound = bettingRoundOpt.get();

        if (createDto.getAmount() == null) {
            createDto.setAmount(0d);
        }

        return switch (createDto.getAction()) {
            case FOLD -> foldAction(playerSession, bettingRound, createDto);
            case CHECK -> checkAction(playerSession, bettingRound, createDto);
            case BET -> betAction(playerSession, bettingRound, createDto);
            case CALL -> callAction(playerSession, bettingRound, createDto);
            case RAISE -> raiseAction(playerSession, bettingRound, createDto);
        };
    }

    private boolean foldAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        playerActionService.create(playerSession, bettingRound, createActionDto);
        return true;
    }

    private boolean checkAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var canPerformCheck = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId())
                .stream().allMatch(action -> action.getActionType() == ActionType.CHECK);
        if (!canPerformCheck) {
            gameLogService.sendErrorMessage(playerSession, "Cannot check as previous actions was not a check");
            return false;
        }
        playerActionService.create(playerSession, bettingRound, createActionDto);
        return true;
    }

    private boolean betAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        if (createActionDto.getAmount() <= 0) {
            gameLogService.sendErrorMessage(playerSession, "Cannot bet %.2f as amount is less than or equal to zero".formatted(createActionDto.getAmount()));
            return false;
        }
        if (createActionDto.getAmount() > playerSession.getFunds()) {
            gameLogService.sendErrorMessage(playerSession, "Cannot bet as %.2f is more than current funds".formatted(createActionDto.getAmount()));
            return false;
        }
        var lastPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
        if (!lastPlayerActions.isEmpty()) {
            var lastPlayerAction = lastPlayerActions.getFirst();
            if (List.of(ActionType.BET, ActionType.CALL, ActionType.RAISE).contains(lastPlayerAction.getActionType())) {
                log.warn("Cannot bet as previous action was not a check");
                gameLogService.sendErrorMessage(playerSession, "Cannot bet as previous action was not a check");
                return false;
            }
        }
        playerActionService.create(playerSession, bettingRound, createActionDto);
        bettingRound = bettingRoundService.updatePot(bettingRound, createActionDto);
        log.info("BettingRound pot for bet updated to {}", bettingRound.getPot());
        return true;
    }

    private boolean callAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var lastPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
        if (lastPlayerActions.isEmpty()) {
            gameLogService.sendErrorMessage(playerSession, "Cannot call as there was no previous action");
            return false;
        }
        var lastPlayerAction = lastPlayerActions.getFirst();
        var lastPlayerActionType = lastPlayerAction.getActionType();
        if (lastPlayerActionType == ActionType.CHECK) {
            gameLogService.sendErrorMessage(playerSession, "Cannot call as previous action was a check");
            return false;
        }
        var amountToCall = lastPlayerActionType.getAmountToCall(lastPlayerAction.getAmount());
        createActionDto.setAmount(amountToCall);
        if (createActionDto.getAmount() > playerSession.getFunds()) {
            gameLogService.sendErrorMessage(playerSession, "Cannot call as %.2f is more than current funds".formatted(createActionDto.getAmount()));
            return false;
        }
        playerActionService.create(playerSession, bettingRound, createActionDto);
        bettingRound = bettingRoundService.updatePot(bettingRound, createActionDto);
        log.info("BettingRound pot for call updated to {}", bettingRound.getPot());
        return true;
    }

    private boolean raiseAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        throw new UnsupportedOperationException("Raise action not implemented yet");
    }
}
