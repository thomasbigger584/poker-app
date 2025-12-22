package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.exception.game.GamePlayerLogException;
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

@Slf4j
@Transactional
@RequiredArgsConstructor
@Component("texasPlayerActionService")
public class TexasPlayerActionService extends GamePlayerActionService {
    private final PlayerActionRepository playerActionRepository;
    private final PlayerActionService playerActionService;
    private final BettingRoundService bettingRoundService;
    private final GameLogService gameLogService;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public PlayerAction onPlayerAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread, CreatePlayerActionDTO createDto) {
        return switch (createDto.getAction()) {
            case FOLD -> foldAction(playerSession, bettingRound, createDto);
            case CHECK -> checkAction(playerSession, bettingRound, createDto);
            case BET -> betAction(playerSession, bettingRound, createDto);
            case CALL -> callAction(playerSession, bettingRound, createDto);
            case RAISE -> raiseAction(playerSession, bettingRound, createDto);
        };
    }

    private PlayerAction foldAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        return playerActionService.create(playerSession, bettingRound, createActionDto);
    }

    private PlayerAction checkAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var canPerformCheck = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId())
                .stream().allMatch(action -> action.getActionType() == ActionType.CHECK);
        if (!canPerformCheck) {
            throw new GamePlayerLogException(playerSession, "Cannot check as previous actions was not a check");
        }
        return playerActionService.create(playerSession, bettingRound, createActionDto);
    }

    private PlayerAction betAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        if (createActionDto.getAmount() <= 0) {
            throw new GamePlayerLogException(playerSession, "Cannot bet $%.2f as amount is less than or equal to zero".formatted(createActionDto.getAmount()));
        }
        if (createActionDto.getAmount() > playerSession.getFunds()) {
            throw new GamePlayerLogException(playerSession, "Cannot bet as $%.2f is more than current funds".formatted(createActionDto.getAmount()));
        }
        var lastPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
        if (!lastPlayerActions.isEmpty()) {
            var lastPlayerAction = lastPlayerActions.getFirst();
            if (List.of(ActionType.BET, ActionType.CALL, ActionType.RAISE).contains(lastPlayerAction.getActionType())) {
                throw new GamePlayerLogException(playerSession, "Cannot bet as previous action was not a check");
            }
        }
        var action = playerActionService.create(playerSession, bettingRound, createActionDto);
        bettingRound = bettingRoundService.updatePot(bettingRound, createActionDto);
        log.info("BettingRound pot for bet updated to {}", bettingRound.getPot());
        return action;
    }

    private PlayerAction callAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var lastPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
        if (lastPlayerActions.isEmpty()) {
            throw new GamePlayerLogException(playerSession, "Cannot call as there was no previous action");
        }
        var lastPlayerAction = lastPlayerActions.getFirst();
        var lastPlayerActionType = lastPlayerAction.getActionType();
        if (lastPlayerActionType == ActionType.CHECK) {
            throw new GamePlayerLogException(playerSession, "Cannot call as previous action was a check");
        }
        var amountToCall = lastPlayerActionType.getAmountToCall(lastPlayerAction.getAmount());
        createActionDto.setAmount(amountToCall);
        if (createActionDto.getAmount() > playerSession.getFunds()) {
            throw new GamePlayerLogException(playerSession, "Cannot call as $%.2f is more than current funds".formatted(createActionDto.getAmount()));
        }
        var action = playerActionService.create(playerSession, bettingRound, createActionDto);
        bettingRound = bettingRoundService.updatePot(bettingRound, createActionDto);
        log.info("BettingRound pot for call updated to {}", bettingRound.getPot());
        return action;
    }

    private PlayerAction raiseAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var lastPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
        if (lastPlayerActions.isEmpty()) {
            throw new GamePlayerLogException(playerSession, "Cannot raise as there was no previous action");
        }
        var lastPlayerAction = lastPlayerActions.getFirst();
        var lastPlayerActionType = lastPlayerAction.getActionType();
        if (lastPlayerActionType == ActionType.CHECK) {
            throw new GamePlayerLogException(playerSession, "Cannot raise as previous action was a check");
        }
        if (createActionDto.getAmount() > playerSession.getFunds()) {
            throw new GamePlayerLogException(playerSession, "Cannot raise as $%.2f is more than current funds".formatted(createActionDto.getAmount()));
        }
        var amountToCall = lastPlayerActionType.getAmountToCall(lastPlayerAction.getAmount());
        if (createActionDto.getAmount() <= amountToCall) {
            throw new GamePlayerLogException(playerSession, "Cannot raise as $%.2f is less than or equal to $%.2f".formatted(createActionDto.getAmount(), amountToCall));
        }
        var action = playerActionService.create(playerSession, bettingRound, createActionDto);
        bettingRound = bettingRoundService.updatePot(bettingRound, createActionDto);
        log.info("BettingRound pot for raise updated to {}", bettingRound.getPot());
        return action;
    }
}
