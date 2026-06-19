package com.twb.pokerapp.service.game;

import com.twb.pokerapp.mapper.BettingRoundMapper;
import com.twb.pokerapp.mapper.CardMapper;
import com.twb.pokerapp.mapper.PlayerSessionMapper;
import com.twb.pokerapp.mapper.RoundMapper;
import com.twb.pokerapp.mapper.RoundPotMapper;
import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.CardRepository;
import com.twb.pokerapp.repository.HandRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.service.game.thread.GameThreadManager;
import com.twb.pokerapp.proto.DealPlayerCardDTO;
import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.proto.RoundStateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.UUID;

/**
 * Assembles the {@link RoundStateDTO} snapshot of the hand currently in progress on a table, so a
 * (re)subscribing client can resume exactly where the hand left off. Returns {@code null} when no
 * hand is being played.
 *
 * <p>Note: dealt hole cards are already broadcast to the whole table during live play (the client
 * hides opponents' cards in the UI), so including every player's cards here leaks nothing new.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoundStateService {

    private final RoundRepository roundRepository;
    private final CardRepository cardRepository;
    private final HandRepository handRepository;
    private final BettingRoundRepository bettingRoundRepository;
    private final PlayerSessionRepository playerSessionRepository;

    private final RoundMapper roundMapper;
    private final BettingRoundMapper bettingRoundMapper;
    private final RoundPotMapper roundPotMapper;
    private final PlayerSessionMapper playerSessionMapper;
    private final CardMapper cardMapper;

    private final GameThreadManager threadManager;

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public RoundStateDTO buildCurrentRoundState(UUID tableId) {
        var roundOpt = roundRepository.findCurrentByTableId(tableId);
        if (roundOpt.isEmpty()) {
            return null;
        }
        var round = roundOpt.get();

        var builder = RoundStateDTO.newBuilder();
        builder.setRound(roundMapper.modelToDto(round));

        var sessions = playerSessionRepository.findConnectedByTableId(tableId);
        for (var session : sessions) {
            var sessionDto = playerSessionMapper.modelToDto(session);
            if (Boolean.TRUE.equals(session.getDealer())) {
                builder.setDealer(sessionDto);
            }
            handRepository.findForPlayerAndRound(session.getId(), round.getId()).ifPresent(hand -> {
                for (var card : hand.getCards()) {
                    builder.addPlayerCards(DealPlayerCardDTO.newBuilder()
                            .setPlayerSession(sessionDto)
                            .setCard(cardMapper.modelToDto(card))
                            .build());
                }
                // Dealt in but no longer active => folded (or dropped out of the hand).
                if (Boolean.FALSE.equals(session.getActive())) {
                    builder.addFoldedPlayers(sessionDto);
                }
            });
        }

        cardRepository.findCommunityCardsForRound(round.getId()).stream()
                .sorted(Comparator.comparingInt(card -> card.getCardType().getNumber()))
                .forEach(card -> builder.addCommunityCards(cardMapper.modelToDto(card)));

        bettingRoundRepository.findCurrentByRoundId(round.getId())
                .ifPresent(bettingRound -> builder.setBettingRound(bettingRoundMapper.modelToDto(bettingRound)));

        round.getRoundPots().forEach(roundPot -> builder.addRoundPots(roundPotMapper.modelToDto(roundPot)));

        var currentTurn = buildCurrentTurn(tableId);
        if (currentTurn != null) {
            builder.setCurrentTurn(currentTurn);
        }

        return builder.build();
    }

    /**
     * The turn currently being awaited, if any, re-stamped with the remaining wait so the client's
     * action buttons / countdown resume correctly. Tied to the live turn latch so buttons are only
     * offered when the server is genuinely blocked waiting for that player.
     */
    private PlayerTurnDTO buildCurrentTurn(UUID tableId) {
        var gameThreadOpt = threadManager.getIfExists(tableId);
        if (gameThreadOpt.isEmpty()) {
            return null;
        }
        var gameThread = gameThreadOpt.get();
        var latch = gameThread.getPlayerTurnLatch();
        var activeTurn = gameThread.getActiveTurn();
        if (latch == null || activeTurn == null) {
            return null;
        }
        var awaitedPlayerId = latch.playerSession().getId();
        var source = activeTurn.turn();
        var turnPlayerId = source.getPlayerSession().getId();
        if (turnPlayerId.isEmpty() || !turnPlayerId.equals(awaitedPlayerId.toString())) {
            return null;
        }
        var remaining = source.getPlayerTurnWaitMs() - (System.currentTimeMillis() - activeTurn.startMillis());
        if (remaining <= 0) {
            return null;
        }
        // Re-stamp the live turn with the remaining wait; all other fields carry over unchanged.
        return source.toBuilder().setPlayerTurnWaitMs(remaining).build();
    }
}
