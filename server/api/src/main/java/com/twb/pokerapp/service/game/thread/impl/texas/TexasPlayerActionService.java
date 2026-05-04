package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.service.PlayerActionService;
import com.twb.pokerapp.service.game.exception.GamePlayerLogException;
import com.twb.pokerapp.service.game.thread.GamePlayerActionService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component("texasPlayerActionService")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TexasPlayerActionService extends GamePlayerActionService {
    private final PlayerActionRepository playerActionRepository;
    private final PlayerActionService playerActionService;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public PlayerAction onPlayerAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread, CreatePlayerActionDTO createDto) {
        return switch (createDto.getAction()) {
            case FOLD -> foldAction(playerSession, bettingRound, createDto);
            case CHECK -> checkAction(playerSession, bettingRound, createDto);
            case BET -> betAction(playerSession, bettingRound, createDto);
            case CALL -> callAction(playerSession, bettingRound, createDto);
            case RAISE -> raiseAction(playerSession, bettingRound, createDto);
            case ALL_IN -> allInAction(playerSession, bettingRound, createDto);
        };
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void onExecuteAutoAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread) {
        var playersNotFolded = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
        var amountToCall = playerActionService.getAmountToCall(playerSession, playersNotFolded);

        var createActionDto = new CreatePlayerActionDTO();
        createActionDto.setAction((amountToCall.compareTo(BigDecimal.ZERO) > 0) ? ActionType.FOLD : ActionType.CHECK);
        super.playerAction(playerSession, gameThread, createActionDto);
    }

    private PlayerAction foldAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        createActionDto.setAmount(null);
        return playerActionService.create(playerSession, bettingRound, createActionDto);
    }

    private PlayerAction checkAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var canPerformCheck = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId())
                .stream().allMatch(action -> action.getActionType() == ActionType.CHECK);
        if (!canPerformCheck) {
            throw new GamePlayerLogException(playerSession, "Cannot check as previous actions was not a check");
        }
        createActionDto.setAmount(BigDecimal.ZERO);
        return playerActionService.create(playerSession, bettingRound, createActionDto);
    }

    private PlayerAction betAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        if (createActionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new GamePlayerLogException(playerSession, "Cannot bet $%.2f as amount is less than or equal to zero".formatted(createActionDto.getAmount()));
        }
        if (createActionDto.getAmount().compareTo(playerSession.getFunds()) >= 0) {
            return allInAction(playerSession, bettingRound, createActionDto);
        }
        var lastPlayerActions = playerActionRepository.findPlayerActionsNotFolded(bettingRound.getId());
        if (!lastPlayerActions.isEmpty()) {
            var lastPlayerAction = lastPlayerActions.getFirst();
            if (List.of(ActionType.BET, ActionType.CALL, ActionType.RAISE, ActionType.ALL_IN).contains(lastPlayerAction.getActionType())) {
                throw new GamePlayerLogException(playerSession, "Cannot bet as previous action was not a check");
            }
        }
        return playerActionService.create(playerSession, bettingRound, createActionDto);
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
        if (createActionDto.getAmount() == null) {
            log.debug("Setting call amount to $%.2f as call amount sent was null".formatted(lastPlayerAction.getAmount()));
            createActionDto.setAmount(lastPlayerAction.getAmount());
        }
        if (createActionDto.getAmount().compareTo(playerSession.getFunds()) >= 0) {
            return allInAction(playerSession, bettingRound, createActionDto);
        }
        var amountToCall = playerActionService.getAmountToCall(playerSession, lastPlayerActions);
        if (createActionDto.getAmount().compareTo(amountToCall) != 0) {
            log.warn("Call amount sent $%.2f not equalled to actual amount to call $%.2f so setting it".formatted(createActionDto.getAmount(), amountToCall));
            createActionDto.setAmount(amountToCall);
        }
        return playerActionService.create(playerSession, bettingRound, createActionDto);
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
        var amountToCall = playerActionService.getAmountToCall(playerSession, lastPlayerActions);
        if (createActionDto.getAmount().compareTo(amountToCall) <= 0) {
            throw new GamePlayerLogException(playerSession, "Cannot raise as $%.2f is less than or equal to $%.2f".formatted(createActionDto.getAmount(), amountToCall));
        }
        if (createActionDto.getAmount().compareTo(playerSession.getFunds()) >= 0) {
            return allInAction(playerSession, bettingRound, createActionDto);
        }
        return playerActionService.create(playerSession, bettingRound, createActionDto);
    }

    private PlayerAction allInAction(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createActionDto) {
        var playerSessionFunds = playerSession.getFunds();
        var createActionAmount = createActionDto.getAmount();
        if (createActionAmount == null || createActionAmount.compareTo(playerSessionFunds) != 0) {
            log.warn("All-In amount sent $%.2f not equalled to actual amount to All-In $%.2f so setting it".formatted(createActionDto.getAmount(), playerSessionFunds));
            createActionDto.setAmount(playerSession.getFunds());
        }
        createActionDto.setAction(ActionType.ALL_IN);
        return playerActionService.create(playerSession, bettingRound, createActionDto);
    }
}
