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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public boolean playerAction(PokerTable table, PlayerSession playerSession, GameThread gameThread, CreatePlayerActionDTO createDto) {
        log.info("***************************************************************");
        log.info("GameThread.playerAction");
        log.info("playerSession = {}, createDto = {}", playerSession, createDto);
        log.info("***************************************************************");
        var bettingRoundOpt = bettingRoundRepository.findLatestInProgress(table.getId());
        if (bettingRoundOpt.isEmpty()) {
            log.error("Latest Betting Round not found for Table ID: {}", table.getId());
            return false;
        }
        var bettingRound = bettingRoundOpt.get();

        if (createDto.getAmount() == null) {
            createDto.setAmount(0d);
        }

        return switch (createDto.getAction()) {
            case FOLD -> foldAction(table, playerSession, bettingRound, createDto);
            case CHECK -> checkAction(table, playerSession, bettingRound, createDto);
            case BET -> betAction(table, playerSession, bettingRound, createDto);
            case CALL -> callAction(table, playerSession, bettingRound, createDto);
            case RAISE -> raiseAction(table, playerSession, bettingRound, createDto);
        };
    }

    private boolean foldAction(PokerTable table, PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        playerActionService.create(playerSession, bettingRound, createActionDto);
        return true;
    }

    private boolean checkAction(PokerTable table, PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var canPerformCheck = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId())
                .stream().allMatch(action -> action.getActionType() == ActionType.CHECK);
        if (!canPerformCheck) {
            log.warn("Cannot check as previous actions was not a check");
            gameLogService.sendErrorMessage(table, "Cannot check as previous actions was not a check");
            return false;
        }
        playerActionService.create(playerSession, bettingRound, createActionDto);
        return true;
    }

    private boolean betAction(PokerTable table, PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        if (createActionDto.getAmount() <= 0) {
            log.warn("Cannot bet as amount is less than or equal to zero");
            gameLogService.sendErrorMessage(table, "Cannot bet as amount is less than or equal to zero");
            return false;
        }
        var lastPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
        if (!lastPlayerActions.isEmpty()) {
            var lastPlayerAction = lastPlayerActions.getFirst();
            if (List.of(ActionType.BET, ActionType.CALL, ActionType.RAISE).contains(lastPlayerAction.getActionType())) {
                log.warn("Cannot bet as previous action was not a check");
                gameLogService.sendErrorMessage(table, "Cannot bet as previous action was not a check");
                return false;
            }
        }
        playerActionService.create(playerSession, bettingRound, createActionDto);
        bettingRoundService.updatePot(bettingRound, createActionDto);
        return true;
    }

    private boolean callAction(PokerTable table, PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var lastPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
        if (lastPlayerActions.isEmpty()) {
            log.warn("Cannot call as there was no previous action");
            gameLogService.sendErrorMessage(table, "Cannot call as there was no previous action");
            return false;
        }
        var lastPlayerAction = lastPlayerActions.getFirst();
        var lastPlayerActionType = lastPlayerAction.getActionType();
        if (lastPlayerActionType == ActionType.CHECK) {
            log.warn("Cannot call as previous action was a check");
            gameLogService.sendErrorMessage(table, "Cannot call as previous action was a check");
            return false;
        }
        var amountToCall = lastPlayerActionType.getAmountToCall(lastPlayerAction.getAmount());
        createActionDto.setAmount(amountToCall);
        playerActionService.create(playerSession, bettingRound, createActionDto);
        bettingRoundService.updatePot(bettingRound, createActionDto);
        return true;
    }

    private boolean raiseAction(PokerTable table, PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        throw new UnsupportedOperationException("Raise action not implemented yet");
    }
}
