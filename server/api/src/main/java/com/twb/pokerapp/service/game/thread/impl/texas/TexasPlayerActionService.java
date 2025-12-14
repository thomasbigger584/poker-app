package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.service.BettingRoundService;
import com.twb.pokerapp.service.PlayerActionService;
import com.twb.pokerapp.service.game.thread.GameLogService;
import com.twb.pokerapp.service.game.thread.GamePlayerActionService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Component("texasPlayerActionService")
public class TexasPlayerActionService extends GamePlayerActionService {
    private final PlayerActionService playerActionService;
    private final BettingRoundService bettingRoundService;
    private final GameLogService gameLogService;

    @Override
    public Optional<PlayerAction> onPlayerAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread, CreatePlayerActionDTO createDto) {
        return switch (createDto.getAction()) {
            case FOLD -> foldAction(playerSession, bettingRound, createDto);
            case CHECK -> checkAction(playerSession, bettingRound, createDto);
            case BET -> betAction(playerSession, bettingRound, createDto);
            case CALL -> callAction(playerSession, bettingRound, createDto);
            case RAISE -> raiseAction(playerSession, bettingRound, createDto);
        };
    }

    private Optional<PlayerAction> foldAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var action = playerActionService.create(playerSession, bettingRound, createActionDto);
        return Optional.of(action);
    }

    private Optional<PlayerAction> checkAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var canPerformCheck = playerActionService.getPlayerActionsNotFolded(bettingRound.getId())
                .stream().allMatch(action -> action.getActionType() == ActionType.CHECK);
        if (!canPerformCheck) {
            gameLogService.sendLogMessage(playerSession, "Cannot check as previous actions was not a check");
            return Optional.empty();
        }
        var action = playerActionService.create(playerSession, bettingRound, createActionDto);
        return Optional.of(action);
    }

    private Optional<PlayerAction> betAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        if (createActionDto.getAmount() <= 0) {
            gameLogService.sendLogMessage(playerSession, "Cannot bet %.2f as amount is less than or equal to zero".formatted(createActionDto.getAmount()));
            return Optional.empty();
        }
        if (createActionDto.getAmount() > playerSession.getFunds()) {
            gameLogService.sendLogMessage(playerSession, "Cannot bet as %.2f is more than current funds".formatted(createActionDto.getAmount()));
            return Optional.empty();
        }
        var lastPlayerActions = playerActionService.getPlayerActionsNotFolded(bettingRound.getId());
        if (!lastPlayerActions.isEmpty()) {
            var lastPlayerAction = lastPlayerActions.getFirst();
            if (List.of(ActionType.BET, ActionType.CALL, ActionType.RAISE).contains(lastPlayerAction.getActionType())) {
                log.warn("Cannot bet as previous action was not a check");
                gameLogService.sendLogMessage(playerSession, "Cannot bet as previous action was not a check");
                return Optional.empty();
            }
        }
        var action = playerActionService.create(playerSession, bettingRound, createActionDto);
        bettingRound = bettingRoundService.updatePot(bettingRound, createActionDto);
        log.info("BettingRound pot for bet updated to {}", bettingRound.getPot());
        return Optional.of(action);
    }

    private Optional<PlayerAction> callAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var lastPlayerActions = playerActionService.getPlayerActionsNotFolded(bettingRound.getId());
        if (lastPlayerActions.isEmpty()) {
            gameLogService.sendLogMessage(playerSession, "Cannot call as there was no previous action");
            return Optional.empty();
        }
        var lastPlayerAction = lastPlayerActions.getFirst();
        var lastPlayerActionType = lastPlayerAction.getActionType();
        if (lastPlayerActionType == ActionType.CHECK) {
            gameLogService.sendLogMessage(playerSession, "Cannot call as previous action was a check");
            return Optional.empty();
        }
        var amountToCall = lastPlayerActionType.getAmountToCall(lastPlayerAction.getAmount());
        createActionDto.setAmount(amountToCall);
        if (createActionDto.getAmount() > playerSession.getFunds()) {
            gameLogService.sendLogMessage(playerSession, "Cannot call as %.2f is more than current funds".formatted(createActionDto.getAmount()));
            return Optional.empty();
        }
        var action = playerActionService.create(playerSession, bettingRound, createActionDto);
        bettingRound = bettingRoundService.updatePot(bettingRound, createActionDto);
        log.info("BettingRound pot for call updated to {}", bettingRound.getPot());
        return Optional.of(action);
    }

    private Optional<PlayerAction> raiseAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        throw new UnsupportedOperationException("Raise action not implemented yet");
    }
}
